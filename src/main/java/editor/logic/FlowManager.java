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
package editor.logic;

import java.io.File;
import java.util.function.BiConsumer;

import editor.logic.entities.Flow;
import editor.utils.EditorException;


public class FlowManager {
	
	public static final String WORK_FLOW_FILE_EXTENSION = ".wf";


	public static void save(Flow flow) throws EditorException {
		FlowSaver.saveFlow(flow);
	}
	
	public static void load(String name, String directory, BiConsumer<Exception, Flow> callback) throws EditorException {
		FlowLoader.load(name, directory, callback);
	}
	
	public static void generate(Flow flow, File inputsDir) throws EditorException {
		DSLWriter.generate(flow, inputsDir);
	}
		
}