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
import repository.IRepository;

import java.util.Collection;
import java.util.Map;

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

    private Collection<Argument> arguments;
    public Collection<Argument> getArguments(){ return this.arguments; }

    private Collection<Output> outputs;
    public Collection<Output> getOutputs(){ return this.outputs; }

    private Map<String, Argument> argumentsByName;

    private Map<String, Output> outputsByName;



    protected Step(){}

    protected Step(double x, double y, dsl.entities.Step dslStep){
        this.dslStep = dslStep;
        this.x = x;
        this.y = y;
        load();
    }



    private void load(){
        loadArguments();
        loadOutputs();
    }

    private void loadArguments() {
        Argument argument;
        for(dsl.entities.Argument dslArgument : dslStep.getCommand().getArguments()){
            argument = new Argument(dslArgument);
            arguments.add(argument);
            argumentsByName.put(argument.getName(), argument);
        }
    }

    private void loadOutputs() {
        Output output;
        for(dsl.entities.Output dslOutput : dslStep.getCommand().getOutputs()){
            output = new Output(dslOutput);
            outputs.add(output);
            outputsByName.put(output.getName(), output);
        }
    }

    public IConfigurator getConfigurator(){
        return dslStep.getConfigurator();
    }

    protected void setConfigurator(IConfigurator configurator){
        dslStep.setConfigurator(configurator);
    }

    public Argument getArgument(String argumentName){
        return argumentsByName.get(argumentName);
    }

    public Output getOutput(String outputName){
        return outputsByName.get(outputName);
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