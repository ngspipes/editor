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
package editor.dataAccess.loader;

import java.io.File;
import java.util.function.Consumer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import descriptors.IToolDescriptor;
import editor.dataAccess.Uris;

public class LogoLoader extends ImageLoader{
	
	private final IToolDescriptor tool;

	public LogoLoader(IToolDescriptor tool, double width, double height, boolean preserveRatio, boolean smoth, Consumer<Image> onFinish){
		super(tool.getOriginRepository().getToolLogo(tool.getName()),  width, height, preserveRatio, smoth, onFinish);
		this.tool = tool;
	}
	
	public LogoLoader(IToolDescriptor tool, Consumer<Image> onFinish){
		super(tool.getOriginRepository().getToolLogo(tool.getName()), onFinish);
		this.tool = tool;
	}
	
	public LogoLoader(IToolDescriptor tool, double width, double height, boolean preserveRatio, boolean smoth, ImageView imageView){
		super(tool.getOriginRepository().getToolLogo(tool.getName()),  width, height, preserveRatio, smoth, imageView);
		this.tool = tool;
	}
	
	public LogoLoader(IToolDescriptor tool, ImageView imageView){
		super(tool.getOriginRepository().getToolLogo(tool.getName()), imageView);
		this.tool = tool;
	}
	
	@Override
	protected String getCachePath(){
		String repoDir = Uris.CACHE_DIR + Uris.SEP + super.hash(tool.getOriginRepository().getLocation());
		
		File repo = new File(repoDir);
		if(!repo.exists())
			repo.mkdirs();
			
		return repoDir + Uris.SEP + tool.getName();
	}
	

}
