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
package editor.userInterface.utils;

import components.Window;
import descriptors.IToolDescriptor;
import editor.dataAccess.Uris;
import editor.dataAccess.loader.ImageLoaderTask;
import editor.dataAccess.repository.RepositoryManager;
import editor.transversal.EditorException;
import editor.transversal.task.Task;
import editor.transversal.task.TaskFactory;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import repository.IRepository;

import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class UIUtils {

	public static FXMLLoader getLoader(String fXMLWindowPath){
        URL location = ClassLoader.getSystemResource(fXMLWindowPath);
        FXMLLoader loader = new FXMLLoader(location);
        return loader;
    }
	
	public static Tooltip createTooltip(String text, boolean wrap, double maxWidth, double maxHeight){
		Tooltip tip = new Tooltip(text);

		tip.setWrapText(wrap);
		tip.setMaxWidth(maxWidth);
		tip.setMaxHeight(maxHeight);
		
		return tip;
	}

	public static void set3DEffect(Node node, boolean setShadow, boolean setReflection){
		DropShadow shadow = new DropShadow();
		shadow.setOffsetY(5.0);
	    shadow.setOffsetX(5.0);
	    shadow.setColor(Color.GRAY);
	    
	    Reflection reflection = new Reflection();
	    reflection.setTopOffset(-15);
	    reflection.setBottomOpacity(0.15);
		
	    if(setShadow && !setReflection)
	    	node.setEffect(shadow);
	    else if(!setShadow && setReflection)
	    	node.setEffect(reflection);
	    else if(setShadow && setReflection){
	    	shadow.setInput(reflection);
	    	node.setEffect(shadow);
	    }
	}


	public static void loadRepository(String type, String location, BiConsumer<Exception, IRepository> callback) {
		try {
			Task<IRepository> task = loadRepository(type, location);
			task.succeededEvent.addListener(() -> {
				Platform.runLater(() -> callback.accept(null, task.getValue()));
			});

			task.failedEvent.addListener(() -> {
				Platform.runLater(() -> callback.accept(task.getException(), null));
			});

			TaskFactory.execute(task);
		} catch (EditorException e) {
			callback.accept(e, null);
		}
	}

	public static Task<IRepository> loadRepository(String type, String location) throws EditorException {
		BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
		AtomicBoolean finishFlag = new AtomicBoolean(false);

		Window<?,?> progressWindow = openLoadWindow("Loading Repository", messageQueue, finishFlag);

		Task<IRepository> task = RepositoryManager.getRepositoryAsync(type, location);

		task.finishedEvent.addListener(()-> {
			finishFlag.set(true);
			Platform.runLater(progressWindow::close);
		});

		return task;
	}

	private static Window<?,?> openLoadWindow(String loadMessage,
											  BlockingQueue<String> messageQueue,
											  AtomicBoolean finishFlag) throws EditorException {
		Window<?,?> window;
		try {
			window = Dialog.getLoadingWindow(loadMessage, messageQueue, finishFlag);
			window.open();
		} catch(Exception ex) {
			throw new EditorException("Error loading progress Window!", ex);
		}

		return window;
	}


	public static Task<Image> loadImage(ImageView view, String location) {
		view.setImage(new Image(Uris.LOADING_IMAGE));

		Task<Image> task = new ImageLoaderTask(location);

		task.succeededEvent.addListener(() -> view.setImage(task.getValue()));

		TaskFactory.execute(task);

		return task;
	}

	public static Task<Image> loadLogo(ImageView view, IToolDescriptor tool) {
		IRepository repository = tool.getOriginRepository();
		String toolName = tool.getName();

		return loadImage(view, repository.getToolLogo(toolName));
	}

	public static Task<Image> loadImage(String location) {
		Task<Image> task = new ImageLoaderTask(location);

		TaskFactory.execute(task);

		return task;
	}

	public static Task<Image> loadLogo(IToolDescriptor tool) {
		IRepository repository = tool.getOriginRepository();
		String toolName = tool.getName();

		return loadImage(repository.getToolLogo(toolName));
	}

}
