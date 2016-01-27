package editor.userInterface.utils.pallet;

import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import components.multiOption.Operations;

import dsl.entities.Output;
import editor.userInterface.controllers.FXMLOutputListViewCellController;
import editor.utils.Utils;

public class OutputsPallet extends Pallet<Output>{
	
	private static final String TAG = "OutputsPallet";
	
	private final Function<Output, Operations> getOperations;
	
	public OutputsPallet(TextField textField, ListView<Output> listView, Function<Output, Operations> getOperations){
		super(textField, listView);
		this.getOperations = getOperations;
	}

	@Override
	protected boolean filter(Output output, String pattern) {
		return output.getDescriptor().getName().toLowerCase().startsWith(pattern.toLowerCase());
	}

	@Override
	protected Node getCellRoot(Output output) {
		try {
			return FXMLOutputListViewCellController.mount(new FXMLOutputListViewCellController.Data(output, getOperations.apply(output)));
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading Output Pallet item!");
		}
		
		return null;
	}

}
