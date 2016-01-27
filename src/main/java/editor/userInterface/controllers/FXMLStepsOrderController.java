package editor.userInterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;

import editor.dataAccess.Uris;
import editor.dataAccess.loader.LogoLoader;
import editor.logic.entities.EditorStep;

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
		
		step.orderEvent.addListner(this::setOrder);
	}
	
	private void setOrder(int order){
		lOrder.setText(order + "�");
	}
		
}
