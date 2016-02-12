package crux;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;

public class SymbolTable {
	public static String studentName = "Jeffrey Thompson";
    public static String studentID = "jeffrelt";
    public static String uciNetID = "12987953";
	
	public SymbolTable parent;
	
	static public LinkedHashMap<String,Symbol> pre_defined = build_pre_defined();
	
	static public LinkedHashMap<String,Symbol> reserved_types = build_reserved_types();
	
	private static LinkedHashMap<String,Symbol> build_reserved_types()
	{
		LinkedHashMap<String,Symbol> hm = new LinkedHashMap<String,Symbol>();
		hm.put("void", new Symbol("void"));
		hm.put("bool", new Symbol("bool"));
		hm.put("int", new Symbol("int"));
		hm.put("float", new Symbol("float"));
		hm.put("boolean", new Symbol("boolean")); //Is this one valid?
		return hm;
	}
	
	private static LinkedHashMap<String,Symbol> build_pre_defined()
	{
		LinkedHashMap<String,Symbol> hm = new LinkedHashMap<String,Symbol>();
		hm.put("readInt", new Symbol("readInt"));
		hm.put("readFloat", new Symbol("readFloat"));
		hm.put("printBool", new Symbol("printBool"));
		hm.put("printInt", new Symbol("printInt"));
		hm.put("printFloat", new Symbol("printFloat"));
		hm.put("println", new Symbol("println"));
		return hm;
	}
	
	private LinkedHashMap<String,Symbol> table;
    
    public SymbolTable()
    {
    	table = new LinkedHashMap<String,Symbol>();
    }
    
    public Symbol lookup(String name) throws SymbolNotFoundError
    {
    	if( pre_defined.containsKey(name) )
    		return pre_defined.get(name);
    	if( reserved_types.containsKey(name) ) //this will need to change
    		return reserved_types.get(name);
    	SymbolTable walker = this;
    	while( walker != null ){
    		if( walker.table.containsKey(name) )
        		return walker.table.get(name);
        	walker = walker.parent;
        }
    	throw new SymbolNotFoundError(name);
    }
       
    public Symbol insert(String name) throws RedeclarationError
    {
    	if( reserved_types.containsKey(name) )  //this will need to change
    		return reserved_types.get(name);
        if( pre_defined.containsKey(name) )
        	throw new RedeclarationError(pre_defined.get(name));
        if( table.containsKey(name) )
        	throw new RedeclarationError(table.get(name));
        Symbol new_symbol = new Symbol(name);
        table.put(name, new_symbol);
        return new_symbol;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (Symbol s : pre_defined.values())
        {
        	sb.append(s.toString());
            sb.append('\n');
        }
        
        SymbolTable walker = this;
        Stack<SymbolTable> stack = new Stack<SymbolTable>();
        while ( walker.parent != null ){
        	stack.push(walker);
        	walker = walker.parent;
        }
        
        String indent = new String();
        while( true ){
        	for (Symbol s : walker.table.values())
            {
                sb.append(indent + s.toString());
                sb.append('\n');
            }
        	if( stack.isEmpty() )
        		break;
        	indent += "  ";
        	walker = stack.pop();
        } 
        return sb.toString();
    }
}

class SymbolNotFoundError extends Error
{
    private static final long serialVersionUID = 1L;
    private String name;
    
    SymbolNotFoundError(String name)
    {
        this.name = name;
    }
    
    public String name()
    {
        return name;
    }
}

class RedeclarationError extends Error
{
    private static final long serialVersionUID = 1L;

    public RedeclarationError(Symbol sym)
    {
        super("Symbol " + sym + " being redeclared.");
    }
}
