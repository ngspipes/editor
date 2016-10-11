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
package editor.dataAccess.repository;

import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import repository.IRepository;

import java.util.*;

public class EagerToolDescriptor implements IToolDescriptor{
	
	private final IRepository originRepository;
	@Override
	public IRepository getOriginRepository(){ return originRepository; }
	@Override
	public void setOriginRepository(IRepository originRepository) {	throw new UnsupportedOperationException(); }
	
	private final String name;
	private final int requiredMemory;
	private final String argumentsComposer;
	private final String version;
	private final String description;
	private final String author; 
	private final Collection<String> documentation;
	private final List<ICommandDescriptor> commands;
	private final Map<String, ICommandDescriptor> commandsByName;


	
	public EagerToolDescriptor(IToolDescriptor tool, IRepository originRepository) {
		this.originRepository = originRepository;
		this.name = tool.getName();
		this.requiredMemory = tool.getRequiredMemory();
		this.argumentsComposer = tool.getArgumentsComposer();
		this.version = tool.getVersion();
		this.description = tool.getDescription();
		this.author = tool.getAuthor();
		this.documentation = tool.getDocumentation();
		this.commands = new LinkedList<>();
		this.commandsByName = new HashMap<>();

		loadCommands(tool);
	}
	
	private void loadCommands(IToolDescriptor tool){
		EagerCommandDescriptor eagerCommand;

		for(ICommandDescriptor command : tool.getCommands()){
			eagerCommand = new EagerCommandDescriptor(command, this);
			commands.add(eagerCommand);
			commandsByName.put(eagerCommand.getName(), eagerCommand);
		}
	}
	


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public int getRequiredMemory() {
		return requiredMemory;
	}

	@Override
	public String getArgumentsComposer() {
		return argumentsComposer;
	}

	@Override
	public Collection<String> getDocumentation() {
		//return new List to make sure that list is not changed
		return new LinkedList<>(documentation);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<ICommandDescriptor> getCommands() {
		//return new List to make sure that list is not changed
		return new LinkedList<>(commands);
	}

	@Override
	public ICommandDescriptor getCommand(String commandName) {
		return commandsByName.get(commandName);
	}

}
