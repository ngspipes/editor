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
import editor.dataAccess.Uris;
import editor.logic.workflow.Argument;
import editor.logic.workflow.Step;
import editor.logic.workflow.Workflow;
import editor.logic.workflow.WorkflowManager;
import editor.logic.workflow.WorkflowManager.WorkflowEvents;
import editor.userInterface.utils.Dialog;
import editor.userInterface.utils.UIUtils;
import javafx.beans.value.ChangeListener;
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
import java.util.function.Consumer;


public class FXMLArgumentListViewCellController implements IInitializable<FXMLArgumentListViewCellController.Data> {

	public static class Data{
		public final Argument argument;
		public final Operations operations;

		public Data(Argument argument, Operations operations) {
			this.argument =  argument;
			this.operations = operations;
		}
	}



	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_ARGUMENTS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}



	@FXML
	private Label lArgumentName;
	@FXML
	private TextField tFArgumentValue;
	@FXML
	private HBox root;
	@FXML
	private Button bSearch;

	private WorkflowEvents events;
	private Workflow workflow;
	private Step step;
	private Argument argument;
	private Operations operations;



	@Override
	public void init(Data data) {
		this.argument = data.argument;
		this.operations = data.operations;
		this.step = WorkflowManager.getStepOfArgument(argument);
		this.workflow = WorkflowManager.getWorkflowOfStep(step);
		this.events = WorkflowManager.getEventsFor(workflow);

		loadUIComponents();
		registerListeners();
	}



	private void loadUIComponents(){
		String argumentName = argument.getName();
		String argumentDescription = argument.getDescription();

		lArgumentName.setText(argumentName);
		String tip = argumentName + "\n" + argumentDescription;
		Tooltip.install(lArgumentName, UIUtils.createTooltip(tip, true, 300, 200));
		
		showValue(argument.getValue());
		
		new Menu<>(root, operations).mount();
		
		loadSearchButton();
	}
	
	private void loadSearchButton(){
		String argumentType = argument.getType();

		if(argumentType.equals(ArgumentValidator.FILE_TYPE_NAME)){
			new ButtonMagnifier<>(bSearch).mount();
			bSearch.setTooltip(new Tooltip("Search"));
			bSearch.setOnMouseClicked(this::onSearchClicked);
		} else {
			bSearch.setVisible(false);
		}
	}
	
	public void onSearchClicked(MouseEvent event){
		File file = Dialog.getFile("Choose File");
		
		if(file != null)
			WorkflowManager.setArgumentValue(argument, file.getPath());
	}

	private void registerListeners(){
		registerArgumentValueListener();
		registerTextFiledValueListener();
	}

	private void registerArgumentValueListener(){
		Consumer<String> valueListener = this::showValue;

		registerArgumentValueListenerOnStepAddition(valueListener);
		unregisterArgumentValueListenerOnStepRemoval(valueListener);

		registerArgumentValueListener(valueListener);
	}

	private void registerArgumentValueListenerOnStepAddition(Consumer<String> listener){
		events.stepAdditionEvent.addListener((s) -> {
			if(s == step){
				registerArgumentValueListener(listener);
				showValue(argument.getValue());
			}
		});
	}

	private void unregisterArgumentValueListenerOnStepRemoval(Consumer<String> listener){
		events.stepRemovalEvent.addListener((s) -> {
			if(s == step)
				unregisterArgumentValueListener(listener);
		});
	}

	private void registerArgumentValueListener(Consumer<String> listener){
		events.argumentEvents.get(argument).addListener(listener);
	}

	private void unregisterArgumentValueListener(Consumer<String> listener){
		events.argumentEvents.get(argument).removeListener(listener);
	}

	private void registerTextFiledValueListener() {
		ChangeListener<String> listener = (obs, oldValue, newValue) -> {
			if(WorkflowManager.isAssociatedWithWorkflow(argument))
				setArgumentValue(newValue);
			else
				Dialog.showError("This step is not associated with any workflow!");
		};

		tFArgumentValue.textProperty().addListener(listener);
	}

	private void setArgumentValue(String newValue){
		if(newValue != null && newValue.isEmpty())
			WorkflowManager.setArgumentValue(argument, null);
		else
			WorkflowManager.setArgumentValue(argument, newValue);
	}

	public void showValue(String value){
		if(value == null)
			value = "";
		
		if(!value.equals(tFArgumentValue.getText()))
			tFArgumentValue.setText(value);
	}

}
