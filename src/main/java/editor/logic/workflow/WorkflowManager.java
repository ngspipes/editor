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
import dsl.entities.Argument;
import dsl.entities.Command;
import dsl.entities.Output;
import editor.dataAccess.Uris;
import editor.transversal.EditorException;
import editor.transversal.task.BasicTask;
import editor.transversal.task.Task;
import exceptions.CommandBuilderException;
import exceptions.RepositoryException;
import org.json.JSONException;
import repository.IRepository;
import utils.Event;
import utils.IO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class WorkflowManager {

    public static class WorkflowEvents{
        public final Workflow workflow;

        public final Event<Boolean> saveEvent = new Event<>();
        public final Event<String> nameEvent = new Event<>();
        public final Event<String> directoryEvent = new Event<>();
        public final Event<Step> stepAdditionEvent = new Event<>();
        public final Event<Step> stepRemovalEvent = new Event<>();
        public final Event<Channel> channelAdditionEvent = new Event<>();
        public final Event<Channel> channelRemovalEvent = new Event<>();
        public final Map<Channel, Event<Chain>> chainAdditionEvents = new HashMap<>();
        public final Map<Channel, Event<Chain>> chainRemovalEvents = new HashMap<>();
        public final Map<Step, Event<IConfigurator>> configuratorEvents = new HashMap<>();
        public final Map<Argument, Event<String>> argumentEvents = new HashMap<>();
        public final Map<Step, Event<Double>> stepXEvents = new HashMap<>();
        public final Map<Step, Event<Double>> stepYEvents = new HashMap<>();



        protected WorkflowEvents(Workflow workflow){
            this.workflow = workflow;
        }
    }



    private static final Object LOCK = new Object();
    private static final Map<Workflow, WorkflowEvents> EVENTS = new HashMap<>();
    private static final Map<Step, Workflow> WORKFLOW_BY_STEP = new HashMap<>();
    private static final Map<Channel, Workflow> WORKFLOW_BY_CHANNEL = new HashMap<>();
    private static final Map<Argument, Step> STEP_BY_ARGUMENT = new HashMap<>();

    public static final String WORK_FLOW_FILE_EXTENSION = ".wf";
    public static final String WORK_FLOW_DSL_FILE_EXTENSION = ".pipes";



    //LOAD
    private static void loadWorkflowEvents(Workflow workflow) {
        synchronized (LOCK){
            EVENTS.put(workflow, new WorkflowEvents(workflow));

            for(Channel channel : workflow.getChannels())
                loadChannel(workflow, channel);

            for(Step step : workflow.getSteps())
                loadStep(workflow, step);
        }
    }

    private static void loadChannel(Workflow workflow, Channel channel){
        synchronized (LOCK){
            loadChannelEvents(workflow, channel);
            loadChannelMaps(workflow, channel);
        }
    }

    private static void loadChannelEvents(Workflow workflow, Channel channel){
        synchronized (LOCK){
            WorkflowEvents events = EVENTS.get(workflow);

            events.chainAdditionEvents.put(channel, new Event<>());
            events.chainRemovalEvents.put(channel, new Event<>());
        }
    }

    private static void loadChannelMaps(Workflow workflow, Channel channel){
        synchronized (LOCK){
            WORKFLOW_BY_CHANNEL.put(channel, workflow);
        }
    }

    private static void unloadChannel(Workflow workflow, Channel channel){
        synchronized (LOCK){
            unloadChannelEvents(workflow, channel);
            unloadChannelMaps(workflow, channel);
        }
    }

    private static void unloadChannelEvents(Workflow workflow, Channel channel){
        synchronized (LOCK){
            WorkflowEvents events = EVENTS.get(workflow);

            events.chainAdditionEvents.remove(channel);
            events.chainRemovalEvents.remove(channel);
        }
    }

    private static void unloadChannelMaps(Workflow workflow, Channel channel){
        synchronized (LOCK){
            WORKFLOW_BY_CHANNEL.remove(channel, workflow);
        }
    }

    private static void loadStep(Workflow workflow, Step step){
        synchronized (LOCK){
            loadStepEvents(workflow, step);
            loadStepMaps(workflow, step);
        }
    }

    private static void loadStepEvents(Workflow workflow, Step step){
        synchronized (LOCK){
            WorkflowEvents events = EVENTS.get(workflow);

            events.configuratorEvents.put(step, new Event<>());
            events.stepXEvents.put(step, new Event<>());
            events.stepYEvents.put(step, new Event<>());

            for(Argument argument : step.getArguments())
                events.argumentEvents.put(argument, new Event<>());
        }
    }

    private static void loadStepMaps(Workflow workflow, Step step){
        synchronized (LOCK){
            for(Argument argument : step.getArguments())
                STEP_BY_ARGUMENT.put(argument, step);

            WORKFLOW_BY_STEP.put(step, workflow);
        }
    }

    private static void unloadStep(Workflow workflow, Step step){
        synchronized (LOCK){
            unloadStepEvents(workflow, step);
            unloadStepMaps(workflow, step);
        }
    }

    private static void unloadStepEvents(Workflow workflow, Step step){
        synchronized (LOCK){
            WorkflowEvents events = EVENTS.get(workflow);

            events.configuratorEvents.remove(step);
            events.stepXEvents.remove(step);
            events.stepYEvents.remove(step);

            for(Argument argument : step.getArguments())
                events.argumentEvents.remove(argument);
        }
    }

    private static void unloadStepMaps(Workflow workflow, Step step){
        synchronized (LOCK){
            for(Argument argument : step.getArguments())
                STEP_BY_ARGUMENT.remove(argument, step);

            WORKFLOW_BY_STEP.remove(step, workflow);
        }
    }

    //TRIGGER
    private static void fireSaveEvent(Workflow workflow, Boolean saved){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.saveEvent.trigger(saved);
        }
    }

    private static void fireNameEvent(Workflow workflow, String name){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.nameEvent.trigger(name);
        }
    }

    private static void fireDirectoryEvent(Workflow workflow, String directory){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.directoryEvent.trigger(directory);
        }
    }

    private static void fireStepAdditionEvent(Workflow workflow, Step step){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.stepAdditionEvent.trigger(step);
        }
    }

    private static void fireStepRemovalEvent(Workflow workflow, Step step){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.stepRemovalEvent.trigger(step);
        }
    }

    private static void fireChannelAdditionEvent(Workflow workflow, Channel channel){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.channelAdditionEvent.trigger(channel);
        }
    }

    private static void fireChannelRemovalEvent(Workflow workflow, Channel channel){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.channelRemovalEvent.trigger(channel);
        }
    }

    private static void fireChainAdditionEvent(Workflow workflow, Channel channel, Chain chain){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.chainAdditionEvents.get(channel).trigger(chain);
        }
    }

    private static void fireChainRemovalEvent(Workflow workflow, Channel channel, Chain chain){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.chainRemovalEvents.get(channel).trigger(chain);
        }
    }

    private static void fireConfiguratorEvent(Workflow workflow, Step step, IConfigurator configurator){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.configuratorEvents.get(step).trigger(configurator);
        }
    }

    private static void fireArgumentEvent(Workflow workflow, Argument argument, String value){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.argumentEvents.get(argument).trigger(value);
        }
    }

    private static void fireStepXEvent(Workflow workflow, Step step, Double x){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.stepXEvents.get(step).trigger(x);
        }
    }

    private static void fireStepYEvent(Workflow workflow, Step step, Double y){
        synchronized (LOCK){
            WorkflowEvents event = EVENTS.get(workflow);
            event.stepYEvents.get(step).trigger(y);
        }
    }

    //FACTORY
    public static Workflow createWorkflow(String name, String directory){
        Workflow workflow = new Workflow();

        workflow.setName(name);
        workflow.setDirectory(directory);
        workflow.setSteps(new LinkedList<>());
        workflow.setChannels(new LinkedList<>());

        loadWorkflowEvents(workflow);

        return workflow;
    }

    public static Step createStep(double x, double y, ICommandDescriptor command, IConfigurator configurator) throws EditorException {
        try{
            dsl.entities.Step dslStep = new dsl.entities.Step(new Command(command, null), configurator);
            return new Step(x, y, dslStep);
        } catch(CommandBuilderException ex) {
            throw new EditorException("Error creating step!", ex);
        }
    }

    public static Channel createChannel(Step from, Step to){
        return new Channel(from, to, new LinkedList<>());
    }

    public static Chain createChain(Argument argument, Output output){
        dsl.entities.Chain dslChain = new dsl.entities.Chain(argument, output);

        return new Chain(output, argument, dslChain);
    }

    //WRITE OPERATIONS
    public WorkflowEvents getEventsFor(Workflow workflow){
        synchronized (LOCK){
            return EVENTS.get(workflow);
        }
    }

    private static void setWorkflowSave(Workflow workflow, boolean saved){
        synchronized (LOCK){
            workflow.setSaved(saved);

            fireSaveEvent(workflow, saved);
        }
    }

    public static void setWorkflowName(Workflow workflow, String name){
        synchronized (LOCK){
            workflow.setName(name);

            fireNameEvent(workflow, name);

            setWorkflowSave(workflow, false);
        }
    }

    public static void setWorkflowDirectory(Workflow workflow, String directory){
        synchronized (LOCK){
            workflow.setDirectory(directory);

            fireDirectoryEvent(workflow, directory);

            setWorkflowSave(workflow, false);
        }
    }

    public static void addStepToWorkflow(Workflow workflow, Step step){
        synchronized (LOCK){
            workflow.addStep(step);

            loadStep(workflow, step);

            fireStepAdditionEvent(workflow, step);

            setWorkflowSave(workflow, false);
        }
    }

    public static void removeStepFromWorkflow(Workflow workflow, Step step){
        synchronized (LOCK){
            for(Channel channel : workflow.getChannels())
                if(channel.getFrom() == step || channel.getTo() == step)
                    removeChannelFromWorkflow(workflow, channel);

            workflow.removeStep(step);

            unloadStep(workflow, step);

            fireStepRemovalEvent(workflow, step);

            setWorkflowSave(workflow, false);
        }
    }

    public static void addChannelToWorkflow(Workflow workflow, Channel channel){
        synchronized (LOCK){
            workflow.addChannel(channel);

            loadChannel(workflow, channel);

            fireChannelAdditionEvent(workflow, channel);

            setWorkflowSave(workflow, false);
        }
    }

    public static void removeChannelFromWorkflow(Workflow workflow, Channel channel){
        synchronized (LOCK){
            workflow.removeChannel(channel);

            unloadChannel(workflow, channel);

            fireChannelRemovalEvent(workflow, channel);

            setWorkflowSave(workflow, false);
        }
    }

    public static void addChainToChannel(Channel channel, Chain chain){
        synchronized (LOCK){
            Workflow workflow = WORKFLOW_BY_CHANNEL.get(channel);

            chain.getDSLChain().connect();

            channel.addChain(chain);

            fireChainAdditionEvent(workflow, channel, chain);

            setWorkflowSave(workflow, false);
        }
    }

    public static void removeChainFromChannel(Channel channel, Chain chain){
        synchronized (LOCK){
            Workflow workflow = WORKFLOW_BY_CHANNEL.get(channel);

            chain.getDSLChain().disconnect();

            channel.removeChain(chain);

            fireChainRemovalEvent(workflow, channel, chain);

            setWorkflowSave(workflow, false);
        }
    }

    public static void setStepConfigurator(Step step, IConfigurator configurator){
        synchronized (LOCK){
            Workflow workflow = WORKFLOW_BY_STEP.get(step);

            step.setConfigurator(configurator);

            fireConfiguratorEvent(workflow, step, configurator);

            setWorkflowSave(workflow, false);
        }
    }

    public static void setArgumentValue(Step step, Argument argument, String value){
        synchronized (LOCK){
            Workflow workflow = WORKFLOW_BY_STEP.get(step);

            argument.setValue(value);

            fireArgumentEvent(workflow, argument, value);

            setWorkflowSave(workflow, false);
        }
    }

    public static void setStepX(Step step, double x){
        synchronized (LOCK){
            Workflow workflow = WORKFLOW_BY_STEP.get(step);

            step.setX(x);

            fireStepXEvent(workflow, step, x);

            setWorkflowSave(workflow, false);
        }
    }

    public static void setStepY(Step step, double y){
        synchronized (LOCK){
            Workflow workflow = WORKFLOW_BY_STEP.get(step);

            step.setY(y);

            fireStepYEvent(workflow, step, y);

            setWorkflowSave(workflow, false);
        }
    }


    public static IRepository getRepositoryUsedOn(Workflow workflow){
        Collection<Step> steps = workflow.getSteps();

        if(steps.isEmpty())
            return null;
        else
            return steps.stream().findFirst().get().getRepository();
    }

    public static Task<Void> saveAsync(Workflow workflow){
        return new BasicTask<>(() -> {
            save(workflow);
            return null;
        });
    }

    public static void save(Workflow workflow) throws EditorException {
        try{
            String data = WorkflowSerializer.serialize(workflow);
            String path = getPath(workflow);

            IO.write(data, path);

            setWorkflowSave(workflow, true);
        }catch(JSONException | IOException ex){
            throw new EditorException("Error saving workflow!", ex);
        }
    }

    public static Task<Workflow> loadAsync(File file){
        return new BasicTask<>(() -> load(file));
    }

    public static Workflow load(File file) throws EditorException {
        try{
            String data = IO.read(file.getPath());

            Workflow workflow = WorkflowSerializer.deserialize(data);

            workflow.setName(file.getName());
            workflow.setDirectory(file.getParent());
            workflow.setSaved(true);

            loadWorkflowEvents(workflow);

            return workflow;
        }catch(JSONException | IOException | RepositoryException | CommandBuilderException ex){
            throw new EditorException("Error loading workflow!", ex);
        }
    }

    public static Task<Void> generateAsync(Workflow workflow, File inputDir){
        return new BasicTask<>(() -> {
            generate(workflow, inputDir);
            return null;
        });
    }

    public static void generate(Workflow workflow, File inputsDir) throws EditorException {
        try{
            if(!inputsDir.exists())
                inputsDir.mkdirs();

            copyFiles(workflow, inputsDir);

            String path = getPipelinePath(workflow);
            String pipeline = DSLWriter.parse(workflow, inputsDir.getPath());

            IO.write(pipeline, path);
        } catch(IOException ex) {
            throw new EditorException("", ex);
        }
    }

    private static void copyFiles(Workflow workflow, File inputsDir) throws EditorException{
        for(Step step : workflow.getSteps())
            for(Argument argument : step.getArguments())
                if( argument.getValue() != null &&
                    DSLWriter.isFileArgument(argument) &&
                    !isChained(argument, step, workflow) &&
                    !DSLWriter.inInputsDir(argument, inputsDir.getAbsolutePath()))
                    copy(argument, inputsDir);
    }

    private static boolean isChained(Argument argument, Step step, Workflow workflow) {
        for(Channel channel : workflow.getChannels())
            if(channel.getTo() == step || channel.getFrom() == step)
                for(Chain chain : channel.getChains())
                    if(chain.getArgument() == argument)
                        return true;

        return false;
    }

    private static void copy(Argument argument, File inputsDir) throws EditorException{
        String dest = DSLWriter.getFilePath(argument, inputsDir.getAbsolutePath());
        File srcFile = new File(argument.getValue());
        File destFile = new File(dest);

        Path srcPath = srcFile.toPath();
        Path destPath = destFile.toPath();

        if(!srcFile.exists())//Source does not exist
            return;

        if(destFile.exists())//Dest already exists
            return;

        if(!destFile.getParentFile().exists())//Dest dir does not exist
            destFile.getParentFile().mkdirs();

        try {
            Files.copy(srcPath, destPath);
        } catch (IOException e) {
            throw new EditorException("Error copying file " + argument.getValue(), e);
        }
    }

    private static String getPath(Workflow workflow){
        String directory = workflow.getDirectory();
        String file = workflow.getName() + WorkflowManager.WORK_FLOW_FILE_EXTENSION;

        return directory + Uris.SEP + file;
    }

    private static String getPipelinePath(Workflow workflow){
        String directory = workflow.getDirectory();
        String file = workflow.getName() + WorkflowManager.WORK_FLOW_DSL_FILE_EXTENSION;

        return directory + Uris.SEP + file;
    }

}
