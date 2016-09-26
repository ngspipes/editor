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
package editor.logic.workflow;

import java.util.Collection;
import java.util.LinkedList;

public class Workflow {

    private String name;
    public String getName(){ return this.name; }
    protected void setName(String name){ this.name = name; }

    private String directory;
    public String getDirectory(){ return this.directory; }
    protected void setDirectory(String directory){ this.directory = directory; }

    private Collection<Step> steps;
    public Collection<Step> getSteps(){ return new LinkedList<>(this.steps); }
    protected void setSteps(Collection<Step> steps){ this.steps = steps; }

    private Collection<Channel> channels;
    public Collection<Channel> getChannels(){ return this.channels; }
    protected void setChannels(Collection<Channel> channels){ this.channels = channels; }

    private boolean saved;
    public boolean getSaved(){ return this.saved; }
    protected void setSaved(boolean saved){ this.saved = saved; }



    protected Workflow(){}

    protected Workflow(String name, String directory, Collection<Step> steps, Collection<Channel> channels, boolean saved){
        this.name = name;
        this.directory = directory;
        this.steps = steps;
        this.channels = channels;
        this.saved = saved;
    }



    protected boolean addStep(Step step){
        return this.steps.add(step);
    }

    protected boolean removeStep(Step step){
        return this.steps.remove(step);
    }

    protected boolean addChannel(Channel channel){
        return this.channels.add(channel);
    }

    protected boolean removeChannel(Channel channel){
        return this.channels.remove(channel);
    }

}
