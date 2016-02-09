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
package editor.userInterface.controllers.help;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;

import editor.dataAccess.Uris;

public class FXMLVideoListViewItemController implements IInitializable<String> {
	
	public static Node mount(String videoName) throws ComponentException {
		
		FXMLFile<Node, String> fxmlFile = new FXMLFile<>(Uris.FXML_VIDEOS_LIST_VIEW_ITEM, videoName);
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	public static Node mount() throws ComponentException {
		return mount(null);
	} 

	@FXML
	public Label lVideoName;
	
	@FXML
	public ImageView ivVideoFile;
	
	@Override
	public void init(String name) throws ComponentException {
		lVideoName.setText(name);
	}

}
