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
import editor.dataAccess.repository.RepositoryManager;
import editor.utils.EditorException;
import editor.utils.task.Task;
import editor.utils.task.TaskFactory;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import repository.IRepository;

import java.net.URL;
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


	public static void _(String type, String location, BiConsumer<Exception, IRepository> callback) {
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
		Window<?,?> progressWindow = openLoadWindow("Loading Repository");

		Task<IRepository> task = RepositoryManager.getRepositoryAsync(type, location);

		task.finishedEvent.addListener(()-> Platform.runLater(progressWindow::close));

		return task;
	}

	private static Window<?,?> openLoadWindow(String loadMessage) throws EditorException {
		Window<?,?> window;

		try {
			window = Dialog.getLoadingWindow(loadMessage);
			window.open();
		} catch(Exception ex) {
			throw new EditorException("Error loading progress Window!", ex);
		}

		return window;
	}
	
}
