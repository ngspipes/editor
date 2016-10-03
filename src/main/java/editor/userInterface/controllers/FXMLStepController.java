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
import editor.userInterface.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;


public class FXMLStepController implements IInitializable<FXMLStepController.Data>{

	public static class Data{
		public final Step step;

		public Data(Step step){
			this.step = step;
		}
	}



	private static final Image DEFAULT_TOOL_LOGO = new Image(Uris.TOOL_LOGO_IMAGE);



	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_STEP;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	


    @FXML
    private ImageView iVToolLogo;
    @FXML
    private Label lCommandName;
    
    private Step step;
    
	
   
    public void init(Data data) {
    	this.step = data.step;
		
    	loadUIComponents();
    }
	
	
    
    private void loadUIComponents() {
    	iVToolLogo.setImage(DEFAULT_TOOL_LOGO);
		UIUtils.loadLogo(iVToolLogo, step.getTool());

    	UIUtils.set3DEffect(iVToolLogo, true, false);
    	
        lCommandName.setText(step.getCommand().getName());
    }

}
