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

import components.Window;
import components.Zoomer;
import components.connect.Coordinates;
import components.connect.connection.NSideConnection;
import components.connect.connector.Connector;
import components.connect.connector.ConnectorPointer;
import configurators.IConfigurator;
import descriptors.ICommandDescriptor;
import dsl.entities.Command;
import dsl.entities.Step;
import editor.EditorOperations;
import editor.logic.entities.EditorChain;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Elements;
import editor.logic.entities.Flow;
import editor.transversal.EditorException;
import editor.transversal.Utils;
import editor.userInterface.controllers.FXMLChainController;
import editor.userInterface.controllers.FXMLChainController.Data;
import editor.userInterface.controllers.FXMLStepController;
import exceptions.RepositoryException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jfxutils.ComponentException;
import repository.IRepository;
import workflow.WorkflowConfigurator;
import workflow.elements.Workflow;
import workflow.elements.WorkflowConnection;
import workflow.elements.WorkflowItem;

import java.util.Collection;


public class ChainPaper {
	
	private final String TAG = "ChainPaper";

	private final WorkflowConfigurator config = new WorkflowConfigurator()
											.setDefaultConnectorSupplier(this::createConnector)
											.setConnectionFactory((item)->new NSideConnection<>(item.getRoot(), 50))
											.setDragInfoConverter(this::convertDragInfo)
											.setDefaultConnectionFactory(this::createWorkflowConnection)
											.setWorkflowItemKeyExtractor((wfi)->wfi.getState())
											.setWorkflowConnectionKeyExtractor((wfc)->wfc.getState())
											.setItemAdditionValidator(this::validateItemAddition)
											.setItemRemovalValidator(this::validateItemRemoval)
											.setConnectionAdditionValidator(this::validateConnectionAddition)
											.setConnectionRemovalValidator(this::validateConnectionRemoval)
											.setPermitCircularConnection(false)
											.setPermitItemSelfConnection(false);
	private final Flow flow;
	private final Workflow workflow;
	private final ScrollPane pane;


	public ChainPaper(Flow flow) throws EditorException {
		this.flow = flow;
		this.workflow = new Workflow(config);
		this.pane = new ScrollPane();
		load();
		registerListeners();
	}

	private void load()throws EditorException{
		setPane();
		
		setInitialItems();
		setInitialConnections();
			
		workflow.clearHistoric();
	}

	private void setPane() {
		Pane workflowPane = workflow.getRoot();
		workflowPane.setStyle("-fx-background-color : white");
		workflowPane.setMinSize(WorkflowPaper.WIDTH, WorkflowPaper.HEIGHT);
		new Zoomer(workflowPane, WorkflowPaper.MAX_ZOOM, WorkflowPaper.MIN_ZOOM).mount();
		
		pane.setContent(workflowPane);
		pane.setPannable(true);
		pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	}
	
	private void setInitialItems() throws EditorException {
		for(EditorStep step : flow.getElements().getSteps())
			workflow.elements.addItem(createWorkflowItem(step));
	}
	
	private void setInitialConnections() {
		WorkflowConnection connection;
		WorkflowItem initItem;
		WorkflowItem endItem;
		for(EditorChain chain : flow.getElements().getChains()){
			initItem = workflow.elements.getItem(chain.getFrom());
			endItem = workflow.elements.getItem(chain.getTo());
			connection = createWorkflowConnection(initItem, endItem, chain);
			workflow.addConnection(connection);
		}
	}
	
	private void registerListeners(){
		workflow.events.addItem.addListener((item)->flow.addStep((EditorStep)item.getState()));
		workflow.events.removeItem.addListener((item)->flow.removeStep((EditorStep)item.getState()));
		workflow.events.connect.addListener((connection)->{
			EditorChain chain = (EditorChain)connection.getState();
			chain.connectEvent.addListener((connected)->{
				if(!connected)
					workflow.removeConnection(connection);
			});
			flow.addChain(chain);
		});
		workflow.events.disconnect.addListener((connection)->flow.removeChain((EditorChain)connection.getState()));
	}
	
	
	
	private boolean validateItemAddition(WorkflowItem item){
		try {
			flow.validateStepAddition((EditorStep)item.getState());
		} catch (Exception ex) {
			Utils.treatException(ex, TAG, "Invalid Step addition!");
			return false;
		}
		
		return true;
	}
	
	private boolean validateItemRemoval(WorkflowItem item){
		try {
			flow.validateStepRemoval((EditorStep)item.getState());
		} catch (Exception ex) {
			Utils.treatException(ex, TAG, "Invalid Step removal!");
			return false;
		}
		
		return true;
	}
	
	private boolean validateConnectionAddition(WorkflowConnection connection){
		try {
			if(connection.getState() == null)
				return false; 
			
			flow.validateChainAddition((EditorChain)connection.getState());
		} catch (Exception ex) {
			Utils.treatException(ex, TAG, "Invalid Step Chain addition!");
			return false;
		}
		
		return true;
	}
	
	private boolean validateConnectionRemoval(WorkflowConnection connection){
		try {
			flow.validateChainRemoval((EditorChain)connection.getState());
		} catch (Exception ex) {
			Utils.treatException(ex, TAG, "Invalid Step Chain removal!");
			return false;
		}
		
		return true;
	}

	
	
	private WorkflowItem convertDragInfo(Object command){
		return createWorkflowItem((ICommandDescriptor)command);
	} 
	
	private WorkflowItem createWorkflowItem(ICommandDescriptor commandDescriptor){
		try{
			Command command = new Command(commandDescriptor, null);
			Step dslStep = new Step(command, getDefaultConfigurator(commandDescriptor));
			dslStep.setOrder(flow.getElements().getStepsCount()+1);
			EditorStep step = new EditorStep(0, 0, dslStep);
			return createWorkflowItem(step);
		}catch(Exception ex){
			Utils.treatException(ex, TAG, "Error loading WorkflowItem!");
		}
		return null;
	}
	
	private WorkflowItem createWorkflowItem(EditorStep step) throws EditorException {
		try{
			Node graphic = getItemGraphic(step);
			WorkflowItem item = workflow.itemFactory.create(graphic, step, new Coordinates(step.getX(), step.getY()));
			
			item.positionEvent.addListener((coordinates)->{
				step.setX(coordinates.getX());
				step.setY(coordinates.getY());
			});
			
			item.doubleClickEvent.addListener(()->{
				EditorOperations.loadStepArea(step);
				EditorOperations.slideInStepArea();
			});
			
			
			item.getRoot().setStyle(item.getRoot().getStyle() + ";-fx-border-color : #E5E5E5");
			item.getRoot().focusedProperty().addListener((obs, wasFocus, isFocus)->{
				if(isFocus)
					item.getRoot().setStyle(item.getRoot().getStyle().replace(";-fx-border-color : #E5E5E5", ";-fx-border-color : #D9B2FF"));
				else
					item.getRoot().setStyle(item.getRoot().getStyle().replace(";-fx-border-color : #D9B2FF", ";-fx-border-color : #E5E5E5"));
			});
			
			return item;	
		}catch(ComponentException ex){
			throw new EditorException("Error loading WorkflowItem!", ex);
		}
	}
	
	private IConfigurator getDefaultConfigurator(ICommandDescriptor command) throws RepositoryException {
		IRepository repository = command.getOriginTool().getOriginRepository();
		String toolName = command.getOriginTool().getName();
		return repository.getConfigurationsFor(toolName).iterator().next();
	}
	
	private Node getItemGraphic(EditorStep step) throws ComponentException {
		return FXMLStepController.mount(new editor.userInterface.controllers.FXMLStepController.Data(step));
	}
	
	
	
	private WorkflowConnection createWorkflowConnection(WorkflowItem initItem, WorkflowItem endItem){
		initiateChain(initItem, endItem);
		return createWorkflowConnection(initItem, endItem, null);
	}
	
	private void initiateChain(WorkflowItem initItem, WorkflowItem endItem){	
		try{
			Window<Parent, Void> window = new Window<>((Parent)null, "Choose chain");
			
			Data data = new Data((EditorStep)initItem.getState(), (EditorStep)endItem.getState(), (chain)->{
				window.close();
				workflow.elements.addConnection(createWorkflowConnection(initItem, endItem, chain));
			});
			
			Parent root = (Parent)FXMLChainController.mount(data);
			window.setRoot(root);
			window.open();	
		} catch(ComponentException ex) {
			Utils.treatException(ex, TAG, "Error loading chain window!");
		}
	}
	
	private WorkflowConnection createWorkflowConnection(WorkflowItem initItem, WorkflowItem endItem, EditorChain chain) {
		WorkflowConnection connection = workflow.connectionFactory.create(initItem, endItem, chain);
		if (chain != null) {
			Connector connector = connection.getConnector();
			Node root = connection.getRoot();

			Label label = new Label();
			label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
			label.setTextFill(Color.web("#2d7eff"));

			ChangeListener<Boolean> listener = (obs, wasFocus, isFocus) -> {
				if(isFocus){
					int chains = getChainsNumber(initItem, endItem);
					if(chains <= 0){
						label.setVisible(false);
						label.setText("");
					} else {
						label.setVisible(true);
						label.setText(Integer.toString(chains));
						label.setLayoutX(connector.getInitX());
						label.setLayoutY(connector.getInitY());
					}
				} else {
					label.setVisible(false);
					label.setText("");
				}
			};

			root.focusedProperty().addListener(listener);
			initItem.getRoot().focusedProperty().addListener(listener);
			endItem.getRoot().focusedProperty().addListener(listener);
			initItem.positionEvent.addListener(()->{
				label.setLayoutX(connector.getInitX());
				label.setLayoutY(connector.getInitY());
			});
			endItem.positionEvent.addListener(()->{
				label.setLayoutX(connector.getInitX());
				label.setLayoutY(connector.getInitY());
			});

			Platform.runLater(() -> {
				AnchorPane parent = (AnchorPane) root.getParent();
				if(parent != null){
					parent.getChildren().add(label);
				}
			});
		}

		return connection;
	}

	private int getChainsNumber(WorkflowItem initItem, WorkflowItem endItem){
		EditorStep initStep = (EditorStep) initItem.getState();
		EditorStep endStep = (EditorStep) endItem.getState();

		Flow workflow = initStep.getOriginFlow();
		Elements elements = workflow.getElements();

		Collection<EditorChain> chainsFromInitStep = elements.getChainsFrom(initStep);

		int count = 0;
		for(EditorChain chain : chainsFromInitStep)
			if(chain.getTo() == endStep)
				count++;

		return count;
	}
	
	
	
	private Connector createConnector(){
		Connector connector = new ConnectorPointer();
		
		Line line = connector.getNode();
		Polygon tip = (Polygon)connector.tips.getEnd();

		line.setStyle("-fx-stroke-width: 3px");
		line.focusedProperty().addListener((obs, wasFocus, isFocus)->{
			if(isFocus){
				line.setStroke(Color.rgb(217, 178, 255));
				tip.setStroke(Color.rgb(217, 178, 255));
				tip.setFill(Color.rgb(217, 178, 255));
			}else{
				line.setStroke(Color.BLACK);
				tip.setStroke(Color.BLACK);
				tip.setFill(Color.BLACK);
			}
		});
		
		return connector;
	}
	
	
	
	public Node getContent(){
		return pane;
	}

}
