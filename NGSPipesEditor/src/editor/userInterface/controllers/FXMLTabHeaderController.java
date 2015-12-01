package editor.userInterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.ComponentException;
import utils.IInitializable;

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
		
		workflow.saveEvent.addListner(this::setSavedImage);
		
		workflow.nameEvent.addListner((newName)->lName.setText(newName));
		
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
