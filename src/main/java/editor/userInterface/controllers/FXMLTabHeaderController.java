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
import components.animation.changeComponent.ChangeButtonOnPass;
import editor.dataAccess.Uris;
import editor.logic.workflow.Workflow;
import editor.logic.workflow.WorkflowManager;
import editor.logic.workflow.WorkflowManager.WorkflowEvents;
import editor.userInterface.controllers.FXMLTabHeaderController.Data;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

public class FXMLTabHeaderController implements IInitializable<Data>{

	public static class Data{
		public final Workflow workflow;
		public final Runnable onClose;

		public Data(Workflow workflow, Runnable onClose){
			this.workflow = workflow;
			this.onClose = onClose;
		}
	}



	private static final Image CLOSE_BUTTON_SELECTED_IMAGE = new Image(Uris.CLOSE_SELECTED_IMAGE);
	private static final Image CLOSE_BUTTON_UNSELECTED_IMAGE = new Image(Uris.CLOSE_UNSELECTED_IMAGE);
	private static final Image SAVED_IMAGE = new Image(Uris.SAVED_IMAGE);
	private static final Image NOT_SAVED_IMAGE = new Image(Uris.NOT_SAVED_IMAGE);



	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_TAB_HEADER;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}

	
	
	@FXML
	private ImageView iVSaved;
	@FXML
	private Label lName;
	@FXML
	private Button bClose;

	private WorkflowEvents events;
	private Workflow workflow;
	private Runnable onClose;


	
	@Override
	public void init(Data data) {
		this.workflow = data.workflow;
		this.onClose = data.onClose;
		this.events = WorkflowManager.getEventsFor(workflow);

		loadUIComponents();
		registerListeners();
	}



	private void loadUIComponents(){
		setSavedImage(workflow.getSaved());

		lName.setText(workflow.getName());

		loadCloseButton();
	}

	private void loadCloseButton(){
		bClose.setGraphic(new ImageView(CLOSE_BUTTON_UNSELECTED_IMAGE));
		new ChangeButtonOnPass<>(bClose, CLOSE_BUTTON_SELECTED_IMAGE, CLOSE_BUTTON_UNSELECTED_IMAGE).mount();
		bClose.setOnMouseClicked((event) -> onClose.run());
	}

	private void registerListeners(){
		events.saveEvent.addListener(this::setSavedImage);
		events.nameEvent.addListener((name) -> lName.setText(name));
	}
	
	private void setSavedImage(boolean saved){
		if(saved)
			iVSaved.setImage(SAVED_IMAGE);
		else
			iVSaved.setImage(NOT_SAVED_IMAGE);
	}

}
