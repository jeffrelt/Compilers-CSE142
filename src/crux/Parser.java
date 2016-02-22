package crux;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ast.Command;
import ast.DeclarationList;

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
    
    public ast.Command parse()
    {
    	initSymbolTable();
    	try {
            return program();
        } catch (QuitParseException q) {
            return new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");
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
    public ast.Expression literal()
    {
    	Token tok = expectRetrieve(NonTerminal.LITERAL);
        return Command.newLiteral(tok);
    }
    
    // designator := IDENTIFIER { "[" expression0 "]" } .
    public ast.Expression designator()
    {
    	Token t = expectRetrieve(Token.Kind.IDENTIFIER);
    	ast.Expression exp = new ast.AddressOf(t.lineNumber(),t.charPosition(),tryResolveSymbol(t));
        while (accept(Token.Kind.OPEN_BRACKET)) {
        	exp = new ast.Index(lineNumber(), charPosition(), exp, expression0());
            expect(Token.Kind.CLOSE_BRACKET);
        }
        return exp;
    }
    
    // type := IDENTIFIER .
    public Symbol type()
    {
    	// this will need to change
    	return tryResolveSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    }
    		 
    // op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
    public Token op0()
    {
    	return expectRetrieve(NonTerminal.OP0);
    }
    
    // op1 := "+" | "-" | "or" .
    public Token op1()
    {
    	return expectRetrieve(NonTerminal.OP1);
    }
    
    // op2 := "*" | "/" | "and" .
    public Token op2()
    {
    	return expectRetrieve(NonTerminal.OP2);
    }
    		 
    // expression0 := expression1 [ op0 expression1 ] .
    public ast.Expression expression0()
    {
    	ast.Expression exp = expression1();
    	if(have(NonTerminal.OP0)){
    		Token op = op0();
    		exp = ast.Command.newExpression(exp, op, expression1());
    	}
    	
    	return exp;
    }
    		
    // expression1 := expression2 { op1  expression2 } .
    public ast.Expression expression1()
    {
    	ast.Expression exp = expression2();
    	while(have(NonTerminal.OP1)){
    		Token op = op1();
    		exp = ast.Command.newExpression(exp, op, expression2());
    	}
    	
    	return exp;
    }
    		
    // expression2 := expression3 { op2 expression3 } .
    public ast.Expression expression2()
    {
    	ast.Expression exp = expression3();
    	while(have(NonTerminal.OP2)){
    		Token op = op2();
    		exp = ast.Command.newExpression(exp, op, expression3());
    	}
    	
    	return exp;
    }
    		
    // expression3 := "not" expression3 | "(" expression0 ")" | designator 
    // 				  | call-expression | literal .
    public ast.Expression expression3()
    {
    	ast.Expression exp;
    	if(have(Token.Kind.NOT)){
    		Token op = expectRetrieve(Token.Kind.NOT);
    		return new ast.LogicalNot(op.lineNumber(), op.charPosition(), expression3());
    	}
    	else if(accept(Token.Kind.OPEN_PAREN)){
    		exp = expression0();
    		expect(Token.Kind.CLOSE_PAREN);
    	}
    	else if(have(NonTerminal.DESIGNATOR))
    		exp = new ast.Dereference(lineNumber(),charPosition(), designator());
    	else if(have(NonTerminal.CALL_EXPRESSION))
    		exp = call_expression();
    	else if(have(NonTerminal.LITERAL))
    		exp = literal();
    	else{
    		String errorMessage = reportSyntaxError(NonTerminal.EXPRESSION3);
            throw new QuitParseException(errorMessage);
    	}
    	return exp;
    }
    		       
    // call-expression := "::" IDENTIFIER "(" expression-list ")" .
    public ast.Call call_expression()
    {
    	Token start = expectRetrieve(Token.Kind.CALL);
    	Symbol sym = tryResolveSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.OPEN_PAREN);
    	ast.ExpressionList args = expression_list();
    	expect(Token.Kind.CLOSE_PAREN);
    	return new ast.Call(start.lineNumber(),start.charPosition(),sym,args);
    }
    
    // expression-list := [ expression0 { "," expression0 } ] .
    public ast.ExpressionList expression_list()
    {
    	ast.ExpressionList el = new ast.ExpressionList(lineNumber(), charPosition());
    	if(have(NonTerminal.EXPRESSION0)){
    		el.add(expression0());
    		while(accept(Token.Kind.COMMA))
    			el.add(expression0());
    	}
    	return el;
    }
    
    // parameter := IDENTIFIER ":" type .
    public Symbol parameter()
    {
    	Symbol s = tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.COLON);
    	type();
    	return s;
    }
    		
    // parameter-list := [ parameter { "," parameter } ] .
    public List<Symbol> parameter_list()
    {
    	List<Symbol> args = new ArrayList<Symbol>();
    	if(have(Token.Kind.IDENTIFIER)){
    		args.add(parameter());
    		while(accept(Token.Kind.COMMA))
    			args.add(parameter());
    	}
    	return args;
    }
    		 
    // variable-declaration := "var" IDENTIFIER ":" type ";"
    public ast.VariableDeclaration variable_declaration()
    {
    	Token start = expectRetrieve(Token.Kind.VAR);
    	Symbol sym = tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.COLON);
    	// still not sure what we do with type!
    	type();
    	expect(Token.Kind.SEMICOLON);
    	return new ast.VariableDeclaration(start.lineNumber(),start.charPosition(), sym);
    }
    		
    // array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
    public ast.ArrayDeclaration array_declaration()
    {
    	Token start = expectRetrieve(Token.Kind.ARRAY);
    	Symbol symbol = tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER));
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
    	return new ast.ArrayDeclaration(start.lineNumber(),start.charPosition(), symbol);
    }		
    
    // function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
    public ast.FunctionDefinition function_definition()
    {
    	Token start = expectRetrieve(Token.Kind.FUNC);
    	Symbol func = tryDeclareSymbol( expectRetrieve(Token.Kind.IDENTIFIER) );
    	expect(Token.Kind.OPEN_PAREN);
    	enterScope();
    	List<Symbol> args = parameter_list();
    	expect(Token.Kind.CLOSE_PAREN);
    	expect(Token.Kind.COLON);
    	type();
    	ast.StatementList body = statement_block();
    	exitScope();
    	return new ast.FunctionDefinition(start.lineNumber(), start.charPosition(), func, args, body);
    }		
    		
    // declaration := variable-declaration | array-declaration | function-definition .
    public ast.Declaration declaration()
    {
    	if(have(Token.Kind.VAR)){
    		return variable_declaration();
    	}
    	if(have(Token.Kind.ARRAY)){
    		return array_declaration();
    	}
    	if(have(Token.Kind.FUNC)){
    		return function_definition();
    	}
		String errorMessage = reportSyntaxError(NonTerminal.DECLARATION);
        throw new QuitParseException(errorMessage);
    }		
    		
    // declaration-list := { declaration } .
    public ast.DeclarationList declaration_list()
    {
    	ast.DeclarationList dl = new DeclarationList(lineNumber(),charPosition());
    	while(have(NonTerminal.DECLARATION)){
    		dl.add(declaration());
    	}
    	return dl;
    }	
    		 
    // assignment-statement := "let" designator "=" expression0 ";"
    public ast.Assignment assignment_statement()
    {
    	Token start = expectRetrieve(Token.Kind.LET);
    	ast.Expression dest = designator();
    	expect(Token.Kind.ASSIGN);
    	ast.Expression src = expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	return new ast.Assignment(start.lineNumber(),start.charPosition(), dest, src);
    }		
    		
    // call-statement := call-expression ";"
    public ast.Statement call_statement()
    {
    	ast.Statement st = call_expression();
    	expect(Token.Kind.SEMICOLON);
    	
    	return st;
    }		
    		
    // if-statement := "if" expression0 statement-block [ "else" statement-block ] .
    public ast.IfElseBranch if_statement()
    {
    	Token start = expectRetrieve(Token.Kind.IF);
    	ast.Expression cond = expression0();
    	enterScope();
    	ast.StatementList thenBlock = statement_block();
    	exitScope();
    	ast.StatementList elseBlock;
    	if(accept(Token.Kind.ELSE)){
    		enterScope();
    		elseBlock = statement_block();
    		exitScope();
    	}
    	else
    		elseBlock = new ast.StatementList(lineNumber(),charPosition());
    	
    	return new ast.IfElseBranch(start.lineNumber(), start.charPosition(), cond, thenBlock, elseBlock);
    }		
    		
    // while-statement := "while" expression0 statement-block .
    public ast.WhileLoop while_statement()
    {
    	Token start = expectRetrieve(Token.Kind.WHILE);
    	ast.Expression cond = expression0();
    	enterScope();
    	ast.StatementList body = statement_block();
    	exitScope();
    	
    	return new ast.WhileLoop(start.lineNumber(), start.charPosition(), cond, body);
    }
    		
    // return-statement := "return" expression0 ";" .
    public ast.Return return_statement()
    {
    	Token start = expectRetrieve(Token.Kind.RETURN);
    	ast.Expression arg = expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	return new ast.Return(start.lineNumber(), start.charPosition(), arg);
    }	
    		
    //statement := variable-declaration | call-statement | assignment-statement 
    //				| if-statement | while-statement | return-statement .
    public ast.Statement statement()
    {
    	if(have(Token.Kind.VAR))
    		return variable_declaration();
    	else if(have(Token.Kind.CALL))
    		return call_statement();
    	else if(have(Token.Kind.LET))
    		return assignment_statement();
    	else if(have(Token.Kind.IF))
    		return if_statement();
    	else if(have(Token.Kind.WHILE))
    		return while_statement();
    	else if(have(Token.Kind.RETURN))
    		return return_statement();
    	else{
    		String errorMessage = reportSyntaxError(NonTerminal.STATEMENT);
            throw new QuitParseException(errorMessage);
    	}
    }
    
    // statement-list := { statement } .
    public ast.StatementList statement_list()
    {
    	ast.StatementList sl = new ast.StatementList(lineNumber(),charPosition());
    	while(have(NonTerminal.STATEMENT))
    		sl.add(statement());
    	return sl;
    }
    		
    // statement-block := "{" statement-list "}" .
    public ast.StatementList statement_block()
    {
    	expect(Token.Kind.OPEN_BRACE);
    	ast.StatementList sl = statement_list();
    	expect(Token.Kind.CLOSE_BRACE);
    	return sl;
    }
    		 
    // program := declaration-list EOF .
    public ast.Command program()
    {
    	ast.Command cmd = declaration_list();
    	expect(Token.Kind.EOF);
    	return cmd;
    }
    
}
