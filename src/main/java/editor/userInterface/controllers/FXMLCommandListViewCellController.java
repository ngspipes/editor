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

import components.Draggable;
import components.FXMLFile;
import components.animation.changeMouse.ChangeMouseOnPass;
import components.animation.changeMouse.ChangeMouseOnPress;
import components.animation.magnifier.ImageMagnifier;
import components.multiOption.Menu;
import components.multiOption.Operations;
import descriptors.ICommandDescriptor;
import editor.dataAccess.Uris;
import editor.transversal.task.Task;
import editor.userInterface.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import jfxutils.ComponentException;
import jfxutils.IInitializable;
import workflow.elements.Workflow;

public class FXMLCommandListViewCellController implements IInitializable<FXMLCommandListViewCellController.Data> {

	public static class Data{
		public final ICommandDescriptor command;
		public final Operations operations;

		public Data(ICommandDescriptor command, Operations operations){
			this.command = command;
			this.operations = operations;
		}
	}



	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_COMMANDS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	


	private static final String DEFAULT_TOOL_LOGO_URI = Uris.TOOL_LOGO_IMAGE;
	private static final Cursor ON_ENTER_CURSOR = Cursor.OPEN_HAND;
	private static final Cursor ON_EXIT_CURSOR = Cursor.DEFAULT;
	private static final Cursor ON_PRESS_CURSOR = Cursor.CLOSED_HAND;
	private static final Cursor ON_RELEASE_CURSOR = Cursor.OPEN_HAND;
	private static final int IMAGE_WIDTH = 85;
	private static final int IMAGE_HEIGHT = 85;
	private static final double LOGO_MAGNIFICATION = 1.20;
	


	@FXML
	private ImageView iVToolLogo;
	@FXML
	private Label lCommandName;
	@FXML
	private VBox root;

	private ICommandDescriptor command;
	private Operations operations;



	@Override
	public void init(Data data) throws ComponentException {
		this.command = data.command;
		this.operations = data.operations;
		
		loadUIComponents();
	}



	private void loadUIComponents() throws ComponentException {
		lCommandName.setText(command.getName());

		String tip = command.getName() + "\n" + command.getDescription();
		Tooltip.install(root, UIUtils.createTooltip(tip, true, 300, 200));

		new Menu<>(root, operations).mount();

		loadLogoImage();

		loadMouseEvents();
	}

	private void loadLogoImage() {
		iVToolLogo.setImage(new Image(DEFAULT_TOOL_LOGO_URI, IMAGE_WIDTH, IMAGE_HEIGHT, true, true));
		iVToolLogo.fitWidthProperty().setValue(IMAGE_WIDTH);
		iVToolLogo.fitHeightProperty().setValue(IMAGE_HEIGHT);
		new ImageMagnifier<>(iVToolLogo, LOGO_MAGNIFICATION).mount();

		UIUtils.loadLogo(iVToolLogo, command.getOriginTool());

		UIUtils.set3DEffect(iVToolLogo, true, true);
	}

	private void loadMouseEvents() {
		new ChangeMouseOnPress<>(root, ON_PRESS_CURSOR, ON_RELEASE_CURSOR).mount();
		new ChangeMouseOnPass<>(root, ON_ENTER_CURSOR, ON_EXIT_CURSOR).mount();
		mountDraggable();
	}
	
	private void mountDraggable(){
		Draggable<VBox,Object> draggable = new Draggable<>(root, null, null, null, Workflow.GROUP, TransferMode.COPY, new Image(DEFAULT_TOOL_LOGO_URI));
		draggable.setReceives(false);
		draggable.setSends(true);
		draggable.setPermitSelfDrag(false);
		draggable.setInfo(command);
		draggable.mount();

		Task<Image> task = UIUtils.loadLogo(command.getOriginTool());
		task.succeededEvent.addListener(() -> draggable.setDragView(task.getValue()));
	}

}
