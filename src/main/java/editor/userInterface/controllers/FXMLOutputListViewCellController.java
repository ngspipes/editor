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
import components.multiOption.Menu;
import components.multiOption.Operations;
import editor.dataAccess.Uris;
import editor.logic.workflow.Output;
import editor.logic.workflow.Step;
import editor.logic.workflow.Workflow;
import editor.logic.workflow.WorkflowManager;
import editor.logic.workflow.WorkflowManager.WorkflowEvents;
import editor.userInterface.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import java.util.function.Consumer;

public class FXMLOutputListViewCellController implements IInitializable<FXMLOutputListViewCellController.Data> {

	public static class Data{
		public final Output output;
		public final Operations operations;

		public Data(Output output, Operations operations) {
			this.output =  output;
			this.operations = operations;
		}
	}



	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_OUTPUTS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	


	@FXML
	private Label lOutputName;
	
	@FXML
	private Label lOutputValue;

	@FXML
	private AnchorPane root;

	private WorkflowEvents events;
	private Workflow workflow;
	private Step step;
	private Output output;
	private Operations operations;
	private Tooltip valueTip;



	@Override
	public void init(Data data) {
		this.output = data.output;
		this.operations = data.operations;
		this.step = WorkflowManager.getStepOfOutput(output);
		this.workflow = WorkflowManager.getWorkflowOfStep(step);
		this.events = WorkflowManager.getEventsFor(workflow);

		loadUIComponents();
		registerValueListeners();
	}


	
	private void loadUIComponents(){
		lOutputName.setText(output.getName());
		
		String tip = output.getName() + "\n" + output.getDescription();
		Tooltip.install(lOutputName, UIUtils.createTooltip(tip, true, 300, 200));
		
		valueTip = UIUtils.createTooltip("", true, 300, 200);
		Tooltip.install(lOutputValue, valueTip);
		
		showValue(output.getValue());
		
		new Menu<Node>(root, operations).mount();
	}

	private void registerValueListeners(){
		Consumer<String> listener = this::showValue;

		registerOutputValueListenerOnStepAddition(listener);
		unregisterOutputValueListenerOnStepRemoval(listener);

		registerOutputValueListener(listener);
	}

	private void registerOutputValueListenerOnStepAddition(Consumer<String> listener){
		events.stepAdditionEvent.addListener((s) -> {
			if(s == step){
				registerOutputValueListener(listener);
				showValue(output.getValue());
			}
		});
	}

	private void unregisterOutputValueListenerOnStepRemoval(Consumer<String> listener){
		events.stepRemovalEvent.addListener((s) -> {
			if(s == step)
				unregisterOutputValueListener(listener);
		});
	}

	private void registerOutputValueListener(Consumer<String> listener){
		events.outputEvents.get(output).addListener(listener);
	}

	private void unregisterOutputValueListener(Consumer<String> listener){
		events.outputEvents.get(output).removeListener(listener);
	}

	private void showValue(String value){
		if(value==null)
			value="";

		lOutputValue.setText(value);
		valueTip.setText(value);
	}
	
}
