package editor.dataAccess.repository;

import descriptors.ICommandDescriptor;
import descriptors.IOutputDescriptor;


public class EditorOutputDescriptor implements IOutputDescriptor{
	
	private ICommandDescriptor originCommand;
	public ICommandDescriptor getOriginCommand(){ return originCommand; }
	public void setOriginCommand(ICommandDescriptor originCommand){ this.originCommand = originCommand; }
	
	private final String name;
	private final String description;
	private final String value;
	private final String argumentName;
	private final String type;
	
	
	public EditorOutputDescriptor(IOutputDescriptor output, EditorCommandDescriptor originCommand) {
		this.originCommand = originCommand;
		this.name = output.getName();
		this.description = output.getDescription();
		this.value = output.getValue();
		this.argumentName = output.getArgumentName();
		this.type = output.getType();
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String getArgumentName() {
		return argumentName	;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

}
