package editor.userInterface.areas;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import repository.IRepository;

import components.Window;
import components.multiOption.Operations;
import components.multiOption.Operations.Operation;

import descriptors.IToolDescriptor;
import editor.EditorOperations;
import editor.userInterface.components.PaneSlider.SlideSide;
import editor.userInterface.controllers.FXMLRepositoryDescriptionController;
import editor.userInterface.utils.pallet.ToolsPallet;
import editor.utils.EditorException;
import editor.utils.Log;
import editor.utils.Utils;
import exceptions.RepositoryException;

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
		
		Log.debug(TAG, "Tool " + tool.getName() + " choosen");
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
