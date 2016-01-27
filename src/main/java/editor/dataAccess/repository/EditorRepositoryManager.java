package editor.dataAccess.repository;

import java.util.function.BiConsumer;

import javafx.application.Platform;
import repository.IRepository;
import rutils.Cache;

import components.Window;

import dsl.managers.Support;
import editor.userInterface.utils.Dialog;
import editor.utils.EditorException;
import editor.utils.Utils;
import exceptions.RepositoryException;


public class EditorRepositoryManager {
	
	private static final Cache<String, IRepository> CACHE = new Cache<>();
	private static final String TAG = "EditorRepositoryManager";
	
	public static void getRepository(String type, String location, BiConsumer<Exception, IRepository> callback) {
		Window<?,?> progress = null;
		try{
			progress = Dialog.getLoadingWindow("Loading Workflow");
			progress.open();
		}catch(Exception ex){
			Utils.treatException(ex, TAG, "Error loading progress Window!");
		}
		
		final Window<?,?> progressWindow = progress;
		
		new Thread(()->{
			IRepository repo = null;
			Exception ex = null;
			
			String key = "TYPE_" + type + "_LOCAL_" + location;
			try {
				if((repo = CACHE.get(key)) == null){
					repo = get(type, location);
					CACHE.add(key, repo);
				}				
			} catch (EditorException e) {
				ex = e;
			}

			IRepository repository = repo;
			Exception exception = ex;
			
			Platform.runLater(()-> {
				if(progressWindow != null)
					progressWindow.close();
				
				callback.accept(exception, repository);	
			});
		}).start();
	}
	
	private static EditorRepository get(String type, String location) throws EditorException {
		try{
			IRepository repository = Support.getRepository(type, location);
			return new EditorRepository(repository);
		}catch(RepositoryException ex){
			throw new EditorException("Error loading repository!", ex);
		}
	}

}
