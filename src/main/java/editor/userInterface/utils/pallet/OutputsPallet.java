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
import editor.logic.workflow.Output;
import editor.transversal.Utils;
import editor.userInterface.controllers.FXMLOutputListViewCellController;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.function.Function;

public class OutputsPallet extends Pallet<Output>{
	
	private static final String TAG = OutputsPallet.class.getSimpleName();



	private final Function<Output, Operations> getOperations;



	public OutputsPallet(TextField textFieldFilter, ListView<Output> listView, Function<Output, Operations> getOperations){
		super(textFieldFilter, listView);
		this.getOperations = getOperations;
	}



	@Override
	protected boolean filter(Output output, String pattern) {
		pattern = pattern.toLowerCase();
		String outputName = output.getName();

		return outputName.startsWith(pattern);
	}

	@Override
	protected Node getCellRoot(Output output) {
		try {
			return FXMLOutputListViewCellController.mount(new FXMLOutputListViewCellController.Data(output, getOperations.apply(output)));
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading Output Pallet item!");
		}
		
		return null;
	}

}
