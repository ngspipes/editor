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

import configurators.IConfigurator;
import repository.IRepository;

import java.util.LinkedList;
import java.util.List;

public class EagerConfigurator implements IConfigurator {

	private final IRepository originRepository;
	@Override
	public IRepository getOriginRepository(){ return originRepository; }
	@Override
	public void setOriginRepository(IRepository originRepository){ throw new UnsupportedOperationException(); }
	
	private final String name;
	private final String builder;
	private final String uri;
	private final List<String> setup;



	public EagerConfigurator(IConfigurator config, IRepository originRepository) {
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
		//return new List to make sure that list is not changed
		return new LinkedList<>(setup);
	}

}
