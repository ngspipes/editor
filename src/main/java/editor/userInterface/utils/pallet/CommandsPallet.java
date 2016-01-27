package editor.userInterface.utils.pallet;

import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import components.multiOption.Operations;

import descriptors.ICommandDescriptor;
import editor.userInterface.controllers.FXMLCommandListViewCellController;
import editor.userInterface.controllers.FXMLCommandListViewCellController.Data;
import editor.utils.Utils;


public class CommandsPallet extends Pallet<ICommandDescriptor>{

	private static final String TAG = "CommandsPallet";
	
	private final Function<ICommandDescriptor, Operations> getOperations;
	
	public CommandsPallet(	TextField textField, 
							ListView<ICommandDescriptor> listView, 
							Function<ICommandDescriptor, Operations> getOperations) {
		super(textField, listView);
		this.getOperations = getOperations;
	}

	@Override
	protected boolean filter(ICommandDescriptor command, String pattern) {
		return command.getName().toLowerCase().startsWith(pattern.toLowerCase());
	}

	@Override
	protected Node getCellRoot(ICommandDescriptor command) {
		try {
			return FXMLCommandListViewCellController.mount( new Data(command, getOperations.apply(command)));
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading Tool Pallet item!");
		}
		
		return null;
	}
	
}
