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

import configurators.IConfigurator;
import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import dsl.entities.Argument;
import dsl.entities.Output;
import repository.IRepository;

import java.util.Collection;

public class Step {

    private double x;
    public double getX(){ return x; }
    protected void setX(double x){ this.x = x; }

    private double y;
    public double getY(){ return y; }
    protected void setY(double y){ this.y = y; }

    private dsl.entities.Step dslStep;
    protected dsl.entities.Step getDSLStep(){ return this.dslStep; }
    protected void setDSLStep(dsl.entities.Step dslStep){ this.dslStep = dslStep; }



    protected Step(){}

    protected Step(double x, double y, dsl.entities.Step dslStep){
        this.dslStep = dslStep;
        this.x = x;
        this.y = y;
    }



    public IConfigurator getConfigurator(){
        return dslStep.getConfigurator();
    }

    protected void setConfigurator(IConfigurator configurator){
        dslStep.setConfigurator(configurator);
    }

    public Collection<Argument> getArguments(){
        return dslStep.getCommand().getArguments();
    }

    public Collection<Output> getOutputs(){
        return dslStep.getCommand().getOutputs();
    }

    public Argument getArgument(String argumentName){
        return dslStep.getCommand().getArgument(argumentName);
    }

    public Output getOutput(String outputName){
        return dslStep.getCommand().getOutput(outputName);
    }

    public IRepository getRepository(){
        return dslStep.getCommand().getDescriptor().getOriginTool().getOriginRepository();
    }

    public IToolDescriptor getTool(){
        return dslStep.getCommand().getDescriptor().getOriginTool();
    }

    public ICommandDescriptor getCommand(){
        return dslStep.getCommand().getDescriptor();
    }

}