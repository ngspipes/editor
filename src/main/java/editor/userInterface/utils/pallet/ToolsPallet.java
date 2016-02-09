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

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import components.multiOption.Operations;

import descriptors.IToolDescriptor;
import editor.userInterface.controllers.FXMLToolListViewCellController;
import editor.userInterface.controllers.FXMLToolListViewCellController.Data;
import editor.utils.Utils;

public class ToolsPallet extends Pallet<IToolDescriptor>{
	
	private static final String TAG = "ToolsPallet";
	
	private final Function<IToolDescriptor, Operations> getOperations;

	public ToolsPallet(TextField textField, ListView<IToolDescriptor> listView, Consumer<IToolDescriptor> onToolChoose, Function<IToolDescriptor, Operations> getOperations){
		super(textField, listView);
		this.getOperations = getOperations;
		this.listView.getSelectionModel().selectedItemProperty().addListener((obs, prev, curr)->onToolChoose.accept(curr));
	}

	@Override
	protected boolean filter(IToolDescriptor tool, String pattern) {
		return tool.getName().toLowerCase().startsWith(pattern.toLowerCase());
	}

	@Override
	protected Node getCellRoot(IToolDescriptor tool) {
		try {
			return FXMLToolListViewCellController.mount(new Data(tool, getOperations.apply(tool)));
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading Tool Pallet item!");
		}
		
		return null;
	}
	
}
