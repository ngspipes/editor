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
package editor.utils.task;

import javafx.embed.swing.JFXPanel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static editor.utils.ThrowableFunctionalInterfaces.Runnable;
import static editor.utils.ThrowableFunctionalInterfaces.Supplier;

public class TaskFactory {

    private static final int THREADS_NUMBER = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService POOL = Executors.newFixedThreadPool(THREADS_NUMBER);



    static {
        new JFXPanel(); // this will prepare JavaFX toolkit and environment
    }



    //Runs on Pool
    public static void execute(Task<?> task) {
        task.scheduledEvent.fire();
        POOL.execute(task);
    }

    public static <T> Task<T> createAndRun(Supplier<T> action){
        Task<T> task = new BasicTask<>(action);

        execute(task);

        return task;
    }

    public static Task<Void> createAndRun(Runnable action) {
        return createAndRun(() -> {
            action.run();
            return null;
        });
    }

}
