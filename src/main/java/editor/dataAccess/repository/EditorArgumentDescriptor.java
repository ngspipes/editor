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
