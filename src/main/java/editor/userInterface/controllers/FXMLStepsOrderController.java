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
import editor.dataAccess.loader.LogoLoader;
import editor.logic.entities.EditorStep;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

public class FXMLStepsOrderController implements IInitializable<EditorStep>{
	
	public static Node mount(EditorStep step) throws ComponentException {
		String fXMLPath = Uris.FXML_STEPS_ORDER;
		FXMLFile<Node, EditorStep> fxmlFile = new FXMLFile<>(fXMLPath, step);

		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}	
	
	private static final Image DEFAULT_TOOL_LOGO = new Image(Uris.TOOL_LOGO_IMAGE);
	
	@FXML
	private Label lOrder;
	@FXML
	private Label lTool;
	@FXML
	private Label lCommand;
	@FXML
	private ImageView iVLogo;
	
	private EditorStep step;
	
	@Override
	public void init(EditorStep step) throws ComponentException {
		this.step = step;
		load();
	}
	
	private void load(){
		setOrder(step.getOrder());
		lTool.setText(step.getToolDescriptor().getName());
		lCommand.setText(step.getCommandDescriptor().getName());
		
		iVLogo.setImage(DEFAULT_TOOL_LOGO);
		new LogoLoader(step.getToolDescriptor(), iVLogo).load();
		
		step.orderEvent.addListener(this::setOrder);
	}

	private void setOrder(int order){
		lOrder.setText(Integer.toString(order));
	}

}
