package semantic_analyzer;

import java.util.List;
import java.util.Stack;

import error_handle.Errors;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantic_analyzer.IdInfo.TypeOfId;
import syntactic_analyzer.syntax.*;
import syntactic_analyzer.syntax.Expression.*;
import syntactic_analyzer.syntax.Statement.*;

public class IdAndTypeChecker {

	public IdManager idManager;
	/* 
	 * This stack "currScope" keeps track of where we are for privacy purposes:
	 * Empty stack means we are parsing class definitions (at the begining of the file).
	 * If the size of the stack is 1, we are in the main and the peek is "MAIN".
	 * If the size is >1 each String on the stack represents a class name.
	 */
	public Stack<String> currScope;
	
	public IdAndTypeChecker(){
		this.idManager = new IdManager();
		this.currScope = new Stack<String>();
	}
	
	public int getType(Expression exp) throws Errors {
		//----------IDENTIFIER EXPRESSIONS----------//
		if (exp.getClass() == Identifier.class) {
			Identifier id = (Identifier) exp;
			IdInfo inf = this.idManager.getIdInfo(id.toString());
			if (inf == null)
				throw new Errors(Errors.IDNOTDECL, id);
			if (id.isRhs == true && inf.init == false)
				throw new Errors(Errors.NOTINITVAR, id);
			if (!inf.isPublic && inf.typeOfId == TypeOfId.ATTRIBUTE && this.currScope.peek() != inf.className.id.toString())
				throw new Errors(Errors.PRIVATTRIB, id);
			if (!inf.isPublic && inf.typeOfId == TypeOfId.METHOD && this.currScope.peek() != inf.className.id.toString())
				throw new Errors(Errors.PRIVMETHOD, id);
			return inf.type;
		} //----------BINARY EXPRESSIONS----------//
		else if (exp.getClass() == BinExpression.class) {
			BinExpression bExp = (BinExpression) exp;
			bExp.lhs.setRhs(true);
			bExp.rhs.setRhs(true);
			int lhsType = getType(bExp.lhs);
			int rhsType = getType(bExp.rhs);
			bExp.lhs.setRhs(false);
			bExp.rhs.setRhs(false);
			int operator = bExp.binOp;
			// Check that the lhs type is the same of the rhs
			if (lhsType != rhsType)
				throw new Errors(Errors.NONCOMPTYPES, bExp.lhs, bExp.rhs);
			// Check that booleans go with OR or AND operators
			if (operator == ConstantValues.OR || operator == ConstantValues.AND) {
				if (lhsType == ConstantValues.BOOLKW)
					return lhsType;
				else
					throw new Errors(Errors.BINARYINCONG, bExp.lhs, bExp.rhs);
			}
			// Check that only ints or doubles go with == and != operators
			if (operator == ConstantValues.EQ || operator == ConstantValues.DIST) {
				if (lhsType == ConstantValues.INTKW || lhsType == ConstantValues.DOUBLEKW)
					return ConstantValues.BOOLKW;
				else 
					throw new Errors(Errors.EQDIST, bExp);
			}
			// Check that the ints and doubles go with the LE, GE, LT, GT operators
			if (operator == ConstantValues.LT || operator == ConstantValues.GT 
					|| operator == ConstantValues.LE || operator == ConstantValues.GE) {
				if(lhsType == ConstantValues.INTKW || lhsType == ConstantValues.DOUBLEKW)
					return ConstantValues.BOOLKW;
				else
					throw new Errors(Errors.BINARYINCONG, bExp);
			}
			// Check that the ints and doubles go with the ADD, SUBS, MULT and DIV operators
			if (operator == ConstantValues.ADD || operator == ConstantValues.SUBS
					|| operator == ConstantValues.MULT || operator == ConstantValues.DIV){
				if(lhsType == ConstantValues.INTKW || lhsType == ConstantValues.DOUBLEKW)
					return lhsType;
				else
					throw new Errors(Errors.BINARYINCONG, bExp);	
			}
			// Check that only ints are use for the MOD operation
			if (operator == ConstantValues.MOD && lhsType != ConstantValues.INTKW)
				throw new Errors(Errors.BINARYINCONG, bExp.lhs, bExp.rhs);
			// If we reach this point, something is wrong
			return -1;
		} //----------UNARY EXPRESSIONS----------//
		else if (exp.getClass() == UnaryExpression.class) {
			UnaryExpression unExp = (UnaryExpression) exp;
			unExp.exp.setRhs(true);
			int type = getType(unExp.exp);
			unExp.exp.setRhs(false);
			int operator = unExp.unOp;
			// Check that the type is bool for negation
			if (operator == ConstantValues.NEG) {
				if(type == ConstantValues.BOOLKW)
					return type;
				else
					throw new Errors(Errors.UNARYINCONG, unExp);
			}
			// Check that the type is int or double if the operator is + or -
			if (operator == ConstantValues.ADD || operator == ConstantValues.SUBS) {
				if (type == ConstantValues.INTKW || type == ConstantValues.DOUBLEKW)
					return type;
				else
					throw new Errors(Errors.UNARYINCONG, unExp);
			}
			// If we reach this point there is some error
			throw new Errors(Errors.UNKNOWNTYPE, exp);
		} //----------BOOLEAN EXPRESSIONS----------//
		else if (exp.getClass() == BoolValue.class) {
			return ConstantValues.BOOLKW;
		} //----------CHAR EXPRESSIONS----------//
		else if (exp.getClass() == CharValue.class) {
			return ConstantValues.CHARKW;
		} //----------STRING EXPRESSIONS----------//
		else if (exp.getClass() == StringValue.class) {
			return ConstantValues.STRINGKW;
		} //----------INTEGER EXPRESSIONS----------//
		else if (exp.getClass() == IntegerValue.class) {
			return ConstantValues.INTKW;
		} //----------DOUBLE EXPRESSIONS----------//
		else if (exp.getClass() == DoubleValue.class) {
			return ConstantValues.DOUBLEKW;
		} //----------ATTRIBUTE OF AN OBJECT EXPRESSIONS----------//
		else if (exp.getClass() == AttribObject.class) {
			AttribObject attObj = (AttribObject) exp;
			IdInfo inf = this.idManager.getIdInfo(attObj.attrib.toString());
			return inf.type;
		} //----------ELEMENT OF AN ARRAY EXPRESSIONS----------//
		else if (exp.getClass() == ArrayElement.class) {
			ArrayElement arrElem = (ArrayElement) exp;
			IdInfo inf = this.idManager.getIdInfo(arrElem.arrName.toString());
			return inf.type;
		} //----------CALL TO A METHOD EXPRESSIONS----------//
		else if (exp.getClass() == MethodCall.class) {
			MethodCall v = (MethodCall) exp;
			IdInfo infObj = this.idManager.getIdInfo(v.object.toString()); 
			// Check if the object has been declared already
			if (infObj == null)
				throw new Errors(Errors.OBJNONDECL, v.object);
			IdInfo inf = this.idManager.getIdInfo(v.method.id);
			IdInfo infClass = this.idManager.getIdInfo(infObj.className.toString());
			// Check that the method exists and that it is indeed a method
			if (inf == null || (inf != null && inf.typeOfId != TypeOfId.METHOD))
				throw new Errors(Errors.NOSUCHMETHOD, v.method);
			if (!inf.isPublic && this.currScope.peek() != inf.className.id.toString())
				throw new Errors(Errors.PRIVMETHOD, v.method);
			Method meth = null;
			for (Method m : infClass.methList)
				if (m.getMethName().toString().equals(v.method.toString()))
					meth = m;
			if (v.expList != null) {	
				List<Parameter> pList = meth.getParamList();
				if (v.expList.size() != pList.size())
					throw new Errors(Errors.PARAMLISTSIZE, v.method);
				for (int i = 0; i < v.expList.size(); i++)
					if (getType(v.expList.get(i)) != pList.get(i).getType())
						throw new Errors(Errors.PARAMTYPEERROR, v.expList.get(i));
			}
			return meth.getType();
		} //----------UNKNOWN TYPE----------//
		else
			throw new Errors(Errors.UNKNOWNTYPE, exp);
	}
	
	/**
	 * 
	 * PRE AND POST VISITS FOR SYNTAX ELEMENTS
	 * 
	 */
	
	public void preVisit(Program a) throws Errors {
		this.idManager.openBlock();		
	}

	public void postVisit(Program a) throws Errors {
		this.idManager.closeBlock();
	}
	
	public void preVisit(MyClass c) throws Errors {
		// Push class name in current scope for privacy purposes
		this.currScope.push(c.getClassName().id);
		// Check if the class has been defined already
		// At this point idManager.stackTable.size() == 1
		if (this.idManager.getIdInfo(c.getClassName().toString()) != null)
			throw new Errors(Errors.CLASSALREADYDEC, c.getClassName());
		IdInfo inf = IdInfo.IdInfoMyClass(c.getClassName());
		inf.setMethods(c.getClassMethods());
		this.idManager.insertInfo(c.getClassName().toString(), inf);
	}
	
	public void postVisit(MyClass c) throws Errors {
		this.currScope.pop();
	}
	
	public void preVisit(Attribute a) throws Errors { }

	public void postVisit(Attribute a) throws Errors { }

	public void preVisit(Constructor c) throws Errors { }
	
	public void postVisit(Constructor c) throws Errors { }
	
	public void preVisit(Method m) throws Errors {
		if (m.getRetExpression() != null && this.getType(m.getRetExpression()) != m.getType())
				throw new Errors(Errors.RETVALERROR, m.getRetExpression());
	}
	
	public void postVisit(Method m) throws Errors { }
	
	public void preVisit(Parameter p) throws Errors { }

	public void postVisit(Parameter p) throws Errors { }
	
	public void preVisit(VoidMethod m) throws Errors { }
	
	public void postVisit(VoidMethod m) throws Errors { }
	
	/**
	 * 
	 * PRE AND POST VISITS FOR STATEMENTS
	 * 
	 */
	
	public void preVisit(ObjectDeclaration o) throws Errors {
		// Check if the object has been declared already
		if(this.idManager.getIdInfo(o.objName.toString()) != null)
			 throw new Errors(Errors.OBJALREADYDEC, o.objName);
		// This checks that the name of the constructor matches the name of the class
		if(!o.classNameL.toString().equals(o.classNameR.toString())) {
			String str = "The class name on the left " + o.classNameL.toString() + 
				" doesn't match with the constructor name on the right " + o.classNameL.toString() +  
				" of the declaration of the object " + o.objName + ".";
			throw new Errors(Errors.ARRAYDECLTYPE, str);
		}
		// Check that the class exists
		IdInfo inf = this.idManager.getIdInfo(o.classNameL.toString());
		if (inf == null)
			throw new Errors(Errors.NOSUCHCLASS, o.classNameL);
		if(o.expList != null) {
			List<Parameter> pList = inf.methList.get(0).getParamList();
			if (o.expList.size() != pList.size())
				throw new Errors(Errors.PARAMLISTSIZE, o.classNameL);
			for(int i = 0; i < o.expList.size(); i++) {
				if(getType(o.expList.get(i)) != pList.get(i).getType())
					throw new Errors(Errors.PARAMTYPEERROR, o.expList.get(i));
			}
		}
	}
	
	public void postVisit(ObjectDeclaration o) throws Errors {
		IdInfo inf = IdInfo.IdInfoObject(o.classNameL);
		this.idManager.insertInfo(o.objName.toString(), inf);
	}

	public void preVisit(VariableDeclaration v) throws Errors {
		// Check if it has already been declared
		// If it was already declared but as an attribute of an object it is ok
		IdInfo inf = this.idManager.getIdInfo(v.varName.toString());
		if (inf != null && inf.typeOfId != TypeOfId.ATTRIBUTE)
			throw new Errors(Errors.VARALREADYDEC, v.varName);
		if (inf != null)
			throw new Errors(Errors.VARALREADYDECASATTRIB, v.varName);
		if (v.exp != null) {
			// For the case of a declaration with assignation, check types
			v.exp.setRhs(true);
			if (getType(v.exp) != v.type)
				throw new Errors(Errors.VARDECL, v.exp, v.varName);
			v.exp.setRhs(false);
		}
	}

	public void postVisit(VariableDeclaration v) throws Errors {
		// Check if it has been initialized
		IdInfo inf;
		if (v.exp != null) {
			inf = IdInfo.IdInfoVariable(v.varName, v.type, true);
			v.varName.isInit = true;
		} else {
			inf = IdInfo.IdInfoVariable(v.varName, v.type, false);
			v.varName.isInit = false;
		} 
		this.idManager.insertInfo(v.varName.toString(), inf);
	}

	public void preVisit(Assign a) throws Errors {
		// Check if the variable has been declared
		if (this.idManager.getIdInfo(a.varName.toString()) == null)
			throw new Errors(Errors.VARNONDEC, a.varName);
		// Check that it is indeed a basic variable (or an attribute)
		IdInfo inf = this.idManager.getIdInfo(a.varName.toString());
		if (inf.typeOfId != TypeOfId.VARIABLE && inf.typeOfId != TypeOfId.ATTRIBUTE)
			throw new Errors(Errors.NOTBASIC, a.varName);
		// The only case when there is an attribute on the lhs of an assign is inside its own class
		if (inf.typeOfId == TypeOfId.ATTRIBUTE && !this.currScope.empty() 
				&& this.currScope.peek() != inf.className.toString())
			throw new Errors(Errors.ASSIGNATTRIB, a.varName, inf.className);
		// Check that the types matches
		a.exp.setRhs(true);
		if (getType(a.varName) != getType(a.exp))
			throw new Errors(Errors.ASSIGNTYPE, a.varName, a.exp);
		a.exp.setRhs(false);
	}

	public void postVisit(Assign a) throws Errors {
		// Now, it is initialized
		a.varName.isInit = true;
		// Also update with this info the IdInfo associated
		IdInfo inf = this.idManager.getIdInfo(a.varName.toString());
		inf.init = true;
	}
	
	public void preVisit(AttribObjectAssign a) throws Errors {
		// Check if the object has been declared
		if (this.idManager.getIdInfo(a.attrObj.obj.toString()) == null)
			throw new Errors(Errors.OBJNONDECL, a.attrObj.obj);
		// Check that it is indeed an attribute
		IdInfo inf = this.idManager.getIdInfo(a.attrObj.attrib.toString());
		if (inf == null || (inf != null && inf.typeOfId != TypeOfId.ATTRIBUTE))
			throw new Errors(Errors.NOTATTRIBUTE, a.attrObj.attrib);
		// If we trying to access a private attribute from somewhere we shouldn't => error
		if (!inf.isPublic &&  this.currScope.peek() != inf.className.toString())
			throw new Errors(Errors.PRIVATTRIB, a.attrObj.attrib);
		IdInfo inf1 = this.idManager.getIdInfo(a.attrObj.obj.toString());
		if(!inf.className.toString().equals(inf1.className.toString()))
			throw new Errors(Errors.ATTNOTBELONG, a.attrObj.attrib, a.attrObj.obj);
		// Check that the types match
		if (getType(a.attrObj.attrib) != getType(a.exp))
			throw new Errors(Errors.ATTRIBTYPE, a.attrObj, a.exp);
	}

	public void postVisit(AttribObjectAssign a) throws Errors {
		// Now, it is initialized
		a.attrObj.attrib.isInit = true;
		// Also update with this info the IdInfo associated
		IdInfo inf = this.idManager.getIdInfo(a.attrObj.attrib.toString());
		inf.init = true;
	}

	public void preVisit(ArrayAssign a) throws Errors {
		// Check if the array has been declared
		if(this.idManager.getIdInfo(a.arrElem.arrName.toString()) == null)
			 throw new Errors(Errors.ARRAYNONDEC, a.arrElem.arrName);
		// Check that it is indeed an array
		IdInfo inf = this.idManager.getIdInfo(a.arrElem.arrName.toString());
		if(inf.typeOfId != TypeOfId.ARRAYVAR)
			throw new Errors(Errors.NOTANARRAY, a.arrElem.arrName);
		// Check that the types match
		if(getType(a.arrElem) != getType(a.exp))
			throw new Errors(Errors.ARRAYASSIGNTYPE, a.arrElem, a.exp);
	}

	public void postVisit(ArrayAssign a) throws Errors { }
	
	public void preVisit(ArrayDeclaration a) throws Errors {
		// Check if the array has been declared
		if(this.idManager.getIdInfo(a.arrName.toString()) != null)
			 throw new Errors(Errors.ARRAYALREADYDEC, a.arrName);
		// Check that types match
		if(a.typeL != a.typeR) {
			String str = "The type on the lhs (" + ConstantValues.toString(a.typeL)+ 
				") doesn't match with the type on the rhs (" + ConstantValues.toString(a.typeR)+ 
				") of the declaration of the array " + a.arrName+ ".";
			throw new Errors(Errors.ARRAYDECLTYPE, str);
		}	 
	}

	public void postVisit(ArrayDeclaration a) throws Errors {
		IdInfo inf;
		// Add to id manager
		inf = IdInfo.IdInfoArrayVar(a.arrName, a.size, a.typeL);
		this.idManager.insertInfo(a.arrName.toString(), inf);
		// This could be bad, but would require some time to code it properly...
		a.arrName.isInit = true;
	}

	public void preVisit(While w) throws Errors {
		if(getType(w.bExp) != ConstantValues.BOOLKW)
			 throw new Errors(Errors.BOOLEXPECTED, w.bExp);
		this.idManager.openBlock();
	}

	public void postVisit(While w) throws Errors {
		this.idManager.closeBlock();
	}

	public void preVisit(If i) throws Errors {
		if(getType(i.bExp) != ConstantValues.BOOLKW)
			 throw new Errors(Errors.BOOLEXPECTED, i.bExp);
		this.idManager.openBlock();
	}

	public void postVisit(If i) throws Errors {
		this.idManager.closeBlock();
	}

	public void preVisit(IfElse i) throws Errors {
		if(getType(i.bExp) != ConstantValues.BOOLKW)
			 throw new Errors(Errors.BOOLEXPECTED, i.bExp);
		this.idManager.openBlock();
	}

	public void postVisit(IfElse i) throws Errors {
		this.idManager.closeBlock();
	}

	public void preVisit(For f) throws Errors {
		if(getType(f.bExp) != ConstantValues.BOOLKW)
			 throw new Errors(Errors.BOOLEXPECTED, f.bExp);
		this.idManager.openBlock();
	}

	public void postVisit(For f) throws Errors {
		this.idManager.closeBlock();
	}

	public void preVisit(SwitchStatement s, int v) throws Errors {
		if(s.value != v)
			throw new Errors(Errors.SWITCHNONCONSEC, new IntegerValue(new Location(0,0), new Location(0,0), s.value));
		this.idManager.openBlock();
	}

	public void postVisit(SwitchStatement s) throws Errors {
		this.idManager.closeBlock();
	}

	public void preVisit(Switch s) throws Errors {
		if(getType(s.exp) != ConstantValues.INTKW)
			 throw new Errors(Errors.INTEXPECTED, s.exp);
	}

	public void postVisit(Switch s) throws Errors { }
	
	public void preVisit(VoidMethCall v) throws Errors {	
		// Check if the object has been declared already
		if (this.idManager.getIdInfo(v.object.toString()) == null)
			throw new Errors(Errors.OBJNONDECL, v.object);
		IdInfo inf = this.idManager.getIdInfo(v.method.toString());
		IdInfo infClass = this.idManager.getIdInfo(inf.className.toString());
		// Check that the method exists and that it is indeed a method
		if (inf != null && inf.typeOfId != TypeOfId.METHOD)
			throw new Errors(Errors.NOSUCHMETHOD, v.method);
		if (!inf.isPublic && this.currScope.peek() != inf.className.id.toString())
			throw new Errors(Errors.PRIVMETHOD, v.method);
		if (v.expList != null) {
			Method meth = null;
			for (Method m : infClass.methList)
				if (m.getMethName().toString().equals(v.method.toString()))
					meth = m;
			List<Parameter> pList = meth.getParamList();
			if (v.expList.size() != pList.size())
				throw new Errors(Errors.PARAMLISTSIZE, v.method);
			for (int i = 0; i < v.expList.size(); i++) {
				if (getType(v.expList.get(i)) != pList.get(i).getType())
					throw new Errors(Errors.PARAMTYPEERROR, v.expList.get(i));
			}
		}
	}
	

	public void postVisit(VoidMethCall v) throws Errors { }


}
