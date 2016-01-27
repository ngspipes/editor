package editor.logic;

import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dsl.entities.Argument;
import editor.logic.entities.EditorChain;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Flow;
import editor.utils.EditorException;

class FlowSaver {
	
	protected static final String NAME_JSON_KEY = "name";
	
	protected static final String REPOSITORY_JSON_KEY = "repository";
	protected static final String REPOSITORY_TYPE_JSON_KEY = "repositoryType";
	protected static final String REPOSITORY_LOCATION_JSON_KEY = "repositoryLocation";
	
	protected static final String STEPS_JSON_KEY = "steps";
	protected static final String STEP_ORDER_JSON_KEY = "order";
	protected static final String STEP_X_POS_JSON_KEY = "xPos";
	protected static final String STEP_Y_POSS_JSON_KEY = "yPos";
	protected static final String STEP_TOOL_NAME_JSON_KEY = "toolName";
	protected static final String STEP_COMMAND_NAME_JSON_KEY = "commandName";
	protected static final String STEP_CONFIGURATOR_NAME_JSON_KEY = "configuratorName";
	protected static final String STEP_ARGUMENTS_JSON_KEY = "arguments";
	protected static final String STEP_ARGUMENT_NAME_JSON_KEY = "argumentName";
	protected static final String STEP_ARGUMENT_VALUE_JSON_KEY = "argumentValue";
	
	protected static final String CHAINS_JSON_KEY = "chains";
	protected static final String CHAIN_STEP_FROM_ORDER_JSON_KEY = "from";
	protected static final String CHAIN_STEP_TO_ORDER_JSON_KEY = "to";
	protected static final String CHAIN_STEP_ARGUMENT_NAME_JSON_KEY = "argument";
	protected static final String CHAIN_STEP_OUTPUT_NAME_JSON_KEY = "output";


	public static void saveFlow(Flow flow) throws EditorException {
		try{
			save(getFlowData(flow), getPath(flow));
			flow.setSaved(true);
		}catch(Exception e){
			throw new EditorException("Error saving flow!", e);
		}
	}
	 
	private static  void save(JSONObject data, String path) throws Exception {
		PrintWriter writer = null;

		try{
			writer = new PrintWriter(path, "UTF-8");
			writer.println(data.toString(5));
		}finally{
			if(writer != null)
				writer.close();
		}
	}
	
	public static String getPath(Flow flow){
		return flow.getDirectory() + "/" + flow.getName() + FlowManager.WORK_FLOW_FILE_EXTENSION;
	}
	
	
	private static JSONObject getFlowData(Flow flow) throws JSONException {
		JSONObject data = new JSONObject();

		data.put(NAME_JSON_KEY, flow.getName());
		data.put(REPOSITORY_JSON_KEY, getRepositoryData(flow));
		data.put(STEPS_JSON_KEY, getStepsData(flow));
		data.put(CHAINS_JSON_KEY, getChainsData(flow));

		return data;
	}

	private static JSONObject getRepositoryData(Flow flow) throws JSONException {
		JSONObject data = new JSONObject();

		if(flow.getRepository() == null)
			return data;

		data.put(REPOSITORY_LOCATION_JSON_KEY, flow.getRepository().getLocation());
		data.put(REPOSITORY_TYPE_JSON_KEY, flow.getRepository().getType());

		return data;
	}

	private static JSONArray getStepsData(Flow flow) throws JSONException {
		JSONArray data =  new JSONArray();

		for(EditorStep step : flow.getElements().getSteps())
			data.put(getStepData(step));

		return data;
	}

	private static JSONObject getStepData(EditorStep step) throws JSONException {
		JSONObject data = new JSONObject();

		data.put(STEP_ORDER_JSON_KEY, step.getOrder());
		data.put(STEP_X_POS_JSON_KEY, step.getX());
		data.put(STEP_Y_POSS_JSON_KEY, step.getY());
		data.put(STEP_TOOL_NAME_JSON_KEY, step.getToolDescriptor().getName());
		data.put(STEP_COMMAND_NAME_JSON_KEY, step.getCommandDescriptor().getName());
		data.put(STEP_CONFIGURATOR_NAME_JSON_KEY, step.getConfigurator().getName());
		data.put(STEP_ARGUMENTS_JSON_KEY, getArgumentsData(step));

		return data;
	}

	private static JSONArray getArgumentsData(EditorStep step) throws JSONException {
		JSONArray data = new JSONArray();

		for(Argument argument : step.getArguments())
			data.put(getArgumentData(argument));

		return data;
	}

	private static JSONObject getArgumentData(Argument argument) throws JSONException {
		JSONObject data = new JSONObject();

		data.put(STEP_ARGUMENT_NAME_JSON_KEY, argument.getDescriptor().getName());
		data.put(STEP_ARGUMENT_VALUE_JSON_KEY, argument.getValue());

		return data;
	}

	private static JSONArray getChainsData(Flow flow) throws JSONException {
		JSONArray data =  new JSONArray();

		for(EditorChain chain : flow.getElements().getChains())
			data.put(getChainData(chain));

		return data;
	}
	
	private static JSONObject getChainData(EditorChain chain) throws JSONException {
		JSONObject data = new JSONObject();

		data.put(CHAIN_STEP_FROM_ORDER_JSON_KEY, chain.getFrom().getOrder());
		data.put(CHAIN_STEP_TO_ORDER_JSON_KEY, chain.getTo().getOrder());
		data.put(CHAIN_STEP_ARGUMENT_NAME_JSON_KEY, chain.getArgument().getName());
		data.put(CHAIN_STEP_OUTPUT_NAME_JSON_KEY, chain.getOutput().getName());

		return data;
	}
	
}
