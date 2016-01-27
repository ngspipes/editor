package editor.userInterface.controllers;

import java.io.File;
import java.util.function.BiConsumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;

import editor.dataAccess.Uris;
import editor.userInterface.utils.Dialog;

public class FXMLCreateWorkflowController implements IInitializable<BiConsumer<String, String>>{
    
	public static Node mount(BiConsumer<String, String> onCreate) throws ComponentException {
		String fXMLPath = Uris.FXML_CREATE_WORKFLOW;
		FXMLFile<Node, BiConsumer<String, String>> fxmlFile = new FXMLFile<>(fXMLPath, onCreate);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	
    @FXML
    private TextField txtFName;
    @FXML
    private Button bSearch;
    @FXML
    private TextField txtFDirectory;
    @FXML
    private Button bCreate;

    private BiConsumer<String, String> onCreate;
    
    @FXML
    private void onSearchClick(ActionEvent event) {
       File selectedDirectory = Dialog.getDirectory("Repository Directory");
       
       String path = selectedDirectory.getAbsolutePath();   
       if(path != null && path.length()>0)
           txtFDirectory.setText(path);
    }
    
    @FXML
    private void onCreateClick(ActionEvent event) {
       String directory = txtFDirectory.getText();
       String name = txtFName.getText();
       
       if(directory == null || directory.isEmpty() || !new File(directory).exists()){
    	   Dialog.showError("Invalid directory!");
    	   return;
       }

       if(directory == null || directory.isEmpty() || name == null || name.isEmpty()){
    	   Dialog.showError("Invalid name!");
    	   return;
       }
           
       onCreate.accept(directory, name);
    }

    @Override
    public void init(BiConsumer<String, String> onCreate){
        this.onCreate = onCreate;
    }
    
}