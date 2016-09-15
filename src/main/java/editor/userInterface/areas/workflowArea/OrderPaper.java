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
package editor.userInterface.areas.workflowArea;

import components.DoubleClickable;
import editor.EditorOperations;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Flow;
import editor.userInterface.controllers.FXMLStepsOrderController;
import editor.transversal.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import jfxutils.ComponentException;

import java.util.function.Consumer;


public class OrderPaper {
			
	public class StepListViewCell extends ListCell<EditorStep> {
	    @Override
	    public void updateItem(EditorStep step, boolean empty) {
	        super.updateItem(step, empty);

	        Node graphic = null;
	        
	        if (step != null) {
	        	try{
	        		graphic = FXMLStepsOrderController.mount(step);
	        	}catch(ComponentException ex){
	        		Utils.treatException(ex, TAG, "Error loading ListView order Item " + step.getOrder());
	        	}
	        }    
	          
	        setGraphic(graphic);
	        setAlignment(Pos.CENTER);
	    }
	}
	
	private static final String TAG = "OrderPaper";
	
	
	private final Flow flow;
	private final ObservableList<EditorStep> steps;
	private final Consumer<Integer> onOrderChange;
	private final ListView<EditorStep> listView;	
	
	public OrderPaper(Flow flow){
		this.flow = flow;
		this.steps = FXCollections.observableArrayList(flow.getElements().getSteps());
		this.listView = new ListView<>();
		this.onOrderChange = (newOrder)->steps.sort((a,b)->a.getOrder()-b.getOrder());
		load();
	}
	
	private void load(){
		listView.setItems(steps);
		listView.setCellFactory((listView)-> new StepListViewCell());
		
		flow.getElements().addStepEvent.addListener(this::onAdd);
		flow.getElements().removeStepEvent.addListener(this::onRemove);
		
		for(EditorStep step : flow.getElements().getSteps())
			step.orderEvent.addListener(this.onOrderChange);
		
		steps.sort((a,b)->a.getOrder()-b.getOrder());
		
		new DoubleClickable<>(listView, ()->{
			EditorStep selectedStep = listView.getSelectionModel().getSelectedItem();
			EditorOperations.loadStepArea(selectedStep);
			EditorOperations.slideInStepArea();
		}).mount();
	}
	
	private void onAdd(EditorStep step){
		step.orderEvent.addListener(this.onOrderChange);
		steps.add(step);
	}
	
	private void onRemove(EditorStep step){
		step.orderEvent.removeListener(this.onOrderChange);
		steps.remove(step);
	}
	
	public Node getContent(){
		return listView;
	}
	
}