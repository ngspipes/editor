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

import java.util.function.BiConsumer;

import editor.utils.WorkQueue;
import javafx.application.Platform;
import repository.IRepository;
import utils.Cache;

import components.Window;

import dsl.managers.Support;
import editor.userInterface.utils.Dialog;
import editor.utils.EditorException;
import editor.utils.Utils;
import exceptions.RepositoryException;


public class RepositoryManager {
	
	private static final Cache<String, IRepository> CACHE = new Cache<>();
	private static final String TAG = "EditorRepositoryManager";
	
	public static void getRepository(String type, String location, BiConsumer<Exception, IRepository> callback) {
		Window<?,?> progress = null;
		try{
			progress = Dialog.getLoadingWindow("Loading Pipeline");
			progress.open();
		}catch(Exception ex){
			Utils.treatException(ex, TAG, "Error loading progress Window!");
		}
		
		final Window<?,?> progressWindow = progress;

		WorkQueue.run(()->{
			IRepository repo = null;
			Exception ex = null;

			String key = "TYPE_" + type + "_LOCAL_" + location;
			try {
				if((repo = CACHE.get(key)) == null){
					repo = get(type, location);
					CACHE.add(key, repo);
				}
			} catch (EditorException e) {
				ex = e;
			}

			IRepository repository = repo;
			Exception exception = ex;

			Platform.runLater(()-> {
				if(progressWindow != null)
					progressWindow.close();

				callback.accept(exception, repository);
			});
		});
	}
	
	private static EagerRepository get(String type, String location) throws EditorException {
		try{
			IRepository repository = Support.getRepository(type, location);
			return new EagerRepository(repository);
		}catch(RepositoryException ex){
			throw new EditorException("Error loading repository!", ex);
		}
	}

}
