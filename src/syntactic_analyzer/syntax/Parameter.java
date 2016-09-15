package syntactic_analyzer.syntax;

import error_handle.Errors;
import semantic_analyzer.IdAndTypeChecker;
import syntactic_analyzer.syntax.Expression.Identifier;

public class Parameter {

	private int type;
	private Identifier parName;
	private Identifier methName;
	
	public Parameter(int t, Identifier id){
		this.type = t;
		this.parName = id;
		this.methName = null;
		this.parName.isInit = true;
	} 
	
	public int getType() {
		return this.type;
	}
	
	public Identifier getMethName() {
		return this.methName;
	}

	public Identifier getParName() {
		return this.parName;
	}
	
	public String toString(){
		return ConstantValues.toString(this.type) + " " + this.parName;
	}

	public void check(IdAndTypeChecker checker) throws Errors {
		checker.preVisit(this);
		checker.postVisit(this);
	}
	
}
