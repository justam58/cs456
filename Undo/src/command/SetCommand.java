package command;

import java.util.ArrayList;

import listener.ModelListener;
import sparkClass.Root;
import able.Command;

public class SetCommand implements Command {
	
	private String valueToSet;
	private ArrayList<String> path;
	private Root root;
	private String previousValue = null;
	private ModelListener objectToSet;
	
	public SetCommand(Root root, ArrayList<String> path, String valueToSet, ModelListener objectToSet){
		this.root = root;
		this.path = path;
		this.valueToSet = valueToSet;
		this.objectToSet = objectToSet;
	}

	@Override
	public void redo() {
		if(previousValue == null){
			previousValue = root.getModelValue(path, root.model, 0);
		}
		root.updateModel(path, root.model, 0, valueToSet);
		if(objectToSet != null && path.size() <= 0){
			objectToSet.modelChanged(valueToSet);
		}
	}

	@Override
	public void undo() {
		root.updateModel(path, root.model, 0, previousValue);
		if(objectToSet != null && path.size() <= 0){
			objectToSet.modelChanged(previousValue);
		}
	}
	
	public void setPreviousValue(String pre){
		previousValue = pre;
	}
	
	public void setValueToSet(String value){
		valueToSet = value;
	}

	@Override
	public void doIt() {
		redo();
	}

}
