package editor.userInterface.areas;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import utils.ComponentException;

import components.Window;

import editor.EditorOperations;
import editor.dataAccess.Uris;
import editor.dataAccess.repository.EditorRepositoryManager;
import editor.userInterface.utils.Dialog;
import editor.utils.Utils;

public class MenuBar {

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface ItemAnnotation{
		public String menuId();
		public String itemId();
	}

	private static final String TAG = "MenuBar";

	private static final Map<String, KeyCodeCombination> SHORT_CUTS;

	static{
		SHORT_CUTS = new HashMap<>();

		SHORT_CUTS.put("save", new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		SHORT_CUTS.put("saveAll", new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));
		SHORT_CUTS.put("generatePipeline", new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
		SHORT_CUTS.put("generateAllPipelines", new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));
		SHORT_CUTS.put("close", new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
		SHORT_CUTS.put("closeAll", new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));
		SHORT_CUTS.put("_new", new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		SHORT_CUTS.put("open", new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		SHORT_CUTS.put("help", new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
		SHORT_CUTS.put("slideOutAllAreas", new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN));
		SHORT_CUTS.put("slideOutRepositoryArea", new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN));
		SHORT_CUTS.put("slideOutToolArea", new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN));
		SHORT_CUTS.put("slideOutStepArea", new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN));
		SHORT_CUTS.put("slideInAllAreas", new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN, KeyCodeCombination.ALT_DOWN));
		SHORT_CUTS.put("slideInRepositoryArea", new KeyCodeCombination(KeyCode.R, KeyCombination.SHIFT_DOWN, KeyCodeCombination.ALT_DOWN));
		SHORT_CUTS.put("slideInToolArea", new KeyCodeCombination(KeyCode.T, KeyCombination.SHIFT_DOWN, KeyCodeCombination.ALT_DOWN));
		SHORT_CUTS.put("slideInStepArea", new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCodeCombination.ALT_DOWN));
	}



	private final javafx.scene.control.MenuBar menuBar;

	public MenuBar(javafx.scene.control.MenuBar menuBar){
		this.menuBar = menuBar;
		load();
	}

	private void load(){
		for(Method m : this.getClass().getMethods())
			if(isHandler(m))
				setHandler(m);
	}

	private boolean isHandler(Method method){
		return !Modifier.isStatic(method.getModifiers()) &&
				method.getAnnotation(ItemAnnotation.class) != null &&
				method.getParameters().length == 0;
	}

	private void setHandler(Method method){
		ItemAnnotation iA = method.getAnnotation(ItemAnnotation.class);
		MenuItem menuItem = getMenuItem(iA.menuId(), iA.itemId());

		menuItem.setOnAction((event)-> {
			try {
				method.invoke(MenuBar.this, (Object[])null);
			} catch (Exception e) {
				Utils.treatException(e, TAG, "Error excuting action!");
			} 
		});

		if(SHORT_CUTS.containsKey(iA.itemId()))
			menuItem.setAccelerator(SHORT_CUTS.get(iA.itemId()));
	}

	private MenuItem getMenuItem(String menuId, String itemId){
		Menu menu = menuBar.getMenus().filtered((m)->m.getId().equals(menuId)).get(0);
		MenuItem menuItem = menu.getItems().filtered((i)->i.getId().equals(itemId)).get(0);

		return menuItem;
	}


	@ItemAnnotation(menuId = "file", itemId = "open")
	public void fileOpen(){
		EditorOperations.openWorkflow();
	}

	@ItemAnnotation(menuId = "file", itemId = "saveAll")
	public void fileSaveAll(){
		EditorOperations.saveAllWorkflows();
	}

	@ItemAnnotation(menuId = "file", itemId = "save")
	public void fileSave(){
		EditorOperations.saveSelectedWorkflow();
	}

	@ItemAnnotation(menuId = "file", itemId = "closeAll")
	public void fileCloseAll(){
		EditorOperations.closeAllWorkflows();
	}

	@ItemAnnotation(menuId = "file", itemId = "close")
	public void fileClose(){
		EditorOperations.closeSelectedWorkflow();
	}

	@ItemAnnotation(menuId = "file", itemId = "generateAllPipelines")
	public void fileGenerateAllPipelines(){
		EditorOperations.generateAllWorkflows();
	}

	@ItemAnnotation(menuId = "file", itemId = "generatePipeline")
	public void fileGeneratePipeline(){
		EditorOperations.generateSelectedWorkflow();
	}

	@ItemAnnotation(menuId = "file", itemId = "_new")
	public void _new(){
		EditorOperations.createNewWorkflow();
	}

	@ItemAnnotation(menuId = "repository", itemId = "changeRepository")
	public void repositoryChangeRepository(){
		try{
			Dialog.changeRepository((type, location)->{
				EditorRepositoryManager.getRepository(type, location,(ex, repo)->{
					if(ex!=null)
						Utils.treatException(ex, TAG, "Error loading Repository!");
					else
						EditorOperations.loadRepositoryArea(repo);		
				});
			});	
		}catch(Exception e){
			Utils.treatException(e, TAG, "Error changing Repository!");
		}
	}

	@ItemAnnotation(menuId = "help", itemId = "about")
	public void helpAbout(){
		try{
			new Window<>(Uris.FXML_HELP).open();
		}catch(ComponentException ex){
			Utils.treatException(ex, TAG, "Error loading Help window!");
		}
	}

	@ItemAnnotation(menuId = "help", itemId = "shortcuts")
	public void helpShortCuts(){
		try{
			new Window<>(Uris.FXML_SHORTCUTS).open();
		}catch(ComponentException ex){
			Utils.treatException(ex, TAG, "Error loading Shortcuts window!");
		}
	}

	@ItemAnnotation(menuId = "window", itemId = "slideOutAllAreas")
	public void windowSlideOutAllAreas(){
		EditorOperations.slideOutAllAreas();
	}

	@ItemAnnotation(menuId = "window", itemId = "slideOutRepositoryArea")
	public void windowSlideOutRepositoryArea(){
		EditorOperations.slideOutRepositoryArea();
	}

	@ItemAnnotation(menuId = "window", itemId = "slideOutToolArea")
	public void windowSlideOutToolArea(){
		EditorOperations.slideOutToolArea();
	}

	@ItemAnnotation(menuId = "window", itemId = "slideOutStepArea")
	public void windowSlideOutStepArea(){
		EditorOperations.slideOutStepArea();
	}

	@ItemAnnotation(menuId = "window", itemId = "slideInAllAreas")
	public void windowSlideInAllAreas(){
		EditorOperations.slideInAllAreas();
	}

	@ItemAnnotation(menuId = "window", itemId = "slideInRepositoryArea")
	public void windowSlideInRepositoryArea(){
		EditorOperations.slideInRepositoryArea();
	}

	@ItemAnnotation(menuId = "window", itemId = "slideInToolArea")
	public void windowSlideInToolArea(){
		EditorOperations.slideInToolArea();
	}

	@ItemAnnotation(menuId = "window", itemId = "slideInStepArea")
	public void windowSlideInStepArea(){
		EditorOperations.slideInStepArea();
	}

}
