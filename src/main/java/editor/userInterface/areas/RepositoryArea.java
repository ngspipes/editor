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

import components.Window;
import components.multiOption.Operations;
import components.multiOption.Operations.Operation;
import descriptors.IToolDescriptor;
import editor.EditorOperations;
import editor.userInterface.components.PaneSlider.SlideSide;
import editor.userInterface.controllers.FXMLRepositoryDescriptionController;
import editor.userInterface.utils.pallet.ToolsPallet;
import editor.transversal.EditorException;
import editor.transversal.Log;
import editor.transversal.Utils;
import exceptions.RepositoryException;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import repository.IRepository;

public class RepositoryArea extends Area{

	private static final String TAG = "RepositoryPallet";
	
	private static final String DESCRIPTION_WINDOW_TITLE = "Tool Description";
	private static final String DESCRIPTION_MENU_ITEM = "Description";
	private static final String OPEN_MENU_ITEM = "Open";
	
	
	private final ToolsPallet toolsPallet;
	
	
	public RepositoryArea(	AnchorPane repositoryAreaPane, Button expandButton,
							TextField toolFilter, ListView<IToolDescriptor> toolsListView){
		super(SlideSide.LEFT, repositoryAreaPane, expandButton);
		this.toolsPallet = new ToolsPallet(toolFilter, toolsListView, this::onToolChoose, this::getToolOperations);
	}
	
	public void load(IRepository repository) throws EditorException{
		Log.debug(TAG, "Loading Repository type : " + repository.getType()+ " location : " + repository.getLocation());
		
		try{
			toolsPallet.load(repository.getAllTools());			
		}catch(RepositoryException e){
			throw new EditorException("Error loading Repository " + repository.getLocation(), e);
		}
	}
	
	public void clear(){
		Log.debug(TAG, "Clearing");
		toolsPallet.clear();
	}
	
	private void onToolChoose(IToolDescriptor tool){
		if(tool==null){
			EditorOperations.hideToolArea();	
			return;
		}
		
		Log.debug(TAG, "Tool " + tool.getName() + " chosen");
		EditorOperations.hideToolArea();
		EditorOperations.loadToolArea(tool);
		EditorOperations.slideInToolArea();
	}

	private Operations getToolOperations(IToolDescriptor tool){
		Operations operations = new Operations();
		
		operations.add(new Operation(OPEN_MENU_ITEM, ()->onToolChoose(tool)));
		operations.add(new Operation(DESCRIPTION_MENU_ITEM, ()->{
			try{	
				IRepository repository = tool.getOriginRepository();
				Parent root = (Parent)FXMLRepositoryDescriptionController.mount(new FXMLRepositoryDescriptionController.Data(repository, tool));
				new Window<>(root, DESCRIPTION_WINDOW_TITLE).open();
			} catch(Exception e) {
				Utils.treatException(e, TAG, "Error loading description window");
			}
		}));
		
		return operations;
	}

}
