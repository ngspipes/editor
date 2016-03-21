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

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;
import components.animation.changeComponent.ChangeButtonOnPass;

import editor.dataAccess.Uris;
import editor.logic.entities.Flow;
import editor.userInterface.controllers.FXMLTabHeaderController.Data;

public class FXMLTabHeaderController implements IInitializable<Data>{
	
	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_TAB_HEADER;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
 
	
	
	public static class Data{
		public final Flow workflow;
		public final Runnable onClose;
		
		public Data(Flow workflow, Runnable onClose){
			this.workflow = workflow;
			this.onClose = onClose;
		}
		
	}
	
	
	@FXML
	private ImageView iVSaved;
	@FXML
	private Label lName;
	@FXML
	private Button bClose;
	
	
	private Flow workflow;
	private Runnable onClose;

	
	@Override
	public void init(Data data) {
		this.workflow = data.workflow;
		this.onClose = data.onClose;
		load();
	}
	
	private void load(){
		setSavedImage(workflow.getSaved());
		bClose.setGraphic(new ImageView(new Image(Uris.CLOSE_DESELECTED_IMAGE)));
		lName.setText(workflow.getName());
		
		workflow.saveEvent.addListener(this::setSavedImage);
		
		workflow.nameEvent.addListener((newName)->lName.setText(newName));
		
		new ChangeButtonOnPass<>(bClose, Uris.CLOSE_SELECTED_IMAGE, Uris.CLOSE_DESELECTED_IMAGE).mount();
		
		bClose.setOnMouseClicked((event)->onClose.run());
	}
	
	private void setSavedImage(boolean saved){
		if(saved)
			iVSaved.setImage(new Image(Uris.SAVED_IMAGE));
		else
			iVSaved.setImage(new Image(Uris.NOT_SAVED_IMAGE));		
	}

}
