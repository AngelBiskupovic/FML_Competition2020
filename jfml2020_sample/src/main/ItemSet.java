package main;

import java.util.ArrayList;
import java.util.List;

public class ItemSet {
	// ************************************************************
	// Field
	List<Item> itemSet = new ArrayList<>();

	// ************************************************************
	// Constructor
	public ItemSet() {}

	// ************************************************************
	// Method
	public void addItem(Item item) {
		itemSet.add(item);
	}

	public Item getItem(int p) {
		if(p > itemSet.size()) {
			return null;
		}
		else {
			return itemSet.get(p);
		}
	}

	public List<Item> getItems(){
		return this.itemSet;
	}

	public int getItemSize() {
		return itemSet.size();
	}

	public int getDimension() {
		if(this.itemSet.size() > 0) {
			return itemSet.get(0).getDimension();
		}
		else {
			return -1;
		}
	}

}
