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

import editor.transversal.task.BasicTask;
import javafx.scene.image.Image;
import utils.Cache;


public class ImageLoaderTask extends BasicTask<Image> {

    private static final Cache<String, Image> CACHE = new Cache<>();



    public ImageLoaderTask(String location){
        super(() -> {
            Image image = CACHE.get(location);

            if(image == null){
                image = new Image(location, false);
                CACHE.add(location, image);
            }

            return image;
        });
    }

}
