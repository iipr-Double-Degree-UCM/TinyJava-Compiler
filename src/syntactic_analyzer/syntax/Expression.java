package syntactic_analyzer.syntax;

import java.util.List;

import code_generator.CodeManager;
import code_generator.Instruction;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Expression {

	public Location locleft;
	public Location locright;
	public boolean isRhs = false;
	
	public Expression(Location l, Location r){
		this.locleft = l;
		this.locright = r;
	}
	
	// For checking if the id has been initialized
	public void setRhs(boolean val) {
		this.isRhs = val;
	}
	
	public void code_R(CodeManager coder) { }
	
	public void code_L(CodeManager coder) { }
	
	/**
	 * BINARY EXPRESSION
	 */
	public static class BinExpression extends Expression {

		public Expression lhs, rhs;
		public int binOp;
		
		public BinExpression(Location l, Location r, Expression e1, int op, Expression e2) {
			super(l, r);
			this.lhs = e1;
			this.rhs = e2;
			this.binOp = op;
			this.lhs.setRhs(this.isRhs);
			this.rhs.setRhs(this.isRhs);
		}

		public String toString() {
			return this.lhs.toString() + " " + ConstantValues.toString(this.binOp) + " " + this.rhs.toString();
		}
		
		public void code_R(CodeManager coder) { 
			this.lhs.code_R(coder);
			this.rhs.code_R(coder);
			coder.addInstruction(this.binOp, 0);
		}
		
	}
	
	/**
	 * UNARY EXPRESSION
	 */
	public static class UnaryExpression extends Expression {

		public Expression exp;
		public int unOp;
		
		public UnaryExpression(Location l, Location r, Expression e, int op) {
			super(l, r);
			this.exp = e;
			this.unOp = op;
			this.exp.setRhs(this.isRhs);
		}

		public String toString() {
			return ConstantValues.toString(this.unOp) + this.exp.toString();
		}
		
		public void code_R(CodeManager coder) { 
			this.exp.code_R(coder);
			if (this.unOp == ConstantValues.SUBS)
				coder.addInstruction(Instruction.NEG, 0);
			else if (this.unOp == ConstantValues.NEG)
				coder.addInstruction(Instruction.NOT, 0);
			// else, this.unOp == ConstantValues.ADD => Do nothing
		}
	}
	
	/**
	 * IDENTIFIER EXPRESSION
	 */
	public static class Identifier extends Expression {

		public String id;
		public boolean isInit = false;
		
		public Identifier(Location l, Location r, String i) {
			super(l, r);
			this.id = i;
		}

		public String toString() { 
			return this.id;
		}	
		
		public void code_L(CodeManager coder) {
			coder.addInstruction(Instruction.LDC, coder.rho(this.id));
		}
		
		public void code_R(CodeManager coder) {
			this.code_L(coder);
			coder.addInstruction(Instruction.IND, 0);
		}

	}

	/**
	 * BOOLEAN VALUE EXPRESSION
	 */
	public static class BoolValue extends Expression {

		public boolean value;
		
		public BoolValue(Location l, Location r, boolean b) {
			super(l, r);
			this.value = b;
		}

		public String toString() {
			if(this.value)
				return "true";
			else 
				return "false";
		}
		
		public void code_R(CodeManager coder) {  
			coder.addInstruction(Instruction.LDC, new Boolean(this.value));
		}
	}
	
	/**
	 * CHAR VALUE EXPRESSION
	 */
	public static class CharValue extends Expression {

		public char value;
		
		public CharValue(Location l, Location r, char c) {
			super(l, r);
			this.value = c;
		}

		public String toString() { 
			return this.value + "";
		}
		
		public void code_R(CodeManager coder) {
			coder.addInstruction(Instruction.LDC, new Character(this.value));
		}
	}
	
	/**
	 * STRING VALUE EXPRESSION
	 */
	public static class StringValue extends Expression {

		public String value;
		
		public StringValue(Location l, Location r, String s) {
			super(l, r);
			this.value = s;
		}

		public String toString() { 
			return this.value;
		}
		
		public void code_R(CodeManager coder) {  
			coder.addInstruction(Instruction.LDC, this.value);
		}
	}
	
	/**
	 * INTEGER VALUE EXPRESSION
	 */
	public static class IntegerValue extends Expression {

		public int value;
		
		public IntegerValue(Location l, Location r, int i) {
			super(l, r);
			this.value = i;
		}

		public String toString() { 
			return String.valueOf(this.value);
		}
		
		public void code_R(CodeManager coder) {  
			coder.addInstruction(Instruction.LDC, new Integer(this.value));
		}
	}
	
	/**
	 * DOUBLE VALUE EXPRESSION
	 */
	public static class DoubleValue extends Expression {

		public double value;
		
		public DoubleValue(Location l, Location r, double d) {
			super(l, r);
			this.value = d;
		}

		public String toString() { 
			return String.valueOf(this.value);
		}
		
		public void code_R(CodeManager coder) {  
			coder.addInstruction(Instruction.LDC, new Double(value));
		}
	}
	
	/**
	 * ATTRIBUTE OF AN OBJECT EXPRESSION
	 */
	public static class AttribObject extends Expression {

		public Identifier obj, attrib;
		
		public AttribObject(Location l, Location r, Identifier i, Identifier el) {
			super(l, r);
			this.obj = i;
			this.attrib = el;
		}

		public Identifier getObject(){
			return this.obj;
		}
		
		public Identifier getAttribute(){
			return this.attrib;
		}
		
		public String toString() { 
			return this.obj + "." + this.attrib;
		}
		
		public void code_L(CodeManager coder) {
			String myClass = coder.getObjectClass(this.obj.id);
			Integer offset = coder.getAttrOffset(myClass, this.attrib.id);
			coder.addInstruction(Instruction.LDC, offset);
			coder.addInstruction(Instruction.INC, new Integer(coder.rho(this.obj.id)));
		}
		
		public void code_R(CodeManager coder) {
			this.code_L(coder);
			coder.addInstruction(Instruction.IND, 0);
		}
	}
	
	/**
	 * ELEMENT OF AN ARRAY EXPRESSION
	 */
	public static class ArrayElement extends Expression {

		public Identifier arrName;
		public Expression index;
		
		public ArrayElement(Location l, Location r, Identifier a, Expression i) {
			super(l, r);
			this.arrName = a;
			this.index = i;
			this.index.setRhs(this.isRhs);
		}

		public String toString() {
			return this.arrName + "[" + this.index + "]";
		}
		
		/** NOTE:
		 * Since the arrays in our programming language are only 1-dimensional
		 * we can implement the code_L and code_R methods as follows.
		 * First we do STORE[SP] := index, and then STORE[SP] := STORE[SP] + rho(arrName),
		 * which leaves the address of the array element on top of STORE.
		 */
		public void code_L(CodeManager coder) {
			index.code_R(coder);
			coder.addInstruction(Instruction.INC, new Integer(coder.rho(this.arrName.id)));
		}
		
		public void code_R(CodeManager coder) {
			this.code_L(coder);
			coder.addInstruction(Instruction.IND, 0);
		}
	}
	
	/**
	 * METHOD CALL EXPRESSION (a method that returns a value)
	 */
	public static class MethodCall extends Expression {

		public Identifier object;
		public Identifier method;
		public List<Expression> expList;
		
		public static MethodCall CallWithParams(Location l, Location r, Identifier o, Identifier m, List<Expression> el){
			return new MethodCall(l, r, o, m, el);
		}
		
		public static MethodCall CallNoParams(Location l, Location r, Identifier o, Identifier m){
			return new MethodCall(l, r, o, m, null);
		}
		
		public MethodCall(Location l, Location r, Identifier o, Identifier m, List<Expression> el) {
			super(l, r);
			this.object = o;
			this.method = m;
			this.expList = el;
		}

		public String toString() {
			String strVal = this.object + "." + this.method + "(";
			if (this.expList != null) {
				for (int i = 0; i < this.expList.size() - 1; i++) {
					strVal += this.expList.get(i) + ", ";
				}
				strVal += this.expList.get(this.expList.size() - 1) + ")";
			} else
				strVal += ")";
			return strVal;
		}
		
		public void code_R(CodeManager coder) {
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
			// And now we add to cupSize the size of each parameter (each of
			// them is basic)
			for (Expression e : this.expList) {
				cupSize++;
				e.code_R(coder);
			}
			coder.addCUPInstruction(Instruction.CUP, cupSize, coder.getMethLine(this.method.id));
		}
		
	}
	
}
