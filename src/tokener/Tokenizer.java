package tokener;

import java.util.*;

public class Tokenizer {


	public static void main(String[] args) {

		String buffer = "if y>=0 then x:=2;//comments";
		StringTokenizer strtok;
		String [] opers = new String[] {">=", "==", "<", "<>", ":=", "+", "-", "(", ")", "{", "}", ",", ";"};
		
		strtok = new StringTokenizer( buffer, "/"); // searches for comments in line of code

		buffer = strtok.nextToken(); // takes token with only code
		
		System.out.println("original buffer >> " + buffer);
		
		for (int i=0; i < opers.length; i++){	// loops through all operators
			buffer = space(opers[i], buffer);
			System.out.println("buffer for "+opers[i]+" >> "+buffer);
		}
		
		strtok = new StringTokenizer( buffer, " ");
		
		while (strtok.hasMoreTokens())
			System.out.println("token >> |" + strtok.nextToken() + "|"); // gets all tokens
		
	}
	
	public static String space (String oper, String buffer){ // takes operator and buffer 
		
		int iOf = buffer.indexOf(oper);

		if ( iOf != -1){	// if it finds an operator
			String one = buffer.substring(0, iOf);
			String two = buffer.substring(iOf + oper.length()); // adds the size of the operator
			buffer = one + " " + oper + " " + two;
		}
		return buffer;
	}

}
