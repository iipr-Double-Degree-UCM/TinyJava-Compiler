package syntactic_analyzer.syntax;

public class ConstantValues {
	
	// For the types
	public static final int INTKW = 1;
	public static final int DOUBLEKW = 2;
	public static final int BOOLKW = 3;
	public static final int CHARKW = 4;
	public static final int STRINGKW = 5;

	// For the boolean constants
	public static final int TRUEKW = 6;
	public static final int FALSEKW = 7;
	
	// For the (binary and unary) operators
	public static final int OR = 8;
	public static final int AND = 9;
	public static final int EQ = 10;
	public static final int DIST = 11;
	public static final int LT = 12;
	public static final int GT = 13;
	public static final int LE = 14;
	public static final int GE = 15;
	public static final int ADD = 16;
	public static final int SUBS = 17;
	public static final int MULT = 18;
	public static final int DIV = 19;
	public static final int MOD = 20;
	public static final int NEG = 21;

	// Basic types have size equals to 1
	public static int getTypeSize(int type) {
		switch (type) {
		case INTKW:
			return 1;
		case DOUBLEKW:
			return 1;
		case BOOLKW:
			return 1;
		case CHARKW:
			return 1;
		default:
			return 1;
		}
	}

	public static String toString(int cons) {
		switch (cons) {
		case 1:
			return "int";
		case 2:
			return "double";
		case 3:
			return "bool";
		case 4:
			return "char";
		case 5:
			return "string";
		case 6:
			return "true";
		case 7:
			return "false";
		case 8:
			return "||";
		case 9:
			return "&&";
		case 10:
			return "==";
		case 11:
			return "!=";
		case 12:
			return "<";
		case 13:
			return ">";
		case 14:
			return "<=";
		case 15:
			return ">=";
		case 16:
			return "+";
		case 17:
			return "-";
		case 18:
			return "*";
		case 19:
			return "/";
		case 20:
			return "%";
		case 21:
			return "!";
		default:
			return "UNKNOWN CONSTANT!";
		}
	}
}
