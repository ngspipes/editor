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

public class Channel {

    private Step from;
    public Step getFrom(){ return this.from; }
    protected void setFrom(Step from){ this.from = from; }

    private Step to;
    public Step getTo(){ return this.to; }
    protected void setTo(Step to){ this.to = to; }

    private Collection<Chain> chains;
    public Collection<Chain> getChains(){ return this.chains; }
    protected void setChains(Collection<Chain> chains){ this.chains = chains; }



    protected Channel(){}

    protected Channel(Step from, Step to, Collection<Chain> chains){
        this.from = from;
        this.to = to;
        this.chains = chains;
    }



    protected boolean addChain(Chain chain){
        return this.chains.add(chain);
    }

    protected boolean removeChain(Chain chain){
        return this.chains.remove(chain);
    }

}
