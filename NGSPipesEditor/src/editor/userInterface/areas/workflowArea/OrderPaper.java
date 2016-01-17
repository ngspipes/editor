package editor.userInterface.areas.workflowArea;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import utils.ComponentException;

import components.DoubleClickable;

import editor.EditorOperations;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Flow;
import editor.userInterface.controllers.FXMLStepsOrderController;
import editor.utils.Utils;


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
		
		flow.getElements().addStepEvent.addListner(this::onAdd);
		flow.getElements().removeStepEvent.addListner(this::onRemove);
		
		for(EditorStep step : flow.getElements().getSteps())
			step.orderEvent.addListner(this.onOrderChange);
		
		steps.sort((a,b)->a.getOrder()-b.getOrder());
		
		new DoubleClickable<>(listView, ()->{
			EditorStep selectedStep = listView.getSelectionModel().getSelectedItem();
			EditorOperations.loadStepArea(selectedStep);
			EditorOperations.slideInStepArea();
		}).mount();
	}
	
	private void onAdd(EditorStep step){
		step.orderEvent.addListner(this.onOrderChange);
		steps.add(step);
	}
	
	private void onRemove(EditorStep step){
		step.orderEvent.removeListner(this.onOrderChange);
		steps.remove(step);
	}
	
	public Node getContent(){
		return listView;
	}
	
}