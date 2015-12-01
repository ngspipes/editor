package editor.logic.entities;

import repository.IRepository;
import utils.Event;
import editor.utils.EditorException;
import editor.utils.Log;

public class Flow {

	private static final String TAG = "Flow";
	
	public final Event<Boolean> saveEvent;
	private boolean saved;
	public boolean getSaved(){ return saved; }
	public void setSaved(boolean saved){ 
		this.saved = saved;
		saveEvent.trigger(saved);
	}
	
	public final Event<String> nameEvent;
	private String name;
	public String getName(){ return name; }
	public void setName(String name){
		this.name = name;
		setSaved(false);
		nameEvent.trigger(name);
	}

	public final Event<String> directoryEvent;
	private String directory;
	public String getDirectory(){ return directory; }
	public void setDirectory(String directory){
		this.directory = directory;
		setSaved(false);
		directoryEvent.trigger(directory);
	}

	private IRepository repository;
	public IRepository getRepository(){ return repository; }
	
	private final Elements elements;
	public Elements getElements(){ return elements; }
	
	
	
	public Flow(String name, String directory, Elements elements){
		Log.debug(TAG, "Instanciating name : " + name + " directory : " + directory);
		
		this.saveEvent = new Event<>();
		this.nameEvent = new Event<>();
		this.name = name;
		this.directoryEvent = new Event<>();
		this.directory = directory;
		this.elements = elements;
		loadInitialElements();
	}

	public Flow(String name, String directory){
		this(name, directory, new Elements());
	}

	private void loadInitialElements(){
		elements.getSteps().forEach(this::addStep);
		elements.getChains().forEach(this::addChain);
	}

	
	
	public void addStep(EditorStep step) {
		Log.debug(TAG, "Adding Step(command : " + step.getCommandDescriptor().getName() + " from tool : " + step.getToolDescriptor().getName() + ") on " + getLogId());
		
		step.setOriginFlow(this);
		
		if(repository == null)
			repository = step.getRepository();

		elements.addStep(step);
		
		step.saveEvent.addListner((stepSaved)->{
			if(!stepSaved && this.saved)
				setSaved(false);
		});
		
		setSaved(false);
	}

	public void removeStep(EditorStep step){
		Log.debug(TAG, "Removing Step(command : " + step.getCommandDescriptor().getName() + " from tool : " + step.getToolDescriptor().getName() + ") on " + getLogId());
		
		step.setOriginFlow(null);
		
		elements.removeStep(step);

		if(!elements.hasSteps())
			repository = null;
		
		setSaved(false);
	}

	public void addChain(EditorChain chain){
		chain.connect();
		elements.addChain(chain);
		setSaved(false);
	}
	
	public void removeChain(EditorChain chain){
		chain.disconnect();
		elements.removeChain(chain);
		setSaved(false);
	}

	
	
	public void validateStepAddition(EditorStep step) throws EditorException {
		if(repository!=null && !step.getRepository().equals(repository))
			throw new EditorException("You can't add commands from diferent Repositories!");
	}
	
	public void validateStepRemotion(EditorStep step) throws EditorException {}
	
	public void validateChainAddition(EditorChain chain) throws EditorException {}
	
	public void validateChainRemotion(EditorChain chain) throws EditorException {}

	
	
	private String getLogId(){
		return "name : " + name + " directory : " + directory;
	}
	
}