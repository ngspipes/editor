package editor.userInterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import utils.ComponentException;
import utils.IInitializable;
import workflow.elements.Workflow;

import components.Draggable;
import components.FXMLFile;
import components.animation.changeMouse.ChangeMouseOnPass;
import components.animation.changeMouse.ChangeMouseOnPress;
import components.animation.magnifier.ImageMagnifier;
import components.multiOption.Menu;
import components.multiOption.Operations;

import descriptor.ICommandDescriptor;
import editor.dataAccess.Uris;
import editor.dataAccess.loader.LogoLoader;
import editor.userInterface.utils.Utils;

public class FXMLCommandListViewCellController implements IInitializable<FXMLCommandListViewCellController.Data> {

	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_COMMANDS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	public static class Data{
		public final ICommandDescriptor command;
		public final Operations operations;

		public Data(ICommandDescriptor command, Operations operations){
			this.command = command;
			this.operations = operations;
		}
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
		
		loadComponents();
	}

	private void loadComponents() throws ComponentException {
		lCommandName.setText(command.getName());
		
		iVToolLogo.setImage(new Image(DEFAULT_TOOL_LOGO_URI, IMAGE_WIDTH, IMAGE_HEIGHT, true, true));
		new ImageMagnifier<>(iVToolLogo, LOGO_MAGNIFICATION).mount();
		new LogoLoader(command.getOriginTool(), IMAGE_WIDTH, IMAGE_HEIGHT, true, true, iVToolLogo).load();
		
		Utils.set3DEffect(iVToolLogo, true, true);
		
		loadButtonEvents();
	}

	private void loadButtonEvents() throws ComponentException {
		String tip = command.getName() + "\n" + command.getDescription();
		Tooltip.install(root, Utils.createTooltip(tip, true, 300, 200));
		
		new Menu<>(root, operations).mount();
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
		
		new LogoLoader(command.getOriginTool(), (image)->draggable.setDragView(image)).load();
	}

}
