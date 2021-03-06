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
package editor.userInterface.utils;

import components.Window;
import editor.transversal.EditorException;
import editor.userInterface.controllers.FXMLChangeRepositoryController;
import editor.userInterface.controllers.FXMLCreateWorkflowController;
import editor.userInterface.controllers.FXMLLoadController;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import jfxutils.ComponentException;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;



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

    public static ButtonType getOverridePermission() {
        String title = "Override Pipeline";
        String header = "There is a pipeline with same name in this directory!";
        String text = "Do you want override the existent one?";

        return getPermission(title, header, text);
    }

    public static ButtonType getSavePermission() {
        String title = "Save Pipeline";
        String header = "Current Pipeline is not saved!";
        String text = "Do you want to save it?\nChoose ok to save or cancel to close without save.";
        
        return getPermission(title, header, text);
    }
    
    public static ButtonType getCopyFilesPermission() {
    	String title = "Permission to copy files";
        String header = "Allow copy files";
        String text = "All input files not existent in the specified directory will be copied.\nDo you accept?";
        
        return getPermission(title, header, text);
    }
    
    public static ButtonType getPermission(String title, String header, String text) {
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

    public static Window<Parent, Void> getLoadingWindow(String title,
                                                        BlockingQueue messageQueue,
                                                        AtomicBoolean finish) throws EditorException {
        try{
            Window<Parent,Void> window = new Window<>((Parent)null, title);

            Parent root = (Parent) FXMLLoadController.mount(new FXMLLoadController.Data(messageQueue, finish));

            window.setRoot(root);

            return window;
        } catch(ComponentException ex) {
            throw new EditorException("Error loading Load window!", ex);
        }
    }
    
}
