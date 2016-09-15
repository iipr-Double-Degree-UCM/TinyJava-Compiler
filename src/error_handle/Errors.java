package error_handle;

import syntactic_analyzer.syntax.Expression;
import syntactic_analyzer.syntax.Expression.Identifier;

@SuppressWarnings("serial")

public class Errors extends Exception {

	// Type of error
	private int errorType;
	// Error message that is displayed
	private String errorMsg;

	public Errors(int errorType, String errorMsg) {
		this.errorType = errorType;
		this.errorMsg = errorMsg;
	}
	
	public void print() {
		System.err.println(this.errorMsg);
	}

	// General errors
	public static final int OTHERERROR = 0;
	public static final int SYNTACTICERROR = 1;
	public static final int LEXICALERROR = 2;
	
	// Identification of Identifiers errors
	public static final int VARALREADYDEC = 3;
	public static final int NOTINITVAR = 4;
	public static final int VARNONDEC = 5;
	public static final int ARRAYNONDEC = 6;
	public static final int ARRAYALREADYDEC = 7;
	public static final int NOTANARRAY = 8;
	public static final int NOTBASIC = 9;
	public static final int OBJNONDECL = 10;
	public static final int NOTATTRIBUTE = 11;
	public static final int ATTNOTBELONG = 12;
	public static final int OBJALREADYDEC = 13;
	public static final int NOSUCHCLASS = 55;
	public static final int NOSUCHMETHOD = 56;
	public static final int CLASSALREADYDEC = 57;
	public static final int METHALREADYDEF = 58;
	public static final int ATTRALREADYDEF = 59;
	public static final int IDNOTDECL = 60;
	public static final int VARALREADYDECASATTRIB = 61;
	
	
	// Type checking errors
	public static final int NONCOMPTYPES = 14;
	public static final int BINARYINCONG = 15;
	public static final int UNARYINCONG = 16;
	public static final int UNKNOWNTYPE = 17;
	public static final int EQDIST = 18;
	public static final int RETVALERROR = 19;
	public static final int VARDECL = 20;
	public static final int ARRAYDECLTYPE = 21;
	public static final int ARRAYASSIGNTYPE = 22;
	public static final int ASSIGNTYPE = 23;
	public static final int ATTRIBTYPE = 24;
	public static final int BOOLEXPECTED = 25;
	public static final int INTEXPECTED = 26;
	public static final int PARAMTYPEERROR = 27;
	
	// Privacy errors
	public static final int PRIVATTRIB = 35;
	public static final int PRIVMETHOD = 36;
	public static final int ASSIGNATTRIB = 37;
	
	// Other errors
	public static final int SWITCHNONCONSEC = 45;
	public static final int PARAMLISTSIZE = 46;
	
	
	
	public Errors(int errType, Expression e) {
		this.errorType = errType;
		this.errorMsg = selectMessage(this.errorType, e, null);
	}
	
	public Errors(int errType, Expression e1, Expression e2) {
		this.errorType = errType;
		this.errorMsg = selectMessage(this.errorType, e1, e2);
	}

	private String selectMessage(int errType, Expression e1, Expression e2) {
		String str = null;
		switch (errType) {
		case OTHERERROR:
			str = "Undefined error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn();
			break;
		case VARALREADYDEC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": Variable "
					+ e1.toString() + " is already declared.";
			break;
		case VARALREADYDECASATTRIB:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": Variable "
					+ e1.toString() + " is already declared as attribute of a class.";
			break;
		case NOTINITVAR:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": Variable "
					+ e1.toString() + " hasn't been initialized.";
			break;
		case VARNONDEC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": Variable "
					+ e1.toString() + " hasn't been declared.";
			break;
		case ARRAYNONDEC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The array "
					+ e1.toString() + "[] hasn't been declared.";
			break;
		case ARRAYALREADYDEC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The array "
					+ e1.toString() + "[] is already declared.";
			break;
		case NOTANARRAY:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The variable "
					+ e1.toString() + " is not an array.";
			break;
		case NOTBASIC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The variable "
					+ e1.toString() + " is not a basic variable.";
			break;
		case OBJNONDECL:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The object "
					+ e1.toString() + " hasn't been instantiated.";
			break;
		case NOTATTRIBUTE:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The attribute "
					+ e1.toString() + " is not an attribute.";
			break;	
		case ATTNOTBELONG:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The attribute "
					+ e1.toString() + " doesn't belong to the object " + e2.toString() + ".";
			break;	
		case OBJALREADYDEC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The object "
					+ e1.toString() + " is already instantiated.";
			break;
		case NOSUCHCLASS:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The class "
					+ e1.toString() + " hasn't been defined.";
			break;
		case NOSUCHMETHOD:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The method "
					+ e1.toString() + " hasn't been defined.";
			break;
		case CLASSALREADYDEC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The class "
					+ e1.toString() + " is already defined.";
			break;
		case METHALREADYDEF:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The method "
					+ e1.toString() + " is already defined in the class " + e2.toString() + ".";
			break;
		case ATTRALREADYDEF:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The attribute "
					+ e1.toString() + " is already defined in the class " + e2.toString() + ".";
			break;
		case IDNOTDECL:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The identifier "
					+ e1.toString() + " hasn't been declared.";
			break;
			
			
		case NONCOMPTYPES:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The types of the expressions "
					+ e1.toString() + " and " + e2.toString() + " don't match.";
			break;
		case BINARYINCONG:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The types of the expressions "
					+ e1.toString() + " and " + e2.toString() + " don't match with the given binary operator.";
			break;
		case UNARYINCONG:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the expression "
					+ e1.toString() + " don't match with the given unary operator.";
			break;
		case UNKNOWNTYPE:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the expression "
					+ e1.toString() + " is not recognized.";
			break;
		case EQDIST:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the expressions "
					+ e1.toString() + " and " + e2.toString() + " can only be (both) integer or (both) double.";
			break;
		case RETVALERROR:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the expression "
					+ e1.toString() + " doesn't match the method type.";
			break;
		case VARDECL:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the expression "
					+ e1.toString() + " doesn't match with the type of the declared variable " + e2.toString() + ".";
			break;
		case ARRAYASSIGNTYPE:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the array element "
					+ e1.toString() + " doesn't match with the type of the expression " + e2.toString() + ".";
			break;
		case ASSIGNTYPE:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the basic variable "
					+ e1.toString() + " doesn't match with the type of the expression " + e2.toString() + ".";
			break;
		case ATTRIBTYPE:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the attribute "
					+ e1.toString() + " doesn't match with the type of the expression " + e2.toString() + ".";
			break;
		case BOOLEXPECTED:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the expression "
					+ e1.toString() + " should be boolean.";
			break;
		case INTEXPECTED:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the expression "
					+ e1.toString() + " should be integer.";
			break;
		case PARAMTYPEERROR:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The type of the parameter "
					+ e1.toString() + " is wrong.";
			break;
		
		
		case PRIVATTRIB:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The attribute "
					+ e1.toString() + " is not accesible since it is private.";
			break;
		case PRIVMETHOD:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": The method "
					+ e1.toString() + " is not accesible since it is private.";
			break;
		case ASSIGNATTRIB:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": Tryed to access the attribute "
					+ e1.toString() + " from the class " + e2.toString() + " on the lhs of an assign with no object.";
			break;
			
			
		case SWITCHNONCONSEC:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + ": Switch statement " +
					 "only accepts integer consecutive values starting at 0, got " + e1.toString() + " here.";
			break;
		case PARAMLISTSIZE:
			str = "Error at line " + e1.locleft.getLine() + ", column " + e1.locleft.getColumn() + 
					": The number of parameters of the call doesn't match with the number of parameters of the method " +
					e1.toString() + ".";  
			break;
			
		}
		return str;
	}

}
