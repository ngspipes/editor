package editor.dataAccess.repository;

import descriptor.IArgumentDescriptor;
import descriptor.ICommandDescriptor;

public class EditorArgumentDescriptor implements IArgumentDescriptor{
	
	private ICommandDescriptor originCommand;
	public ICommandDescriptor getOriginCommand(){ return originCommand; }
	public void setOriginCommand(ICommandDescriptor originCommand){ this.originCommand = originCommand; }
	
	private final String name;
	private final String description;
	private final String type;
	private final boolean required;
	private final int order;
	
	
	
	public EditorArgumentDescriptor(IArgumentDescriptor argument, EditorCommandDescriptor originCommand) {
		this.originCommand = originCommand;
		this.name = argument.getName();
		this.description = argument.getDescription();
		this.type = argument.getType();
		this.required = argument.getRequired();
		this.order = argument.getOrder();
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
	public boolean getRequired() {
		return required;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public int getOrder(){
		return order;
	}
	
}
