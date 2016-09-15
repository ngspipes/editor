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
import dsl.ArgumentValidator;
import dsl.entities.Argument;
import dsl.entities.Output;
import editor.dataAccess.Uris;
import editor.logic.entities.EditorChain;
import editor.logic.entities.EditorStep;
import editor.userInterface.controllers.FXMLChainController.Data;
import editor.userInterface.utils.Dialog;
import editor.userInterface.utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class FXMLChainController implements IInitializable<Data>{

	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_CHAIN;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

		fxmlFile.build();

		return fxmlFile.getRoot();
	}

	public static class Data{
		public final EditorStep from;
		public final EditorStep to;
		public final Consumer<EditorChain> onChain;

		public Data(EditorStep from, EditorStep to, Consumer<EditorChain> onChain){
			this.from = from;
			this.to = to;
			this.onChain = onChain;
		}
	}


	private static final Image DEFAULT_TOOL_LOG = new Image(Uris.TOOL_LOGO_IMAGE);
	
	@FXML
	private ComboBox<Output> cBOutputs;
	@FXML
	private ComboBox<Argument> cBArguments;
	@FXML
	private Button bChain;
	@FXML
	private ImageView iVOutput;
	@FXML
	private ImageView iVArgument;


	private EditorStep from;
	private EditorStep to;
	private Consumer<EditorChain> onChain;


	@Override
	public void init(Data data) throws ComponentException {
		this.from = data.from;
		this.to = data.to;
		this.onChain = data.onChain;
		load();
	}

	private void load(){
		iVOutput.setImage(DEFAULT_TOOL_LOG);
		iVArgument.setImage(DEFAULT_TOOL_LOG);

		UIUtils.loadLogo(iVOutput, from.getToolDescriptor());
		UIUtils.loadLogo(iVArgument, to.getToolDescriptor());

		new ButtonMagnifier<>(bChain).mount();
		bChain.setTooltip(new Tooltip("Chain"));

		loadOutputsComboBox();
		loadArgumentsComboBox();
		setOnButtonClicked();
	}

	private void loadOutputsComboBox() {
		Collection<Output> outputs = new LinkedList<>(from.getOutputs());
		cBOutputs.setItems(FXCollections.observableArrayList(outputs));
		setOutputComboBoxFactory();
	}

	private void setOutputComboBoxFactory() {
		cBOutputs.setCellFactory(new Callback<ListView<Output>, ListCell<Output>>() {
			public ListCell<Output> call(ListView<Output> param) {
				ListCell<Output> cell = new ListCell<Output>() {
					public void updateItem(Output output, boolean empty) {
						super.updateItem(output, empty);
						if (output != null) {
							Label label = new Label(output.getName());
							Tooltip.install(label, new Tooltip(output.getName()));
							setGraphic(label);
						}
						else 
							setGraphic(null);
					}
				};
				return cell;
			}
		});
		
		cBOutputs.setConverter(new StringConverter<Output>() {
			public Output fromString(String name) {
				return from.getOutput(name);
			}

			public String toString(Output output) {
				if (output == null) 
					return null;

				return output.getName();
			}
		});
	}

	private void loadArgumentsComboBox() {
		Collection<Argument> arguments = new LinkedList<>(to.getArguments());
		arguments = arguments.parallelStream().filter((arg)->arg.getType().equals(ArgumentValidator.FILE_TYPE_NAME)).collect(Collectors.toList());
		cBArguments.setItems(FXCollections.observableArrayList(arguments));
		setArgumentsComboBoxFactory();
	}
	
	private void setArgumentsComboBoxFactory(){
		cBArguments.setCellFactory(new Callback<ListView<Argument>, ListCell<Argument>>() {
			@Override
			public ListCell<Argument> call(ListView<Argument> param) {

				ListCell<Argument> cell = new ListCell<Argument>() {
					@Override 
					public void updateItem(Argument argument, boolean empty) {
						super.updateItem(argument, empty);
						if (argument != null) {
							Label label = new Label(argument.getName());
							Tooltip.install(label, new Tooltip(argument.getName()));
							setGraphic(label);
						}
						else 
							setGraphic(null);
					}
				};

				return cell;
			}
		});
		
		cBArguments.setConverter(new StringConverter<Argument>() {
			public Argument fromString(String name) {
				return to.getArgument(name);
			}

			public String toString(Argument argument) {
				if (argument == null) 
					return null;

				return argument.getName();
			}
		});
	}

	private void setOnButtonClicked() {
		bChain.setOnMouseClicked((event)->{
			Argument argument = cBArguments.getSelectionModel().getSelectedItem();
			Output output = cBOutputs.getSelectionModel().getSelectedItem();

			if(argument == null || output == null){
				Dialog.showError("You must specify and Argument and an Output!");
			}else{
				EditorChain chain = new EditorChain(from, output, to, argument);
				onChain.accept(chain);
			}
		});
	}


}
