package syntactic_analyzer.syntax;

import java.util.*;

import code_generator.CodeManager;
import error_handle.Errors;
import semantic_analyzer.IdAndTypeChecker;
import semantic_analyzer.IdInfo;
import syntactic_analyzer.syntax.Expression.Identifier;

public class MyClass {
	
	private Identifier className;
	private List<Attribute> attrList;
	private List<Method> methList;
	
	public static MyClass buildClass(Identifier cn, List<Attribute> al, List<Method> ml){
		return new MyClass(cn, al, ml);
	}

	public MyClass(Identifier cn, List<Attribute> al, List<Method> ml){
		this.className = cn;
		this.attrList = al;
		this.methList = ml;
		if(this.attrList != null)
			for(Attribute a : this.attrList)
				a.setClass(this.className);
		if(this.methList != null)
			for(Method m : this.methList)
				m.setClass(this.className);
	}
	
	public Identifier getClassName() {
		return this.className;
	}
	
	public List<Attribute> getAttributes() {
		return this.attrList;
	}
	
	public List<Method> getClassMethods() {
		return this.methList;
	}
	
	public String toString(){
		String strVal = "class " + this.className + "{\n\n";
		if(this.attrList != null)
			for(int i = 0; i < this.attrList.size(); i++)
				strVal += this.attrList.get(i);
		strVal += "\n";
		if(this.methList != null)
			for(int i = 0; i < this.methList.size(); i++)
				strVal += this.methList.get(i);
		return strVal + "\n}\n";
	}
	
	public void check(IdAndTypeChecker checker) throws Errors{
		checker.preVisit(this);
		List<String> attNames = new ArrayList<String>();
		for (Attribute a : this.attrList) {
			// Check for unique attribute names
			if (attNames.indexOf(a.getAttrName().id) != -1)
				throw new Errors(Errors.ATTRALREADYDEF, a.getAttrName(), this.getClassName());
			checker.idManager.insertInfo(a.getAttrName().toString(), IdInfo.IdInfoAttrib(a.getIsPublic(), this.className, a.getType(), false));
			attNames.add(a.getAttrName().id);
		}
		if(this.attrList != null)
			for(int i = 0; i < this.attrList.size(); i++)
				this.attrList.get(i).check(checker);		
		
		if (this.methList != null) {
			List<String> methNames = new ArrayList<String>();
			for (Method m : this.methList) {
				// Check for unique method names
				if (methNames.indexOf(m.methName.id) != -1)
					throw new Errors(Errors.METHALREADYDEF, m.getMethName(), this.getClassName());
				IdInfo infMet = IdInfo.IdInfoMeth(m.getIsPublic(), this.className);
				checker.idManager.insertInfo(m.getMethName().toString(), infMet);
				methNames.add(m.getMethName().id);
				// Open a new block and check that the current method is fine
				checker.idManager.openBlock();
				if (!m.paramList.isEmpty()) {
					for (int i = 0; i < m.paramList.size(); i++) {
						// It is checked later, but it is safe to assume here that
						// the parameters are initialized because of the method call
						IdInfo infPar = IdInfo.IdInfoVariable(m.paramList.get(i).getParName(), m.paramList.get(i).getType(), true);
						infPar.init = true;
						checker.idManager.insertInfo(m.paramList.get(i).getParName().toString(), infPar);
						m.paramList.get(i).getParName().isInit = true;
						
					}
					for (Attribute a : this.attrList) {
						checker.idManager.insertInfo(a.getAttrName().toString(), IdInfo.IdInfoAttrib(a.getIsPublic(), this.className, a.getType(), true));
						attNames.add(a.getAttrName().id);
					}
				}
				// Note that here the method can only access the parameters of the call and the attributes of the class
				m.check(checker);
				checker.idManager.closeBlock();
			}
		}		
		checker.postVisit(this);
	}
	
	public void code(CodeManager coder) {
		// This is for accessing to the right attribute one the expression AttribObject
		for (int i = 0; i < this.attrList.size(); i++)
			coder.addAttrOffset(this.className.id, this.attrList.get(i).getAttrName().id, i);
		for (int i = 0; i < this.methList.size(); i++)
			this.methList.get(i).code(coder);
	}
	
}
