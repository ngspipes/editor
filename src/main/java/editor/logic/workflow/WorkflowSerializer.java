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
import descriptors.IToolDescriptor;
import dsl.entities.Command;
import editor.dataAccess.repository.RepositoryManager;
import editor.transversal.EditorException;
import exceptions.CommandBuilderException;
import exceptions.RepositoryException;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import repository.IRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class WorkflowSerializer {

    private static final String REPOSITORY_JSON_KEY = "repository";
    private static final String REPOSITORY_TYPE_JSON_KEY = "repositoryType";
    private static final String REPOSITORY_LOCATION_JSON_KEY = "repositoryLocation";

    private static final String STEPS_JSON_KEY = "steps";
    private static final String STEP_ID_JSON_KEY = "id";
    private static final String STEP_X_POS_JSON_KEY = "xPos";
    private static final String STEP_Y_POS_JSON_KEY = "yPos";
    private static final String STEP_TOOL_NAME_JSON_KEY = "toolName";
    private static final String STEP_COMMAND_NAME_JSON_KEY = "commandName";
    private static final String STEP_CONFIGURATOR_NAME_JSON_KEY = "configuratorName";
    private static final String STEP_ARGUMENTS_JSON_KEY = "arguments";
    private static final String STEP_ARGUMENT_NAME_JSON_KEY = "argumentName";
    private static final String STEP_ARGUMENT_VALUE_JSON_KEY = "argumentValue";

    private static final String CHANNELS_JSON_KEY = "channels";
    private static final String CHANNEL_FROM_STEP_ID_JSON_KEY = "from";
    private static final String CHANNEL_TO_STEP_ID_JSON_KEY = "to";
    private static final String CHAINS_JSON_KEY = "chains";
    private static final String CHAIN_STEP_ARGUMENT_NAME_JSON_KEY = "argument";
    private static final String CHAIN_STEP_OUTPUT_NAME_JSON_KEY = "output";


    //SERIALIZER
    public static String serialize(Workflow workflow) throws JSONException {
        return serializeWorkflow(workflow).toString(5);
    }

    private static JSONObject serializeWorkflow(Workflow workflow) throws JSONException {
        JSONObject workflowData = new JSONObject();

        JSONObject repositoryData = serializeRepository(workflow);
        JSONArray stepsData = serializeSteps(workflow);
        JSONArray channelsData = serializeChannels(workflow);

        workflowData.put(REPOSITORY_JSON_KEY, repositoryData);
        workflowData.put(STEPS_JSON_KEY, stepsData);
        workflowData.put(CHANNELS_JSON_KEY, channelsData);

        return workflowData;
    }

    private static JSONObject serializeRepository(Workflow workflow) throws JSONException {
        IRepository repository = WorkflowManager.getRepositoryUsedOn(workflow);

        JSONObject repositoryData = new JSONObject();

        if(repository != null){
            repositoryData.put(REPOSITORY_TYPE_JSON_KEY, repository.getType());
            repositoryData.put(REPOSITORY_LOCATION_JSON_KEY, repository.getLocation());
        }

        return repositoryData;
    }

    private static JSONArray serializeSteps(Workflow workflow) throws JSONException {
        JSONArray stepsData = new JSONArray();

        for(Step step : workflow.getSteps())
            stepsData.put(serializeStep(step));

        return stepsData;
    }

    private static JSONObject serializeStep(Step step) throws JSONException {
        JSONObject stepData = new JSONObject();

        stepData.put(STEP_ID_JSON_KEY, step.hashCode());
        stepData.put(STEP_X_POS_JSON_KEY, step.getX());
        stepData.put(STEP_Y_POS_JSON_KEY, step.getY());
        stepData.put(STEP_TOOL_NAME_JSON_KEY, step.getTool().getName());
        stepData.put(STEP_COMMAND_NAME_JSON_KEY, step.getCommand().getName());

        IConfigurator configurator = step.getConfigurator();
        stepData.put(STEP_CONFIGURATOR_NAME_JSON_KEY, configurator == null ? JSONObject.NULL : configurator.getName());

        stepData.put(STEP_ARGUMENTS_JSON_KEY, serializeArguments(step));

        return stepData;
    }

    private static JSONArray serializeArguments(Step step) throws JSONException {
        JSONArray argumentsData = new JSONArray();

        for(Argument argument : step.getArguments())
            if(argument.getValue() != null)
                argumentsData.put(serializeArgument(argument));

        return argumentsData;
    }

    private static JSONObject serializeArgument(Argument argument) throws JSONException{
        JSONObject argumentData = new JSONObject();

        argumentData.put(STEP_ARGUMENT_NAME_JSON_KEY, argument.getName());
        argumentData.put(STEP_ARGUMENT_VALUE_JSON_KEY, argument.getValue() == null ? JSONObject.NULL : argument.getValue());

        return argumentData;
    }

    private static JSONArray serializeChannels(Workflow workflow) throws JSONException {
        JSONArray channelsData = new JSONArray();

        for(Channel channel : workflow.getChannels())
            channelsData.put(serializeChannel(channel));

        return channelsData;
    }

    private static JSONObject serializeChannel(Channel channel) throws JSONException {
        JSONObject channelData = new JSONObject();

        channelData.put(CHANNEL_FROM_STEP_ID_JSON_KEY, channel.getFrom().hashCode());
        channelData.put(CHANNEL_TO_STEP_ID_JSON_KEY, channel.getTo().hashCode());
        channelData.put(CHAINS_JSON_KEY, serializeChains(channel));

        return channelData;
    }

    private static JSONArray serializeChains(Channel channel) throws JSONException {
        JSONArray chainsData = new JSONArray();

        for(Chain chain :  channel.getChains())
            chainsData.put(serializeChain(chain));

        return chainsData;
    }

    private static JSONObject serializeChain(Chain chain) throws JSONException {
        JSONObject chainData = new JSONObject();

        chainData.put(CHAIN_STEP_ARGUMENT_NAME_JSON_KEY, chain.getArgument().getName());
        chainData.put(CHAIN_STEP_OUTPUT_NAME_JSON_KEY, chain.getOutput().getName());

        return chainData;
    }


    //DESERIALIZER
    public static Workflow deserialize(String data) throws JSONException, EditorException, RepositoryException, CommandBuilderException {
        JSONObject workflowData = new JSONObject(data);
        return deserializeWorkflow(workflowData);
    }

    private static Workflow deserializeWorkflow(JSONObject workflowData) throws JSONException, EditorException, RepositoryException, CommandBuilderException {
        Workflow workflow = new Workflow();

        JSONObject repositoryData = workflowData.getJSONObject(REPOSITORY_JSON_KEY);
        JSONArray stepsData = workflowData.getJSONArray(STEPS_JSON_KEY);
        JSONArray channelsData = workflowData.getJSONArray(CHANNELS_JSON_KEY);

        IRepository repository = deserializeRepository(repositoryData);
        Map<Integer, Step> stepsById = deserializeSteps(stepsData, repository);

        workflow.setSteps(new LinkedList<>(stepsById.values()));
        workflow.setChannels(deserializeChannels(channelsData, stepsById));

        return workflow;
    }

    private static IRepository deserializeRepository(JSONObject repositoryData) throws JSONException, EditorException {
        if(repositoryData.length() == 0)
            return null;

        String type = repositoryData.getString(REPOSITORY_TYPE_JSON_KEY);
        String location = repositoryData.getString(REPOSITORY_LOCATION_JSON_KEY);

        return RepositoryManager.getRepository(type, location);
    }

    private static Map<Integer, Step> deserializeSteps(JSONArray stepsData, IRepository repository) throws JSONException, RepositoryException, CommandBuilderException {
        Map<Integer, Step> steps = new HashMap<>();

        for(int i=0; i<stepsData.length(); ++i){
            Pair<Integer, Step> step = deserializeStep(stepsData.getJSONObject(i), repository);
            steps.put(step.getKey(), step.getValue());
        }

        return steps;
    }

    private static Pair<Integer, Step> deserializeStep(JSONObject stepData, IRepository repository) throws JSONException, RepositoryException, CommandBuilderException {
        Step step = new Step();

        int id = stepData.getInt(STEP_ID_JSON_KEY);
        double x = stepData.getDouble(STEP_X_POS_JSON_KEY);
        double y = stepData.getDouble(STEP_Y_POS_JSON_KEY);
        String toolName = stepData.getString(STEP_TOOL_NAME_JSON_KEY);
        String commandName = stepData.getString(STEP_COMMAND_NAME_JSON_KEY);
        String configuratorName = stepData.getString(STEP_CONFIGURATOR_NAME_JSON_KEY);
        Collection<Argument> arguments = deserializeArguments(stepData.getJSONArray(STEP_ARGUMENTS_JSON_KEY));

        IToolDescriptor tool = repository.getTool(toolName);
        Command command = new Command(tool.getCommand(commandName), null);
        IConfigurator configurator = configuratorName == null ? null : repository.getConfigurationFor(toolName, configuratorName);
        dsl.entities.Step dslStep = new dsl.entities.Step(command, configurator);

        step.setX(x);
        step.setY(y);
        step.setDSLStep(dslStep);

        for(Argument argument : arguments)
            step.getArgument(argument.getName()).setValue(argument.getValue());

        return new Pair<>(id, step);
    }

    private static Collection<Argument> deserializeArguments(JSONArray argumentsData) throws JSONException {
        Collection<Argument> arguments = new LinkedList<>();

        for(int i=0; i<argumentsData.length(); ++i)
            arguments.add(deserializeArgument(argumentsData.getJSONObject(i)));

        return arguments;
    }

    private static Argument deserializeArgument(JSONObject argumentData) throws JSONException{
        String name = argumentData.getString(STEP_ARGUMENT_NAME_JSON_KEY);
        String value = argumentData.getString(STEP_ARGUMENT_VALUE_JSON_KEY);

        return new Argument(){
            @Override
            public String getName(){return name;}
            @Override
            public String getValue(){return value;}
        };
    }

    private static Collection<Channel> deserializeChannels(JSONArray channelsData, Map<Integer, Step> stepsById) throws JSONException {
        Collection<Channel> channels = new LinkedList<>();

        for(int i=0; i<channelsData.length(); ++i)
            channels.add(deserializeChannel(channelsData.getJSONObject(i), stepsById));

        return channels;
    }

    private static Channel deserializeChannel(JSONObject channelData, Map<Integer, Step> stepsById) throws JSONException {
        int fromStepId = channelData.getInt(CHANNEL_FROM_STEP_ID_JSON_KEY);
        int toStepId = channelData.getInt(CHANNEL_TO_STEP_ID_JSON_KEY);
        JSONArray chainsData = channelData.getJSONArray(CHAINS_JSON_KEY);

        Step from = stepsById.get(fromStepId);
        Step to = stepsById.get(toStepId);
        Collection<Chain> chains = deserializeChains(chainsData, from, to);

        return new Channel(from, to, chains);
    }

    private static Collection<Chain> deserializeChains(JSONArray chainsData, Step from, Step to) throws JSONException {
        Collection<Chain> chains = new LinkedList<>();

        for(int i=0; i<chainsData.length(); ++i)
            chains.add(deserializeChain(chainsData.getJSONObject(i), from, to));

        return chains;
    }

    private static Chain deserializeChain(JSONObject chainData, Step from, Step to) throws JSONException {
        String argumentName = chainData.getString(CHAIN_STEP_ARGUMENT_NAME_JSON_KEY);
        String outputName = chainData.getString(CHAIN_STEP_OUTPUT_NAME_JSON_KEY);

        Argument argument = to.getArgument(argumentName);
        Output output = from.getOutput(outputName);
        dsl.entities.Chain dslChain = new dsl.entities.Chain(argument.getDSLArgument(), output.getDSLOutput());
        dslChain.connect();

        return new Chain(output, argument, dslChain);
    }

}
