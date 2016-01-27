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
import editor.userInterface.utils.Utils;


public class FXMLStepController implements IInitializable<FXMLStepController.Data>{

	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_STEP;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	
	public static class Data{
		public final EditorStep step;
		
		public Data(EditorStep step){
			this.step = step;
		}
	}
	
	private static final Image DEFAULT_TOOL_LOGO = new Image(Uris.TOOL_LOGO_IMAGE);
    @FXML
    private ImageView iVToolLogo;
    @FXML
    private Label lCommandName;
    
    private EditorStep step;
    
   
    public void init(Data data) {
    	this.step = data.step;
    	load();
    }
    
    private void load() {
    	iVToolLogo.setImage(DEFAULT_TOOL_LOGO);
    	new LogoLoader(step.getToolDescriptor(), iVToolLogo).load();
    	Utils.set3DEffect(iVToolLogo, true, false);
    	
        lCommandName.setText(step.getCommandDescriptor().getName());
    }

}
