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
package editor.logic;

import configurators.IConfigurator;
import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import dsl.entities.Argument;
import dsl.entities.Command;
import dsl.entities.Output;
import dsl.entities.Step;
import editor.dataAccess.Uris;
import editor.logic.entities.EditorChain;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Flow;
import editor.transversal.EditorException;
import editor.transversal.Utils;
import editor.userInterface.utils.UIUtils;
import exceptions.CommandBuilderException;
import exceptions.RepositoryException;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import repository.IRepository;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;



class FlowLoader {

	private static final String TAG = FlowLoader.class.getSimpleName();

	public static void load(String name, String directory, BiConsumer<Exception, Flow> callBack) throws EditorException {
		String flowPath = directory + Uris.SEP + name + FlowManager.WORK_FLOW_FILE_EXTENSION;
		File flowFile = new File(flowPath);

		if(!flowFile.exists())
			throw new EditorException("Nonexistent file");

		try{
			Flow flow = new Flow(name, directory);
			
			load(new JSONObject(utils.IO.read(flowPath)), flow, (ex)->{
				if(ex!=null)
					callBack.accept(ex, null);
				else
					callBack.accept(null, flow);
			});
			
		}catch(Exception e){
			throw new EditorException("Error loading flow!", e);
		}
	}

	private static void load(JSONObject json, Flow flow, Consumer<Exception> callback) {
		try {
			loadRepository((JSONObject) json.get(FlowSaver.REPOSITORY_JSON_KEY), (ex, repo)->{
				if(ex!=null)
					callback.accept(ex);
				else{
					try {
						List<EditorStep> steps = getSteps(repo, json.getJSONArray(FlowSaver.STEPS_JSON_KEY));
						Collection<EditorChain> chains = getChains(steps, json.getJSONArray(FlowSaver.CHAINS_JSON_KEY));
						
						steps.forEach(flow::addStep);
						chains.forEach(flow::addChain);
						
						flow.setSaved(true);
					} catch (Exception e) {
						ex = e;
					}
					
					if(ex!=null)
						callback.accept(ex);
					else
						callback.accept(null);
				}
			});
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading flow");
		}
	}

	// LOAD CHAINS
	private static Collection<EditorChain> getChains(List<EditorStep> steps, JSONArray data) throws JSONException {
		Collection<EditorChain> chains = new LinkedList<>();
		
		if(data.length()!=0)
			for(int i=0; i<data.length(); ++i)
				chains.add(getChain(data.getJSONObject(i), steps));
		
		return chains;
	}

	private static EditorChain getChain(JSONObject data, Collection<EditorStep> steps) throws JSONException {
		EditorStep from = getStepByOrder(data.getInt(FlowSaver.CHAIN_STEP_FROM_ORDER_JSON_KEY), steps);
		EditorStep to = getStepByOrder(data.getInt(FlowSaver.CHAIN_STEP_TO_ORDER_JSON_KEY), steps);
		Output output = from.getOutput(data.getString(FlowSaver.CHAIN_STEP_OUTPUT_NAME_JSON_KEY));
		Argument argument = to.getArgument(data.getString(FlowSaver.CHAIN_STEP_ARGUMENT_NAME_JSON_KEY));


		return new EditorChain(from, output, to, argument);
	}

	private static EditorStep getStepByOrder(int stepOrder, Collection<EditorStep> steps) {

		for(EditorStep step : steps)
			if(step.getOrder() == stepOrder)
				return step;

		return null;
	}




	// LOAD STEPS
	private static List<EditorStep> getSteps(IRepository repository, JSONArray data) throws JSONException, RepositoryException, CommandBuilderException {
		List<EditorStep> steps = new LinkedList<>();
		
		if(data.length()!=0)
			for(int i=0; i<data.length(); ++i)
				steps.add(getStep(data.getJSONObject(i), repository));
		
		return steps;
	}

	private static EditorStep getStep(JSONObject data, IRepository repository) throws JSONException, RepositoryException, CommandBuilderException {
		int order = data.getInt(FlowSaver.STEP_ORDER_JSON_KEY);
		double x = data.getDouble(FlowSaver.STEP_X_POS_JSON_KEY);
		double y = data.getDouble(FlowSaver.STEP_Y_POSS_JSON_KEY);
		IToolDescriptor toolDescriptor = repository.getTool(data.getString(FlowSaver.STEP_TOOL_NAME_JSON_KEY));
		ICommandDescriptor commandDescriptor = toolDescriptor.getCommand(data.getString(FlowSaver.STEP_COMMAND_NAME_JSON_KEY));
		IConfigurator configurator = repository.getConfigurationFor(toolDescriptor.getName(), data.getString(FlowSaver.STEP_CONFIGURATOR_NAME_JSON_KEY));
		Map<String, String> argumentsValues = getArgumentsValues(data.getJSONArray(FlowSaver.STEP_ARGUMENTS_JSON_KEY));

		Command command = new Command(commandDescriptor, null);
		Step dslStep = new Step(command, configurator);
		dslStep.setOrder(order);

		for(String argName : argumentsValues.keySet())
			command.getArgument(argName).setValue(argumentsValues.get(argName));

		return new EditorStep(x, y, dslStep);
	}

	private static void loadRepository(JSONObject repo, BiConsumer<Exception, IRepository> callback) throws JSONException {
		if(repo.length()==0)
			Platform.runLater(()->callback.accept(null, null));
		else
			UIUtils._(repo.getString(FlowSaver.REPOSITORY_TYPE_JSON_KEY), repo.getString(FlowSaver.REPOSITORY_LOCATION_JSON_KEY), callback);
	}

	private static Map<String, String> getArgumentsValues(JSONArray data) throws JSONException {
		Map<String, String> argumentsValues = new HashMap<>();

		JSONObject arg;
		for(int i=0; i<data.length(); ++i){
			arg = data.getJSONObject(i);
			argumentsValues.put(arg.getString(FlowSaver.STEP_ARGUMENT_NAME_JSON_KEY), 
					getArgumentValue(arg));
		}

		return argumentsValues;
	}

	private static String getArgumentValue(JSONObject arg) throws JSONException {
		String value = arg.has(FlowSaver.STEP_ARGUMENT_VALUE_JSON_KEY) ? 
				arg.getString(FlowSaver.STEP_ARGUMENT_VALUE_JSON_KEY): null;
				return value;
	}

}
