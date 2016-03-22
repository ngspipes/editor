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
package editor.utils;

import editor.userInterface.utils.Dialog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class Utils {

	public static String getStackTrace(Throwable t){
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static void treatException(Exception e, String tag, String msg){
		if(e instanceof EditorException){
			Log.error(tag, msg+"\n"+getStackTrace(e));
			Dialog.showError(e.getMessage());
		}else{
			Log.error(tag, msg+"\n"+getStackTrace(e));
			Dialog.showError(msg);
		}
		
		e.printStackTrace();
	}
	
	@SafeVarargs
	public static <T> Collection<T> distinct(Collection<T>... colls){
		List<T> elems = new LinkedList<>();

		for(Collection<T> coll : colls)
			coll.forEach((elem)->{
				if(!elems.contains(elem))
					elems.add(elem);
			});
		
		return elems;
	} 
	
}
