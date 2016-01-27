package editor.userInterface.areas.workflowArea;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jfxutils.ComponentException;

import components.DoubleClickable;

import editor.EditorOperations;
import editor.logic.entities.Flow;
import editor.userInterface.controllers.FXMLTabHeaderController;
import editor.userInterface.controllers.FXMLTabHeaderController.Data;
import editor.utils.EditorException;

public class WorkflowPaper {

	protected static final double MAX_ZOOM = 3;
	protected static final double MIN_ZOOM = .5;
	protected static final int WIDTH = 4000;
	protected static final int HEIGHT = 4000;

	
	private final Flow flow;
	private final Tab tab;
	private final TabPane tabContent;
	private final Tab orderTab;
	private final Tab chainTab;
	private final OrderPaper orderPaper;
	private final ChainPaper chainPaper;


	public WorkflowPaper(Flow flow) throws EditorException {
		this.flow = flow;
		this.tab = new Tab();
		this.tabContent = new TabPane();
		this.orderTab = new Tab();
		this.chainTab = new Tab();
		this.orderPaper = new OrderPaper(flow);
		this.chainPaper = new ChainPaper(flow);
		setTabHeader();
		loadTabs();
	}

	private void setTabHeader() throws EditorException {
		try{
			Node root = FXMLTabHeaderController.mount(new Data(flow, ()->EditorOperations.closeWorkflow(flow)));
			new DoubleClickable<>(root, EditorOperations::slideOutAllAreas).mount();
			tab.setGraphic(root);
		} catch(ComponentException ex) {
			throw new EditorException("Error loading Tab header!", ex);
		}
	}

	private void loadTabs() throws EditorException {
		tab.setContent(tabContent);
				
		tabContent.getTabs().add(chainTab);
		tabContent.getTabs().add(orderTab);
		
		tabContent.getSelectionModel().selectedItemProperty().addListener((ev)->EditorOperations.slideOutStepArea());
		
		chainTab.setClosable(false);
		orderTab.setClosable(false);
		
		chainTab.setGraphic(new Label("Chain"));
		orderTab.setGraphic(new Label("Order"));
		
		chainTab.getGraphic().setStyle("-fx-background-color : transparent");
		orderTab.getGraphic().setStyle("-fx-background-color : transparent");
		
		chainTab.setContent(chainPaper.getContent());
		orderTab.setContent(orderPaper.getContent());
	}
	
	public Tab getTab(){
		return tab;
	}
	
}
