package editor.userInterface.controllers;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.ComponentException;
import utils.IInitializable;

import components.FXMLFile;

import dsl.managers.Support;
import editor.dataAccess.Uris;
import editor.userInterface.utils.Dialog;

public class FXMLChangeRepositoryController implements IInitializable<BiConsumer<String, String>>{
    
	public static Node mount(BiConsumer<String, String> onChange) throws ComponentException {
		String fXMLPath = Uris.FXML_CHANGE_REPOSITORY;
		FXMLFile<Node, BiConsumer<String, String>> fxmlFile = new FXMLFile<>(fXMLPath, onChange);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	
	private static final Image REMOTE_IMAGE = new Image(Uris.REMOTE_REPOSITORY_IMAGE);
	private static final Image LOCAL_IMAGE = new Image(Uris.LOCAL_REPOSITORY_IMAGE);
	private static final Image GITHUB_IMAGE = new Image(Uris.GITHUB_REPOSITORY_IMAGE);
    private static final String LOCAL_TYPE = Support.REPOSITORY_LOCAL;
    private static final String REMOTE_TYPE = Support.REPOSITORY_REMOTE;
    private static final String GITHUB_TYPE = Support.REPOSITORY_GITHUB;
    private static final String DEFAULT_TYPE = "Default";
    private static final List<String> TYPES;
    
    static{
        TYPES = new LinkedList<>();
        TYPES.add(LOCAL_TYPE);
        TYPES.add(REMOTE_TYPE);
        TYPES.add(DEFAULT_TYPE);
        TYPES.add(GITHUB_TYPE);
    }
    
    @FXML
    private ImageView iVRight;
    @FXML
    private ImageView iVLeft;
    @FXML
    private TextField txtFPath;
    @FXML
    private Button bSearch;
    @FXML
    private TextField txtFURL;
    @FXML
    private Button bChange;
    @FXML
    private ComboBox<String> cBDirectoryType;
    
    private final Map<String, Runnable> handleTypes;
    private final Map<String, Runnable> handleChange;
    
    private BiConsumer<String, String> onChange;
    
    
    @FXML
    private void onSearchClick(ActionEvent event) {
       File selectedDirectory = Dialog.getDirectory("Repository Directory");
       
       String path = selectedDirectory.getAbsolutePath();   
       if(path != null && path.length()>0)
           txtFPath.setText(path);
    }
    
    @FXML
    private void onChangeClick(ActionEvent event) {
       handleChange.get(cBDirectoryType.getValue()).run();
    }
    
    @FXML
    private void onDirectoryTypeClick(ActionEvent event) {
       handleTypes.get(cBDirectoryType.getValue()).run();
    }
    
    
    //Modes
    
    private void remoteMode(){
        txtFURL.setDisable(false);
        txtFPath.setDisable(true);
        bSearch.setDisable(true);
        bChange.setDisable(false);
        iVLeft.setImage(REMOTE_IMAGE);
        iVRight.setImage(REMOTE_IMAGE);
    }
    
    private void githubMode(){
        txtFURL.setDisable(false);
        txtFPath.setDisable(true);
        bSearch.setDisable(true);
        bChange.setDisable(false);
        iVLeft.setImage(GITHUB_IMAGE);
        iVRight.setImage(GITHUB_IMAGE);
    }
    
    private void localMode(){
        txtFURL.setDisable(true);
        txtFPath.setDisable(false);
        bSearch.setDisable(false);
        bChange.setDisable(false);
        iVLeft.setImage(LOCAL_IMAGE);
        iVRight.setImage(LOCAL_IMAGE);
    }
    
    private void defaultMode(){
        txtFURL.setDisable(true);
        txtFPath.setDisable(true);
        bSearch.setDisable(true);
        bChange.setDisable(false);
        iVLeft.setImage(LOCAL_IMAGE);
        iVRight.setImage(LOCAL_IMAGE);
    }
    
    
    //Handlers
    
    private void defaultChangeHandle(){
    	onChange.accept(LOCAL_TYPE, Uris.DEFAULT_REPOSITORY_DIR);
    }
    
    private void localChangeHandle(){
    	String path = txtFPath.getText();
    	
    	if(path==null || path.isEmpty())
    		Dialog.showError("Invalid Path!");
    	else
    		onChange.accept(LOCAL_TYPE, path);
    }
    
    private void remoteChangeHandle(){
    	String uri = txtFURL.getText();
    	
    	if(uri==null || uri.isEmpty())
    		Dialog.showError("Invalid URI!");
    	else	
    		onChange.accept(REMOTE_TYPE, uri);
    }
    
    private void githubChangeHandle(){
    	String uri = txtFURL.getText();
    	
    	if(uri==null || uri.isEmpty())
    		Dialog.showError("Invalid URI!");
    	else	
    		onChange.accept(GITHUB_TYPE, uri);
    }
    
    public FXMLChangeRepositoryController(){
        handleTypes = new HashMap<>();
        handleTypes.put(LOCAL_TYPE, this::localMode);
        handleTypes.put(REMOTE_TYPE, this::remoteMode);
        handleTypes.put(DEFAULT_TYPE, this::defaultMode);
        handleTypes.put(GITHUB_TYPE, this::githubMode);
        
        handleChange = new HashMap<>();
        handleChange.put(LOCAL_TYPE, this::localChangeHandle);
        handleChange.put(REMOTE_TYPE, this::remoteChangeHandle);
        handleChange.put(DEFAULT_TYPE, this::defaultChangeHandle);
        handleChange.put(GITHUB_TYPE, this::githubChangeHandle);
    }
    
    @Override
    public void init(BiConsumer<String, String> onChange) {
        this.onChange = onChange;
        cBDirectoryType.setItems(FXCollections.observableArrayList(TYPES));
    }
    
}

