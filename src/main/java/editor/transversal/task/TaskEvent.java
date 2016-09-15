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
package editor.transversal.task;

import java.util.Collection;
import java.util.LinkedList;

public class TaskEvent<T> {

    private final Object lock = new Object();

    private final Collection<Runnable> listeners = new LinkedList<>();

    private boolean fired;
    public boolean hasFired(){ return this.fired; }



    public TaskEvent(){}



    public void addListener(Runnable listener){
        synchronized (lock) {
            listeners.add(listener);

            if(fired)
                listener.run();
        }
    }

    public void removeListener(Runnable listener){
        synchronized (lock) {
            listeners.remove(listener);
        }
    }

    public boolean await() throws InterruptedException {
        synchronized (lock) {
            while(!fired)
                lock.wait();

            return true;
        }
    }

    public boolean await(long timeout) throws InterruptedException {
        synchronized (lock) {
            long finalTime = System.currentTimeMillis() + timeout;
            long timeToFinish;

            while(true){
                timeToFinish = finalTime - System.currentTimeMillis();

                if(fired || timeToFinish<0)
                    break;

                lock.wait(timeToFinish);
            }

            return fired;
        }
    }

    public void fire(){
        synchronized (lock) {
            if(!fired){
                this.fired = true;

                lock.notifyAll();

                new LinkedList<>(listeners).forEach(Runnable::run);
            }
        }
    }

}
