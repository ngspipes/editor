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

import dsl.entities.Argument;
import dsl.entities.Output;

public class Chain {

    private Output output;
    public Output getOutput(){ return this.output; }
    protected void setOutput(Output output){ this.output = output; }

    private Argument argument;
    public Argument getArgument(){ return this.argument; }
    protected void setArgument(Argument argument){ this.argument = argument; }

    private  dsl.entities.Chain dslChain;
    protected dsl.entities.Chain getDSLChain(){ return this.dslChain; }
    protected void setDSLChain(dsl.entities.Chain dslChain){ this.dslChain = dslChain; }



    protected Chain(){}

    protected Chain(Output output, Argument argument, dsl.entities.Chain dslChain){
        this.output = output;
        this.argument = argument;
        this.dslChain = dslChain;
    }

}
