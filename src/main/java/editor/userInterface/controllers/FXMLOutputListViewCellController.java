package editor.userInterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;
import components.multiOption.Menu;
import components.multiOption.Operations;

import dsl.entities.Output;
import editor.dataAccess.Uris;
import editor.userInterface.utils.Utils;

public class FXMLOutputListViewCellController implements IInitializable<FXMLOutputListViewCellController.Data> {

	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_OUTPUTS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	
	public static class Data{
		public final Output output;
		public final Operations operations;
		
		public Data(Output output, Operations operations) {
			this.output =  output;
			this.operations = operations;
		}
	}

	@FXML
	private Label lOutputName;
	
	@FXML
	private Label lOutputValue;

	@FXML
	private AnchorPane root;
	
	private Output output;
	private Operations operations;
	private Tooltip valueTip;

	@Override
	public void init(Data data) {
		this.output = data.output;
		this.operations = data.operations;
		load();
		output.valueChangedEvent.addListner(this::showValue);
	}
	
	private void load(){
		lOutputName.setText(output.getDescriptor().getName());
		
		String nameTip = output.getName() + "\n" + output.getDescriptor().getDescription();
		Tooltip.install(lOutputName, Utils.createTooltip(nameTip, true, 300, 200));
		
		valueTip = Utils.createTooltip("", true, 300, 200);
		Tooltip.install(lOutputValue, valueTip);
		
		showValue(output.getValue());
		
		new Menu<Node>(root, operations).mount();
	}
	
	private void showValue(String value){
		if(value==null)
			value="";
		
		if(!value.equals(lOutputValue.getText())){
			lOutputValue.setText(value);
			valueTip.setText(value);
		}
	}
	
}
