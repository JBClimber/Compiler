package tokener;

// dirty implementation of FOR loop preCompiler

import java.io.IOException;

public class PreCompiler {
	
	public Compiler c;
	
	private String initial = "";
	private String relation = "while ";
	private String body = "";
	private String crement = "";
	
	private int i = 0;
	private int start = 0;
	private int end = 0;
	
	public PreCompiler(Compiler c){
		this.c = c;
	}
	
	public void preCompiler(String bufferCopy){

		if (c.s.tokens[0].lexeme.equals("for")){

			part1();
			part2();
			part3();
			part4();
			System.out.println("Compile as :");
			System.out.println(initial);
			System.out.println(relation + body);
			try {
				c.bwPC.write(initial+"\n");
				c.bwPC.write(relation + body + "\n");
				c.bwPC.flush();
			} catch (IOException e) {
				System.err.println("IO exception : "+ e);
			}
			
			initial = "";
			relation = "while ";
			body = "";
			crement = "";
			i = 0;
			start = 0;
			end = 0;
		}
		else{
			try {
				c.bwPC.write(bufferCopy+"\n");
			} catch (IOException e) {
				System.err.println("IO exception : " + e);
			}
		}
	}

	private void part1(){		// START OF LOOP AT
		for (i=1; i < c.s.tokens.length; i++){
			if (c.s.tokens[i] == null)
				break;
			else if (c.s.tokens[i].lexeme.equals("(")){
				start = i+1;
			}
			else if (c.s.tokens[i].lexeme.equals(";")){
				end = i;
				break;
			}
		}
		for (; start <= end; start++)
			initial += c.s.tokens[start].lexeme + " ";
	}
	private void part2(){		// RELATION OP
		end++;
		start = end;
		for (i=start; i < c.s.tokens.length; i++ ){
			if (c.s.tokens[i] == null)
				break;
			if (c.s.tokens[i].lexeme.equals(";")){
				end = i;
				break;
			}
		}
		for (; start < end; start++)
			relation += c.s.tokens[start].lexeme + " ";
		relation += "do ";
	}
	private void part3(){		// INCREMENT DECREMENT 
		start = start+1;
		for (i=start; i < c.s.tokens.length; i++){
			if (c.s.tokens[i] == null)
				break;
			else if(c.s.tokens[i].lexeme.equals(")")){
				end = i-1;
				break;
			}
		}
		if ((end-start) == 2){
			if (c.s.tokens[start].attribute.equals("idnt") && c.s.tokens[start+1].lexeme.equals("+") && c.s.tokens[end].lexeme.equals("+")){
				// var + +
				crement = c.s.tokens[start].lexeme + " := " + c.s.tokens[start].lexeme + " + 1 ";
			}
			else if (c.s.tokens[start].attribute.equals("idnt") && c.s.tokens[start+1].lexeme.equals("-") && c.s.tokens[end].lexeme.equals("-")){
				// var - -
				crement = c.s.tokens[start].lexeme + " := " + c.s.tokens[start].lexeme + " + 1 ;";
			}
		}
		else{
			for (; start <= end; start++){
				if (c.s.tokens[i] == null)
					break;
				else{
					crement += c.s.tokens[start].lexeme + " ";
				}
			}
		}
		crement += ";";
	}
	private void part4(){		// BODY 
		
		if (c.buffer.contains("{")){
			for (i=end; i < c.s.tokens.length; i++){
				if (c.s.tokens[i] == null)
					break;
				else if (c.s.tokens[i].lexeme.equals("{")){
					start = i+1;
				}
				else if (c.s.tokens[i].lexeme.equals("}")){
					end = i-1;
					break;
				}
			}
			for (i=start; i<= end; i++){
				body += c.s.tokens[i].lexeme + " ";
			}
		}
		else{
			start = end+2;
			
			for (i=end; i < c.s.tokens.length; i++){
				if (c.s.tokens[i] == null)
					break;
				else if (c.s.tokens[i].lexeme.equals(";")){
					end = i;
				}
			}
			for (i=start; i<= end; i++){
				body += c.s.tokens[i].lexeme + " ";
			}
		}
		body = "{ " + body + " " + crement + " }";
	}

}
