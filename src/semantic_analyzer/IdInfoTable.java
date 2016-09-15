package semantic_analyzer;

import java.util.HashMap;
import syntactic_analyzer.syntax.Expression.Identifier;

public class IdInfoTable {

	private HashMap<String, IdInfo> idTable;
	// First free address
	private int address;
	
	public IdInfoTable(int a){
		this.idTable = new HashMap<String, IdInfo>();
		this.address = a;
	}

	public boolean existInTable(String id) {
		return this.idTable.containsKey(id);
	}

	public void addToTable(String id, IdInfo i) {
		this.idTable.put(id, i);
	}

	public void updateInfo(String id, IdInfo i) {
		this.idTable.replace(id, i);
	}

	public IdInfo getIdInfo(String id) {
		return this.idTable.get(id);
	}
	
	public int incAddress(int inc) {
		int aux = this.address;
		this.address = this.address + inc;
		return aux;
	}

	public int getAddress() {
		return this.address;
	}

}
