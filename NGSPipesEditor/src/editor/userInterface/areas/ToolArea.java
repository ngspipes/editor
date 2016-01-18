package editor.userInterface.areas;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import repository.IRepository;

import components.Window;
import components.multiOption.Operations;
import components.multiOption.Operations.Operation;

import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import editor.userInterface.components.PaneSlider.SlideSide;
import editor.userInterface.controllers.FXMLRepositoryDescriptionController;
import editor.userInterface.utils.pallet.CommandsPallet;
import editor.utils.EditorException;
import editor.utils.Log;
import editor.utils.Utils;


public class ToolArea extends Area{
	
	private static final String TAG = "ToolArea";
	
	private static final String DESCRIPTION_WINDOW_TITLE = "Command Description";
	private static final String DESCRIPTION_MENU_ITEM = "Description";
	

	private final CommandsPallet commandPallet;
	
	public ToolArea(AnchorPane toolAreaPane, Button expandButton, ListView<ICommandDescriptor> commandsListView){
		super(SlideSide.DOWN, toolAreaPane, expandButton);
		this.commandPallet = new CommandsPallet(new TextField(), commandsListView, this::getCommandOperations);
	}
	
	public void load(IToolDescriptor tool) throws EditorException{
		Log.debug(TAG, "Loading Tool : " + tool.getName());
		commandPallet.load(tool.getCommands());
	}
	
	public void clear(){
		Log.debug(TAG, "Clearing");
		commandPallet.clear();
	}

	private Operations getCommandOperations(ICommandDescriptor command){
		Operations operations = new Operations();
		
		operations.add(new Operation(DESCRIPTION_MENU_ITEM, ()->{
			try{	
				IRepository repository = command.getOriginTool().getOriginRepository();
				IToolDescriptor tool = command.getOriginTool();
				
				Parent root = (Parent)FXMLRepositoryDescriptionController.mount(new FXMLRepositoryDescriptionController.Data(repository, tool, command));
				new Window<>(root, DESCRIPTION_WINDOW_TITLE).open();
			} catch(Exception e) {
				Utils.treatException(e, TAG, "Error loading description window");
			}
		}));
		
		return operations;
	}

}
