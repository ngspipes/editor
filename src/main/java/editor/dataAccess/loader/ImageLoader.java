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

import editor.utils.WorkQueue;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Cache;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class ImageLoader {

    private static final Cache<String, Image> CACHE = new Cache<>();

    private final String location;
    private final AtomicBoolean cancel;
    private final AtomicBoolean finish;
    private final Consumer<Image> onFinish;


    public ImageLoader(String location, Consumer<Image> onFinish){
        this.location = location;
        this.onFinish = onFinish;
        this.cancel = new AtomicBoolean(false);
        this.finish = new AtomicBoolean(false);
    }

    public ImageLoader(String location, ImageView imageView){
        this(location, imageView::setImage);
    }



    public void load(){
        WorkQueue.run(()->{
            if(cancel.get())
                return;

            final Image image = getImage();

            finish.set(true);

            if(cancel.get())
                return;

            Platform.runLater(()->{
                if(cancel.get())
                    return;

                onFinish.accept(image);
            });
        });
    }

    private Image getImage(){
        Image image = CACHE.get(location);

        if(cancel.get())
            return null;

        if(image == null){
            image = new Image(location, false);
            CACHE.add(location, image);
        }

        return image;
    }

    public void cancel(){
        cancel.set(true);
    }

    public Boolean isCanceled(){
        return cancel.get();
    }

    public Boolean hasFinished(){
        return finish.get();
    }

}
