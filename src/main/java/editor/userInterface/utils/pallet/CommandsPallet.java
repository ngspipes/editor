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
package editor.userInterface.utils.pallet;

import components.multiOption.Operations;
import descriptors.ICommandDescriptor;
import editor.userInterface.controllers.FXMLCommandListViewCellController;
import editor.userInterface.controllers.FXMLCommandListViewCellController.Data;
import editor.transversal.Utils;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.function.Function;


public class CommandsPallet extends Pallet<ICommandDescriptor>{

	private static final String TAG = "CommandsPallet";
	
	private final Function<ICommandDescriptor, Operations> getOperations;
	
	public CommandsPallet(	TextField textField, 
							ListView<ICommandDescriptor> listView, 
							Function<ICommandDescriptor, Operations> getOperations) {
		super(textField, listView);
		this.getOperations = getOperations;
	}

	@Override
	protected boolean filter(ICommandDescriptor command, String pattern) {
		return command.getName().toLowerCase().startsWith(pattern.toLowerCase());
	}

	@Override
	protected Node getCellRoot(ICommandDescriptor command) {
		try {
			return FXMLCommandListViewCellController.mount( new Data(command, getOperations.apply(command)));
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading Tool Pallet item!");
		}
		
		return null;
	}
	
}
