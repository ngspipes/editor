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

import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import dsl.entities.Argument;
import dsl.entities.Output;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;


public class FXMLDocumentController {
	
	@FXML
	public MenuBar menuBar;
	
	@FXML
	public AnchorPane utilityButtonsPane;
	
	@FXML
	public TabPane workflowTablePane;
	
	@FXML
	public Button expandRepositoryAreaButton;
	@FXML
	public Button expandStepAreaButton;
	@FXML
	public Button expandToolAreaButton;

	@FXML
	public AnchorPane repositoryAreaPane;
	@FXML
	public AnchorPane stepAreaPane;	
	@FXML 
	public AnchorPane toolAreaPane;
	
	@FXML
	public TabPane tPStepProperties;
	
	@FXML 
	public AnchorPane stepInfoPane;
	
	@FXML
	public TextField tFToolFilter;
	@FXML
	public TextField tFArgumentFilter;
	@FXML
	public TextField tFOutputFilter;
	
	@FXML
	public ListView<IToolDescriptor> lVTools;
	@FXML
	public ListView<Argument> lVArguments;
	@FXML
	public ListView<Output> lVOutputs;
	@FXML
	public ListView<ICommandDescriptor> lVCommands;
}