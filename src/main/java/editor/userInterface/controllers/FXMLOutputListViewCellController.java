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
package editor.userInterface.controllers;

import components.FXMLFile;
import components.multiOption.Menu;
import components.multiOption.Operations;
import dsl.entities.Output;
import editor.dataAccess.Uris;
import editor.userInterface.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

public class FXMLOutputListViewCellController implements IInitializable<FXMLOutputListViewCellController.Data> {

	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_OUTPUTS_LIST_VIEW_ITEM;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	
	
	public static class Data{
		public final Output output;
		public final Operations operations;
		
		public Data(Output output, Operations operations) {
			this.output =  output;
			this.operations = operations;
		}
	}

	@FXML
	private Label lOutputName;
	
	@FXML
	private Label lOutputValue;

	@FXML
	private AnchorPane root;
	
	private Output output;
	private Operations operations;
	private Tooltip valueTip;

	@Override
	public void init(Data data) {
		this.output = data.output;
		this.operations = data.operations;
		load();
		output.valueChangedEvent.addListener(this::showValue);
	}
	
	private void load(){
		lOutputName.setText(output.getDescriptor().getName());
		
		String nameTip = output.getName() + "\n" + output.getDescriptor().getDescription();
		Tooltip.install(lOutputName, UIUtils.createTooltip(nameTip, true, 300, 200));
		
		valueTip = UIUtils.createTooltip("", true, 300, 200);
		Tooltip.install(lOutputValue, valueTip);
		
		showValue(output.getValue());
		
		new Menu<Node>(root, operations).mount();
	}
	
	private void showValue(String value){
		if(value==null)
			value="";
		
		if(!value.equals(lOutputValue.getText())){
			lOutputValue.setText(value);
			valueTip.setText(value);
		}
	}
	
}
