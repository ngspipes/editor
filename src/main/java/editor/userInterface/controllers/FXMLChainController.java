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
import editor.dataAccess.Uris;
import editor.logic.workflow.*;
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
import java.util.function.Function;


public class FXMLChainController implements IInitializable<Data>{

	private static class ListCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {

		private final Function<T, String> nameExtractor;

		public ListCellFactory(Function<T, String> nameExtractor){
			this.nameExtractor = nameExtractor;
		}

		public ListCell<T> call(ListView<T> param) {
			return new ListCell<T>() {
				public void updateItem(T t, boolean empty) {
					super.updateItem(t, empty);
					if (t != null) {
						String name = nameExtractor.apply(t);
						Label label = new Label(name);
						Tooltip.install(label, new Tooltip(name));
						setGraphic(label);
					}
					else
						setGraphic(null);
				}
			};
		}

	}

	public static class Data{
		public final Step from;
		public final Step to;
		public final Consumer<Chain> onChain;

		public Data(Step from, Step to, Consumer<Chain> onChain){
			this.from = from;
			this.to = to;
			this.onChain = onChain;
		}
	}



	private static final Image DEFAULT_TOOL_LOG = new Image(Uris.TOOL_LOGO_IMAGE);



	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_CHAIN;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

		fxmlFile.build();

		return fxmlFile.getRoot();
	}



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

	private Step from;
	private Step to;
	private Consumer<Chain> onChain;



	@Override
	public void init(Data data) throws ComponentException {
		this.from = data.from;
		this.to = data.to;
		this.onChain = data.onChain;

		loadUIComponents();
	}



	private void loadUIComponents(){
		iVOutput.setImage(DEFAULT_TOOL_LOG);
		iVArgument.setImage(DEFAULT_TOOL_LOG);

		UIUtils.loadLogo(iVOutput, from.getTool());
		UIUtils.loadLogo(iVArgument, to.getTool());

		loadOutputsComboBox();
		loadArgumentsComboBox();
		loadChainButton();
	}

	private void loadOutputsComboBox() {
		Collection<Output> outputs = new LinkedList<>(from.getOutputs());
		cBOutputs.setItems(FXCollections.observableArrayList(outputs));
		setOutputComboBoxFactory();
	}

	private void setOutputComboBoxFactory() {
		cBOutputs.setCellFactory(new ListCellFactory<>(Output::getName));
		
		cBOutputs.setConverter(new StringConverter<Output>() {
			public Output fromString(String name) {
				return from.getOutput(name);
			}

			public String toString(Output output) {
				return output == null ? null : output.getName();
			}
		});
	}

	private void loadArgumentsComboBox() {
		Collection<Argument> arguments = getArgumentsOfType(ArgumentValidator.FILE_TYPE_NAME);
		cBArguments.setItems(FXCollections.observableArrayList(arguments));
		setArgumentsComboBoxFactory();
	}

	private Collection<Argument> getArgumentsOfType(String type){
		Collection<Argument> arguments = new LinkedList<>();

		to.getArguments().forEach((argument) -> {
			if(argument.getType().equals(type))
				arguments.add(argument);
		});

		return arguments;
	}

	private void setArgumentsComboBoxFactory(){
		cBArguments.setCellFactory(new ListCellFactory<>(Argument::getName));
		
		cBArguments.setConverter(new StringConverter<Argument>() {
			public Argument fromString(String name) {
				return to.getArgument(name);
			}

			public String toString(Argument argument) {
				return argument == null ? null : argument.getName();
			}
		});
	}

	private void loadChainButton() {
		new ButtonMagnifier<>(bChain).mount();
		bChain.setTooltip(new Tooltip("Chain"));

		bChain.setOnMouseClicked((event) -> {
			Argument argument = cBArguments.getSelectionModel().getSelectedItem();
			Output output = cBOutputs.getSelectionModel().getSelectedItem();

			if(argument == null || output == null) {
				Dialog.showError("You must specify an Argument and an Output!");
			} else {
				Chain chain = WorkflowManager.createChain(argument, output);
				onChain.accept(chain);
			}
		});
	}

}
