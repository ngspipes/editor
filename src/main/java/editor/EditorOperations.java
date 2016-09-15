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
package editor;

import descriptors.IToolDescriptor;
import dsl.managers.Support;
import editor.dataAccess.Uris;
import editor.logic.FlowManager;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Flow;
import editor.userInterface.areas.RepositoryArea;
import editor.userInterface.areas.StepArea;
import editor.userInterface.areas.ToolArea;
import editor.userInterface.areas.workflowArea.WorkflowArea;
import editor.userInterface.utils.Dialog;
import editor.userInterface.utils.UIUtils;
import editor.utils.EditorException;
import editor.utils.Log;
import editor.utils.Utils;
import javafx.scene.control.ButtonType;
import repository.IRepository;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;


public class EditorOperations {

	private static final String TAG = "EditorOperations";
	
	public static IRepository DEFAULT_REPOSITORY;
	public static RepositoryArea repositoryArea;
	public static StepArea stepArea;
	public static ToolArea toolArea;
	public static WorkflowArea workflowArea;
	
	public static void load(RepositoryArea repositoryArea, StepArea stepArea, ToolArea toolArea, WorkflowArea workflowArea){
		EditorOperations.repositoryArea = repositoryArea;
		EditorOperations.toolArea = toolArea;
		EditorOperations.stepArea = stepArea;
		EditorOperations.workflowArea = workflowArea;
		
		UIUtils._(Support.REPOSITORY_LOCAL, Uris.DEFAULT_REPOSITORY_DIR, (ex, repo)->{
			if(ex!=null)
				Utils.treatException(ex, TAG, "Error loading DEFAULT_REPOSITORY!");
			else{
				DEFAULT_REPOSITORY = repo;
				loadRepositoryArea(DEFAULT_REPOSITORY);	
			}
		});			
	}
	
		
	public static void hideStepArea(){
		stepArea.hide();
	}
	
	public static void showStepArea(){
		stepArea.show();
	}
	
	public static void slideInStepArea(){
		stepArea.slideIn();
	}
	
	public static void slideOutStepArea(){
		stepArea.slideOut();
	}

	public static void showStepAreaArguments(){
		stepArea.showArguments();
	}
	
	public static void showStepAreaOutputs(){
		stepArea.showOutputs();
	}
	
	public static void loadStepArea(EditorStep step){
		try{
			stepArea.load(step);	
		}catch(Exception e){
			Utils.treatException(e, TAG, "Error loading step!");
		}
	}

	public static void clearStepArea(){
		stepArea.clear();
	}
	
	
	
	public static void hideRepositoryArea(){
		repositoryArea.hide();
	}
	
	public static void showRepositoryArea(){
		repositoryArea.show();
	}
	
	public static void slideInRepositoryArea(){
		repositoryArea.slideIn();
	}
	
	public static void slideOutRepositoryArea(){
		repositoryArea.slideOut();
	}
		
	public static void loadRepositoryArea(IRepository repository){
		try{
			repositoryArea.load(repository);	
			clearToolArea();
			slideOutToolArea();
		} catch(Exception e) {
			Utils.treatException(e, TAG, "Error loading repository" + repository.getLocation() + "!");
		}
	}

	public static void loadDefaultRepository(){
		loadRepositoryArea(DEFAULT_REPOSITORY);
	}
	
	public static void clearRepositoryArea(){
		repositoryArea.clear();
	}

	
	
	public static void showToolArea(){
		toolArea.show();
	}
	
	public static void hideToolArea(){
		toolArea.hide();
	}
	
	public static void slideInToolArea(){
		toolArea.slideIn();
	}
	
	public static void slideOutToolArea(){
		toolArea.slideOut();
	}
	
	public static void loadToolArea(IToolDescriptor tool){
		try{
			toolArea.load(tool);
		}catch(Exception ex){
			Utils.treatException(ex, TAG, "Error loading tool " + tool.getName());
		}
	}
	
	public static void clearToolArea(){
		toolArea.clear();
	}
	
	

	public static void hideAllAreas(){
		hideRepositoryArea();
		hideStepArea();
		hideToolArea();
	}
	
	public static void showAllAreas(){
		showRepositoryArea();
		showStepArea();
		showToolArea();
	}

	public static void slideOutAllAreas(){
		slideOutRepositoryArea();
		slideOutStepArea();
		slideOutToolArea();
	}
	
	public static void slideInAllAreas(){
		slideInRepositoryArea();
		slideInStepArea();
		slideInToolArea();
	}

	
	
	public static void createNewWorkflow(){
		try{
			Log.debug(TAG, "Creating new Workflow");
    		Dialog.getNewFileDirectory( (directory, name)->{
				if(name.endsWith(FlowManager.WORK_FLOW_FILE_EXTENSION))
					name = name.replace(FlowManager.WORK_FLOW_FILE_EXTENSION, "");

				String path = directory + Uris.SEP + name + FlowManager.WORK_FLOW_FILE_EXTENSION;

				if(new File(path).exists() && !getPermissionToOverride())
					return;

				openWorkflow(new Flow(name, directory));
			}  );
    	}catch(Exception e){
    		Utils.treatException(e, TAG, "Error creating new File!");
    	}
	}

	private static boolean getPermissionToOverride() {
		ButtonType permission = Dialog.getOverridePermission();

		if(permission.equals(ButtonType.OK)){
			Log.debug(TAG, "User accepted to override Workflow");
			return true;
		}

		return false;
	}

	public static void generateSelectedWorkflow(){
		Flow workflow = workflowArea.getSelectedWorkflow();
		
		generateWorkflow(workflow);
	}
	
	public static void generateAllWorkflows() {
		workflowArea.getAllWorkflows().forEach(EditorOperations::generateWorkflow);
	}
	
	public static void generateWorkflow(Flow flow){
		try{ 
			File inputsDir = Dialog.getDirectory("Indicate inputs Directory");
			
			if(inputsDir == null)
				return; 
			
			ButtonType permission = Dialog.getCopyFilesPermission();
			
			if(permission.equals(ButtonType.OK)){
				FlowManager.generate(flow, inputsDir);
				Dialog.showConfirmation("Generated successfully!");
			}
			
		} catch(Exception ex) {
			Utils.treatException(ex, TAG, "Error generating pipeline!");
		}	
	}
	
	public static void saveSelectedWorkflow() {
		Flow flow = workflowArea.getSelectedWorkflow();
		try{
			if(flow != null)
				FlowManager.save(flow);
		}catch(Exception e ){
			Utils.treatException(e, TAG, "Error saving workflow " + flow.getDirectory() + "/" + flow.getName() + "!");
		}
	}
	
	public static void saveAllWorkflows() {
		for(Flow flow : workflowArea.getAllWorkflows())
			try{
				FlowManager.save(flow);
			}catch(Exception e ){
				Utils.treatException(e, TAG, "Error saving workflow " + flow.getDirectory() + "/" + flow.getName() + "!");
			}	
	}
	
	public static void openAllWorkflows(Collection<Flow> workflows){
		workflows.forEach(EditorOperations::openWorkflow);
	}
	
	public static void openWorkflow(){
		File file = Dialog.getFile("Pipeline");
        
        if(file==null) 
        	return;
        
        try{
        	if(!file.getName().endsWith(FlowManager.WORK_FLOW_FILE_EXTENSION))
    			throw new EditorException("Workflow must be a " + FlowManager.WORK_FLOW_FILE_EXTENSION + " file!");
        	
        	String name = file.getName().replace(FlowManager.WORK_FLOW_FILE_EXTENSION, "");
        	String directory = file.getParent();
        	
        	FlowManager.load(name, directory, (ex, flow)->{
        		if(ex!=null)
        			Utils.treatException(ex, TAG, "Error loading workflow " + file.getAbsolutePath());
        		else
        			openWorkflow(flow);	
        	});
        }catch(Exception e){
        	Utils.treatException(e, TAG, "Error loading workflow " + file.getAbsolutePath());
        }
	}
	
	public static void openWorkflow(Flow workflow){
		if(workflow.getRepository()!=null)
			loadRepositoryArea(workflow.getRepository());
		
		workflowArea.open(workflow);		
	}

	public static void closeSelectedWorkflow() {
		Flow workflow = workflowArea.getSelectedWorkflow();
		
		if(workflow != null)
			closeWorkflow(workflow);
	}
	
	public static void closeAllWorkflows(){
		Collection<Flow> openedWorkflows = new LinkedList<>(workflowArea.getAllWorkflows());
		openedWorkflows.forEach(EditorOperations::closeWorkflow);
	}
	
	public static void closeWorkflow(Flow workflow){
    	try{
    		if(getPermissionToClose(workflow))
    			workflowArea.close(workflow);
    	}catch(Exception e){
    		Utils.treatException(e, TAG, "Error closing workflow!");
    	}
	}

	private static boolean getPermissionToClose(Flow flow) throws EditorException {
	    if(flow.getSaved())
	       return true;
	    
	    ButtonType permission = Dialog.getSavePermission();
	    if(permission.equals(ButtonType.OK)){
	    	Log.debug(TAG, "User accepted to save Workflow");
	    	FlowManager.save(flow);
	        return true;
	    }
	    
	    if(permission.equals(ButtonType.CANCEL)){
	    	Log.debug(TAG, "User discarded changes made on Workflow");
	    	return true;
	    }
	    
	    Log.debug(TAG, "User didn't answered to save Workflow");
	    return false;
	}
	
}