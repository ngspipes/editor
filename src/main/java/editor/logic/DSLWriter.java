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

import dsl.ArgumentValidator;
import dsl.entities.Argument;
import editor.dataAccess.Uris;
import editor.logic.entities.EditorChain;
import editor.logic.entities.EditorStep;
import editor.logic.entities.Flow;
import editor.utils.EditorException;
import editor.utils.Log;
import utils.IO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class DSLWriter {

	private static String getFilePath(Argument argument, File inputsDir){
		Path argumentPath = new File(argument.getValue()).toPath();
		
		StringBuilder path = new StringBuilder(inputsDir.getAbsolutePath()).append(Uris.SEP);
		
		argumentPath.forEach((p)->path.append(p).append(Uris.SEP));

		return path.toString().substring(0, path.length()-Uris.SEP.length()).replace("\\", "/"); // REMOVE LAST SEPARATOR
	}
	
	private static String extractInputDirPath(String pathStr, File inputsDir){
		Path filePath = new File(pathStr).toPath();
		Path inputsDirPath = inputsDir.toPath();
		
		int beginIdx = inputsDirPath.getNameCount();
		int endIdx = filePath.getNameCount();

		return filePath.subpath(beginIdx, endIdx).toString().replace("\\", "/");
	}

	private static final String TAG = "DSLWriter";
	private static final String EXTENSION = ".pipes";
	private static final String PIPELINE = "Pipeline";
	private static final String TOOL = "tool";
	private static final String COMMAND = "command";
	private static final String ARGUMENT = "argument";
	private static final String CHAIN = "chain";
	private static final String BEGIN_ELEMENT = "{";
	private static final String END_ELEMENT = "}";
	private static final String SEPARATOR = " ";
	private static final String PARAMETER_DELIMITER = "\"";
	private static final String LINE_BREAK = "\n";
	private static final String DIRECTORY_DELIMITER = Uris.SEP;


	public static void generate(Flow flow, File inputsDir) throws EditorException {
		if(!inputsDir.exists())
			inputsDir.mkdirs();

		copyFiles(flow, inputsDir);

		String pipeline = constructPipeline(flow, inputsDir);
		write(getPipelinePath(flow.getDirectory(), flow.getName()), pipeline);
	}


	private static void copyFiles(Flow flow, File inputsDir) throws EditorException{
		for(EditorStep step : flow.getElements().getSteps())
			for(Argument argument  : step.getArguments())
				if(argument.getValue()!=null && isFileArgument(argument) && !isChain(argument, step) && !alreadyInInputsDir(argument, inputsDir))
					copy(argument, inputsDir);
	}

	private static boolean isFileArgument(Argument argument) {
		return argument.getType().equals(ArgumentValidator.FILE_TYPE_NAME);
	}

	private static boolean isChain(Argument argument, EditorStep step) {
		for(EditorChain chain : step.getOriginFlow().getElements().getChainsTo(step))
			if(chain.getArgument() == argument)
				return true;

		return false;
	}

	private static boolean alreadyInInputsDir(Argument argument, File inputsDir) {
		return argument.getValue().startsWith(inputsDir.getAbsolutePath());
	}

	private static void copy(Argument argument, File inputsDir) throws EditorException{
		String dest = getFilePath(argument, inputsDir);
		File srcFile = new File(argument.getValue());
		File destFile = new File(dest);

		Path srcPath = srcFile.toPath();
		Path destPath = destFile.toPath();

		if(!srcFile.exists())
			return;
		
		if(destFile.exists())
			return;

		if(!destFile.getParentFile().exists())
			destFile.getParentFile().mkdirs();

		try {
			Files.copy(srcPath, destPath);
		} catch (IOException e) {
			throw new EditorException("Error copying file " + argument.getValue(), e);
		}
	}	

	private static Map<EditorStep, Integer> getToolsOrder(List<EditorStep> orderedSteps){
		Map<EditorStep, Integer> toolPos = new HashMap<	>();
		Map<String, Integer> counter = new HashMap<>();
		
		String toolName;
		int count;
		for(EditorStep step : orderedSteps){
			toolName = step.getToolDescriptor().getName();
			
			if(!counter.containsKey(toolName))
				counter.put(toolName, 0);
			
			count = counter.get(toolName);
			counter.remove(toolName);
			counter.put(toolName, ++count);
			
			toolPos.put(step, count);
		}
		
		return toolPos;
	}
	
	private static String constructPipeline(Flow flow, File inputsDir){
		List<EditorStep> orderedSteps = new LinkedList<>(flow.getElements().getSteps());
		Collections.sort(orderedSteps, (a,b)->a.getOrder()-b.getOrder());

		Map<EditorStep, Integer> toolsOrder = getToolsOrder(orderedSteps);
		
		StringBuilder pipeline = new StringBuilder();

		pipeline.append(PIPELINE)
		.append(SEPARATOR)
		.append(constructParameter(flow.getRepository().getType()))
		.append(SEPARATOR)
		.append(constructParameter(flow.getRepository().getLocation()))
		.append(SEPARATOR)
		.append(BEGIN_ELEMENT).append(LINE_BREAK);

		for(EditorStep step : orderedSteps)
			pipeline.append(constructTool(step, toolsOrder, inputsDir).replaceAll("(?m)^", "\t")).append(LINE_BREAK);

		pipeline.append(END_ELEMENT);

		return pipeline.toString();
	}

	private static String constructTool(EditorStep step, Map<EditorStep, Integer> toolsOrder, File inputsDir){
		StringBuilder tool = new StringBuilder();

		tool.append(TOOL)
		.append(SEPARATOR)
		.append(constructParameter(step.getToolDescriptor().getName()))
		.append(SEPARATOR)
		.append(constructParameter(step.getConfigurator().getName()))
		.append(SEPARATOR)
		.append(BEGIN_ELEMENT).append(LINE_BREAK)
		.append(constructCommand(step, toolsOrder, inputsDir).replaceAll("(?m)^", "\t")).append(LINE_BREAK)
		.append(END_ELEMENT);

		return tool.toString();
	}

	private static String constructCommand(EditorStep step, Map<EditorStep, Integer> toolsOrder, File inputsDir){
		StringBuilder command = new StringBuilder();

		command.append(COMMAND)
		.append(SEPARATOR)
		.append(constructParameter(step.getCommandDescriptor().getName()))
		.append(SEPARATOR)
		.append(BEGIN_ELEMENT).append(LINE_BREAK)
		.append(constructCommandInfo(step, toolsOrder, inputsDir).replaceAll("(?m)^", "\t")).append(LINE_BREAK)
		.append(END_ELEMENT);

		return command.toString();
	}

	private static String constructCommandInfo(EditorStep step, Map<EditorStep, Integer> toolsOrder, File inputsDir){
		StringBuilder commandInfo = new StringBuilder();

		List<Argument> orderedArguments = new LinkedList<>(step.getArguments());
		orderedArguments = orderedArguments.parallelStream().filter((a)->a.getValue()!=null).collect(Collectors.toList());
		Collections.sort(orderedArguments, (a,b)->a.getOrder()-b.getOrder());

		Collection<EditorChain> chains = step.getOriginFlow().getElements().getChainsTo(step);
		EditorChain chain;

		for(Argument argument : orderedArguments){
			if((chain = getChainsOf(argument, chains)) != null)
				commandInfo.append(constructChain(chain, toolsOrder)).append(LINE_BREAK);
			else
				commandInfo.append(constructArgument(argument, inputsDir)).append(LINE_BREAK);	
		}

		if(commandInfo.length() == 0)
			return "";
		else //REMOVE LAST LINE_BREAK
			return commandInfo.toString().substring(0, commandInfo.length()-LINE_BREAK.length());
	}

	private static String constructChain(EditorChain chain, Map<EditorStep, Integer> toolsOrder){
		StringBuilder ch = new StringBuilder();

		ch.append(CHAIN)
		.append(SEPARATOR)
		.append(constructParameter(chain.getArgument().getName()))
		.append(SEPARATOR)
		.append(toolsOrder.get(chain.getFrom()))
		.append(SEPARATOR)
		.append(constructParameter(chain.getFrom().getToolDescriptor().getName()))
		.append(SEPARATOR)
		.append(1)
		.append(SEPARATOR)
		.append(constructParameter(chain.getFrom().getCommandDescriptor().getName()))
		.append(SEPARATOR)
		.append(constructParameter(chain.getOutput().getName()));

		return ch.toString();
	}

	private static String constructArgument(Argument argument, File inputsDir){
		StringBuilder arg= new StringBuilder();

		arg.append(ARGUMENT)
		.append(SEPARATOR)
		.append(constructParameter(argument.getName()))
		.append(SEPARATOR);

		if(isFileArgument(argument))
			arg.append(constructParameter(getFileArgumentValue(argument, inputsDir)));
		else
			arg.append(constructParameter(argument.getValue()));

		return arg.toString();
	}

	private static String getFileArgumentValue(Argument argument, File inputsDir){
		String path = argument.getValue();

		if(!alreadyInInputsDir(argument, inputsDir))
			path = getFilePath(argument, inputsDir);

		return extractInputDirPath(path, inputsDir);
	}


	private static String constructParameter(String value){
		return PARAMETER_DELIMITER + value + PARAMETER_DELIMITER;
	}

	private static EditorChain getChainsOf(Argument argument, Collection<EditorChain> chains){
		for(EditorChain chain : chains)
			if(chain.getArgument().equals(argument))
				return chain;

		return null;
	}

	private static String getPipelinePath(String directory, String name) {
		return directory + DIRECTORY_DELIMITER + name + EXTENSION;
	}

	public static void write(String dstPath, String pipeline) throws  EditorException{
		try {
			IO.write(pipeline, dstPath);
		} catch (IOException e) {
			Log.error(TAG, "Error generating pipes file");
			throw new EditorException("Error generating pipes file!", e);
		}
	}

}
