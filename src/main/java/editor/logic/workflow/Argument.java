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

public class Argument {

    private dsl.entities.Argument dslArgument;
    protected dsl.entities.Argument getDSLArgument(){ return this.dslArgument; }
    protected void setDSLArgument(dsl.entities.Argument dslArgument){ this.dslArgument = dslArgument; }



    protected Argument(){}

    protected Argument(dsl.entities.Argument dslArgument){
        this.dslArgument = dslArgument;
    }



    public String getValue(){
        return dslArgument.getValue();
    }

    protected void setValue(String value){
        dslArgument.setValue(value);
    }

    public String getName(){
        return dslArgument.getName();
    }

    public String getType(){
        return dslArgument.getType();
    }

    public boolean getRequired(){
        return dslArgument.getRequired();
    }

    public int getOrder(){
        return dslArgument.getOrder();
    }

    public String getDescription() {
        return dslArgument.getDescriptor().getDescription();
    }
}