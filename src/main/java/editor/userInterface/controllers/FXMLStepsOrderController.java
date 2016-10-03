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
import editor.dataAccess.Uris;
import editor.logic.workflow.Step;
import editor.logic.workflow.Workflow;
import editor.logic.workflow.WorkflowManager;
import editor.logic.workflow.WorkflowManager.WorkflowEvents;
import editor.userInterface.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import java.util.function.Consumer;

public class FXMLStepsOrderController implements IInitializable<Step>{

	private static final Image DEFAULT_TOOL_LOGO = new Image(Uris.TOOL_LOGO_IMAGE);



	public static Node mount(Step step) throws ComponentException {
		String fXMLPath = Uris.FXML_STEPS_ORDER;
		FXMLFile<Node, Step> fxmlFile = new FXMLFile<>(fXMLPath, step);

		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}	


	
	@FXML
	private Label lOrder;
	@FXML
	private Label lTool;
	@FXML
	private Label lCommand;
	@FXML
	private ImageView iVLogo;

	private WorkflowEvents events;
	private Workflow workflow;
	private Step step;



	@Override
	public void init(Step step) throws ComponentException {
		this.step = step;
		this.workflow = WorkflowManager.getWorkflowOfStep(step);
		this.events = WorkflowManager.getEventsFor(workflow);

		loadUIComponents();
		registerOrderListeners();
	}


	
	private void loadUIComponents(){
		showOrder(step.getOrder());

		lTool.setText(step.getTool().getName());

		lCommand.setText(step.getCommand().getName());
		
		iVLogo.setImage(DEFAULT_TOOL_LOGO);
		UIUtils.loadLogo(iVLogo, step.getTool());
	}

	private void showOrder(int order){
		lOrder.setText(Integer.toString(order));
	}

	private void registerOrderListeners() {
		Consumer<Integer> listener = this::showOrder;

		registerOrderListenerOnStepAddition(listener);
		unregisterOrderListenerOnStepRemoval(listener);

		registerOrderListener(listener);
	}

	private void registerOrderListenerOnStepAddition(Consumer<Integer> listener) {
		events.stepAdditionEvent.addListener((s) -> {
			if(s == step){
				registerOrderListener(listener);
				showOrder(step.getOrder());
			}
		});
	}

	private void unregisterOrderListenerOnStepRemoval(Consumer<Integer> listener) {
		events.stepRemovalEvent.addListener((s) -> {
			if(s == step)
				unregisterOrderListener(listener);
		});
	}

	private void registerOrderListener(Consumer<Integer> listener){
		events.orderEvents.get(step).addListener(listener);
	}

	private void unregisterOrderListener(Consumer<Integer> listener){
		events.orderEvents.get(step).removeListener(listener);
	}

}
