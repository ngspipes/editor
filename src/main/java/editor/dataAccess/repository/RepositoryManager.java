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

import dsl.managers.Support;
import editor.utils.EditorException;
import editor.utils.task.BasicTask;
import editor.utils.task.Task;
import exceptions.RepositoryException;
import repository.IRepository;
import utils.Cache;


public class RepositoryManager {
	
	private static final Cache<String, IRepository> CACHE = new Cache<>();
	private static final String TAG = RepositoryManager.class.getSimpleName();



	public static Task<IRepository> getRepositoryAsync(String type, String location) {
		return new BasicTask<>(() -> getRepository(type, location));
	}

	public static IRepository getRepository(String type, String location) throws EditorException {
		IRepository repository;
		String key = "TYPE_" + type + "_LOCAL_" + location;

		if((repository = CACHE.get(key)) == null){
			repository = createEagerRepository(type, location);
			CACHE.add(key, repository);
		}

		return repository;
	}

	private static IRepository createEagerRepository(String type, String location) throws EditorException {
		try{
			IRepository repository = Support.getRepository(type, location);
			return new EagerRepository(repository);
		}catch(RepositoryException ex){
			throw new EditorException("Error loading repository!", ex);
		}
	}

}
