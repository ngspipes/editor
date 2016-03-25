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
package editor.userInterface.controllers;

import components.FXMLFile;
import components.animation.magnifier.ButtonMagnifier;
import components.multiOption.Menu;
import components.multiOption.Operations;
import dsl.ArgumentValidator;
import dsl.entities.Argument;
import editor.dataAccess.Uris;
import editor.userInterface.utils.Dialog;
import editor.userInterface.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import java.io.File;


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
		argument.valueChangedEvent.addListener(this::showValue);
		tFArgumentValue.textProperty().addListener((obs, oldValue, newValue) -> {
			if(newValue != null && newValue.isEmpty())
				argument.setValue(null);
			else
				argument.setValue(newValue);
		});
	}
	
	
	public void showValue(String value){
		if(value == null)
			value = "";
		
		if(!value.equals(tFArgumentValue.getText()))
			tFArgumentValue.setText(value);
	}

}
