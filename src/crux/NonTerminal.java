package crux;
import java.util.HashSet;
import java.util.Set;

public enum NonTerminal {
    
    // TODO: mention that we are not modeling the empty string
    // TODO: mention that we are not doing a first set for every line in the grammar
    //       some lines have already been handled by the CruxScanner
    
    DESIGNATOR(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// designator := IDENTIFIER { "[" expression0 "]" } .
            add(Token.Kind.IDENTIFIER);
        }}),
    TYPE(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// type := IDENTIFIER .
            add(Token.Kind.IDENTIFIER);
        }}),
    LITERAL(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// literal := INTEGER | FLOAT | TRUE | FALSE .
        	add(Token.Kind.INTEGER);
        	add(Token.Kind.FLOAT);
        	add(Token.Kind.TRUE);
        	add(Token.Kind.FALSE);
        }}),
    CALL_EXPRESSION(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// call-expression := "::" IDENTIFIER "(" expression-list ")" .
        	add(Token.Kind.CALL);
        }}),
    OP0(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
        	add(Token.Kind.GREATER_EQUAL);
        	add(Token.Kind.LESSER_EQUAL);
        	add(Token.Kind.NOT_EQUAL);
        	add(Token.Kind.EQUAL);
        	add(Token.Kind.GREATER_THAN);
        	add(Token.Kind.LESS_THAN);
       }}),
    OP1(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// op1 := "+" | "-" | "or" .
        	add(Token.Kind.ADD);
        	add(Token.Kind.SUB);
        	add(Token.Kind.OR);
       }}),
    OP2(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// op2 := "*" | "/" | "and" .
        	add(Token.Kind.MUL);
        	add(Token.Kind.DIV);
        	add(Token.Kind.AND);
       }}),
    EXPRESSION3(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// expression3 := "not" expression3 | "(" expression0 ")" | designator 
            // 				  | call-expression | literal .
        	add(Token.Kind.NOT);
        	add(Token.Kind.OPEN_PAREN);
        	add(Token.Kind.AND);
        	add(Token.Kind.IDENTIFIER);
        	add(Token.Kind.CALL);
        	add(Token.Kind.INTEGER);
        	add(Token.Kind.FLOAT);
        	add(Token.Kind.TRUE);
        	add(Token.Kind.FALSE);
        	
       }}),
    EXPRESSION2(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// expression2 := expression3 { op2 expression3 } .
        	add(Token.Kind.NOT);
        	add(Token.Kind.OPEN_PAREN);
        	add(Token.Kind.AND);
        	add(Token.Kind.IDENTIFIER);
        	add(Token.Kind.CALL);
        	add(Token.Kind.INTEGER);
        	add(Token.Kind.FLOAT);
        	add(Token.Kind.TRUE);
        	add(Token.Kind.FALSE);
        }}),
    EXPRESSION1(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// expression1 := expression2 { op1  expression2 } .
        	add(Token.Kind.NOT);
        	add(Token.Kind.OPEN_PAREN);
        	add(Token.Kind.AND);
        	add(Token.Kind.IDENTIFIER);
        	add(Token.Kind.CALL);
        	add(Token.Kind.INTEGER);
        	add(Token.Kind.FLOAT);
        	add(Token.Kind.TRUE);
        	add(Token.Kind.FALSE);
        }}),
    EXPRESSION0(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// expression0 := expression1 [ op0 expression1 ] .
        	add(Token.Kind.NOT);
        	add(Token.Kind.OPEN_PAREN);
        	add(Token.Kind.AND);
        	add(Token.Kind.IDENTIFIER);
        	add(Token.Kind.CALL);
        	add(Token.Kind.INTEGER);
        	add(Token.Kind.FLOAT);
        	add(Token.Kind.TRUE);
        	add(Token.Kind.FALSE);
        }}),
    EXPRESSION_LIST(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// expression-list := [ expression0 { "," expression0 } ] .
        	add(Token.Kind.NOT);
        	add(Token.Kind.OPEN_PAREN);
        	add(Token.Kind.AND);
        	add(Token.Kind.IDENTIFIER);
        	add(Token.Kind.CALL);
        	add(Token.Kind.INTEGER);
        	add(Token.Kind.FLOAT);
        	add(Token.Kind.TRUE);
        	add(Token.Kind.FALSE);
        }}),
    PARAMETER(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// parameter := IDENTIFIER ":" type .
        	add(Token.Kind.IDENTIFIER);
        }}),
    PARAMETER_LIST(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// parameter-list := [ parameter { "," parameter } ] .
        	add(Token.Kind.IDENTIFIER);
        }}),
    VARIABLE_DECLARATION(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// variable-declaration := "var" IDENTIFIER ":" type ";"
        	add(Token.Kind.VAR);
        }}),
    ARRAY_DECLARATION(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
        	add(Token.Kind.ARRAY);
        }}),
    FUNCTION_DEFINITION(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
        	add(Token.Kind.FUNC);
        }}),
    DECLARATION(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// declaration := variable-declaration | array-declaration | function-definition .
        	add(Token.Kind.VAR);
        	add(Token.Kind.ARRAY);
        	add(Token.Kind.FUNC);
        }}),
    DECLARATION_LIST(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// declaration-list := { declaration } .
        	add(Token.Kind.VAR);
        	add(Token.Kind.ARRAY);
        	// assignment-statement := "let" designator "=" expression0 ";"
        }}),    
    ASSIGNMENT_STATEMENT(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// assignment-statement := "let" designator "=" expression0 ";"
        	add(Token.Kind.LET);
        }}),
    CALL_STATEMENT(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// call-statement := call-expression ";"
        	add(Token.Kind.CALL);
        }}),
    IF_STATEMENT(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// if-statement := "if" expression0 statement-block [ "else" statement-block ] .
        	add(Token.Kind.IF);
        }}),
    WHILE_STATEMENT(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// while-statement := "while" expression0 statement-block .
        	add(Token.Kind.WHILE);
        }}),
    RETURN_STATEMENT(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// return-statement := "return" expression0 ";" .
        	add(Token.Kind.RETURN);
        }}),
    STATEMENT(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	//statement := variable-declaration | call-statement | assignment-statement 
            //				| if-statement | while-statement | return-statement .
        	add(Token.Kind.VAR);
        	add(Token.Kind.CALL);
        	add(Token.Kind.LET);
        	add(Token.Kind.IF);
        	add(Token.Kind.WHILE);
        	add(Token.Kind.RETURN);
        }}),
    STATEMENT_LIST(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// statement-list := { statement } .
        	// nothing needed
        }}),
    STATEMENT_BLOCK(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// statement-block := "{" statement-list "}" .
        	add(Token.Kind.OPEN_BRACE);
        }}),
    PROGRAM(new HashSet<Token.Kind>() {
        private static final long serialVersionUID = 1L;
        {
        	// program := declaration-list EOF .
        	// nothing needed
        }});
           
    public final HashSet<Token.Kind> firstSet = new HashSet<Token.Kind>();

    NonTerminal(HashSet<Token.Kind> t)
    {
        firstSet.addAll(t);
    }
    
    public final Set<Token.Kind> firstSet()
    {
        return firstSet;
    }
}
