/*-
 * Copyright (c) 2016, NGSPipes Team <ngspipes@gmail.com>
 * All rights reserved.
 *
 * This file is part of NGSPipes <http://ngspipes.github.io/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package editor.userInterface.utils.pallet;

import editor.transversal.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.Collection;
import java.util.LinkedList;

public abstract class Pallet<T>{
	
	public class PalletListViewCell extends ListCell<T> {
		
	    @Override
	    public void updateItem(T item, boolean empty) {
	        super.updateItem(item, empty);

	        if (item != null) 
	            setGraphic(Pallet.this.getCellRoot(item));
	        else 
	            setGraphic(null);
	    }
	    
	}
		
	private static final String TAG = "Pallet";
	
	protected final TextField textField;
	protected final ListView<T> listView;
	
	private ObservableList<T>  items;
	private FilteredList<T> filter;
	private Collection<T> currentItems;
	
	public Pallet(TextField textfield, ListView<T> listView){
		this.textField = textfield;
		this.listView = listView;
		
		loadListView();
		loadTextField();
	}

	private void loadListView(){
		currentItems = new LinkedList<>();
		items = FXCollections.observableArrayList(currentItems);
		filter = new FilteredList<>(items, tool -> true);    

		listView.setItems(filter);

		listView.setCellFactory((listView)-> new PalletListViewCell());
	}
	
	private void loadTextField(){
		textField.textProperty().addListener(obs->{
			String pattern = textField.getText(); 

			if(pattern == null || pattern.isEmpty())
				filter.setPredicate(tool -> true);
			else 
				filter.setPredicate((item)-> filter(item, pattern));
		});
	}
	
	public void load(Collection<T> items){
		currentItems = items;
		refresh();
	}
	
	public Collection<T> getCurrentItems(){
		return currentItems;
	}
	
	public void refresh(){
		Log.debug(TAG, "Refreshing");
		
		items.clear();
		items.addAll(currentItems);
	}
	
	public void clear(){
		Log.debug(TAG, "Clearing");
		
		textField.setText("");
		load(new LinkedList<>());
	}
	
	//CONTRACT
	protected abstract boolean filter(T item, String pattern);
	protected abstract Node getCellRoot(T item);
	
}
