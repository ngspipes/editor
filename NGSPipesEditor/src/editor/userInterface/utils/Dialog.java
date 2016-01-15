package editor.userInterface.utils;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import utils.ComponentException;

import components.Window;

import editor.userInterface.controllers.FXMLChangeRepositoryController;
import editor.userInterface.controllers.FXMLCreateWorkflowController;
import editor.utils.EditorException;



public class Dialog {

    public static void showError(String msg) {
        show(AlertType.ERROR, "Error", msg);
    }

    public static void showWarning(String msg) {
        show(AlertType.WARNING, "Warning", msg);
    }

    public static void showConfirmation(String msg) {
        show(AlertType.CONFIRMATION, "Confirmation", msg);
    }

    public static void showInfo(String msg) {
        show(AlertType.INFORMATION, "Information", msg);
    }

    private static void show(AlertType alertType, String title, String msg) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    public static String getValue(String title, String headerText, String contextText) {
        TextInputDialog dialog = new TextInputDialog("");

        if (title != null && title.length() != 0) 
            dialog.setTitle(title);
        
        if (headerText != null && headerText.length() != 0) 
            dialog.setHeaderText(headerText);
        
        if (contextText != null && contextText.length() != 0) 
            dialog.setContentText(contextText);
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) 
            return result.get();
        else 
        	return null;
    }

    public static ButtonType getSaveConsentiment() {
        String title = "Save Work Flow";
        String header = "Current Work Flow is not saved!";
        String text = "Do you want to save it?\nChoose ok to save or cancel to close without save.";
        
        return getConsentiment(title, header, text);
    }
    
    public static ButtonType getCopyFilesConsentiment() {
    	String title = "Copy files concentiment";
        String header = "Allow copy files";
        String text = "All input files not existent in the specified directory will be copied.\nDo you accept?";
        
        return getConsentiment(title, header, text);
    }
    
    public static ButtonType getConsentiment(String title, String header, String text) {
        Alert alert = new Alert(AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.get();
    }

    public static File getDirectory(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        return chooser.showDialog(null);
    }

    public static File getFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        return chooser.showOpenDialog(null);
    }

    public static void getNewFileDirectory(BiConsumer<String, String> consumer) throws EditorException {
    	try{
    		Window<Parent,?> window = new Window<>((Parent)null, "New File");
    		
    		Parent root = (Parent) FXMLCreateWorkflowController.mount((directory, name)->{
										window.close();
										consumer.accept(directory, name);
									});
    		
    		window.setRoot(root);
    		
    		window.open();
    	}catch(ComponentException ex){
    		throw new EditorException("Error loading CreateWorkflow window!", ex);
    	}
    }

    public static void changeRepository(BiConsumer<String, String> onChange) throws EditorException {
    	try{
    		Window<Parent,?> window = new Window<>((Parent)null, "Change Repository");

    		Parent root = (Parent) FXMLChangeRepositoryController.mount((type, location)->{
						        		window.close();
						        		onChange.accept(type, location);
						        	});
    		
    		window.setRoot(root);
            
        	window.open();	
    	}catch(ComponentException ex){
    		throw new EditorException("Error loading ChangeRepository window!", ex);
    	}
    }

    public static Window<Parent, Void> getLoadingWindow(String title) {
    	ProgressIndicator progressIndicator = new ProgressIndicator();
    	progressIndicator.setProgress(-1.0);	
    	return new Window<Parent, Void>(progressIndicator, title);
    }
    
}