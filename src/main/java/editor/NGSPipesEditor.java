package editor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import editor.dataAccess.Uris;
import editor.userInterface.areas.MenuBar;
import editor.userInterface.areas.RepositoryArea;
import editor.userInterface.areas.StepArea;
import editor.userInterface.areas.ToolArea;
import editor.userInterface.areas.UtilityButtons;
import editor.userInterface.areas.workflowArea.WorkflowArea;
import editor.userInterface.controllers.FXMLDocumentController;
import editor.utils.Log;
import editor.utils.Utils;

public class NGSPipesEditor extends Application {
    
	private static final String TAG = "NGSPipesEditor";
	
	private static boolean load(Stage stage, FXMLDocumentController c){
		try{
			Uris.load();
			
			RepositoryArea repositoryArea = new RepositoryArea(c.repositoryAreaPane, c.expandRepositoryAreaButton, c.tFToolFilter, c.lVTools);
			StepArea stepArea = new StepArea(	c.stepInfoPane, c.tPStepProperties, 
												c.stepAreaPane, c.expandStepAreaButton, 
												c.tFArgumentFilter, c.lVArguments, 
												c.tFOutputFilter, c.lVOutputs);
			ToolArea toolArea = new ToolArea(c.toolAreaPane, c.expandToolAreaButton, c.lVCommands);
			WorkflowArea workflowArea = new WorkflowArea(c.workflowTablePane);
			new UtilityButtons(c.utilityButtonsPane);
			new MenuBar(c.menuBar);
			
			EditorOperations.load(repositoryArea, stepArea, toolArea, workflowArea);
			
			stage.getIcons().add(new Image(Uris.LOGO_IMAGE));
		}catch(Exception ex){
			Utils.treatException(ex, TAG, "Error loading program!");
			return false;
		}
		
		return true;
	}
	
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = editor.userInterface.utils.Utils.getLoader(Uris.FXML_DOCUMENT);
        Parent root = loader.load();	

        if(!load(stage, loader.getController()))
        	return;
        
        Scene scene = new Scene(root);
        
        stage.setOnCloseRequest((event)-> {
        	EditorOperations.closeAllWorkflows();
        	Log.stop();
        	Platform.exit();
        });
        stage.setTitle("NGSPipes Editor");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}