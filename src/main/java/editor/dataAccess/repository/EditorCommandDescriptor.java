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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import descriptors.IArgumentDescriptor;
import descriptors.ICommandDescriptor;
import descriptors.IOutputDescriptor;
import descriptors.IToolDescriptor;

public class EditorCommandDescriptor implements ICommandDescriptor{
	
	private IToolDescriptor originTool;
	public IToolDescriptor getOriginTool(){ return originTool; }
	public void setOriginTool(IToolDescriptor originTool){ this.originTool = originTool; }
	
	private final String name;
	private final String command;
	private final String description;
	private final String argumentsComposer;
	private final List<IArgumentDescriptor> arguments;
	private final List<IOutputDescriptor> outputs;
	private final Map<String, IArgumentDescriptor> args;
	private final Map<String, IOutputDescriptor> otps;
	private final int priority;
	
	public EditorCommandDescriptor(ICommandDescriptor command, EditorToolDescriptor originTool) {
		this.originTool = originTool;
		this.name = command.getName();
		this.command = command.getCommand();
		this.description = command.getDescription();
		this.argumentsComposer = command.getArgumentsComposer();
		this.arguments = new LinkedList<>();
		this.outputs = new LinkedList<>();
		this.args = new HashMap<>();
		this.otps = new HashMap<>();
		this.priority = command.getPriority();
		
		loadArguments(command);
		loadOutputs(command);
	}

	private void loadArguments(ICommandDescriptor command) {
		EditorArgumentDescriptor editorArgument;
		for(IArgumentDescriptor arg : command.getArguments()){
			editorArgument = new EditorArgumentDescriptor(arg, this);
			arguments.add(editorArgument);
			args.put(editorArgument.getName(), editorArgument);
		}
	}
	
	private void loadOutputs(ICommandDescriptor command) {
		EditorOutputDescriptor editorOutput;
		for(IOutputDescriptor out : command.getOutputs()){
			editorOutput = new EditorOutputDescriptor(out, this);
			outputs.add(editorOutput);
			otps.put(editorOutput.getName(), editorOutput);
		}
	}
	
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getArgumentsComposer() {
		return argumentsComposer;
	}

	@Override
	public List<IArgumentDescriptor> getArguments() {
		return arguments;
	}

	@Override
	public List<IOutputDescriptor> getOutputs() {
		return outputs;
	}

	@Override
	public IArgumentDescriptor getArgument(String argumentName) {
		return args.get(argumentName);
	}

	@Override
	public IOutputDescriptor getOutput(String outputName) {
		return otps.get(outputName);
	}

	public int getPriority(){
		return priority;
	}
	
}
