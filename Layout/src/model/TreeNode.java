package model;

import java.util.ArrayList;

import listener.ModelListener;

public class TreeNode {
	
	public String attribute;
	public String value;
	public ArrayList<TreeNode> kids = new ArrayList<TreeNode>();
	public ArrayList<ModelListener> listeners = new ArrayList<ModelListener>();
	
	public TreeNode(String attribute, String value){
		this.attribute = attribute;
		this.value = value;
	}
	
	public void notifyListeners(String newValue){
		for(int i = 0; i < listeners.size(); i++){
			listeners.get(i).modelChanged(newValue);
		}
	}
	
	public void addListener(ArrayList<String> models, TreeNode current, int index, ModelListener listener){
		if(models.size()-1 == index){
			String target = models.get(index);
			for(int i = 0; i < current.kids.size(); i++){
				if(current.kids.get(i).attribute.equals(target)){
					current.kids.get(i).listeners.add(listener);
					return;
				}
			}
		}
		String target = models.get(index);
		for(int i = 0; i < current.kids.size(); i++){
			if(current.kids.get(i).attribute.equals(target)){
				current.kids.get(i).addListener(models, current.kids.get(i), index+1, listener);
			}
		}
	}
	
	public String getValue(ArrayList<String> models, TreeNode current, int index){
		if(models.size()-1 == index){
			String target = models.get(index);
			for(int i = 0; i < current.kids.size(); i++){
				if(current.kids.get(i).attribute.equals(target)){
					return current.kids.get(i).value;
				}
			}
		}
		String target = models.get(index);
		for(int i = 0; i < current.kids.size(); i++){
			if(current.kids.get(i).attribute.equals(target)){
				return current.kids.get(i).getValue(models, current.kids.get(i), index+1);
			}
		}
		return null;
	}
	
	public TreeNode update(ArrayList<String> models, TreeNode current, int index, String value){
		if(models.size()-1 == index){
			String target = models.get(index);
			for(int i = 0; i < current.kids.size(); i++){
				if(current.kids.get(i).attribute.equals(target)){
					current.kids.get(i).value = value;
					current.kids.get(i).notifyListeners(value);
				}
			}
			return current;
		}
		String target = models.get(index);
		for(int i = 0; i < current.kids.size(); i++){
			if(current.kids.get(i).attribute.equals(target)){
				current.kids.set(i, update(models, current.kids.get(i), index+1 ,value));
			}
		}
		return current;
	}
	
	public void print(){
		printRec(this);
		System.out.println();
	}
	
	private void printRec(TreeNode current){
		System.out.print(current.attribute);
		if(!current.attribute.equals("")){
			System.out.print(" : ");
		}
		if(current.kids.size() == 0){
			System.out.print(current.value);
		}
		else{
			System.out.print("{ ");
			for(int i = 0; i < current.kids.size(); i++){
				printRec(current.kids.get(i));
				if(i != current.kids.size()-1){
					System.out.print(", ");
				}
			}
			System.out.print(" }");
		}
	}
}
