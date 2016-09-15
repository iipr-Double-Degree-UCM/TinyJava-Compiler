package code_generator;

import java.util.HashMap;
import java.util.LinkedList;

public class CodeManager {

	private String code;
	private int currLine;
	private int currDepth;
	private HashMap<String, Integer> rhoMap;
	private int nextFreeAddress;
	// Hash-map: Class -> Attribute -> Int(meaning attribute position)
	private HashMap<String, HashMap<String, Integer>> attrOffset;
	// Hash-map: Object -> Class
	private HashMap<String, String> objClass;
	// In this queue of queues we insert the code that is waiting for a jump address
	private LinkedList<LinkedList<String>> pendingCode;
	// Hash-map: Method -> Initial line
	private HashMap<String, Integer> methLine;
	
	public CodeManager() {
		this.code = "";
		this.currLine = 0;
		this.currDepth = 0;
		this.rhoMap = new HashMap<String, Integer>();
		this.nextFreeAddress = 5;
		this.attrOffset = new HashMap<String, HashMap<String, Integer>>();
		this.objClass = new HashMap<String, String>();
		this.pendingCode = new LinkedList<LinkedList<String>>();
		this.methLine = new HashMap<String, Integer>();
	}
	
	public String getCode() {
		return code;
	}
	
	public int rho(String var) {
		Integer ret = this.rhoMap.get(var);
		if (ret == null)
			return -1;
		else
			return ret; 
	}
	
	public void addToRho(String var, int size) {
		this.rhoMap.put(var, this.nextFreeAddress);
		this.nextFreeAddress += size;
	}
	
	public void removeFromRho(String var) {
		this.rhoMap.remove(var);
	}

	public int getDepth() {
		return this.currDepth;
	}
	
	public void incDepth() {
		this.currDepth++;
	}

	public void decDepth() {
		this.currDepth--;
	}
	
	public void addCUPInstruction(int inst, int cupSize, int line) {
		Instruction instruction = new Instruction(inst);
		if (this.pendingCode.size() == 0)
			this.code += "{" + this.currLine + "} " + instruction.toString(cupSize, new Integer(line).toString());
		else
			this.pendingCode.peekLast().add("{" + this.currLine + "} " + instruction.toString(cupSize, new Integer(line).toString()));
		this.currLine++;
	}
	
	public void addInstruction(int inst, Object value) {
		Instruction instruction = new Instruction(inst);
		if (this.pendingCode.size() == 0)
			this.code += "{" + this.currLine + "} " + instruction.toString(this.currDepth, value.toString());
		else
			this.pendingCode.peekLast().add("{" + this.currLine + "} " + instruction.toString(this.currDepth, value.toString()));
		this.currLine++;
		// If it is a jump instruction (different from CUP)
		if (inst == Instruction.UJP || inst == Instruction.FJP || inst == Instruction.IXJ) {	
			LinkedList<String> inner = new LinkedList<String>();
			this.pendingCode.add(inner); 
		}
	}
	
	public void popInnerQueue(int incr) {
		// This cannot/shouldn't happen
		//if (this.pendingCode.size() == 0) { }
		// In this case we can add the line number and the queue's content into code
		if (this.pendingCode.size() == 1 || (this.pendingCode.size() == 2 && this.pendingCode.peekLast().isEmpty())) {
			incr += this.currLine;
			this.code += incr + ";\n";
			LinkedList<String> inner = this.pendingCode.remove();
			while (!inner.isEmpty())
				this.code += inner.remove();
		}
		// In this case there we can't add it yet to the final code
		else { // this.pendingCode.size() > 1
			boolean activate = false;
			if (this.pendingCode.peekLast().isEmpty()) {
				activate = true;
				this.pendingCode.removeLast();
			}
			LinkedList<String> inner = this.pendingCode.removeLast();
			incr += this.currLine;
			this.pendingCode.peekLast().add(incr + ";\n");
			while (!inner.isEmpty())
				this.pendingCode.peekLast().add(inner.remove());
			if (activate) {
				LinkedList<String> innerAux = new LinkedList<String>();
				this.pendingCode.add(innerAux); 
			} 
		}
	}
	
	public void addUJPBack(int line) {
		if (this.pendingCode.size() == 0)
			this.code += "{" + this.currLine + "} ujp " + line + ";\n";
		else
			this.pendingCode.peekLast().add("{" + this.currLine + "} ujp " + line + ";\n");
		this.currLine++;
	}
	
	public void addObjectClass(String obj, String myClass) {
		this.objClass.put(obj, myClass);
	}
	
	public String getObjectClass(String obj) {
		return this.objClass.get(obj);
	}
	
	public void addAttrOffset(String myClass, String attr, int offset) {
		HashMap<String, Integer> inner = this.attrOffset.get(myClass);
		if (inner == null) {
			inner = new HashMap<String, Integer>();
			inner.put(attr, offset);
			this.attrOffset.put(myClass, inner);
		} else {
			inner.put(attr, offset);
		}
	}
	
	public Integer getAttrOffset(String myClass, String attr) {
		HashMap<String, Integer> inner = this.attrOffset.get(myClass);
		if (inner == null)
			return new Integer(0);
		else
			return inner.get(attr);
	}
	
	public Integer getAttrCount(String myClass) {
		HashMap<String, Integer> inner = this.attrOffset.get(myClass);
		if (inner == null)
			return new Integer(0);
		else
			return new Integer(inner.size());
	}
	
	public int getCurrLine() {
		return this.currLine;
	}
	
	public void addMethLine(String methName) {
		this.methLine.put(methName, this.currLine);
	}
	
	public int getMethLine(String methName) {
		return this.methLine.get(methName);
	}

	public int numMeths() {
		return this.methLine.size();
	}

}
