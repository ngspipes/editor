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

import descriptors.IArgumentDescriptor;
import descriptors.ICommandDescriptor;
import descriptors.IOutputDescriptor;
import descriptors.IToolDescriptor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EagerCommandDescriptor implements ICommandDescriptor{
	
	private final IToolDescriptor originTool;
	@Override
	public IToolDescriptor getOriginTool(){ return originTool; }
	@Override
	public void setOriginTool(IToolDescriptor originTool){ throw new UnsupportedOperationException(); }
	
	private final String name;
	private final String command;
	private final String description;
	private final String argumentsComposer;
	private final List<IArgumentDescriptor> arguments;
	private final List<IOutputDescriptor> outputs;
	private final Map<String, IArgumentDescriptor> argumentsByName;
	private final Map<String, IOutputDescriptor> outputsByName;
	private final int priority;



	public EagerCommandDescriptor(ICommandDescriptor command, IToolDescriptor originTool) {
		this.originTool = originTool;
		this.name = command.getName();
		this.command = command.getCommand();
		this.description = command.getDescription();
		this.argumentsComposer = command.getArgumentsComposer();
		this.arguments = new LinkedList<>();
		this.outputs = new LinkedList<>();
		this.argumentsByName = new HashMap<>();
		this.outputsByName = new HashMap<>();
		this.priority = command.getPriority();
		
		loadArguments(command);
		loadOutputs(command);
	}

	private void loadArguments(ICommandDescriptor command) {
		EagerArgumentDescriptor eagerArgument;

		for(IArgumentDescriptor arg : command.getArguments()){
			eagerArgument = new EagerArgumentDescriptor(arg, this);
			arguments.add(eagerArgument);
			argumentsByName.put(eagerArgument.getName(), eagerArgument);
		}
	}
	
	private void loadOutputs(ICommandDescriptor command) {
		EagerOutputDescriptor eagerOutput;

		for(IOutputDescriptor out : command.getOutputs()){
			eagerOutput = new EagerOutputDescriptor(out, this);
			outputs.add(eagerOutput);
			outputsByName.put(eagerOutput.getName(), eagerOutput);
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
		//return new List to make sure that list is not changed
		return new LinkedList<>(arguments);
	}

	@Override
	public List<IOutputDescriptor> getOutputs() {
		//return new List to make sure that list is not changed
		return new LinkedList<>(outputs);
	}

	@Override
	public IArgumentDescriptor getArgument(String argumentName) {
		return argumentsByName.get(argumentName);
	}

	@Override
	public IOutputDescriptor getOutput(String outputName) {
		return outputsByName.get(outputName);
	}

	@Override
	public int getPriority(){
		return priority;
	}
	
}
