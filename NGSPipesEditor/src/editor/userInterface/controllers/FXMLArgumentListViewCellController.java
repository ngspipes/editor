package editor.userInterface.controllers;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import utils.ComponentException;
import utils.IInitializable;

import components.FXMLFile;
import components.animation.magnifier.ButtonMagnifier;
import components.multiOption.Menu;
import components.multiOption.Operations;

import dsl.ArgumentValidator;
import dsl.entities.Argument;
import editor.dataAccess.Uris;
import editor.userInterface.utils.Dialog;
import editor.userInterface.utils.Utils;


public class FXMLArgumentListViewCellController implements IInitializable<FXMLArgumentListViewCellController.Data> {
	
	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_ARGUMENTS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	
	public static class Data{
		public final Argument argument;
		public final Operations operations;
		
		public Data(Argument argument, Operations operations) {
			this.argument =  argument;			
			this.operations = operations;
		}
	}

	@FXML
	private Label lArgumentName;
	
	@FXML
	private TextField tFArgumentValue;
	
	@FXML
	private HBox root;
	
	@FXML
	private Button bSearch;
	
	private Argument argument;
	private Operations operations;

	@Override
	public void init(Data data) {
		this.argument = data.argument;
		this.operations = data.operations;
		load();
		registerListeners();
	}
	
	private void load(){
		lArgumentName.setText(argument.getDescriptor().getName());
		String tip = argument.getName() + "\n" + argument.getDescriptor().getDescription();
		Tooltip.install(lArgumentName, Utils.createTooltip(tip, true, 300, 200));
		
		showValue(argument.getValue());
		
		new Menu<>(root, operations).mount();
		
		if(argument.getDescriptor().getType().equals(ArgumentValidator.FILE_TYPE_NAME))
			loadSearchButton();
		else
			bSearch.setVisible(false);
	}
	
	private void loadSearchButton(){
		new ButtonMagnifier<>(bSearch).mount();
		bSearch.setTooltip(new Tooltip("Search"));
		bSearch.setOnMouseClicked(this::onSearchClicked);
	}
	
	public void onSearchClicked(MouseEvent event){
		File file = Dialog.getFile("Choose File");
		
		if(file!=null)
			argument.setValue(file.getPath());
	}
	
	private void registerListeners(){
		argument.valueChangedEvent.addListner(this::showValue);
		tFArgumentValue.textProperty().addListener((obs, oldValue, newValue) -> argument.setValue(newValue));
	}
	
	
	public void showValue(String value){
		if(value == null)
			value = "";
		
		if(!value.equals(tFArgumentValue.getText()))
			tFArgumentValue.setText(value);
	}

}
