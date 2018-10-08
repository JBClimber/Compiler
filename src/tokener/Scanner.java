package tokener;

import java.util.StringTokenizer;

public class Scanner {

	public StringTokenizer strtok;
	public Token[] tokens = new Token[50];
	
	public Identifier[] idnts = new Identifier[50];
	int identNo = 0;
	
	public Procedure[] procs = new Procedure[50];
	int procNo = 0;
	
	public Compiler c;
	private Attributes a;
	private String [] opers = new String[] {">=", "==", "<", "<>", ":=", "+", "-", "(", ")", "{", "}", ",", ";"};
	
	
	public Scanner(Compiler c){
		this.c = c;
		this.a = new Attributes(this);
	}
	
	public void scanner(){
		
		String buffer = c.buffer;
		buffer = comment(buffer);
		
		for ( int i=0; i < opers.length; i++){
			buffer = space(opers[i], buffer);	
		}
		c.buffer = split(buffer);
	}
	
	public String split(String s){	 // adds new lines between found tokens

		strtok = new StringTokenizer( s, " ");
		s = "";
		int tokNo = 0;
		String tokLex = "";
		
		while (strtok.hasMoreTokens()){
			tokLex = strtok.nextToken();	 	// gets the lexeme from the line
			tokens[tokNo] = a.getAttribute(tokLex);
			
			s += "token " + tokNo + " : " + tokLex + "		>>"+ tokens[tokNo].attribute + "		>>"+ tokens[tokNo].address + "\n";
			tokNo++;
		}
		return s;
	}
	
	private String space(String oper, String buf){  // adds spaces between operators (recursive)
		
		int iOf = buf.indexOf(oper);
		
		if ( iOf != -1 ){
			String one = buf.substring(0, iOf);
			String two = buf.substring(iOf + oper.length());
			two = space(oper, two);
			buf = one + " " + oper + " " + two;
		}
		return buf;
	}
	
	private String comment(String buf){		// removes comments from line of code
		
		int com = buf.indexOf("/");
		if ( com != -1){
			buf = buf.substring(0, com);
		}
		return buf;
	}

	public void clearTokens(){
		for (int i=0; i < 50; i++ )
			tokens[i] = null;
	}
	
	// error checking methods =========================================================
	public void printTokens(){
		System.out.println("start printTokens() : ");
		String newLine = "";
		for ( int i=0; i < tokens.length; i++){
			if (tokens[i] == null)
				break;
			else{
				System.out.println("token"+i+" : "+tokens[i].lexeme+"	>>>>"+tokens[i].attribute+"		>>>>"+tokens[i].address);
				newLine += tokens[i].lexeme;
			}
		}
		System.out.println(newLine);
		System.out.println("end printTokens() ;");
	}
	
	public void printIdentifiers(){
		System.out.println("start printIdentifiers() : ");
		for ( int i=0; i < tokens.length; i++){
			if (tokens[i] == null)
				break;
			else{
				System.out.println("identifiers"+i+" : "+idnts[i].name+"	>>>>"+idnts[i].address);
			}
		}
		System.out.println("end printIdentifiers() ;");
	}
	
	public void printProcedures(){
		System.out.println("start printProcedures() : ");
		for ( int i=0; i < tokens.length; i++){
			if (tokens[i] == null)
				break;
			else{
				System.out.println("procedures"+i+" : "+procs[i].name+"	>>>>"+procs[i].address);
			}
		}
		System.out.println("end printProcedures() ;");
	}
}
