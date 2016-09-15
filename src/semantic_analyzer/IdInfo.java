package semantic_analyzer;

import java.util.List;

import syntactic_analyzer.syntax.Expression.Identifier;
import syntactic_analyzer.syntax.Method;

public class IdInfo {
	
	public enum TypeOfId{
		ATTRIBUTE, METHOD, OBJECT, MYCLASS, VARIABLE, ARRAYVAR
	}
	
	// This will be a enum value from TypesOfIds
	public TypeOfId typeOfId;
	// For attributes, methods and void methods
	public boolean isPublic;
	// For attributes, methods and void methods
	public Identifier className;
	// For arrays
	public int size;
	// For basic variables (int, double, char, bool, strings) and attributes
	public int type;
	// For the case of attributes and variables, whether it is initialized or not
	// We don't make sure that arrays are initialized
	public boolean init;
	// For the classes
	public List<Method> methList;
	
	// Constructor
	public IdInfo(TypeOfId typeOfId, boolean p, Identifier cn, int s, int t, boolean i){
		this.typeOfId = typeOfId;
		this.isPublic = p;
		this.className = cn;
		this.size = s;
		this.type = t;
		this.init = i;
	}
	
	// For the information about an attribute
	public static IdInfo IdInfoAttrib(boolean p, Identifier cn, int t, boolean i){
		return new IdInfo(TypeOfId.ATTRIBUTE, p, cn, -1, t, i);
	}	
	
	// For the information about a method
	public static IdInfo IdInfoMeth(boolean p, Identifier cn) {
		return new IdInfo(TypeOfId.METHOD, p, cn, -1, -1, false);
	}

	// For the information about an object
	public static IdInfo IdInfoObject(Identifier cn){
		return new IdInfo(TypeOfId.OBJECT, true, cn, -1, -1, false);
	}	
	
	// For the information about a class
	// Note: For the shake of simplicity, cn is the Identifier of the class
	public static IdInfo IdInfoMyClass(Identifier cn){
		return new IdInfo(TypeOfId.MYCLASS, true, cn, -1, -1, false);
	}	
	
	// For the information about a basic variable
	// Note: For the shake of simplicity, var is the Identifier of the variable
	public static IdInfo IdInfoVariable(Identifier var, int t, boolean i){
		return new IdInfo(TypeOfId.VARIABLE, true, var, -1, t, i);
	}	
	
	// For the information about a basic variable
	// Note: For the shake of simplicity, arr is the Identifier of the array-variable
	public static IdInfo IdInfoArrayVar(Identifier arr, int s, int t){
		return new IdInfo(TypeOfId.ARRAYVAR, true, arr, s, t, false);
	}	
	
	public void setMethods(List<Method> ml) {
		this.methList = ml;
	}

}
