package tokener;

import java.io.IOException;

public class IRcode {

	public String irCode = "";
	public int startIndex, endIndex, newIdNo;
	
	public String[] labels = new String[100];
	public int labelNo;
	
	private int[] ifWhileStack = new int[50];	// stack contains index of labels for nested WHILE or/and IF staments
	private int stackCount = 0;			// used for push() pop() into the stack
	
	public Compiler c;
	
	public IRcode (Compiler c){
		this.c = c;
		this.labelNo = 1;
	}
	
	public void newIdnt (int at){
		endIndex = at;
		
		for( int n=endIndex; n >= 0; n--){
			if(c.s.tokens[n].lexeme.equals("(")){
				startIndex = n;
				break;
			}
		}
		
		c.s.idnts[c.s.identNo] = new Identifier("temp"+newIdNo, Integer.toString(c.dataAdres));
		
		irCode = c.s.idnts[c.s.identNo].name + " := ";
		
		for (int n=startIndex+1; n <= endIndex-1; n++)
			irCode = irCode + c.s.tokens[n].lexeme;
		
		writeIrCode(irCode + ";");
		
		c.s.tokens[startIndex].lexeme = c.s.idnts[c.s.identNo].name;
		c.s.tokens[startIndex].attribute = "idnt";
		c.s.tokens[startIndex].address = Integer.toString(c.dataAdres);
		
		c.as.newStmt( startIndex, endIndex);

		newIdNo++;
		c.dataAdres++;
		c.s.identNo++;
	}
	
	public void assignmentStmt (int at){
		endIndex = at;
		
		for ( int n=endIndex; n >= 0; n--)
			if ( c.s.tokens[n].lexeme.equals(":="))
				startIndex = n-1;
		
		irCode = "";
		for ( int n=startIndex; n <= endIndex; n++)
			irCode = irCode + c.s.tokens[n].lexeme;
		
		writeIrCode(irCode);
		
		c.as.assignmentStmt(startIndex, endIndex);
	}

	public void relExpr (int at){
		endIndex = at;
		
		for ( int n=endIndex; n >= 0; n--){
			if ( c.s.tokens[n].lexeme.equals(">=") || c.s.tokens[n].lexeme.equals("==") || c.s.tokens[n].lexeme.equals("<") || c.s.tokens[n].lexeme.equals("<>") )
				startIndex = n-1;
		}
		
		irCode = "";
		for ( int n=startIndex; n <= endIndex; n++)
			irCode = irCode + c.s.tokens[n].lexeme;

		writeIrCode(irCode);
		
		c.as.relExpr(startIndex, endIndex);
	}
	
	public void ifStart (int i){
		
		labels[labelNo] = "L" + Integer.toString(labelNo);
		push();
		endIndex = i;
		int relIndex = 0;				// check this
		
		for (int n = endIndex; n >= 0; n--){		// finds relational operator
			if ( c.s.tokens[n].attribute.equals("relOp")){
				relIndex = n;
				break;
			}
		}
		
		for (int n=relIndex; n >= 0; n--){			// finds start of if
			if (c.s.tokens[n].lexeme.equals("if")){
				startIndex = n;
				break;
			}
		}
		
		irCode = "if ";
		
		for (int n=startIndex+1; n <= relIndex-1; n++)
			irCode = irCode + c.s.tokens[n].lexeme;
		
		if (c.s.tokens[relIndex].lexeme.equals(">="))
			irCode = irCode + "<";
		if (c.s.tokens[relIndex].lexeme.equals("=="))
			irCode = irCode + "<>";
		if (c.s.tokens[relIndex].lexeme.equals("<>"))
			irCode = irCode + "==";
		if (c.s.tokens[relIndex].lexeme.equals("<"))
			irCode = irCode + ">=";
		
		for (int n=relIndex+1; n <= endIndex-1; n++)	// copies the relational equation
			irCode = irCode + c.s.tokens[n].lexeme;
		
		irCode = irCode + " goto " + labels[labelNo];
		
		writeIrCode(irCode);
		
		c.as.ifStart(startIndex, endIndex);
	}
	public void ifEnd (){

		int correctLabel = pop();
		irCode = labels[correctLabel] + ":";
		writeIrCode(irCode);
		c.as.ifEnd(correctLabel);
	}
	
	public void whileStart (int i){

		labels[labelNo] = "L" + Integer.toString(labelNo);	// while start label
		push();
		endIndex = i;
		int relIndex = 0;
		
		for (int n=endIndex; n >= 0; n--){
			if (c.s.tokens[n].attribute.equals("relOp")){
				relIndex = n;
				break;
			}
		}
		for (int n=relIndex; n >= 0; n--){
			if (c.s.tokens[n].lexeme.equals("while")){
				startIndex = n;
				break;
			}
		}
		
		irCode = labels[labelNo] + ":\nif ";	// prints start label
		
		for (int n=startIndex+1; n <= relIndex-1; n++)
			irCode = irCode + c.s.tokens[n].lexeme;
		
		if (c.s.tokens[relIndex].lexeme.equals(">="))
			irCode = irCode + "<";
		if (c.s.tokens[relIndex].lexeme.equals("=="))
			irCode = irCode + "<>";
		if (c.s.tokens[relIndex].lexeme.equals("<>"))
			irCode = irCode + "==";
		if (c.s.tokens[relIndex].lexeme.equals("<"))
			irCode = irCode + ">=";
		
		for (int n=relIndex+1; n <= endIndex-1; n++)	// copies the relational equation
			irCode = irCode + c.s.tokens[n].lexeme;
		
		labelNo++;	// while end label to exit the while loop
		labels[labelNo] = "L" + Integer.toString(labelNo);
		push();
		irCode = irCode + " goto " + labels[labelNo];
		
		writeIrCode(irCode);
		
		c.as.whileStart(startIndex, endIndex);
	}
	public void whileEnd(){

		int endWhileLabel = pop();
		int continueWhileLabel = pop();
		irCode = "goto " + labels[continueWhileLabel] + "\n" + labels[endWhileLabel] + ":";
		writeIrCode(irCode);
		c.as.whileEnd(continueWhileLabel, endWhileLabel);
	}
	
	public void writeIrCode (String code){
		// writes string of code to ir File
		try{
			irCode = code;
			System.out.println("irCode: "+irCode);
			c.bwIr.write(irCode + "\n");
			c.bwIr.flush();
		} catch (IOException e){
			System.err.println("IO problems writing to ir file write IrCode() "+e);
		}
	}
	
	public void push(){
		ifWhileStack[stackCount] = labelNo;
		stackCount++;
	}
	public int pop(){
		stackCount--;
		return ifWhileStack[stackCount];
	}
}
