package command;

import java.util.ArrayList;

import sparkClass.Root;
import sparkClass.shape.Text;
import able.Command;

public class InsertCommand implements Command {
	
	private String valueToSet;
	private ArrayList<String> path;
	private Root root;
	private String previousValue = null;
	private double cursor;
	private Text textObject;
	
	public InsertCommand(Root root, ArrayList<String> path, String valueToSet, double cursor, Text textObject){
		this.root = root;
		this.path = path;
		this.valueToSet = valueToSet;
		this.cursor = cursor;
		this.textObject = textObject;
	}

	@Override
	public void redo() {
		if(previousValue == null){
			previousValue = root.getModelValue(path, root.model, 0);
		}
		if(textObject.cursor >= 0){
			textObject.cursor = cursor+1;
		}
		root.updateModel(path, root.model, 0, valueToSet);
	}

	@Override
	public void undo() {
		if(textObject.cursor >= 0){
			textObject.cursor = cursor;
		}
		root.updateModel(path, root.model, 0, previousValue);
	}
	
	public void setPreviousValue(String pre){
		previousValue = pre;
	}

	@Override
	public void doIt() {
		if(previousValue == null){
			previousValue = root.getModelValue(path, root.model, 0);
		}
		root.updateModel(path, root.model, 0, valueToSet);
	}

}
