package tokener;

public class Errors {
	
	public String errorList(Token[] tokArr){
		
		String errors = "";

		errors += parentheses(tokArr);
		errors += braces(tokArr);
		errors += semicolon(tokArr);
		
		return errors;
	}
	
	private String parentheses(Token[] arr){ // checks for correct number of parentheses
		int errorCount = 0;
		for (int i=0; i < arr.length; i++){
			if (arr[i] != null){
				if ( arr[i].lexeme.equals("(") )
					errorCount++;
				if ( arr[i].lexeme.equals(")") )
					errorCount--;
			}
		}
		if ( errorCount != 0 )
			return "-- ERROR -- \"missing parenthese(s) \"()\" -- \n";
		else
			return "";
	}
	private String braces(Token[] arr){	// checks for correct number of braces
		int errorCount = 0;
		for (int i=0; i < arr.length; i++){
			if ( arr[i] != null ){
				if ( arr[i].lexeme.equals("{") )
					errorCount++;
				if ( arr[i].lexeme.equals("}") )
					errorCount--;
			}
		}
		if ( errorCount != 0 )
			return "-- ERROR -- missing brace(s) \"{}\" -- \n";
		else
			return "";
	}
	private String semicolon(Token[] arr){ // checks for one semicolon
		
		int semicolon =0;
		int pairOfColon =0;
		for (int i =0; i < arr.length; i++)
			if (arr[i] != null ){
				if ( arr[i].lexeme.equals(";")){
					semicolon++;
				}
				if ( arr[i].lexeme.equals("var") || arr[i].lexeme.equals(":=") || arr[i].lexeme.equals("proc")){
					pairOfColon++;
				}		
			}
		if ( semicolon == pairOfColon )
			return "";
		else
			return "-- ERROR -- extra or missing semicolon \";\" -- \n";
	}

}
