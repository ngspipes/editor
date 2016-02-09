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
