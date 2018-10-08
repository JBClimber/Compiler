package tokener;

import java.io.IOException;

public class Assembly {

	private int n;
	private String asmCode;
	
	private boolean mainFlag;
	
	Compiler c;
	
	public Assembly(Compiler c){
		this.c = c;
	}
	
	public void newStmt( int startIndex, int endIndex ){
		n = startIndex + 1;
		newExpr(startIndex, endIndex);
		
		// erases tokens in tokens Array
		for ( n=startIndex+1; n <= endIndex; n++){
			c.s.tokens[n].lexeme = "";
			c.s.tokens[n].attribute = "";
			c.s.tokens[n].address = "";
		}
	}
	
	public void assignmentStmt(int startIndex, int endIndex ){
		n = startIndex +2;
		newExpr(startIndex, endIndex);
		
		// erases tokens in tokens Array
		for ( int n=startIndex; n <= endIndex; n++){
			c.s.tokens[n].lexeme = "";
			c.s.tokens[n].attribute = "";
			c.s.tokens[n].address = "";
		}
	}
	
	private void newExpr(int startIndex, int endIndex){
		
		writeAsmCode("MVI A, 0");
		
		if ( c.s.tokens[n].attribute.equals("idnt"))
			addIdnt(n);
		else if ( c.s.tokens[n].attribute.equals("num"))
			addNum(n);

		for ( n=startIndex+1; n <= endIndex-1; n++){	
			if ( c.s.tokens[n].lexeme.equals("+") && c.s.tokens[n+1].attribute.equals("idnt") )
				addIdnt(++n);
			else if ( c.s.tokens[n].lexeme.equals("-") && c.s.tokens[n+1].attribute.equals("idnt") )
				subIdnt(++n);
			else if ( c.s.tokens[n].lexeme.equals("+") && c.s.tokens[n+1].attribute.equals("num") )
				addNum(++n);
			else if ( c.s.tokens[n].lexeme.equals("-") && c.s.tokens[n+1].attribute.equals("num") )
				subNum(++n);
		}
		
		writeAsmCode("LXI AR, " + Integer.toHexString(Integer.parseInt(c.s.tokens[startIndex].address)).toUpperCase());
		writeAsmCode("MOV M, A");
	}
	
	public void relExpr (int startIndex, int endIndex){
		n = startIndex + 2;
		
		writeAsmCode("MVI A, 0");

		if ( c.s.tokens[n].attribute.equals("idnt"))
			addIdnt(n);
		else if ( c.s.tokens[n].attribute.equals("num"))
			addNum(n);
		
		for ( n=startIndex+2; n <= endIndex; n++){
			if ( c.s.tokens[n].lexeme.equals("+") && c.s.tokens[n+1].attribute.equals("idnt") )
				addIdnt(++n);
			else if ( c.s.tokens[n].lexeme.equals("-") && c.s.tokens[n+1].attribute.equals("idnt") )
				subIdnt(++n);
			else if ( c.s.tokens[n].lexeme.equals("+") && c.s.tokens[n+1].attribute.equals("num") )
				addNum(++n);
			else if ( c.s.tokens[n].lexeme.equals("-") && c.s.tokens[n+1].attribute.equals("num") )
				subNum(++n);
		}
		
		writeAsmCode("LXI AR, " + Integer.toHexString(Integer.parseInt(c.s.tokens[startIndex].address)).toUpperCase());
		writeAsmCode("SUB M");
		
		if (c.s.tokens[startIndex+1].lexeme.equals(">="))
			writeAsmCode("JL " + c.ir.labels[c.ir.labelNo]);
		else if (c.s.tokens[startIndex+1].lexeme.equals("=="))
			writeAsmCode("JNZ " + c.ir.labels[c.ir.labelNo]);
		else if (c.s.tokens[startIndex+1].lexeme.equals("<"))
			writeAsmCode("JGE " + c.ir.labels[c.ir.labelNo]);
		else if (c.s.tokens[startIndex+1].lexeme.equals("<>"))
			writeAsmCode("JZ " + c.ir.labels[c.ir.labelNo]);
	}
	
	public void ifStart(int startIndex, int endIndex){
		relExpr(startIndex+1, endIndex); //startIndex indicates "if"
		
		for (int n=startIndex; n<= endIndex; n++){
			c.s.tokens[n].lexeme = "";
			c.s.tokens[n].attribute = "";
			c.s.tokens[n].address = "";
		}
		c.ir.labelNo++;		// prepares for next label number
	}
	public void ifEnd (int correctLabel){
		asmCode = c.ir.labels[correctLabel] + ":";
		writeAsmCode(asmCode);
	}
	
	public void whileStart (int startIndex, int endIndex){
		writeAsmCode(c.ir.labels[c.ir.labelNo-1]+":");
		relExpr(startIndex+1, endIndex); //startIndex indicates "while"
		
		for (int n=startIndex; n<= endIndex; n++){
			c.s.tokens[n].lexeme = "";
			c.s.tokens[n].attribute = "";
			c.s.tokens[n].address = "";
		}
		c.ir.labelNo++;		// prepares for next label number
	}
	public void whileEnd (int continueWhileLabel, int endWhileLabel){
		asmCode = "JMP " + c.ir.labels[continueWhileLabel] + "\n" + c.ir.labels[endWhileLabel] + ": ";
		writeAsmCode(asmCode);
	}
	public void call (int i){
		asmCode = c.s.tokens[i-1].lexeme.toUpperCase() + " " + c.s.tokens[i].lexeme.toUpperCase();
		writeAsmCode(asmCode);
		c.ir.writeIrCode(asmCode);
	}
	public void proc (){
		asmCode = c.s.tokens[0].lexeme.toUpperCase() + ":";
		writeAsmCode(asmCode);
		c.ir.writeIrCode(asmCode);
	}
	
	public void start (){
		asmCode = "ORG: " + "1000";		// PM program memory starts at 1000
		writeAsmCode(asmCode);
		asmCode = "JMP L0";
		writeAsmCode(asmCode);
		asmCode = "LO: " + "1010";		// (PM + 10)
		writeAsmCode(asmCode);
		mainFlag = false;
	}
	
	public void end (){
		if (!mainFlag){
			asmCode = "HLT";
			writeAsmCode(asmCode);
			c.ir.writeIrCode(asmCode);
			mainFlag = true;
		}
		else{
			asmCode = "RET";
			writeAsmCode(asmCode);
			c.ir.writeIrCode(asmCode);
		}
	}
	
	private void addIdnt (int tok){
		writeAsmCode("LXI AR, " + Integer.toHexString(Integer.parseInt(c.s.tokens[tok].address)).toUpperCase());	// address
		writeAsmCode("ADD M");
	}
	private void subIdnt (int tok){
		writeAsmCode("LXI AR, " + Integer.toHexString(Integer.parseInt(c.s.tokens[tok].address)).toUpperCase());	// address
		writeAsmCode("SUB M");
	}
	
	private void addNum (int tok){
		writeAsmCode("ADI " + Integer.toHexString(Integer.parseInt(c.s.tokens[tok].lexeme)).toUpperCase());			//lexeme
	}
	private void subNum (int tok){
		writeAsmCode("SUI " + Integer.toHexString(Integer.parseInt(c.s.tokens[tok].lexeme)).toUpperCase());			// lexeme
	}

	private void writeAsmCode (String code){
		// writes string to asm file
		try{
			asmCode = code;
			System.out.println("asmCode: "+asmCode);
			c.bwAsm.write(asmCode + "\n");
			c.bwAsm.flush();
		} catch (IOException e){
			System.err.println("IO problems writing to asm file writeAsmCode() "+e);
		}
	}

}
