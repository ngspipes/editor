package editor.userInterface.utils.pallet;

import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import components.multiOption.Operations;

import dsl.entities.Argument;
import editor.userInterface.controllers.FXMLArgumentListViewCellController;
import editor.utils.Utils;

public class ArgumentsPallet extends Pallet<Argument>{

	private static final String TAG = "ArgumentsPallet";
	
	private final Function<Argument, Operations> getOperations;
	
	public ArgumentsPallet(TextField textField, ListView<Argument> listView, Function<Argument, Operations> getOperations) {
		super(textField, listView);
		this.getOperations = getOperations;
	}


	@Override
	protected boolean filter(Argument argument, String pattern) {
		return argument.getDescriptor().getName().toLowerCase().startsWith(pattern.toLowerCase());
	}


	@Override
	protected Node getCellRoot(Argument argument) {
		try {
			return FXMLArgumentListViewCellController.mount(new FXMLArgumentListViewCellController.Data(argument, getOperations.apply(argument)));
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading Argument Pallet item!");
		}
		
		return null;
	}

}
