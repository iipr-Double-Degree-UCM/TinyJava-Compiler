/*
	 ____________________
	|					 |
	|     USER CODE:     |
	|____________________|

	This is copied into the begining of
	the source file of the generated lexer.

*/

package lexical_analyzer;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;


/*
	 ___________________________
	|		 	     			|
	| OPTIONS AND DECLARATIONS: |
	|___________________________|

	Options such as:
	
	- the name of the class,
	- whether it is public or not,
	- cup compatibility,
	- the interface that it implements,
	- character counting: on,
	- line counting: on,
	- column counting: on,
	- the code enclosed between %{ ... %} 	
	  will be copied into the generated class,
	- the code enclosed between %eofval{ ... %eofval} will be copied into
	  the scanning method and executed each time the end of a file is reached,
	- the code enclosed between %init{ ... %init} will be
	  copied into the constructor of the generated class,
	- macro definitions as regular expressions,
	
	...
	
*/
%%


%public
%class LexicalAnalyzerTiny
%cup
%implements syntactic_analyzer.LexicalClass
%char
%line
%column

%{
	private LexicalAnalyzerOps lexOps;

	public String lexeme() {
		return yytext();
	}

	public int row() {
		return yyline + 1;
	}

	public int column() {
		return yycolumn + 1;
	}

	public LexicalAnalyzerTiny(java.io.Reader in, ComplexSymbolFactory sf) {
		this(in);
		symbolFactory = sf;
	}

	ComplexSymbolFactory symbolFactory;

	private Location locleft() {
		return new Location(yyline + 1, yycolumn + 1);
	}

	private Location locright() {
		return new Location(yyline + 1, yycolumn + yylength());
	}
%} 

%eofval{
  return lexOps.unitEof(locleft(),locright());
%eofval}

%init{
  lexOps = new LexicalAnalyzerOps(this);
%init}

separator = [ \t\r\b\n]
longComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
comment = ("//"[^\n]*|longComment)
char  = ([A-Z]|[a-z])
digit = [0-9]
mayusc = [A-Z]
integer = ({digit})+
double = ({integer})+\.({integer})+
boolVal = (true|false)
stringVal = \"({char}|{digit})*\"
charVal = \'{char}\'

classKW = class
newKW = new
mainKW = main
publicKW = public
privateKW = private
voidKW = void
returnKW = return

whileKW = while
forKW = for
ifKW = if
elseKW = else
switchKW = switch
caseKW = case
intKW = int
doubleKW = double
boolKW = bool
charKW = char
stringKW = String
assign = "="

classId = {mayusc}({char}|{digit})*
identifier = {char}({char}|{digit})*
opAdd = \+
opSubs = \-
opMult = \*
opDiv = "/"
opMod = %
opDistinct = "!="
opNeg = "!"
openPar = \(
closePar = \)
equal = "=="
comma  = \,
dot = \.
semicolon = \;
openBrace = \{
closeBrace = \}
openBracket = \[
closeBracket = \]
lessThan = <
greaterThan = >
lessOrEqThan = \=<
greaterOrEqThan = >\=
and = "&&"
or = "||"

%%
/*
	 _________________
	|				  |
	| LEXICAL RULES:  |
	|_________________|

*/

/* 
	Structure: 
	· For univalued lexic units ->
		{return symbol(symbolName, terminalNameInCUP)}
	· For multivalued lexic units -> 
		{return symbol(symbolName, terminalNameInCUP, lexeme(as yytext())}
*/

{separator}				{}
{longComment}			{}
{comment}				{}
{integer}				{return lexOps.unitInteger(locleft(), locright());}
{double}				{return lexOps.unitDouble(locleft(), locright());}
{boolVal}				{return lexOps.unitBoolVal(locleft(), locright());}
{stringVal}				{return lexOps.unitStringVal(locleft(), locright());}
{charVal}				{return lexOps.unitCharVal(locleft(), locright());}

{classKW}				{return lexOps.unitKWClass(locleft(), locright());}
{newKW}					{return lexOps.unitKWNew(locleft(), locright());}
{mainKW}				{return lexOps.unitKWMain(locleft(), locright());}
{publicKW}				{return lexOps.unitKWPublic(locleft(), locright());}
{privateKW}				{return lexOps.unitKWPrivate(locleft(), locright());}
{voidKW}				{return lexOps.unitKWVoid(locleft(), locright());}
{returnKW}				{return lexOps.unitKWReturn(locleft(), locright());}

{whileKW}				{return lexOps.unitKWWhile(locleft(), locright());}
{forKW}					{return lexOps.unitKWFor(locleft(), locright());}
{ifKW}					{return lexOps.unitKWIf(locleft(), locright());}
{elseKW}				{return lexOps.unitKWElse(locleft(), locright());}
{switchKW}				{return lexOps.unitKWSwitch(locleft(), locright());}
{caseKW}				{return lexOps.unitKWCase(locleft(), locright());}
{intKW}					{return lexOps.unitKWInt(locleft(), locright());}
{doubleKW}				{return lexOps.unitKWDouble(locleft(), locright());}
{boolKW}				{return lexOps.unitKWBool(locleft(), locright());}
{stringKW}				{return lexOps.unitKWString(locleft(), locright());}
{charKW}				{return lexOps.unitKWChar(locleft(), locright());}
{assign}				{return lexOps.unitAssign(locleft(), locright());}

{classId}				{return lexOps.unitClassId(locleft(), locright());}
{identifier}			{return lexOps.unitIdentifier(locleft(), locright());}
{opAdd}					{return lexOps.unitAdd(locleft(), locright());}
{opSubs}				{return lexOps.unitSubs(locleft(), locright());}
{opMult}				{return lexOps.unitMult(locleft(), locright());}
{opDiv}					{return lexOps.unitDiv(locleft(), locright());}
{opMod}					{return lexOps.unitMod(locleft(), locright());}
{opDistinct}			{return lexOps.unitDistinct(locleft(), locright());}
{opNeg}					{return lexOps.unitNeg(locleft(), locright());}
{openPar}				{return lexOps.unitOpenPar(locleft(), locright());}
{closePar}				{return lexOps.unitClosePar(locleft(), locright());} 
{equal}					{return lexOps.unitEqual(locleft(), locright());} 
{comma}					{return lexOps.unitComma(locleft(), locright());}
{dot}					{return lexOps.unitDot(locleft(), locright());}
{semicolon}				{return lexOps.unitSemicolon(locleft(), locright());}
{openBrace}				{return lexOps.unitOpenBrace(locleft(), locright());}
{closeBrace}			{return lexOps.unitCloseBrace(locleft(), locright());}
{openBracket}				{return lexOps.unitOpenBracket(locleft(), locright());}
{closeBracket}			{return lexOps.unitCloseBracket(locleft(), locright());}
{lessThan}				{return lexOps.unitLessThan(locleft(), locright());}
{greaterThan}			{return lexOps.unitGreaterThan(locleft(), locright());}
{lessOrEqThan}			{return lexOps.unitLessOrEqThan(locleft(), locright());}
{greaterOrEqThan}		{return lexOps.unitGreaterOrEqThan(locleft(), locright());}
{and}					{return lexOps.unitAnd(locleft(), locright());}
{or}					{return lexOps.unitOr(locleft(), locright());}
[^]						{lexOps.error(locleft(), locright());}
