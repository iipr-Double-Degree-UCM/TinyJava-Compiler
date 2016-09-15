package lexical_analyzer;

import java_cup.runtime.ComplexSymbolFactory.Location;
import lexical_analyzer.LexicalAnalyzerTiny;
import syntactic_analyzer.LexicalClass;
import error_handle.Errors;

public class LexicalAnalyzerOps {
	
	private LexicalAnalyzerTiny lexA;
	
	public LexicalAnalyzerOps(LexicalAnalyzerTiny lexA) {
		this.lexA = lexA;
	}

	public void error(Location locleft, Location locright) throws error_handle.Errors {
		throw new Errors(Errors.LEXICALERROR, "Lexical error: Unexpected symbol " + lexA.lexeme()
				+ " at: (line, column) = (" + locleft.getLine() + ", " + locleft.getColumn() + ")");
	}

	public LexicalUnit unitEof(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.EOF, "<EOF>");
	}

	public LexicalUnit unitDiv(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.DIV, "/");
	}

	public LexicalUnit unitMult(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.MULT, "*");
	}

	public LexicalUnit unitClassId(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CLASSNAMESTR, lexA.lexeme(), lexA.lexeme());
	}

	public LexicalUnit unitIdentifier(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.IDENTIFIER, lexA.lexeme(), lexA.lexeme());
	}

	public LexicalUnit unitInteger(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.INT, lexA.lexeme(), Integer.parseInt(lexA.lexeme()));
	}

	public LexicalUnit unitDot(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.DOT, ".");
	}

	public LexicalUnit unitAssign(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.ASSIGNSYM, "=");
	}

	public LexicalUnit unitAdd(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.ADD, "+");
	}

	public LexicalUnit unitSubs(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.SUBS, "-");
	}

	public LexicalUnit unitMod(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.MOD, "%");
	}

	public LexicalUnit unitNeg(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.NEG, "!");
	}

	public LexicalUnit unitOpenPar(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.OPENPAR, "(");
	}

	public LexicalUnit unitClosePar(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CLOSEPAR, ")");
	}

	public LexicalUnit unitComma(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.COMMA, ",");
	}

	public LexicalUnit unitSemicolon(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.SEMICOLON, ";");
	}

	public LexicalUnit unitOpenBrace(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.OPENBRACE, "{");
	}

	public LexicalUnit unitCloseBrace(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CLOSEBRACE, "}");
	}

	public LexicalUnit unitOpenBracket(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.OPENBRACKET, "[");
	}

	public LexicalUnit unitCloseBracket(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CLOSEBRACKET, "]");
	}


	public LexicalUnit unitLessThan(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.LT, "<");
	}

	public LexicalUnit unitGreaterThan(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.GT, ">");
	}

	public LexicalUnit unitStringVal(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.STRING, lexA.lexeme(), lexA.lexeme());
	}

	public LexicalUnit unitKWIf(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.IFKW, "if");
	}

	public LexicalUnit unitEqual(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.EQ, "==");
	}

	public LexicalUnit unitLessOrEqThan(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.LE, "<=");
	}

	public LexicalUnit unitDistinct(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.DIST, "!=");
	}

	public LexicalUnit unitGreaterOrEqThan(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.GE, ">=");
	}

	public LexicalUnit unitAnd(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.AND, "&&");
	}

	public LexicalUnit unitOr(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.OR, "||");
	}

	public LexicalUnit unitDouble(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.DOUBLE, lexA.lexeme(), Double.parseDouble(lexA.lexeme()));
	}

	public LexicalUnit unitKWFor(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.FORKW, "for");
	}

	public LexicalUnit unitCharVal(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CHAR, lexA.lexeme(), lexA.lexeme().charAt(1));
	}

	public LexicalUnit unitKWNew(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.NEWKW, "new");
	}

	public LexicalUnit unitKWInt(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.INTKW, "int");
	}

	public LexicalUnit unitBoolVal(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.BOOLVAL, lexA.lexeme(), Boolean.parseBoolean(lexA.lexeme()));
	}

	public LexicalUnit unitKWElse(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.ELSEKW, "else");
	}

	public LexicalUnit unitKWCase(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CASEKW, "case");
	}

	public LexicalUnit unitKWChar(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CHARKW, "char");
	}

	public LexicalUnit unitKWMain(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.MAINKW, "main");
	}

	public LexicalUnit unitKWBool(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.BOOLKW, "bool");
	}

	public LexicalUnit unitKWVoid(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.VOIDKW, "void");
	}

	public LexicalUnit unitKWClass(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.CLASSKW, "class");
	}

	public LexicalUnit unitKWWhile(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.WHILEKW, "while");
	}

	public LexicalUnit unitKWReturn(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.RETURNKW, "return");
	}

	public LexicalUnit unitKWSwitch(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.SWITCHKW, "switch");
	}

	public LexicalUnit unitKWPublic(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.PUBLICKW, "public");
	}

	public LexicalUnit unitKWDouble(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.DOUBLEKW, "double");
	}

	public LexicalUnit unitKWString(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.STRINGKW, "string");
	}

	public LexicalUnit unitKWPrivate(Location locleft, Location locright) {
		return new LexicalUnit(locleft, locright, LexicalClass.PRIVATEKW, "private");
	}
}
