package tokener;

import java.io.IOException;

public class Parser {
	
	public int length;
	public String parse = "";
	private Compiler c;
	
	public Parser (Compiler c){
		this.c = c;
	}
	
	public void parser(){
		length =0;
		
		for (int i=0; i < c.s.tokens.length; i++){
			if ( c.s.tokens[i] != null){
				action(i);
				reduce(i);
				try{
					System.out.println();
					c.bwTok.write("parse = "+ parse + "\n");
				}
				catch (IOException e){
					System.err.println("Error in the parser() : " + e);
				}
			}
			else
				i = 50;
		}
		if (length == 1 && parse.equals("stmt ")){
			try{
				System.out.println("Correct statement :)");
				c.bwTok.write("Correct Statement\n");
			}
			catch(IOException e){
				System.err.println("Error in the parser() : " + e);
			}
		
		}
		else{
			try{
				System.out.println("ERROR in statment :(    length:"+length+"| parse:"+parse);
				c.bwTok.write("Error In Statment\n");
			}
			catch(IOException e){
				System.err.println("Error in the parser() : " + e);
			}
		}
	}
	
	private void action(int i){
		String flag = "";
		if (c.s.tokens[i].attribute.equals("resWd") || c.s.tokens[i].attribute.equals("spChar")){
			flag = "L";
		}
		else if ( c.s.tokens[i].attribute.equals("idnt")){
			if (c.s.tokens[i+1].attribute.equals("relOp") || c.s.tokens[i+1].attribute.equals("assOp") || c.s.tokens[0].attribute.equals("var") || c.s.tokens[i+1].lexeme.equals(",") || c.s.tokens[i-1].lexeme.equals(","))
					flag = "A";
		}
		else if ( c.s.tokens[i].attribute.equals("assOp") )
			flag = "A";
		else if ( c.s.tokens[i].attribute.equals("arithOp") )
			flag = "A";
		else if ( c.s.tokens[i].attribute.equals("relOp") )
			flag = "A";
		else if ( c.s.tokens[i].attribute.equals("procname") )
			flag  = "A";
		
		if ( flag.equals("A")){
			parse += c.s.tokens[i].attribute + " ";
			length++;
		}
		else if ( flag.equals("L")){
			parse += c.s.tokens[i].lexeme + " ";
			length++;
		}
		else if ( flag.equals("") && c.s.tokens[i].attribute.equals("idnt")){
			parse += "term ";
			length++;
		}
		else if ( flag.equals("") && c.s.tokens[i].attribute.equals("num")){
			parse += "term ";
			length++;
		}
		System.out.println("parse action() = |" + parse + "| -- length="+length);
	}
	private void reduce(int i){
					// i is the last item/token added to the parse string
		int x;
		// logic for identifiers    -----------------------------------------------------------------------
		if ( (x = parse.indexOf("idnt , idnt")) >= 0 || (x = parse.indexOf("idntsequ , idnt")) >= 0){
			parse = parse.substring( 0, x) + "idntsequ ";
			length = length - 2;
		}
		if ( (x = parse.indexOf("var idntsequ ;")) >= 0 || (x = parse.indexOf("var idnt ;")) >= 0){
			parse = "specstmt ";
			length = length - 2;
		}
		// logic for procedures     ----------------------------------------------------------------------
		if ( (x=parse.indexOf("procname , procname")) >= 0 || (x=parse.indexOf("procsequ , procname")) >= 0){
			parse = parse.substring(0, x) + "procsequ ";
			length = length - 2;
		}
		if ( (x=parse.indexOf("proc procsequ ;")) >= 0 || (x=parse.indexOf("proc procname ;")) >= 0){
			parse = "specstmt ";
			length = length - 2;
		}
		// logic for main, end, call procname   ------------------------------------------------------------
		if ( (x = parse.indexOf("main ( )")) >= 0){
			parse = "specstmt ";
			length = length -2;
			c.as.start();		// directly to asbnly code
		}
		if ((x=parse.indexOf("procname ( )")) >= 0){
			parse = "specstmt ";
			length = length -2;
			c.as.proc();	// directly to asbnly code
		}
		if ( (x = parse.indexOf("end")) >= 0){
			parse = "specstmt ";
			c.as.end();		// asbly code
		}
		if ((x=parse.indexOf("call procname")) >= 0){
			parse = parse.substring(0, x) + "specstmt ";
			length = length -1;
			System.out.println("this is I : "+i);
			c.as.call(i);		// directly to asbnly code
		}
		
		boolean again = true;
		do {
			if ((x=parse.indexOf("term arithOp term")) >= 0){
				parse = parse.substring(0, x) + "expr ";
				length = length -2;
			}
			else if ((x=parse.indexOf("arithOp term")) >= 0){
				parse = parse.substring(0, x) + "expr ";
				length = length -1;
			}
			else if ((x=parse.indexOf("expr")) >= 0){
				parse = parse.substring(0, x) + "term ";
				// no change in length
				// possible new expression add
			}
			else if ((x=parse.indexOf("( term )")) >= 0){
				parse = parse.substring(0, x) + "term ";
				length = length -2;
				c.ir.newIdnt(i);		// call to IRcode for new temp variable
			}
			else if ((x=parse.indexOf("idnt assOp term ;")) >= 0){
				parse = parse.substring(0, x) + "asstmt ";
				length = length -3;
				c.ir.assignmentStmt(i);	// call for creating assignment statement in ir
			}
			else if ((x=parse.indexOf("idnt relOp term")) >= 0){
				parse = parse.substring(0, x) + "relexp ";
				length = length -2;
			}
			else if ((x=parse.indexOf("if relexp then")) >= 0){	
				parse = parse.substring(0, x) + "ifstart ";
				length = length -2;
				c.ir.ifStart(i);	// from ir to asbly
			}
			else if ((x=parse.indexOf("ifstart stmt")) >= 0){
				parse = parse.substring(0, x) + "ifstmt ";
				length = length -1;
				c.ir.ifEnd();		// from ir to asbly
			}
			else if ((x=parse.indexOf("while relexp do")) >= 0){
				parse = parse.substring(0, x) + "whilestart ";
				length = length -2;
				c.ir.whileStart(i);
			}
			else if ((x=parse.indexOf("whilestart stmt")) >= 0){
				parse = parse.substring(0, x) + "whilestmt ";
				length = length -1;
				c.ir.whileEnd();	// from ir to asbly
			}
			else if ((x=parse.indexOf("asstmt")) >= 0 || (x=parse.indexOf("ifstmt")) >= 0 || (x=parse.indexOf("whilestmt")) >= 0 || (x=parse.indexOf("specstmt")) >= 0){
				parse = parse.substring(0, x) + "stmt ";
				// no change in length
			}
			else if ((x=parse.indexOf("stmt stmt")) >= 0){
				parse = parse.substring(0, x) + "stmt ";
				length = length -1;
			}
			else if (((x=parse.indexOf("{ stmt }")) >= 0)){
				parse = parse.substring(0, x) + "stmt ";
				length = length -2;
			}
			else
				again = false;
		} while (again);
		
		// print out results
		System.out.println("parse reduce() = |" + parse + "| -- length=" + length );
	}
	public void clearParse(){
		parse = "";
	}

}
