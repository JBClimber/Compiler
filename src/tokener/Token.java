package tokener;

public class Token {

	public String lexeme;
	public String attribute;
	public String address;
	public int tokenNo;
	
	public Token (String lex, String attr, String addr){
		lexeme = lex;
		attribute = attr;
		address = addr;
	}
}
