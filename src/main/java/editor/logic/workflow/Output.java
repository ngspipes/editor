///*-
// * Copyright (c) 2016, NGSPipes Team <ngspipes@gmail.com>
// * All rights reserved.
// *
// * This file is part of NGSPipes <http://ngspipes.github.io/>.
// *
// * This program is free software: you can redistribute it and/or modify it
// * under the terms of the GNU General Public License as published by the
// * Free Software Foundation, either version 3 of the License, or (at your
// * option) any later version.
// *
// * This program is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
// * for more details.
// *
// * You should have received a copy of the GNU General Public License along
// * with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
package editor.logic.workflow;

public class Output {

    private dsl.entities.Output dslOutput;
    protected dsl.entities.Output getDSLOutput(){ return this.dslOutput; }
    protected void getDSLOutput(dsl.entities.Output dslOutput){ this.dslOutput = dslOutput; }



    protected Output(){}

    protected Output(dsl.entities.Output dslOutput){
        this.dslOutput = dslOutput;
    }



    public String getValue(){
        return dslOutput.getValue();
    }

    protected void setValue(String value){
        dslOutput.setValue(value);
    }

    public String getName(){
        return dslOutput.getName();
    }

    public String getArgumentName() {
        return dslOutput.getArgumentName();
    }

    public String getType() {
        return dslOutput.getType();
    }

}
