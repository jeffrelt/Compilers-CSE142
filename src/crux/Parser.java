package crux;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
	public static String studentName = "Jeffrey Thompson";
    public static String studentID = "jeffrelt";
    public static String uciNetID = "12987953";
    
 // SymbolTable Management ==========================
    private SymbolTable symbolTable;
    
    private void initSymbolTable()
    {
    	symbolTable = new SymbolTable();
    }
    
    private void enterScope()
    {
        SymbolTable new_table = new SymbolTable();
        new_table.parent = symbolTable;
        symbolTable = new_table;
    }
    
    private void exitScope()
    {
    	symbolTable = symbolTable.parent;
    }

    private Symbol tryResolveSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.lookup(name);
        } catch (SymbolNotFoundError e) {
            String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportResolveSymbolError(String name, int lineNum, int charPos)
    {
        String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    private Symbol tryDeclareSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.insert(name);
        } catch (RedeclarationError re) {
            String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportDeclareSymbolError(String name, int lineNum, int charPos)
    {
        String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }    

// Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();
    
    private String reportSyntaxError(NonTerminal nt)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
     
    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    private class QuitParseException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        public QuitParseException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    private int lineNumber()
    {
        return currentToken.lineNumber();
    }
    
    private int charPosition()
    {
        return currentToken.charPosition();
    }
          
// Parser ==========================================
    private Scanner scanner;
    private Token currentToken;
    
    public Parser(Scanner scanner)
    {
        this.scanner = scanner;
        currentToken = scanner.next();
    }
    
    public void parse()
    {
    	initSymbolTable();
        try {
            program();
        } catch (QuitParseException q) {
            errorBuffer.append("SyntaxError(" + lineNumber() + "," + charPosition() + ")");
            errorBuffer.append("[Could not complete parsing.]");
        }
    }
    
// Helper Methods ==========================================
    private Token expectRetrieve(Token.Kind kind)
    {
        Token tok = currentToken;
        if (accept(kind))
            return tok;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
        
    private Token expectRetrieve(NonTerminal nt)
    {
        Token tok = currentToken;
        if (accept(nt))
            return tok;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
    
    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }
    
    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind());
    }

    private boolean accept(Token.Kind kind)
    {
        if (have(kind)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }    
    
    private boolean accept(NonTerminal nt)
    {
        if (have(nt)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }
   
    private boolean expect(Token.Kind kind)
    {
        if (accept(kind))
            return true;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return false;
    }
        
    private boolean expect(NonTerminal nt)
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return false;
    }
   
// Grammar Rules =====================================================
    
    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public void literal()
    {
    	expect(NonTerminal.LITERAL);
    }
    
    // designator := IDENTIFIER { "[" expression0 "]" } .
    public void designator()
    {
    	tryResolveSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
        while (accept(Token.Kind.OPEN_BRACKET)) {
            expression0();
            expect(Token.Kind.CLOSE_BRACKET);
        }
    }
    
    // type := IDENTIFIER .
    public void type()
    {
    	// this will need to change
    	tryResolveSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    }
    		 
    // op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
    public void op0()
    {
    	expect(NonTerminal.OP0);
    }
    
    // op1 := "+" | "-" | "or" .
    public void op1()
    {
    	expect(NonTerminal.OP1);
    }
    
    // op2 := "*" | "/" | "and" .
    public void op2()
    {
    	expect(NonTerminal.OP2);
    }
    		 
    // expression0 := expression1 [ op0 expression1 ] .
    public void expression0()
    {
    	expression1();
    	if(have(NonTerminal.OP0)){
    		op0();
    		expression1();
    	}
    }
    		
    // expression1 := expression2 { op1  expression2 } .
    public void expression1()
    {
    	expression2();
    	while(have(NonTerminal.OP1)){
    		op1();
    		expression2();
    	}
    }
    		
    // expression2 := expression3 { op2 expression3 } .
    public void expression2()
    {
    	expression3();
    	while(have(NonTerminal.OP2)){
    		op2();
    		expression3();
    	}
    }
    		
    // expression3 := "not" expression3 | "(" expression0 ")" | designator 
    // 				  | call-expression | literal .
    public void expression3()
    {
    	if(accept(Token.Kind.NOT))
    		expression3();
    	else if(accept(Token.Kind.OPEN_PAREN)){
    		expression0();
    		expect(Token.Kind.CLOSE_PAREN);
    	}
    	else if(have(NonTerminal.DESIGNATOR))
    		designator();
    	else if(have(NonTerminal.CALL_EXPRESSION))
    		call_expression();
    	else if(have(NonTerminal.LITERAL))
    		literal();
    	else{
    		String errorMessage = reportSyntaxError(NonTerminal.EXPRESSION3);
            throw new QuitParseException(errorMessage);
    	}
    }
    		       
    // call-expression := "::" IDENTIFIER "(" expression-list ")" .
    public void call_expression()
    {
    	expect(Token.Kind.CALL);
    	tryResolveSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.OPEN_PAREN);
    	expression_list();
    	expect(Token.Kind.CLOSE_PAREN);
    }
    
    // expression-list := [ expression0 { "," expression0 } ] .
    public void expression_list()
    {
    	if(have(NonTerminal.EXPRESSION0)){
    		expression0();
    		while(accept(Token.Kind.COMMA))
    			expression0();
    	}
    }
    
    // parameter := IDENTIFIER ":" type .
    public void parameter()
    {
    	tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.COLON);
    	type();
    }
    		
    // parameter-list := [ parameter { "," parameter } ] .
    public void parameter_list()
    {
    	if(have(Token.Kind.IDENTIFIER)){
    		parameter();
    		while(accept(Token.Kind.COMMA))
    			parameter();
    	}
    }
    		 
    // variable-declaration := "var" IDENTIFIER ":" type ";"
    public void variable_declaration()
    {
    	expect(Token.Kind.VAR);
    	tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.SEMICOLON);
    }
    		
    // array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
    public void array_declaration()
    {
    	expect(Token.Kind.ARRAY);
    	tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.OPEN_BRACKET);
    	expect(Token.Kind.INTEGER);
    	expect(Token.Kind.CLOSE_BRACKET);
    	while(accept(Token.Kind.OPEN_BRACKET)){
    		expect(Token.Kind.INTEGER);
        	expect(Token.Kind.CLOSE_BRACKET);
    	}
    	expect(Token.Kind.SEMICOLON);
    }		
    
    // function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
    public void function_definition()
    {
    	expect(Token.Kind.FUNC);
    	tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.OPEN_PAREN);
    	enterScope();
    	parameter_list();
    	expect(Token.Kind.CLOSE_PAREN);
    	expect(Token.Kind.COLON);
    	type();
    	statement_block();
    	exitScope();
    }		
    		
    // declaration := variable-declaration | array-declaration | function-definition .
    public void declaration()
    {
    	if(have(Token.Kind.VAR)){
    		variable_declaration();
    	}
    	else if(have(Token.Kind.ARRAY)){
    		array_declaration();
    	}
    	else if(have(Token.Kind.FUNC)){
    		function_definition();
    	}
    	else{
    		String errorMessage = reportSyntaxError(NonTerminal.DECLARATION);
            throw new QuitParseException(errorMessage);
    	}
    }		
    		
    // declaration-list := { declaration } .
    public void declaration_list()
    {
    	while(have(NonTerminal.DECLARATION)){
    		declaration();
    	}
    }	
    		 
    // assignment-statement := "let" designator "=" expression0 ";"
    public void assignment_statement()
    {
    	expect(Token.Kind.LET);
    	designator();
    	expect(Token.Kind.ASSIGN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    }		
    		
    // call-statement := call-expression ";"
    public void call_statement()
    {
    	call_expression();
    	expect(Token.Kind.SEMICOLON);
    }		
    		
    // if-statement := "if" expression0 statement-block [ "else" statement-block ] .
    public void if_statement()
    {
    	expect(Token.Kind.IF);
    	expression0();
    	enterScope();
    	statement_block();
    	exitScope();
    	if(accept(Token.Kind.ELSE)){
    		enterScope();
    		statement_block();
    		exitScope();
    	}
    }		
    		
    // while-statement := "while" expression0 statement-block .
    public void while_statement()
    {
    	expect(Token.Kind.WHILE);
    	expression0();
    	enterScope();
    	statement_block();
    	exitScope();
    }	
    		
    // return-statement := "return" expression0 ";" .
    public void return_statement()
    {
    	expect(Token.Kind.RETURN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    }	
    		
    //statement := variable-declaration | call-statement | assignment-statement 
    //				| if-statement | while-statement | return-statement .
    public void statement()
    {
    	if(have(Token.Kind.VAR))
    		variable_declaration();
    	else if(have(Token.Kind.CALL))
    		call_statement();
    	else if(have(Token.Kind.LET))
    		assignment_statement();
    	else if(have(Token.Kind.IF))
    		if_statement();
    	else if(have(Token.Kind.WHILE))
    		while_statement();
    	else if(have(Token.Kind.RETURN))
    		return_statement();
    	else{
    		String errorMessage = reportSyntaxError(NonTerminal.STATEMENT);
            throw new QuitParseException(errorMessage);
    	}
    }
    
    // statement-list := { statement } .
    public void statement_list()
    {
    	while(have(NonTerminal.STATEMENT))
    		statement();
    }
    		
    // statement-block := "{" statement-list "}" .
    public void statement_block()
    {
    	expect(Token.Kind.OPEN_BRACE);
    	statement_list();
    	expect(Token.Kind.CLOSE_BRACE);
    }
    		 
    // program := declaration-list EOF .
    public void program()
    {
    	declaration_list();
    	expect(Token.Kind.EOF);
    }
    
}
