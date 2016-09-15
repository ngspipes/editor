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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Task<T> implements Runnable, Future<T>{

    public final TaskEvent cancelledEvent = new TaskEvent();
    public final TaskEvent scheduledEvent = new TaskEvent();
    public final TaskEvent failedEvent = new TaskEvent();
    public final TaskEvent runningEvent = new TaskEvent();
    public final TaskEvent succeededEvent = new TaskEvent();
    public final TaskEvent finishedEvent = new TaskEvent();

    private T value;

    private Exception exception;



    @Override
    public void run() {
        try {
            if(cancelledEvent.hasFired())
                return;

            runningEvent.fire();

            if(cancelledEvent.hasFired())
                return;

            this.value = execute();

            if(cancelledEvent.hasFired())
                return;

            succeededEvent.fire();
        } catch(Exception e) {
            this.exception = e;
            failedEvent.fire();
        } finally {
            finishedEvent.fire();
        }
    }

    public T getResult() throws Exception {
        finishedEvent.await();

        if(this.exception != null)
            throw this.exception;
        else
            return this.value;
    }

    public T getResult(long timeout) throws Exception {
        if(!finishedEvent.await(timeout))
            throw new TimeoutException();

        if(this.exception != null)
            throw this.exception;
        else
            return this.value;
    }

    public T getValue() {
        return this.value;
    }

    public Exception getException() {
        return exception;
    }

    public void cancel(){
        this.cancelledEvent.fire();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelledEvent.fire();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelledEvent.hasFired();
    }

    @Override
    public boolean isDone() {
        return this.finishedEvent.hasFired();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try{
            return getResult();
        } catch(Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try{
            return getResult(TimeUnit.MICROSECONDS.convert(timeout, unit));
        } catch(Exception e) {
            if(e instanceof InterruptedException)
                throw (InterruptedException) e;

            if(e instanceof TimeoutException)
                throw (TimeoutException) e;

            throw new ExecutionException(e);
        }
    }

    protected abstract T execute() throws Exception;
}