package syntactic_analyzer.syntax;

import java.util.*;

import code_generator.CodeManager;
import code_generator.Instruction;
import error_handle.Errors;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantic_analyzer.IdAndTypeChecker;
import syntactic_analyzer.syntax.Expression.*;

public abstract class Statement {

	public Location locleft;
	public Location locright;
	
	public Statement(Location l, Location r){
		this.locleft = l;
		this.locright = r;
	}
	
	public void check(IdAndTypeChecker checker) throws Errors{ }
	
	public void code(CodeManager coder) { }
	
	/**
	 * OBJECT DECLARATION STATEMENT
	 */
	public static class ObjectDeclaration extends Statement {

		// classNameL is the left-hand-side class name given in the declaration
		public Identifier classNameL;
		public Identifier objName;
		// classNameR is the right-hand-side class name given in the declaration
		public Identifier classNameR;
		public List<Expression> expList;

		public ObjectDeclaration(Location l, Location r, Identifier cnl, Identifier on, Identifier cnr, List<Expression> el) {
			super(l, r);
			this.classNameL = cnl;
			this.objName = on;
			this.classNameR = cnr;
			this.expList = el;
		}

		public String toString() {
			String strVal = this.classNameL.toString() + " " + this.objName + " = new " + this.classNameR + "(";
			if (this.expList != null) {
				for (int i = 0; i < this.expList.size() - 1; i++) {
					strVal += this.expList.get(i) + ", ";
				}
				strVal += this.expList.get(this.expList.size() - 1) + ");";
			} else
				strVal += ");\n";
			return  strVal;
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			// Object declaration class allocates memory for all its attributes
			coder.addToRho(this.objName.id, this.expList.size());
			// This is for accessing to the right attribute one the expression AttribObject
			coder.addObjectClass(this.objName.id, this.classNameL.id);
			// MST (nesting depth proc call) - (nesting depth proc decl)
			// but note that the nesting depth of the procedure declaration is 0
			int value = coder.getDepth();
			coder.incDepth();
			coder.addInstruction(Instruction.MST, value);
			int cupSize = 0;
			// We pass the object by reference:
			// obj.meth(e_1, ..., e_n) <-> meth(obj, e_1, ..., e_n)
			this.objName.code_L(coder);
			// We add to cupSize the size of the object
			cupSize += coder.getAttrCount(coder.getObjectClass(this.objName.id));
			// And now we add to cupSize the size of each parameter (each of
			// them is basic)
			for (Expression e : this.expList) {
				cupSize++;
				e.code_R(coder);
			}
			coder.addCUPInstruction(Instruction.CUP, cupSize, coder.getMethLine(this.classNameL.id));
		}
		
	}
	
	/**
	 * DECLARATION STATEMENT (for basic types)
	 */
	public static class VariableDeclaration extends Statement {

		public int type;
		public Identifier varName;
		public Expression exp;

		public static VariableDeclaration variableDeclarationNoAssign(Location l, Location r, int t, Identifier id){
			return new VariableDeclaration(l, r, t, id, null);
		}
		
		public static VariableDeclaration variableDeclarationWithAssign(Location l, Location r, int t, Identifier id, Expression e){
			return new VariableDeclaration(l, r, t, id, e);
		}
		
		public VariableDeclaration(Location l, Location r, int t, Identifier id, Expression e) {
			super(l, r);
			this.type = t;
			this.varName = id;
			this.exp = e;
		}

		public String toString() {
			if(this.exp != null)
				return ConstantValues.toString(this.type) + " " + this.varName + " = " + this.exp + ";\n";
			else
				return ConstantValues.toString(this.type) + " " + this.varName + ";\n";
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			// If it was previously defined, we don't need to save any changes
			if (coder.rho(this.varName.id) == -1)
				coder.addToRho(this.varName.id, 1);
			if (this.exp != null) {
				this.varName.code_L(coder);
				this.exp.code_R(coder);
				coder.addInstruction(Instruction.STO, new Integer(0));
			}
		}
		
	}
	
	/**
	 * ASSIGN STATEMENT (for basic types)
	 */
	public static class Assign extends Statement {

		public Identifier varName;
		public Expression exp;
		
		public Assign(Location l, Location r, Identifier id, Expression e) {
			super(l, r);
			this.varName = id;
			this.exp = e;
		}

		public String toString() {
			return this.varName + " = " + this.exp + ";\n";
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			this.varName.code_L(coder);
			this.exp.code_R(coder);
			coder.addInstruction(Instruction.STO, new Integer(0));
		}
		
	}
	
	/**
	 * ATTRIBUTE OF AN OBJECT ASSIGN STATEMENT
	 */
	public static class AttribObjectAssign extends Statement {

		public AttribObject attrObj;
		public Expression exp;
		
		public AttribObjectAssign(Location l, Location r, AttribObject ao, Expression e) {
			super(l, r);
			this.attrObj = ao;
			this.exp = e;
		}

		public String toString() {
			return this.attrObj.getObject() + "." + this.attrObj.getAttribute() + " = " + this.exp + ";\n";
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			this.attrObj.code_L(coder);
			this.exp.code_R(coder);
			coder.addInstruction(Instruction.STO, new Integer(0));
		}
		
	}
	
	/**
	 * ARRAY ELEMENT ASSIGN STATEMENT
	 */
	public static class ArrayAssign extends Statement {

		public ArrayElement arrElem;
		public Expression exp;
		
		public ArrayAssign(Location l, Location r, ArrayElement ae, Expression e) {
			super(l, r);
			this.arrElem = ae;
			this.exp = e;
		}

		public String toString() {
			return this.arrElem + " = " + this.exp + ";\n";
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			this.arrElem.code_L(coder);
			this.exp.code_R(coder);
			coder.addInstruction(Instruction.STO, new Integer(0));
		}
	}
	
	/**
	 * ARRAY DECLARATION STATEMENT
	 */
	public static class ArrayDeclaration extends Statement {

		// Array declaration goes like:  int[] x = new int[10];
		// So translated into variables: typeL[] arrName = new typeR[exp];
		public int typeL, typeR;
		public Identifier arrName;
		// The (fixed) size of the array
		public int size;
		
		public ArrayDeclaration(Location l, Location r, Identifier id, int t1, int t2, int s) {
			super(l, r);
			this.typeL = t1;
			this.typeR = t2;
			this.arrName = id;
			this.size = s;
		}

		public String toString() {
			return ConstantValues.toString(this.typeL) + "[] " + this.arrName + " = new "
					+ ConstantValues.toString(this.typeR) + "[" + (Integer)this.size + "];\n";
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			// Save space for all its elements
			coder.addToRho(this.arrName.id, this.size);
		}

	}
	
	/**
	 * WHILE STATEMENT
	 */
	public static class While extends Statement {

		// The expression bExp represents the boolean condition of the loop
		public Expression bExp;
		public List<Statement> stmList;

		public While(Location l, Location r, Expression e, List<Statement> sl) {
			super(l, r);
			this.bExp = e;
			this.stmList = sl;
		}

		public String toString() {
			String strVal = "while(" + this.bExp + ") {\n";
			for (Statement s : this.stmList)
				strVal += s;
			strVal += "}\n";
			return strVal;
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			if(this.stmList != null)
				for(Statement s : this.stmList)
					s.check(checker);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			int line = coder.getCurrLine();
			this.bExp.code_R(coder);
			coder.addInstruction(Instruction.FJP, new Integer(0));
			for(Statement s : this.stmList)
				s.code(coder);
			coder.addUJPBack(line);
			coder.popInnerQueue(0);
		}
		
	}
	
	/**
	 * IF STATEMENT (no ELSE branch)
	 */
	public static class If extends Statement {

		// The expression bExp represents the boolean condition
		public Expression bExp;
		public List<Statement> stmList;

		public If(Location l, Location r, Expression e, List<Statement> sl) {
			super(l, r);
			this.bExp = e;
			this.stmList = sl;
		}

		public String toString() {
			String strVal = "if(" + this.bExp + ") {\n";
			for (Statement s : this.stmList)
				strVal += s;
			strVal += "}\n";
			return strVal;
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			if(this.stmList != null)
				for(Statement s : this.stmList)
					s.check(checker);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			this.bExp.code_R(coder);
			coder.addInstruction(Instruction.FJP, new Integer(0));
			for(Statement s : this.stmList)
				s.code(coder);
			coder.popInnerQueue(0);
		}
		
	}
	
	/**
	 * IF-ELSE STATEMENT
	 */
	public static class IfElse extends Statement {

		// The expression bExp represents the boolean condition
		public Expression bExp;
		public List<Statement> stmListIf;
		public List<Statement> stmListElse;

		public IfElse(Location l, Location r, Expression e, List<Statement> sl1, List<Statement> sl2) {
			super(l, r);
			this.bExp = e;
			this.stmListIf = sl1;
			this.stmListElse = sl2;
		}

		public String toString() {
			String strVal = "if(" + this.bExp + ") {\n";
			for (Statement s : this.stmListIf)
				strVal += s;
			strVal += "} else {\n";
			for (Statement s : this.stmListElse)
				strVal += s;
			strVal += "}\n";
			return strVal;
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			if(this.stmListIf != null)
				for(Statement s : this.stmListIf)
					s.check(checker);
			// Before going to the else branch we must close and open blocks
			checker.idManager.closeBlock();
			checker.idManager.openBlock();
			if(this.stmListElse != null)
				for(Statement s : this.stmListElse)
					s.check(checker);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			this.bExp.code_R(coder);
			coder.addInstruction(Instruction.FJP, new Integer(0));
			for(Statement s : this.stmListIf)
				s.code(coder);
			coder.addInstruction(Instruction.UJP, new Integer(0));
			coder.popInnerQueue(0);
			for(Statement s : this.stmListElse)
				s.code(coder);
			coder.popInnerQueue(0);
		}
		
	}
	
	/**
	 * FOR STATEMENT
	 */
	public static class For extends Statement {

		// The idea is: for(ass1; bExp; ass2) {...}
		// (where ass1 would be sth like i = 0 and ass2 sth like i = i+1)
		public Assign ass1;
		// The expression exp represents the boolean condition
		public Expression bExp;
		public Assign ass2;
		public List<Statement> stmList;
		
		public For(Location l, Location r, Assign a1, Expression e, Assign a2, List<Statement> sl) {
			super(l, r);
			this.ass1 = a1;
			this.bExp = e;
			this.ass2 = a2;
			this.stmList = sl;
		}

		public String toString() {
			String strVal = "for(" + this.ass1 + "\b" + this.bExp + ";" + this.ass2 + "\b\b) {\n";
			for (Statement s : this.stmList)
				strVal += s;
			strVal += "}\n";
			return strVal;
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			ass1.check(checker);
			ass2.check(checker);
			if(this.stmList != null)
				for(Statement s : this.stmList)
					s.check(checker);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			this.ass1.code(coder);
			int line = coder.getCurrLine();
			this.bExp.code_R(coder);
			coder.addInstruction(Instruction.FJP, new Integer(0));
			for(Statement s : this.stmList)
				s.code(coder);
			this.ass2.code(coder);
			coder.addUJPBack(line);
			coder.popInnerQueue(0);
		}
		
	}
	
	/**
	 * SWITCH STATEMENT (inside the switch, each case)
	 */
	public static class SwitchStatement extends Statement {

		// The idea is: case(value) {...}
		// The integer "value" represents the value of each certain case
		public int value;
		public List<Statement> stmList;
		
		public SwitchStatement(Location l, Location r, int n, List<Statement> sl) {
			super(l, r);
			this.value = n;
			this.stmList = sl;
		}

		public String toString() {
			String strVal = "case(" + (Integer)this.value + ") {\n";
			for (Statement s : this.stmList)
				strVal += s;
			strVal += "}\n";
			return strVal;
		}
		
		public void check(IdAndTypeChecker checker, int val) throws Errors{
			checker.preVisit(this, val);
			if(this.stmList != null)
				for(Statement s : this.stmList)
					s.check(checker);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			for (Statement s: this.stmList)
				s.code(coder);
			coder.addInstruction(Instruction.UJP, new Integer(0));
		}
		
	}
	
	/**
	 * SWITCH STATEMENT (the whole switch)
	 */
	public static class Switch extends Statement {

		// The idea is: switch(exp) { ...case(exp){...} ...}
		// The expression exp represents the value that gives the case
		public Expression exp;
		public List<SwitchStatement> switchList;
		
		public Switch(Location l, Location r, Expression e, List<SwitchStatement> sl) {
			super(l, r);
			this.exp = e;
			this.switchList = sl;
		}

		public String toString() {
			String strVal = "switch(" + this.exp + ") {\n";
			for (SwitchStatement s : this.switchList)
				strVal += s;
			strVal += "}\n";
			return strVal;
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			if(this.switchList != null)
				for(int i = 0; i < this.switchList.size(); i++)
					this.switchList.get(i).check(checker, i);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			this.exp.code_R(coder);
			coder.addInstruction(Instruction.NEG, new Integer(0));
			coder.addInstruction(Instruction.IXJ, new Integer(0));
			Stack<Integer> lines = new Stack<Integer>();
			for (SwitchStatement ss : this.switchList) {
				lines.push(coder.getCurrLine());
				ss.code(coder);
			}
			while (lines.size() != 1)
				coder.addUJPBack(lines.pop().intValue());
			// These popInnerQueue() is for adding the UJP jump label of each case
			for (SwitchStatement ss : this.switchList)
				coder.popInnerQueue(1);
			// This popInnerQueue() is for adding the IXJ jump label
			coder.popInnerQueue(0);
			coder.addUJPBack(lines.pop().intValue());
		}
		
	}
	
	/**
	 * CALL TO VOID METHOD STATEMENT
	 */
	public static class VoidMethCall extends Statement {

		public Identifier object;
		public Identifier method;
		public List<Expression> expList;
		
		public static VoidMethCall CallWithParams(Location l, Location r, Identifier o, Identifier m, List<Expression> el){
			return new VoidMethCall(l, r, o, m, el);
		}
		
		public static VoidMethCall CallNoParams(Location l, Location r, Identifier o, Identifier m){
			return new VoidMethCall(l, r, o, m, null);
		}
		
		public VoidMethCall(Location l, Location r, Identifier o, Identifier m, List<Expression> el) {
			super(l, r);
			this.object = o;
			this.method = m;
			this.expList = el;
		}

		public String toString() {
			String strVal = this.object + "." + this.method + "(";
			if (this.expList != null) {
				for (int i = 0; i < this.expList.size() - 1; i++) {
					strVal = strVal + this.expList.get(i) + ", ";
				}
				strVal += this.expList.get(this.expList.size() - 1) + ");\n";
			} else
				strVal += ");\n";
			return strVal;
		}
		
		public void check(IdAndTypeChecker checker) throws Errors{
			checker.preVisit(this);
			checker.postVisit(this);
		}
		
		public void code(CodeManager coder) {
			// MST (nesting depth proc call) - (nesting depth proc decl)
			// but note that the nesting depth of the procedure declaration is 0
			int value = coder.getDepth();
			coder.incDepth();
			coder.addInstruction(Instruction.MST, value);
			int cupSize = 0;
			// We pass the object by reference:
			// obj.meth(e_1, ..., e_n) <-> meth(obj, e_1, ..., e_n)
			this.object.code_L(coder);
			// We add to cupSize the size of the object
			cupSize += coder.getAttrCount(coder.getObjectClass(this.object.id));
			// And now we add to cupSize the size of each parameter (each of them is basic)
			for (Expression e : this.expList) {
				cupSize++;
				e.code_R(coder);
			}
			coder.addCUPInstruction(Instruction.CUP, cupSize, coder.getMethLine(this.method.id));
		}
		
	}
	
}
