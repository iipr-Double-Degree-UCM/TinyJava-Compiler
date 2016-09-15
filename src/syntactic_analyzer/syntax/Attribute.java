package syntactic_analyzer.syntax;

import error_handle.Errors;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantic_analyzer.IdAndTypeChecker;
import syntactic_analyzer.syntax.Expression.Identifier;

public class Attribute {

	// Whether the given attribute is a public (true) or a private (false) one
	private boolean isPublic;
	private int type;
	private Identifier attrName, className;
	private Location locleft, locright;
	
	
	public static Attribute buildAttribute(Location l, Location r, boolean p, int t, Identifier id){
		return new Attribute(l, r, p, t, id);
	}
	
	public Attribute(Location l, Location r, boolean p, int t, Identifier id){
		this.isPublic = p;
		this.type = t;
		this.attrName = id;
		this.locleft = l;
		this.locright = r;
	}
	
	public Identifier getAttrName() {
		return this.attrName;
	}
	
	public boolean getIsPublic() {
		return this.isPublic;
	}
	
	public int getType() {
		return this.type;
	}
	
	public String toString(){
		String strVal = ConstantValues.toString(this.type) + " " + this.attrName + ";\n";
		if(this.isPublic)
			return "public " + strVal;
		else
			return "private " + strVal;
	}

	public void setClass(Identifier cn) {
		this.className = cn;
	}
	
	public void check(IdAndTypeChecker checker) throws Errors{
		checker.preVisit(this);
		checker.postVisit(this);
	}
}
