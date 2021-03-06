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
package editor.transversal;

public class WorkQueue {

    //Runs on dedicated Thread
    public static void run(Runnable action){
        run(action, true, Thread.NORM_PRIORITY);
    }

    //Runs on dedicated Thread
    public static void run(Runnable action, boolean deamon, int priority){
        Thread thread = new Thread(action);
        thread.setDaemon(deamon);
        thread.setPriority(priority);
        thread.start();
    }

}
