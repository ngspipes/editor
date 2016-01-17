package editor.dataAccess.repository;

import java.util.List;

import repository.IRepository;
import configurator.IConfigurator;

public class EditorConfigurator implements IConfigurator {

	private IRepository originRepository;
	public IRepository getOriginRepository(){ return originRepository; }
	public void setOriginRepository(IRepository originRepository){ this.originRepository = originRepository; }
	
	private final String name;
	private final String builder;
	private final String uri;
	private final List<String> setup;

	public EditorConfigurator(IConfigurator config, EditorRepository originRepository) {
		this.originRepository = originRepository;
		this.name = config.getName();
		this.builder = config.getBuilder();
		this.uri = config.getUri();
		this.setup = config.getSetup();
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getBuilder() {
		return builder;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public List<String> getSetup() {
		return setup;
	}

}
