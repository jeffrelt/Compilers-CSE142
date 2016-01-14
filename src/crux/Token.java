package crux;

import java.util.HashMap;

public class Token {
	public static String studentName = "Jeffrey Thompson";
    public static String studentID = "jeffrelt";
    public static String uciNetID = "12987953";
    
	public static enum Kind {
		AND("and"),
		OR("or"),
		NOT("not"),
		LET("let"),
		VAR("var"),
		ARRAY("array"),
		FUNC("func"),
		IF("if"),
		ELSE("else"),
		WHILE("while"),
		TRUE("true"),
		FALSE("false"),
		RETURN("return"),
		
		OPEN_PAREN("("),
		CLOSE_PAREN(")"),
		OPEN_BRACE("{"),
		CLOSE_BRACE("}"),
		OPEN_BRACKET("["),
		CLOSE_BRACKET("]"),
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
		GREATER_EQUAL(">="),
		LESSER_EQUAL("<="),
		NOT_EQUAL("!="),
		EQUAL("=="),
		GREATER_THAN(">"),
		LESS_THAN("<"),
		ASSIGN("="),
		COMMA(","),
		SEMICOLON(";"),
		COLON(":"),
		CALL("::"),
		
		IDENTIFIER(),
		INTEGER(),
		FLOAT(),
		ERROR(),
		EOF("$$"); //impossible to match but won't print the lexeme this way
		
		private String default_lexeme;
		
		Kind()
		{
			default_lexeme = "";
		}
		
		Kind(String lexeme)
		{
			default_lexeme = lexeme;
		}
		
		public boolean hasStaticLexeme()
		{
			return !default_lexeme.equals("");
		}
		
		// OPTIONAL: if you wish to also make convenience functions, feel free
		//           for example, boolean matches(String lexeme)
		//           can report whether a Token.Kind has the given lexeme
		public String getLexeme()
		{
			return default_lexeme;
		}
	}
	
	public final static HashMap<String,Token.Kind> knownTokens = buildMap();
	
	private static HashMap<String,Token.Kind> buildMap()
	{
		HashMap<String,Token.Kind> hm = new HashMap<String,Token.Kind>();
		for(Token.Kind t : Token.Kind.values() )
			hm.put(t.getLexeme(), t);
		return hm;
	}
	
	private int lineNum;
	private int charPos;
	Kind kind;
	private String lexeme = "";
	
	private Token(int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		// if we don't match anything, signal error
		//this.kind = Kind.ERROR;
		//this.lexeme = "No Lexeme Given";
	}
	
	// OPTIONAL: implement factory functions for some tokens, as you see fit 
	
	// This one for special case of EOF
	public static Token EOF(int lineNum, int charPos)
	{
		Token tok = new Token(lineNum, charPos);
		tok.kind = Kind.EOF;
		tok.lexeme = "$$";
		return tok;
	}
	
	// This one for special case of FLOAT
	// made this separate so we don't have to search for a '.' in the number to determine it's a float
	public static Token Float(String lexeme, int lineNum, int charPos)
	{
		Token tok = new Token(lineNum, charPos);
		tok.kind = Kind.FLOAT;
		tok.lexeme = lexeme;
		return tok;
	}
	
	public Token(String lexeme, int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		this.lexeme = lexeme;
		// if in the hash its a known type, so grab the Kind from the hash
		if(knownTokens.containsKey(lexeme))
			this.kind = knownTokens.get(lexeme);
		//starts with a digit it is a INTEGER as we deal with FLOAT elsewhere
		else if(Character.isDigit(lexeme.charAt(0))){
			this.kind = Kind.INTEGER;
		}
		// if not a known token but begins with letter or '_" it's an identifier
		else if(Character.isLetter(lexeme.charAt(0)) || lexeme.charAt(0) == '_')
			this.kind = Kind.IDENTIFIER;
		else{
			// if we don't match anything, it's an ERROR token
			this.kind = Kind.ERROR;
			this.lexeme = "Unexpected character: " + lexeme;
		}
	}
	
	public int lineNumber()
	{
		return lineNum;
	}
	
	public int charPosition()
	{
		return charPos;
	}
	
	// Return the lexeme representing or held by this token
	public String lexeme()
	{
		return this.lexeme;
	}
	
	public String toString()
	{
		// If our kind has a static lexeme defined we don't need to print it.
		if(this.kind.hasStaticLexeme())
			return String.format("%s(lineNum:%d, charPos:%d)", this.kind,this.lineNum,this.charPos);
		else
			return String.format("%s(%s)(lineNum:%d, charPos:%d)", this.kind,this.lexeme,this.lineNum,this.charPos);
	}
	
	// OPTIONAL: function to query a token about its kind
	//           boolean is(Token.Kind kind)
	
	// OPTIONAL: add any additional helper or convenience methods
	//           that you find make for a clean design

}
