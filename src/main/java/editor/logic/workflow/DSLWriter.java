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

import dsl.ArgumentValidator;
import dsl.entities.Argument;
import editor.dataAccess.Uris;
import javafx.util.Pair;
import repository.IRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DSLWriter {

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


    public static String parse(Workflow workflow, String inputsDir) {
        Map<Step, Integer> stepsByOrder = StepSorter.sort(workflow);
        Collection<Step> orderedSteps = orderSteps(stepsByOrder);

        StringBuilder pipeline = new StringBuilder();

        pipeline.append(PIPELINE)
                .append(SEPARATOR)
                .append(parseRepository(WorkflowManager.getRepositoryUsedOn(workflow)))
                .append(SEPARATOR)
                .append(BEGIN_ELEMENT).append(LINE_BREAK)
                .append(parseTools(workflow, orderedSteps, stepsByOrder, inputsDir))
                .append(END_ELEMENT);

        return pipeline.toString();
    }

    private static Collection<Step> orderSteps(Map<Step, Integer> stepsByOrder){
        LinkedList<Step> orderedSteps = new LinkedList<>(stepsByOrder.keySet());

        orderedSteps.sort((stepA, stepB) -> stepsByOrder.get(stepA)-stepsByOrder.get(stepB));

        return orderedSteps;
    }

    private static String parseRepository(IRepository repository){
        return constructString(repository.getType()) + SEPARATOR + constructString(repository.getLocation());
    }

    private static String parseTools(Workflow workflow, Collection<Step> orderedSteps, Map<Step, Integer> stepsByOrder, String inputsDir){
        StringBuilder tools = new StringBuilder();

        String toolParsed;
        for(Step step : orderedSteps){
            toolParsed = parseTool(workflow, step, stepsByOrder, inputsDir);
            tools.append(toolParsed.replaceAll("(?m)^", "\t")).append(LINE_BREAK);
        }

        return tools.toString();
    }

    private static String parseTool(Workflow workflow, Step step, Map<Step, Integer> stepsBuOrder, String inputsDir){
        StringBuilder tool = new StringBuilder();

        tool.append(TOOL)
            .append(SEPARATOR)
            .append(constructString(step.getTool().getName()))
            .append(SEPARATOR)
            .append(constructString(step.getConfigurator().getName()))
            .append(SEPARATOR)
            .append(BEGIN_ELEMENT).append(LINE_BREAK)
            .append(parseCommand(workflow, step, stepsBuOrder, inputsDir).replaceAll("(?m)^", "\t")).append(LINE_BREAK)
            .append(END_ELEMENT);

        return tool.toString();
    }

    private static String parseCommand(Workflow workflow, Step step, Map<Step, Integer> stepsByOrder, String inputsDir){
        StringBuilder command = new StringBuilder();

        command.append(COMMAND)
                .append(SEPARATOR)
                .append(constructString(step.getCommand().getName()))
                .append(SEPARATOR)
                .append(BEGIN_ELEMENT).append(LINE_BREAK)
                .append(parseCommandInfo(workflow, step, stepsByOrder, inputsDir).replaceAll("(?m)^", "\t")).append(LINE_BREAK)
                .append(END_ELEMENT);

        return command.toString();
    }

    private static String parseCommandInfo(Workflow workflow, Step step, Map<Step, Integer> stepsByOrder, String inputsDir){
        StringBuilder commandInfo = new StringBuilder();

        Collection<Argument> orderedArguments = orderAndFilterNullArguments(step);
        Collection<Channel> channelsToStep = getChannelsTo(workflow, step);

        Pair<Channel, Chain> channelChain;
        Channel channel;
        Chain chain;
        String info;
        for(Argument argument : orderedArguments){
            channelChain = getChainOfArgument(argument, channelsToStep);
            if(channelChain != null) {
                channel = channelChain.getKey();
                chain = channelChain.getValue();
                info = parseChain(channel, chain, stepsByOrder);
            } else {
                info = parseArgument(argument, inputsDir);
            }

            commandInfo.append(info).append(LINE_BREAK);
        }

        if(commandInfo.length() == 0)
            return "";
        else //REMOVE LAST LINE_BREAK
            return commandInfo.toString().substring(0, commandInfo.length()-LINE_BREAK.length());
    }

    private static Collection<Argument> orderAndFilterNullArguments(Step step){
        List<Argument> orderedArguments = new LinkedList<>(step.getArguments());

        orderedArguments = orderedArguments.parallelStream().filter((a) -> a.getValue()!=null).collect(Collectors.toList());

        Collections.sort(orderedArguments, (a,b) -> a.getOrder()-b.getOrder());

        return orderedArguments;
    }

    private static Collection<Channel> getChannelsTo(Workflow workflow, Step step){
        Collection<Channel> channelsToStep = new LinkedList<>();

        workflow.getChannels().forEach((channel)->{
            if(channel.getTo() == step)
                channelsToStep.add(channel);
        });

        return channelsToStep;
    }

    private static Pair<Channel, Chain> getChainOfArgument(Argument argument, Collection<Channel> channelsToStep){
        for(Channel channel : channelsToStep)
            for(Chain chain : channel.getChains())
                if(chain.getArgument() == argument)
                    return new Pair<>(channel, chain);

        return null;
    }

    private static String parseChain(Channel channel, Chain chain, Map<Step, Integer> stepsByOrder){
        StringBuilder ch = new StringBuilder();

        ch.append(CHAIN)
            .append(SEPARATOR)
            .append(constructString(chain.getArgument().getName()))
            .append(SEPARATOR)
            .append(stepsByOrder.get(channel.getFrom()))
            .append(SEPARATOR)
            .append(constructString(channel.getFrom().getTool().getName()))
            .append(SEPARATOR)
            .append(1)
            .append(SEPARATOR)
            .append(constructString(channel.getFrom().getCommand().getName()))
            .append(SEPARATOR)
            .append(constructString(chain.getOutput().getName()));

        return ch.toString();
    }

    private static String parseArgument(Argument argument, String inputsDir){
        StringBuilder arg = new StringBuilder();

        arg.append(ARGUMENT)
            .append(SEPARATOR)
            .append(constructString(argument.getName()))
            .append(SEPARATOR);

        if(isFileArgument(argument))
            arg.append(constructString(getFileArgumentValue(argument, inputsDir)));
        else
            arg.append(constructString(argument.getValue()));

        return arg.toString();
    }

    public static boolean isFileArgument(Argument argument) {
        return argument.getType().equals(ArgumentValidator.FILE_TYPE_NAME);
    }

    private static String getFileArgumentValue(Argument argument, String inputsDir){
        String path = argument.getValue();

        if(!inInputsDir(argument, inputsDir))
            path = getFilePath(argument, inputsDir);

        return extractInputDirPath(path, inputsDir);
    }

    private static String constructString(String value){
        return PARAMETER_DELIMITER + value + PARAMETER_DELIMITER;
    }

    public static boolean inInputsDir(Argument argument, String inputsDir) {
        return argument.getValue().startsWith(inputsDir);
    }

    public static String getFilePath(Argument argument, String inputsDir){
        Path argumentPath = new File(argument.getValue()).toPath();

        StringBuilder path = new StringBuilder(inputsDir).append(Uris.SEP);

        argumentPath.forEach((p)->path.append(p).append(Uris.SEP));

        return path.toString().substring(0, path.length()-Uris.SEP.length()).replace("\\", "/"); // REMOVE LAST SEPARATOR
    }

    private static String extractInputDirPath(String pathStr, String inputsDir){
        Path filePath = new File(pathStr).toPath();
        Path inputsDirPath = new File(inputsDir).toPath();

        int beginIdx = inputsDirPath.getNameCount();
        int endIdx = filePath.getNameCount();

        return filePath.subpath(beginIdx, endIdx).toString().replace("\\", "/");
    }

}
