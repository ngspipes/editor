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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;
import components.animation.changeMouse.ChangeMouseOnPass;
import components.animation.magnifier.ImageMagnifier;
import components.multiOption.Menu;
import components.multiOption.Operations;

import descriptors.IToolDescriptor;
import editor.dataAccess.Uris;
import editor.dataAccess.loader.LogoLoader;
import editor.userInterface.utils.Utils;

public class FXMLToolListViewCellController implements IInitializable<FXMLToolListViewCellController.Data> {

	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_TOOLS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	
	public static class Data{
		public final IToolDescriptor tool;
		public final Operations operations;
		
		public Data(IToolDescriptor tool, Operations oparations){
			this.tool = tool;
			this.operations = oparations;
		}
	}
	
	private static final int IMAGE_WIDTH = 85;
	private static final int IMAGE_HEIGHT = 85;
	private static final Image DEFAULT_TOOL_LOGO = new Image(Uris.TOOL_LOGO_IMAGE, IMAGE_WIDTH, IMAGE_HEIGHT, true, true);
	private static final double ITEM_MAGNIFICATION = 1.20;
	

	@FXML
	private ImageView iVToolLogo;
	@FXML
	private Label lToolName;
	@FXML
	private VBox root;
	
	
	private IToolDescriptor tool;
	private Operations operations;

	@Override
	public void init(Data data) throws ComponentException {
		this.tool = data.tool;
		this.operations = data.operations;
		
		loadComponents();
	}

	private void loadComponents() throws ComponentException {
		lToolName.setText(tool.getName());
		
		iVToolLogo.setImage(DEFAULT_TOOL_LOGO);
		new ImageMagnifier<>(iVToolLogo, ITEM_MAGNIFICATION).mount();
		new LogoLoader(tool, IMAGE_WIDTH, IMAGE_HEIGHT, true, true, iVToolLogo).load();
		
		Utils.set3DEffect(iVToolLogo, true, false);
		
		String tip = tool.getName() + "\n" + tool.getDescription();
		Tooltip.install(root, Utils.createTooltip(tip, true, 300, 200));
		
		loadButtonEvents();
	}
	
	private void loadButtonEvents() throws ComponentException {		
		new Menu<>(root, operations).mount();
		new ChangeMouseOnPass<>(root, Cursor.HAND, Cursor.DEFAULT).mount();
	}

}
