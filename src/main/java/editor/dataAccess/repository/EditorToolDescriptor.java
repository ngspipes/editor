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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import repository.IRepository;
import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;

public class EditorToolDescriptor implements IToolDescriptor{
	
	private IRepository originRepository;
	public IRepository getOriginRepository(){ return originRepository; }
	@Override
	public void setOriginRepository(IRepository originRepository) {	this.originRepository = originRepository; }
	
	private final String name;
	private final int requiredMemory;
	private final String version;
	private final String description;
	private final String author; 
	private final Collection<String> documentation;
	private final List<ICommandDescriptor> commandsDescriptors;
	private final Map<String, ICommandDescriptor> cmds;
	
	
	public EditorToolDescriptor(IToolDescriptor tool, EditorRepository originRepository) {
		this.originRepository = originRepository;
		this.name = tool.getName();
		this.requiredMemory = tool.getRequiredMemory();
		this.version = tool.getVersion();
		this.description = tool.getDescription();
		this.author = tool.getAuthor();
		this.documentation = tool.getDocumentaton();
		this.commandsDescriptors = new LinkedList<>();
		this.cmds = new HashMap<>();
		loadCommands(tool);
	}
	
	private void loadCommands(IToolDescriptor tool){
		EditorCommandDescriptor editorCommand;
		for(ICommandDescriptor command : tool.getCommands()){
			editorCommand = new EditorCommandDescriptor(command, this);
			commandsDescriptors.add(editorCommand);
			cmds.put(editorCommand.getName(), editorCommand);
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
	public Collection<String> getDocumentaton() {
		return documentation;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<ICommandDescriptor> getCommands() {
		return commandsDescriptors;
	}

	@Override
	public ICommandDescriptor getCommand(String commandName) {
		return cmds.get(commandName);
	}



}
