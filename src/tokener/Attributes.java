package tokener;

public class Attributes {
	
	private Scanner s;
	
	public Attributes(Scanner s){
		this.s = s;
	}

	public Token getAttribute(String lex){
		
		if (lex.equals("if") || lex.equals("then") || lex.equals("while") || lex.equals("do") || lex.equals("var") ||lex.equals("call") || lex.equals("proc") || lex.equals("main") || lex.equals("end")){
			return new Token( lex, "resWd", "n/a" );
		}
		else if( lex.equals(":=")){
			return new Token( lex, "assOp", "n/a" );
		}
		else if( lex.equals("+") || lex.equals("-") ){
			return new Token( lex, "arithOp", "n/a" );
		}
		else if( lex.equals("(") || lex.equals(")") || lex.equals("{") || lex.equals("}") || lex.equals(",") || lex.equals(";") ){
			return new Token( lex, "spChar", "n/a" );
		}
		else if (lex.equals(">=") || lex.equals("==") || lex.equals("<") || lex.equals("<>") ){
			return new Token( lex, "relOp", "n/a" );
		}
		else if (isNumber(lex)){
			if (isNumInRange(lex))
				return new Token( lex, "num", "n/a" );
			else
				return new Token( lex, "ERROR number out of range", "");
		}

		int iOf;	// index of identifier or procedure
		if (isIdentifier(lex)){
			iOf = inIdentifiers(lex);	// finds the identifier in idnts array
			
			if (  iOf > -1){
				return new Token( lex, "idnt", s.idnts[iOf].address);	// gets address from idnts array at index
			}
			else if ( isFirstTok("var", lex) ){
				s.idnts[s.identNo] = new Identifier( lex, Integer.toString(s.c.dataAdres) );
				s.identNo++;
				return new Token( lex, "idnt", Integer.toString(s.c.dataAdres++) );
			}
		}

		if (isProcName(lex)){
			iOf = inProcedures(lex);	// finds the procedure in procs array
			
			if ( iOf > -1 ){
				return new Token( lex, "procname", "assembler");		// s.procs[iOf].address
			}
			else if ( isFirstTok("proc", lex) ){
				s.procs[s.procNo] = new Procedure( lex, "assemebler");	// address established in the assembler
				s.procNo++;
				return new Token( lex, "procname", "assembler" );
			}
		}
		return new Token( lex, "ERROR", "ERROR" );
	}

	private boolean isProcName(String lexeme){		// only letters
		int val;
		for (int i=0; i < lexeme.length(); i++){
			val = (int) lexeme.charAt(i);
			if (val < 97 || val > 122)
				return false;
		}
		return true;
	}
	private int inProcedures(String lexeme){
		for (int i=0; i < s.procs.length; i++){
			if (s.procs[i] == null)
				return -1;
			if (s.procs[i].name.equals(lexeme))
				return i;
		}
		return -1;
	}
	
	private boolean isIdentifier(String lexeme){	// letter then letters or numbers
		int val;
		for (int i=0; i < lexeme.length(); i++){
			val = (int) lexeme.charAt(i);
			if( i>0 && ( val<48 && (val>57 && val<97) && val>122)){
				return false;
			}
			else if( i==0 && (val<97 || val>122)){
				return false;
			}
		}
		return true;
	}
	private int inIdentifiers(String lexeme){		// checks if variable is in identifier list
													// if found returns the index of else returns -1
		for (int i=0; i < s.idnts.length; i++){
			if (s.idnts[i] == null) // returns false if it ends without finding
				return -1;
			if (s.idnts[i].name.equals(lexeme))
				return i;
		}
		return -1;
	}
	
	private boolean isNumber(String lexeme){
		
		for (int i=0; i < lexeme.length(); i++){	 // checks if all are digits
			if (!lexeme.substring(i, i+1).matches("[0-9]"))
				return false;
		}
		return true;
	}
	private boolean isNumInRange(String lexeme){
		if (!(Integer.valueOf(lexeme)<=127))
			return false;
		return true;
	}

	private boolean isFirstTok(String pv, String lexeme){
		// checks if first token is pv
		// pv could be "proc" or "var"
		if ( s.tokens[0] == null)
			return false;
		if ( s.tokens[0].lexeme.equals(pv))
			return true;
		return false;
	}
	
}
