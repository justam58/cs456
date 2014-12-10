package command;

import java.util.ArrayList;

import sparkClass.Root;
import able.Command;

public class SetCommand implements Command {
	
	private String valueToSet;
	private ArrayList<String> path;
	private Root root;
	private String previousValue = null;
	
	public SetCommand(Root root, ArrayList<String> path, String valueToSet){
		this.root = root;
		this.path = path;
		this.valueToSet = valueToSet;
	}

	@Override
	public void redo() {
		if(previousValue == null){
			previousValue = root.getModelValue(path, root.model, 0);
		}
		root.updateModel(path, root.model, 0, valueToSet);
	}

	@Override
	public void undo() {
		root.updateModel(path, root.model, 0, previousValue);
	}
	
	public void setPreviousValue(String pre){
		previousValue = pre;
	}

	@Override
	public void doIt() {
		redo();
	}

}
