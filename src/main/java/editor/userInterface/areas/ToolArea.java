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
import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import editor.transversal.Log;
import editor.transversal.Utils;
import editor.userInterface.components.PaneSlider.SlideSide;
import editor.userInterface.controllers.FXMLRepositoryDescriptionController;
import editor.userInterface.utils.pallet.CommandsPallet;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import repository.IRepository;


public class ToolArea extends Area{
	
	private static final String TAG = ToolArea.class.getSimpleName();
	
	private static final String DESCRIPTION_WINDOW_TITLE = "Command Description";
	private static final String DESCRIPTION_MENU_ITEM = "Description";
	

	private final CommandsPallet commandPallet;
	
	public ToolArea(AnchorPane toolAreaPane, Button expandButton, ListView<ICommandDescriptor> commandsListView){
		super(SlideSide.DOWN, toolAreaPane, expandButton);
		this.commandPallet = new CommandsPallet(new TextField(), commandsListView, this::getCommandOperations);
	}
	
	public void load(IToolDescriptor tool) {
		Log.debug(TAG, "Loading Tool : " + tool.getName());
		commandPallet.load(tool.getCommands());
	}
	
	public void clear(){
		Log.debug(TAG, "Clearing");
		commandPallet.clear();
	}

	private Operations getCommandOperations(ICommandDescriptor command){
		Operations operations = new Operations();
		
		operations.add(new Operation(DESCRIPTION_MENU_ITEM, ()->{
			try{	
				IRepository repository = command.getOriginTool().getOriginRepository();
				IToolDescriptor tool = command.getOriginTool();
				
				Parent root = (Parent)FXMLRepositoryDescriptionController.mount(new FXMLRepositoryDescriptionController.Data(repository, tool, command));
				new Window<>(root, DESCRIPTION_WINDOW_TITLE).open();
			} catch(Exception e) {
				Utils.treatException(e, TAG, "Error loading description window");
			}
		}));
		
		return operations;
	}

}
