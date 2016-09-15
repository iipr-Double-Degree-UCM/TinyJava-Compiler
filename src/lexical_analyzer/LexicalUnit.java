package lexical_analyzer;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class LexicalUnit extends ComplexSymbol {

	public LexicalUnit(Location left, Location right, int classNum, String lexeme) {
		super(lexeme, classNum, left, right, new Integer(classNum));
		this.left = left;
		this.right = right;
	}
	
	public LexicalUnit(Location left, Location right, int classNum, String lexeme, Object obj) {
		super(lexeme, classNum, left, right, obj);
		this.left = left;
		this.right = right;
	}

	// Return the integer that represents the given lexical unit
	public int classNum() {
		return sym;
	}

	// Return the string conversion of the given lexical unit
	public String lexeme() {
		return (String) value;
	}

	// Return the line of the given lexical unit
	public int line() {
		return left.getLine();
	}

	// Return the column of the given lexical unit
	public int column() {
		return left.getColumn();
	}

	// Return the left location of the given lexical unit
	public Location getLeft() {
		return this.left;
	}

	// Return the right location of the given lexical unit
	public Location getRight() {
		return this.right;
	}
	
	private Location left;
	private Location right;
}