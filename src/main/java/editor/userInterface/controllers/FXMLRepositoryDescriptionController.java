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

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import repository.IRepository;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;

import descriptors.IArgumentDescriptor;
import descriptors.ICommandDescriptor;
import descriptors.IOutputDescriptor;
import descriptors.IToolDescriptor;
import editor.dataAccess.Uris;
import editor.dataAccess.loader.LogoLoader;
import editor.userInterface.utils.Utils;
import exceptions.RepositoryException;


public class FXMLRepositoryDescriptionController implements IInitializable<FXMLRepositoryDescriptionController.Data>{

	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_REPOSITORY_DESCRIPTION;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);
		
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	public static class Data{
		
		public final IRepository repository;
		public final IToolDescriptor tool;
		public final ICommandDescriptor command;
		public final IArgumentDescriptor argument;
		public final IOutputDescriptor output;
		
		public Data(IRepository repository, IToolDescriptor tool, ICommandDescriptor command, IArgumentDescriptor argument, IOutputDescriptor output){
			this.repository = repository;
			this.tool = tool;
			this.command = command;
			this.argument = argument;
			this.output = output;
		}
		
		public Data(IRepository repository, IToolDescriptor tool, ICommandDescriptor command){
			this(repository, tool, command, null, null);
		}
		
		public Data(IRepository repository, IToolDescriptor tool){
			this(repository, tool, null);
		}
		
		public Data(IRepository repository){
			this(repository, null);
		}
	}

	private static final int LOGO_WIDTH = 25;
	private static final int LOGO_HEIGHT = 25;
	
	@FXML
	private TreeView<String> tVTools;
	
	//REPOSITORY
	@FXML
	private Label lRepositoryType;
	@FXML
	private Label lRepositoryLocation;
	
	//TOOL
	@FXML
	private ImageView iVToolLogo;
	@FXML
	private Label lToolName;
	@FXML
	private Label lAuthor;
	@FXML
	private Label lVersion;
	@FXML
	private Label lRequiredMemory;
	@FXML
	private Label lNumberOfCommands;
	@FXML
	private TextArea tAToolDescription;
	@FXML
	private ListView<String> lVDocumentation;
	
	//COMMAND
	@FXML
	private AnchorPane aPCommand;
	@FXML
	private Label lCommandName;
	@FXML
	private Label lCommand;
	@FXML
	private Label lArgumentsComposer;
	@FXML
	private Label lNumberOfArguments;
	@FXML
	private Label lNumberOfOutputs;
	@FXML
	private TextArea tACommandDescription;
	
	//ARGUMENT
	@FXML
	private AnchorPane aPArgument;
	@FXML
	private Label lArgumentName;
	@FXML
	private Label lArgumentType;
	@FXML
	private Label lRequired;
	@FXML
	private TextArea tAArgumentDescription;
	
	//OUTPUT
	@FXML
	private AnchorPane aPOutput;
	@FXML
	private Label lOutputName;
	@FXML
	private Label lOutputType;
	@FXML
	private Label lOutputArgumentName;
	@FXML
	private Label lValue;
	@FXML
	private TextArea tAOutputDescription;
	
	
		
	private IRepository repository;
	private IToolDescriptor initTool;
	private ICommandDescriptor initCommand;
	private IArgumentDescriptor initArgument;
	private IOutputDescriptor initOutput;
	private LogoLoader logoRequest;
	
	
	@Override
	public void init(Data data) throws ComponentException {
		this.repository = data.repository;
		this.initTool = data.tool;
		this.initCommand = data.command;
		this.initArgument = data.argument;
		this.initOutput = data.output;
		load();
	}


	
	private void load() throws ComponentException {
		try{
			loadTree();
		}catch(Exception ex){
			throw new ComponentException("Error loading Repository tree!",ex);
		}
		loadRepositoryInfo();
		loadInitialComponents();	
	}
	
	private void loadRepositoryInfo(){
		lRepositoryType.setText(repository.getType());
		lRepositoryLocation.setText(repository.getLocation());
		Tooltip.install(lRepositoryLocation, new Tooltip(repository.getLocation()));
	}
	
	private void loadInitialComponents() {
		if(initTool!=null)
			loadTool(initTool);
		if(initCommand != null)
			loadCommand(initCommand);
		if(initArgument != null)
			loadArgument(initArgument);
		if(initOutput != null)
			loadOutput(initOutput);
		
		expandTree();
	}
	
	private void expandTree(){
		if(initTool!=null){
			TreeItem<String> tool = getTreeItem(tVTools.getRoot(), initTool.getName());
			selectItem(tool);
			
			if(initCommand!=null){
				TreeItem<String> command = getTreeItem(tool, initCommand.getName());
				selectItem(command);				

				if(initArgument != null)
					selectItem(getTreeItem(command, initArgument.getName()));
				if(initOutput != null)
					selectItem(getOutputItem(command, initOutput.getName()));
			}
		}
	}
	
	private void selectItem(TreeItem<String> item){
		item.expandedProperty().set(true);
		tVTools.getSelectionModel().select(item);
	}
	
	private TreeItem<String> getTreeItem(TreeItem<String> parent, String name){
		for(TreeItem<String> child : parent.getChildren())
			if(child.getValue().equals(name))
				return child;
		
		return null;
	}
	
	private TreeItem<String> getOutputItem(TreeItem<String> command, String name){
		for(int i=command.getChildren().size()-1; i>=0; --i)
			if(command.getChildren().get(i).getValue().equals(name))
				return command.getChildren().get(i);
		
		return null;
	}
	
	private void loadTree() throws RepositoryException {
		TreeItem<String> root = new TreeItem<String>("repository");
		tVTools.setRoot(root);
		loadTreeTools(root);
	}
	
	private void loadTreeTools(TreeItem<String> repositoryItem) throws RepositoryException {
		TreeItem<String> toolItem;
		for(IToolDescriptor tool : repository.getAllTools()){
			toolItem = createTreeItem(tool.getName(), tool, repositoryItem, getToolLoader(tool));
			loadTreeCommands(toolItem, tool);
		}
	}
	
	private void loadTreeCommands(TreeItem<String> toolItem, IToolDescriptor tool) {
		TreeItem<String> commandItem;
		for(ICommandDescriptor command : tool.getCommands()){
			commandItem = createTreeItem(command.getName(), tool, toolItem, getCommandLoader(command));
			loadTreeArguments(commandItem, command);
			loadTreeOutputs(commandItem, command);
		}
	}
	
	private void loadTreeArguments(TreeItem<String> commandItem, ICommandDescriptor command) {
		for(IArgumentDescriptor argument : command.getArguments())
			createTreeItem(argument.getName(), Uris.getResource(Uris.SMALL_IN_IMAGE), commandItem, getArgumentLoader(command, argument));
	}
	
	private void loadTreeOutputs(TreeItem<String> commandItem, ICommandDescriptor command) {
		for(IOutputDescriptor output : command.getOutputs())
			createTreeItem(output.getName(), Uris.getResource(Uris.SMALL_OUT_IMAGE), commandItem, getOutputLoader(command, output));
	}
	
	private TreeItem<String> createTreeItem(String title, IToolDescriptor tool, TreeItem<String> parent, Runnable onSelection) {
		TreeItem<String> item = new TreeItem<String>(title);
		ImageView iVLogo = new ImageView(new Image(Uris.TOOL_LOGO_IMAGE, LOGO_WIDTH, LOGO_HEIGHT, true, true));
		
		tVTools.getSelectionModel().selectedItemProperty().addListener((i)-> {
														if(tVTools.getSelectionModel().getSelectedItem()==item) 
															onSelection.run();
														});
		
		parent.getChildren().add(item);
		
		item.setGraphic(iVLogo);
		iVLogo.fitHeightProperty().setValue(LOGO_HEIGHT);
		iVLogo.fitWidthProperty().setValue(LOGO_WIDTH);

		new LogoLoader(tool, iVLogo).load();
		
		return item;
	}
	
	private TreeItem<String> createTreeItem(String title, String imagePath, TreeItem<String> parent, Runnable onSelection) {
		TreeItem<String> item = new TreeItem<String>(title, new ImageView(new Image(imagePath, LOGO_WIDTH, LOGO_HEIGHT, true, true)));
		
		tVTools.getSelectionModel().selectedItemProperty().addListener((i)-> {
														if(tVTools.getSelectionModel().getSelectedItem()==item) 
															onSelection.run();
														});
		
		parent.getChildren().add(item);
		
		return item;
	}

	
	private Runnable getToolLoader(IToolDescriptor tool){
		return ()->loadTool(tool);
	}
	
	private Runnable getCommandLoader(ICommandDescriptor command){
		return()->{	loadCommand(command);
					cleanArgument();
					cleanOutput();	};
	}
	
	private Runnable getArgumentLoader(ICommandDescriptor command, IArgumentDescriptor argument){
		return()->{	loadCommand(command);
					loadArgument(argument);
					cleanOutput();	};
	}
	
	private Runnable getOutputLoader(ICommandDescriptor command, IOutputDescriptor output){
		return()->{	loadCommand(command);
					loadOutput(output);
					cleanArgument();	};
	}
	
	private void loadTool(IToolDescriptor tool) {
		lToolName.setText(tool.getName());
		lAuthor.setText(tool.getAuthor());
		lVersion.setText(tool.getVersion());
		lRequiredMemory.setText(Integer.toString(tool.getRequiredMemory()));
		lNumberOfCommands.setText(Integer.toString(tool.getCommands().size()));
		
		tAToolDescription.setText(tool.getDescription());
		
		lVDocumentation.setItems(FXCollections.observableArrayList(tool.getDocumentaton()));
		
		if(logoRequest!=null)
			logoRequest.cancel();
		
		iVToolLogo.setImage(new Image(Uris.TOOL_LOGO_IMAGE));
		logoRequest = new LogoLoader(tool, iVToolLogo);
		logoRequest.load();

		Utils.set3DEffect(iVToolLogo, true, true);
	}
	
	private void loadCommand(ICommandDescriptor command){
		lCommandName.setText(command.getName());
		lCommand.setText(command.getCommand());
		lArgumentsComposer.setText(command.getArgumentsComposer());
		lNumberOfArguments.setText(Integer.toString(command.getArguments().size()));
		lNumberOfOutputs.setText(Integer.toString(command.getOutputs().size()));
		tACommandDescription.setText(command.getDescription());
		showCommand();
	}
	
	private void loadArgument(IArgumentDescriptor argument){
		lArgumentName.setText(argument.getName());
		lArgumentType.setText(argument.getType());
		lRequired.setText(Boolean.toString(argument.getRequired()));
		tAArgumentDescription.setText(argument.getDescription());
		showArgument();
	}
	
	private void loadOutput(IOutputDescriptor output){
		lOutputName.setText(output.getName());
		lOutputType.setText(output.getType());
		lOutputArgumentName.setText(output.getArgumentName());
		lValue.setText(output.getValue()== null ? "" : output.getValue());
		tAOutputDescription.setText(output.getDescription());
		showOutput();
	}
	
	@SuppressWarnings("unused")
	private void cleanCommand(){
		lCommandName.setText("");
		lCommand.setText("");
		lArgumentsComposer.setText("");
		lNumberOfArguments.setText("");
		lNumberOfOutputs.setText("");
		tACommandDescription.setText("");
		hideCommand();
	}
	
	private void cleanArgument(){
		lArgumentName.setText("");
		lArgumentType.setText("");
		lRequired.setText("");
		tAArgumentDescription.setText("");
		hideArgument();
	}
	
	private void cleanOutput(){
		lOutputName.setText("");
		lOutputType.setText("");
		lOutputArgumentName.setText("");
		lValue.setText("");
		tAOutputDescription.setText("");
		hideOutput();
	}

	private void hideCommand(){
		aPCommand.setDisable(true);
	}
	
	private void hideArgument(){
		aPArgument.setDisable(true);		
	}
	
	private void hideOutput(){
		aPOutput.setDisable(true);
	}
	
	private void showCommand(){
		aPCommand.setDisable(false);
	}
	
	private void showArgument(){
		aPArgument.setDisable(false);
	}
	
	private void showOutput(){
		aPOutput.setDisable(false);
	}
	
}
