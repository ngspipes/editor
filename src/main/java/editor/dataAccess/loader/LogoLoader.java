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

import java.util.function.Consumer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import descriptors.IToolDescriptor;

public class LogoLoader extends ImageLoader{

	public LogoLoader(IToolDescriptor tool, Consumer<Image> onFinish){
		super(tool.getOriginRepository().getToolLogo(tool.getName()), onFinish);
	}

	public LogoLoader(IToolDescriptor tool, ImageView imageView){
		super(tool.getOriginRepository().getToolLogo(tool.getName()), imageView);
	}

}
