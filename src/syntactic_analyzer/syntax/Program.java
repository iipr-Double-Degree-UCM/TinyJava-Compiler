package syntactic_analyzer.syntax;

import java.util.*;

import code_generator.CodeManager;
import code_generator.Instruction;
import error_handle.Errors;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantic_analyzer.IdAndTypeChecker;
import syntactic_analyzer.syntax.Expression.Identifier;
import syntactic_analyzer.syntax.Statement.ArrayDeclaration;
import syntactic_analyzer.syntax.Statement.ObjectDeclaration;
import syntactic_analyzer.syntax.Statement.VariableDeclaration;

public class Program {
	
	private List<MyClass> classList;
	private List<Statement> stmList;
	
	public static Program buildProgram(List<MyClass> cl, List<Statement> sl){
		return new Program(cl, sl);
	}
	
	public Program(List<MyClass> cl, List<Statement> sl){
		this.classList = cl;
		this.stmList = sl;
	}
	
	public String toString() {
		String strVal = "";
		for(MyClass c : this.classList)
			strVal += c;
		strVal += "\nmain{\n";
		for(Statement s : this.stmList)
			strVal += s;
		strVal += "}";
		return strVal;
	}
	
	public void check(IdAndTypeChecker checker) throws Errors {
		checker.preVisit(this);
		if(this.classList != null)
			for(MyClass c : this.classList)
				c.check(checker);
		checker.currScope.push("MAIN");
		if(this.stmList != null)
			for(Statement s : this.stmList)
				s.check(checker);
		checker.postVisit(this);
	}

	public void code(CodeManager coder) {
		coder.addInstruction(Instruction.UJP, new Integer(0));
		int sizeOfVariables = 0;
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
		if(this.classList != null)
			for(MyClass c : this.classList)
				c.code(coder);
		// Now we can add the label of the previous UJP jump to main
		coder.popInnerQueue(0);
		for (int i = 0; i < coder.numMeths(); i++)
			coder.incDepth();
		if(this.stmList != null)
			for(Statement s : this.stmList)
				s.code(coder);
		coder.addInstruction(Instruction.STP, new Integer(0));
	}

}
