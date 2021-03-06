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
package editor.userInterface.areas;

import components.mounter.ButtonMounter;
import editor.EditorOperations;
import editor.transversal.Utils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import jfxutils.ComponentException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class UtilityButtons {
    
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ButtonAnnotation{
        String buttonId();
        String buttonLegend();
    }
    
    private static final double BUTTON_MAGNIFICATION = 1.20;
    private static final String TAG = "UtilityButtons";
    
    private final AnchorPane utilityButtonsPane;
    
    public UtilityButtons(AnchorPane utilityButtonsPane){
        this.utilityButtonsPane = utilityButtonsPane;
        load();
    }
    
    private void load(){
    	try{
    		for(Method m : this.getClass().getMethods())
                if(isHandler(m))
                    loadButton(m);	
    	} catch(Exception e) {
    		Utils.treatException(e, TAG, "Error loading utility buttons!");
    	}
    }
    
    private boolean isHandler(Method method) {
        return !Modifier.isStatic(method.getModifiers()) &&
                method.getAnnotation(ButtonAnnotation.class) != null &&
                method.getParameters().length == 0;
    }
    
    private void loadButton(Method method) throws ComponentException {
        ButtonAnnotation bA = method.getAnnotation(ButtonAnnotation.class);
        Button button = getButton(bA.buttonId());
        
        new ButtonMounter<>(button)
        		.buttonMagnifier(BUTTON_MAGNIFICATION)
        		.tip(bA.buttonLegend())
        		.mount();
        
        setOnAction(button, method); 
    }
    
    private void setOnAction(Button button, Method method){
    	 button.setOnAction((event)->{
             try{
                 method.invoke(UtilityButtons.this, (Object[])null);
             }catch(Exception e){
            	 Utils.treatException(e, TAG, "Error executing action!");
             }
         });
    }
    
    private Button getButton(String buttonId){
        ObservableList<Node> buttonChild = utilityButtonsPane.getChildren().filtered(
        																(child)->child.getId().equals(buttonId) && child instanceof Button);
        
        return (Button) buttonChild.get(0);
    }
  
 
    @ButtonAnnotation(buttonId = "openBtn", buttonLegend="Open")
    public void open() {
    	EditorOperations.openWorkflow();
    }
    
    @ButtonAnnotation(buttonId = "saveAllBtn", buttonLegend="Save All")
    public void saveAll() {
    	EditorOperations.saveAllWorkflows();
    }
    
    @ButtonAnnotation(buttonId = "saveBtn", buttonLegend="Save")
    public void save() {
    	EditorOperations.saveSelectedWorkflow();
    }
    
    @ButtonAnnotation(buttonId = "closeAllBtn", buttonLegend="Close All")
    public void closeAll() {
    	EditorOperations.closeAllWorkflows();
    }
    
    @ButtonAnnotation(buttonId = "closeBtn", buttonLegend="Close")
    public void close() {
    	EditorOperations.closeSelectedWorkflow();
    }
    
    @ButtonAnnotation(buttonId = "_newBtn", buttonLegend="New")
    public void _new() {
    	EditorOperations.createNewWorkflow();
    }
    
    @ButtonAnnotation(buttonId = "generateBtn", buttonLegend="Generate")
    public void generate() {
    	EditorOperations.generateSelectedWorkflow();
    }
    
    @ButtonAnnotation(buttonId = "generateAllBtn", buttonLegend="Generate All")
    public void generateAll() {
    	EditorOperations.generateAllWorkflows();
    }
    
}
