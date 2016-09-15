package semantic_analyzer;

import java.util.Stack;
import syntactic_analyzer.syntax.Expression.Identifier;

public class IdManager {

	private Stack<IdInfoTable> stackTab;
	
	public IdManager(){
		this.stackTab = new Stack<IdInfoTable>();
	}
	
	// The first available address is always 5
	public void openBlock() {
		if(this.stackTab.isEmpty())
			this.stackTab.push(new IdInfoTable(5));
		else
			this.stackTab.push(new IdInfoTable(this.stackTab.peek().getAddress()));
	}

	public void closeBlock() {
		this.stackTab.pop();
	}

	public void insertInfo(String id, IdInfo i) {
		if(!this.stackTab.peek().existInTable(id))
			this.stackTab.peek().addToTable(id, i);
	}
	
	public IdInfo getIdInfo(String id) {
		for (int i = this.stackTab.size() - 1; i >= 0; i--)
			if (this.stackTab.get(i).existInTable(id))
				return this.stackTab.get(i).getIdInfo(id);
		return null;
	}
	
}
