package editor.userInterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.ComponentException;
import utils.IInitializable;

import components.FXMLFile;

import editor.dataAccess.Uris;
import editor.dataAccess.loader.LogoLoader;
import editor.logic.entities.EditorChain;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Elements;

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
	private Label lChainsFrom;
	@FXML
	private Label lChainsTo;
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
		setChainsFrom();
		setChainsTo();
		
		iVLogo.setImage(DEFAULT_TOOL_LOGO);
		new LogoLoader(step.getToolDescriptor(), iVLogo).load();
		
		step.orderEvent.addListner(this::setOrder);
		Elements elements = step.getOriginFlow().getElements();
		elements.addChainEvent.addListner(this::setChains);
		elements.removeChainEvent.addListner(this::setChains);
	}
	
	private void setOrder(int order){
		lOrder.setText(order + "º");
	}
	
	private void setChains(EditorChain chain){
		if(chain.getFrom() == step)
			setChainsFrom();
		if(chain.getTo() == step)
			setChainsTo();
	}
	
	private void setChainsFrom(){
		if(step.getOriginFlow() == null)
			lChainsFrom.setText("");
		else
			lChainsFrom.setText(step.getOriginFlow().getElements().getChainsFrom(step).size() + "");
	}
	
	private void setChainsTo(){
		if(step.getOriginFlow() == null)
			lChainsTo.setText("");
		else
			lChainsTo.setText(step.getOriginFlow().getElements().getChainsTo(step).size() + "");
	}
	
}
