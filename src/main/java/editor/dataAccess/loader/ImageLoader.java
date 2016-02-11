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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import editor.utils.WorkQueue;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;



public class ImageLoader {

	private final String location;
	private final AtomicBoolean cancel;
	private final AtomicBoolean finish;
	private final Consumer<Image> onFinish;

	private final double width;
	private final double height;
	private final boolean preserveRatio;
	private final boolean smoth;

	public ImageLoader(String location, double width, double height, boolean preserveRatio, boolean smoth, Consumer<Image> onFinish){
		this.location = location;
		this.width = width;
		this.height = height;
		this.preserveRatio = preserveRatio;
		this.smoth = smoth;
		this.onFinish = onFinish;
		this.cancel = new AtomicBoolean(false);
		this.finish = new AtomicBoolean(false);
	}

	public ImageLoader(String location, Consumer<Image> onFinish){
		this(location, -1, -1, true, true, onFinish);
	}

	public ImageLoader(String location, double width, double height, boolean preserveRatio, boolean smoth, ImageView imageView){
		this(location, width, height, preserveRatio, smoth, imageView::setImage);
	}

	public ImageLoader(String location, ImageView imageView){
		this(location, -1, -1, true, true, imageView::setImage);
	}



	public void load(){
		WorkQueue.run(()->{
			Image image = getImage();

			Platform.runLater(()->{
				finish.set(true);
				onFinish.accept(image);
			});
		});
	}

	private Image getImage(){
		String path = getCachePath();

		if(!new File(path).exists())
			saveImage(path);

		if(width==-1 || height==-1)
			return new Image("file:" + path);
		else
			return new Image("file:" + path, width, height, preserveRatio, smoth);
	} 

	protected String getCachePath(){
		return Long.toString(hash(location));
	}

	protected long hash(String str) {
		long h = 1125899906842597L; // prime

		for (int i = 0; i < str.length(); i++)
			h = 31*h + str.charAt(i);

		return h;
	}

	private void saveImage(String path) {
		File outputFile = new File(path);

		BufferedImage bImage = SwingFXUtils.fromFXImage(new Image(location), null);
		try {
			ImageIO.write(bImage, "png", outputFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}			
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
