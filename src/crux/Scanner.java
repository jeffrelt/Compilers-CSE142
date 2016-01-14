package crux;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;

import crux.Token.Kind;

public class Scanner /*implements Iterable<Token>*/ {
	public static String studentName = "Jeffrey Thompson";
    public static String studentID = "jeffrelt";
    public static String uciNetID = "12987953";
	
	private int lineNum;  // current line count
	private int charPos;  // character offset for current line
	private int nextChar; // contains the next char (-1 == EOF)
	private Reader input;

	Scanner(Reader reader) 
	{
		input = reader;
		lineNum = 1;
		charPos = 0; //when we readChar() this goes to 1
		readChar();
	}	
	
	// OPTIONAL: helper function for reading a single char from input
	//           can be used to catch and handle any IOExceptions,
	//           advance the charPos or lineNum, etc.

	private void readChar() 
	{
		try{
			nextChar = input.read();
			if (nextChar == '\n'){
				lineNum+=1;
				charPos=0;
			}
			else
				charPos+=1;
		}catch (IOException e) {
			nextChar = -1;
		}
	}
	
	// Abstracted the various states into the following types
	private static enum scannerState {
		ERROR,
		SPECIAL,
		INTEGER,
		FLOAT,
		WORD;
	}
	/* Invariants:
	 *  1. call assumes that nextChar is already holding an unread character
	 *  2. return leaves nextChar containing an untokenized character
	 */
	public Token next()
	{
		while(true){
			//skip all whitespace
			while(Character.isWhitespace(nextChar))
				readChar();
			//grab our starting place
			int startLine = lineNum;
			int startPos = charPos;
			//if EOF return here
			if(nextChar == -1)
				return Token.EOF(startLine,startPos);
			//staring state and build the lexeme
			scannerState state = scannerState.ERROR;
			String lexeme = String.valueOf((char)nextChar);
			
			// Find the abstracted state of this token
			if(Character.isLetter(nextChar) || nextChar == '_')
				state = scannerState.WORD;
			else if(Character.isDigit(nextChar))
				state = scannerState.INTEGER;
			else if(nextChar == '!' || Token.knownTokens.containsKey(lexeme))
				state = scannerState.SPECIAL;
			
			//this is a label to break from within the case statement to start over when we determine comments
			MAINLOOP:
			while(true){ //loop until we either return or break
				readChar();
				String newLexeme = lexeme+String.valueOf((char)nextChar);
				switch(state){
				case ERROR:
					return new Token(lexeme, startLine,startPos);
				case SPECIAL:
					if(!Token.knownTokens.containsKey(newLexeme)){
						if(newLexeme.equals("//")){
							//comment so skip all chars until we get a '\n'
							while(nextChar != '\n' && nextChar != -1)
								readChar();
							//start over from the top
							break MAINLOOP;
						}
						return new Token(lexeme, startLine,startPos);
					}
					//implicit else - the special token is at most 2 chars long we we can return
					//be sure to advance before returning
					readChar();
					return new Token(newLexeme, startLine,startPos);
				case INTEGER:
					if(nextChar == '.')
						state = scannerState.FLOAT;
					else if(!Character.isDigit(nextChar))
						return new Token(lexeme, startLine,startPos);
					//implicit else - nextChar is OK so keep going
					lexeme = newLexeme;
					break;
				case FLOAT:
					if(!Character.isDigit(nextChar))
						return Token.Float(lexeme, startLine,startPos);
					//implicit else - nextChar is a digit so keep going
					lexeme = newLexeme;
					break;
				case WORD:
					if(!(Character.isLetter(nextChar) || Character.isDigit(nextChar) || nextChar == '_') )
						return new Token(lexeme, startLine,startPos);
					//implicit else - still a valid identifier so keep going
					lexeme = newLexeme;
					break;
				}
			}
		}
	}

	// OPTIONAL: any other methods that you find convenient for implementation or testing
}
