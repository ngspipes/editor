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
package editor.userInterface.controllers.help;

import java.io.IOException;
import java.time.LocalTime;

import org.json.JSONException;
import org.json.JSONObject;

import rutils.IO;


public class JSONVideoDescriptor implements IVideoDescriptor {

	private static final String NAME_KEY = "name";
	private static final String DESCRIPTION_KEY = "description";
	private static final String DURATION_KEY = "duration";
	private static final String DESCRIPTOR_NAME = "Descriptor";

	private static String getVideoLocation(String filePath) {
		StringBuilder videoLocation = new StringBuilder();
		
		videoLocation.append(filePath.substring(0, filePath.indexOf(DESCRIPTOR_NAME)))
					 .append(".mp4");
		
		return videoLocation.toString();
	}
	
	
	
	final String name;
	@Override
	public String getName() { return this.name; }

	final String location;
	@Override
	public String getLocation() { return this.location; }

	final String description;
	@Override
	public String getDescription() { return this.description; }
	
	final LocalTime duration;
	@Override
	public LocalTime getDuration() { return this.duration; }
	
	public JSONVideoDescriptor(String filePath) throws JSONException, IOException {
		this(new JSONObject(IO.read(filePath)), getVideoLocation(filePath));
	}

	public JSONVideoDescriptor(JSONObject json, String location) throws JSONException {
		this(json.getString(NAME_KEY), location, json.getString(DESCRIPTION_KEY), 
			 json.getString(DURATION_KEY));
	}


	public JSONVideoDescriptor(	String name, String location, String description, String duration) {
		this.name = name;
		this.location = location;
		this.description = description;
		this.duration = LocalTime.parse(duration);
	}

}
