package syntactic_analyzer.syntax;

import java.util.List;

import code_generator.CodeManager;
import code_generator.Instruction;
import error_handle.Errors;
import semantic_analyzer.IdAndTypeChecker;
import syntactic_analyzer.syntax.Expression.Identifier;
import syntactic_analyzer.syntax.Statement.ArrayDeclaration;
import syntactic_analyzer.syntax.Statement.ObjectDeclaration;
import syntactic_analyzer.syntax.Statement.VariableDeclaration;

public class Method {

	// Whether the given method is a public (true) or a private (false) one
	protected boolean isPublic;
	// Type of the returned value
	protected int type;
	protected Identifier methName, className;
	protected List<Parameter> paramList;
	protected List<Statement> stmList;
	private Expression retExpression;

	public static Method buildMethod(boolean p, int t, Identifier id, List<Parameter> pl,
			List<Statement> sl, Expression e) {
		return new Method(p, t, id, pl, sl, e);
	}

	public Method(boolean p, int t, Identifier id, List<Parameter> pl,
			List<Statement> sl, Expression e) {
		this.isPublic = p;
		this.type = t;
		this.methName = id;
		this.paramList = pl;
		this.stmList = sl;
		this.retExpression = e;
	}
	
	public Identifier getMethName() {
		return this.methName;
	}
	
	public boolean getIsPublic() {
		return this.isPublic;
	}
	
	public int getType() {
		return this.type;
	}
	
	public Expression getRetExpression() {
		return this.retExpression;
	}
	
	public List<Parameter> getParamList() {
		return this.paramList;
	}

	public String toString() {
		String strVal = ConstantValues.toString(this.type) + " " + this.methName + "(";
		if(!this.paramList.isEmpty()) {
			for (int i = 0; i < this.paramList.size() - 1; i++)
				strVal.concat(this.paramList.get(i) + ", ");
			strVal += this.paramList.get(this.paramList.size() - 1) + ") {\n";
		} else
			strVal += "){\n";
		if(!this.stmList.isEmpty())
			for (Statement s : this.stmList)
				strVal += s;
		strVal = strVal + "return " + this.retExpression + ";\n}\n";
		if (this.isPublic)
			return "public " + strVal;
		else
			return "private " + strVal;
	}

	public void setClass(Identifier cn) {
		this.className = cn;
	}
	
	public void check(IdAndTypeChecker checker) throws Errors{
		checker.preVisit(this);
		if(!this.paramList.isEmpty())
			for (int i = 0; i < this.paramList.size(); i++)
				this.paramList.get(i).check(checker);
		if(!this.stmList.isEmpty())
			for (Statement s : this.stmList)
				s.check(checker);
		checker.postVisit(this);
	}
	
	public void code(CodeManager coder) {
		// This will associate each method to the first line where it starts 
		coder.addMethLine(this.methName.id);
		// Compute the value of l_e <-> sizeOfVariables
		int sizeOfVariables = this.paramList.size();
		for (Statement s : this.stmList) {
			if (s.getClass() == VariableDeclaration.class)
				sizeOfVariables++;
			else if (s.getClass() == ArrayDeclaration.class) {
				ArrayDeclaration ae = (ArrayDeclaration)s;
				sizeOfVariables += ae.size;
			} else if (s.getClass() == ObjectDeclaration.class) {
				ObjectDeclaration oe = (ObjectDeclaration)s;
				sizeOfVariables += coder.getAttrCount(oe.classNameL.id);
			}
		}
		coder.addInstruction(Instruction.SSP, new Integer(sizeOfVariables));
		coder.addInstruction(Instruction.SEP, new Integer(0));
		// Safe parameters into rho
		for (int i = this.paramList.size() - 1; i >= 0; i--) {
			String par = this.paramList.get(i).getParName().id;
			coder.addToRho(par, 1);
			coder.addInstruction(Instruction.SRO, coder.rho(par));
		}
		for (Statement s : this.stmList)
			s.code(coder);
		if (this.retExpression == null)
			coder.addInstruction(Instruction.RETP, new Integer(0));
		else {
			this.retExpression.code_R(coder);
			coder.addInstruction(Instruction.STR, new Integer(0));
			coder.addInstruction(Instruction.RETF, new Integer(0));
		}
		coder.decDepth();
	}
	
}
