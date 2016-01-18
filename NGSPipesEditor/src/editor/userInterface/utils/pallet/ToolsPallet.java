package editor.userInterface.utils.pallet;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import components.multiOption.Operations;

import descriptors.IToolDescriptor;
import editor.userInterface.controllers.FXMLToolListViewCellController;
import editor.userInterface.controllers.FXMLToolListViewCellController.Data;
import editor.utils.Utils;

public class ToolsPallet extends Pallet<IToolDescriptor>{
	
	private static final String TAG = "ToolsPallet";
	
	private final Function<IToolDescriptor, Operations> getOperations;

	public ToolsPallet(TextField textField, ListView<IToolDescriptor> listView, Consumer<IToolDescriptor> onToolChoose, Function<IToolDescriptor, Operations> getOperations){
		super(textField, listView);
		this.getOperations = getOperations;
		this.listView.getSelectionModel().selectedItemProperty().addListener((obs, prev, curr)->onToolChoose.accept(curr));
	}

	@Override
	protected boolean filter(IToolDescriptor tool, String pattern) {
		return tool.getName().toLowerCase().startsWith(pattern.toLowerCase());
	}

	@Override
	protected Node getCellRoot(IToolDescriptor tool) {
		try {
			return FXMLToolListViewCellController.mount(new Data(tool, getOperations.apply(tool)));
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading Tool Pallet item!");
		}
		
		return null;
	}
	
}
