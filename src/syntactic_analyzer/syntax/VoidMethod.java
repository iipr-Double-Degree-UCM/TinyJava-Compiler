package syntactic_analyzer.syntax;

import java.util.List;

import error_handle.Errors;
import semantic_analyzer.IdAndTypeChecker;
import syntactic_analyzer.syntax.Expression.Identifier;

public class VoidMethod extends Method {

	public static Method buildVoidMethod(boolean p, Identifier id, List<Parameter> pl, List<Statement> sl) {
		return new VoidMethod(p, id, pl, sl);
	}

	public VoidMethod(boolean p, Identifier id, List<Parameter> pl, List<Statement> sl) {
		super(p, 0, id, pl, sl, null);
	}
	
	@Override
	public String toString() {
		String strVal = "void " + this.methName + "(";
		if (this.paramList.size() > 0) {
			for (int i = 0; i < this.paramList.size() - 1; i++) {
				strVal = strVal + this.paramList.get(i) + ", ";
			}
			strVal += this.paramList.get(this.paramList.size() - 1) + ") {\n";
		} else
			strVal += ") {\n";
		for (Statement s : this.stmList)
			strVal += s;
		strVal += "}\n";
		if (this.isPublic)
			return "public " + strVal;
		else
			return "private " + strVal;
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

}
