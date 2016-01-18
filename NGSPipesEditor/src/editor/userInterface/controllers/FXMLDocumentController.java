package editor.userInterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import dsl.entities.Argument;
import dsl.entities.Output;


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