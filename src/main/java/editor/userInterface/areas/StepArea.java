package editor.userInterface.areas;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import repository.IRepository;

import components.Window;
import components.multiOption.Operations;
import components.multiOption.Operations.Operation;

import descriptors.IArgumentDescriptor;
import descriptors.ICommandDescriptor;
import descriptors.IOutputDescriptor;
import descriptors.IToolDescriptor;
import dsl.entities.Argument;
import dsl.entities.Output;
import editor.logic.entities.EditorStep;
import editor.userInterface.components.PaneSlider.SlideSide;
import editor.userInterface.controllers.FXMLRepositoryDescriptionController;
import editor.userInterface.controllers.FXMLStepInfoController;
import editor.userInterface.controllers.FXMLStepInfoController.Data;
import editor.userInterface.utils.pallet.ArgumentsPallet;
import editor.userInterface.utils.pallet.OutputsPallet;
import editor.utils.EditorException;
import editor.utils.Log;
import editor.utils.Utils;


public class StepArea extends Area{

	private static final String TAG = "StepPallet";
	private static final int ARGUMENTS_TAB_INDEX = 0;
	private static final int OUTPUTS_TAB_INDEX = 1;
	private static final String ARGUMENT_DESCRIPTION_WINDOW_TITLE = "Argument Description";
	private static final String OUTPUT_DESCRIPTION_WINDOW_TITLE = "Output Description";
	private static final String DESCRIPTION_MENU_ITEM = "Description";
	
	private final AnchorPane stepInfoPane;
	private final TabPane propertiesTabPane;
	private ArgumentsPallet argumentsPallet;
	private OutputsPallet outputsPallet;
	
	public StepArea(AnchorPane stepInfoPane, TabPane propertiesTabPane, 
					AnchorPane stepAreaPane, Button expandButton,
					TextField argumentsFilter, ListView<Argument> argumentsListView, 
					TextField outputsFilter, ListView<Output> outputsListView) {	
		super(SlideSide.RIGHT, stepAreaPane, expandButton);
		this.stepInfoPane = stepInfoPane;
		this.propertiesTabPane = propertiesTabPane;
		this.argumentsPallet = new ArgumentsPallet(argumentsFilter, argumentsListView, this::getArgumentOperations);
		this.outputsPallet = new OutputsPallet(outputsFilter, outputsListView, this::getOutputOperations);
	}

	
	public void showArguments(){
		propertiesTabPane.getSelectionModel().select(ARGUMENTS_TAB_INDEX);
	}
	
	public void showOutputs(){
		propertiesTabPane.getSelectionModel().select(OUTPUTS_TAB_INDEX);	
	}
	
	public void load(EditorStep step) throws EditorException {	
		Log.debug(TAG, "Loading Step command : " + step.getCommandDescriptor().getName() + " from tool : " + step.getToolDescriptor().getName());
		
		try{
			Node root = FXMLStepInfoController.mount(new Data(step));
			stepInfoPane.getChildren().clear();
			stepInfoPane.getChildren().add(root);
			
			argumentsPallet.load(step.getArguments());
			outputsPallet.load(step.getOutputs());
		}catch(Exception e){
			throw new EditorException("Error loding step!");
		}
	}
	
	public void clear(){
		Log.debug(TAG, "Clearing");
		
		stepInfoPane.getChildren().clear();
		argumentsPallet.clear();
		outputsPallet.clear();
	}
	
	
	private Operations getArgumentOperations(Argument argument){
		Operations operations = new Operations();
		
		operations.add(new Operation(DESCRIPTION_MENU_ITEM, ()->{
			try{	
				IArgumentDescriptor argDescriptor = argument.getDescriptor();
				ICommandDescriptor command = argDescriptor.getOriginCommand();
				IToolDescriptor tool = command.getOriginTool();
				IRepository repository = tool.getOriginRepository();
				
				FXMLRepositoryDescriptionController.Data data = new FXMLRepositoryDescriptionController.Data(repository, tool, command, argDescriptor, null);
				Parent root = (Parent)FXMLRepositoryDescriptionController.mount(data);
				
				new Window<>(root, ARGUMENT_DESCRIPTION_WINDOW_TITLE).open();
			} catch(Exception e) {
				Utils.treatException(e, TAG, "Error loading description window");
			}
		}));
		
		return operations;
	}
	
	private Operations getOutputOperations(Output output){
		Operations operations = new Operations();
		
		operations.add(new Operation(DESCRIPTION_MENU_ITEM, ()->{
			try{
				IOutputDescriptor outDescriptor = output.getDescriptor();
				ICommandDescriptor command = outDescriptor.getOriginCommand();
				IToolDescriptor tool = command.getOriginTool();
				IRepository repository = tool.getOriginRepository();
				
				FXMLRepositoryDescriptionController.Data data = new FXMLRepositoryDescriptionController.Data(repository, tool, command, null, outDescriptor);
				Parent root = (Parent)FXMLRepositoryDescriptionController.mount(data);
				
				new Window<>(root, OUTPUT_DESCRIPTION_WINDOW_TITLE).open();
			} catch(Exception e) {
				Utils.treatException(e, TAG, "Error loading description window");
			}
		}));
		
		return operations;
	}
	
}
