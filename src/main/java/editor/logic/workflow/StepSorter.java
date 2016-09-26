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


import descriptors.IToolDescriptor;
import editor.transversal.Utils;

import java.util.*;

public class StepSorter {

    public static Map<Step, Integer> sort(Workflow workflow){
        Collection<Step> steps = workflow.getSteps();
        Map<Step, Collection<Channel>> channelsToByStep = getChannelsToByStep(workflow);
        Map<Step, Collection<Channel>> channelsFromByStep = getChannelsFromByStep(workflow);

        return new StepSorter(steps, channelsFromByStep, channelsToByStep).order();
    }

    private static Map<Step, Collection<Channel>> getChannelsToByStep(Workflow workflow){
        Map<Step, Collection<Channel>> channelsToByStep = new HashMap<>();

        workflow.getSteps().forEach((step) -> channelsToByStep.put(step, getChannelsTo(workflow, step)));

        return channelsToByStep;
    }

    private static Collection<Channel> getChannelsTo(Workflow workflow, Step step){
        Collection<Channel> channelsTo = new LinkedList<>();

        workflow.getChannels().forEach((channel) -> {
            if(channel.getTo() == step)
                channelsTo.add(channel);
        });

        return channelsTo;
    }

    private static Map<Step, Collection<Channel>> getChannelsFromByStep(Workflow workflow){
        Map<Step, Collection<Channel>> channelsFromByStep = new HashMap<>();

        workflow.getSteps().forEach((step) -> channelsFromByStep.put(step, getChannelsFrom(workflow, step)));

        return channelsFromByStep;
    }

    private static Collection<Channel> getChannelsFrom(Workflow workflow, Step step){
        Collection<Channel> channelsFrom = new LinkedList<>();

        workflow.getChannels().forEach((channel) -> {
            if(channel.getFrom() == step)
                channelsFrom.add(channel);
        });

        return channelsFrom;
    }



    private Collection<Step> steps;
    private Map<Step, Collection<Step>> chainsFrom;
    private Map<Step, Collection<Step>> chainsTo;



    private StepSorter(Collection<Step> steps, Map<Step, Collection<Channel>> channelsFrom, Map<Step, Collection<Channel>> channelsTo) {
        //With Step we can get all Chain from and to it self, but we only want Steps so we obtain then from getMapFrom() and getMapTo()
        //There possibly are multiple chains between 2 steps, due to the fact that 1 files originates 2 chains we also resolve it with same methods
        this.steps = new LinkedList<>(steps);
        this.chainsFrom = getMapFrom(channelsFrom);
        this.chainsTo = getMapTo(channelsTo);
    }



    public Map<Step, Integer> order(){
        List<Step> roots = getRoots();
        List<Step> auxSteps = new LinkedList<>(roots);

        while(!roots.isEmpty()){
            Step root = roots.remove(0);

            for(Step step : chainsFrom.get(root)){
                chainsTo.get(step).remove(root);

                if(chainsTo.get(step).isEmpty()) {
                    roots.add(0, step);
                    if(!auxSteps.contains(step))
                        auxSteps.add(step);
                }
            }
        }

        Map<Step, Integer> stepsByOrder = new HashMap<>();
        for (int idx = 0; idx < auxSteps.size(); ++idx)
            stepsByOrder.put(auxSteps.get(idx), idx+1);

        return stepsByOrder;
    }

    private List<Step> getRoots(){
        List<Step> roots = new LinkedList<>();

        steps.forEach((step) -> {
            if(chainsTo.get(step).isEmpty() && isHighestPriorityOfTool(step))
                roots.add(step);
        });

        return roots;
    }

    private boolean isHighestPriorityOfTool(Step step) {
        IToolDescriptor tool = step.getTool();
        int priority = step.getCommand().getPriority();

        IToolDescriptor currTool;
        int currPriority;
        for (Step currStep : steps) {
            currTool = currStep.getTool();
            currPriority = currStep.getCommand().getPriority();

            if(tool.equals(currTool) && priority < currPriority)
                return false;
        }

        return true;
    }

    private Map<Step, Collection<Step>> getMapTo(Map<Step, Collection<Channel>> originalChannelsTo) {
        Map<Step, Collection<Step>> chainsTo = new HashMap<>();

        Collection<Channel> channelsToStep;
        Collection<Step> stepsFrom;
        Collection<Step> morePriority;

        for(Step step : steps){
            channelsToStep = originalChannelsTo.get(step);
            stepsFrom = extractFroms(channelsToStep);
            morePriority = getStepWithMorePriority(step);
            chainsTo.put(step, Utils.distinct(stepsFrom, morePriority));
        }

        return chainsTo;
    }

    private Map<Step, Collection<Step>> getMapFrom(Map<Step, Collection<Channel>> originalChannelsFrom) {
        Map<Step, Collection<Step>> chainsFrom = new HashMap<>();

        Collection<Channel> channelsFromStep;
        Collection<Step> stepsTo;
        Collection<Step> lessPriority;

        for(Step step : steps){
            channelsFromStep = originalChannelsFrom.get(step);
            stepsTo = extractTos(channelsFromStep);
            lessPriority = getStepWithLessPriority(step);
            chainsFrom.put(step, Utils.distinct(stepsTo, lessPriority));
        }

        return chainsFrom;
    }

    private Collection<Step> getStepWithMorePriority(Step step){
        Collection<Step> morePriority = new LinkedList<>();
        IToolDescriptor tool = step.getTool();
        int priority = step.getCommand().getPriority();

        IToolDescriptor currTool;
        int currPriority;
        for (Step currStep : steps) {
            currTool = currStep.getTool();
            currPriority = currStep.getCommand().getPriority();

            if(tool.equals(currTool) && priority < currPriority)
                morePriority.add(currStep);
        }

        return morePriority;
    }

    private Collection<Step> getStepWithLessPriority(Step step){
        Collection<Step> lessPriority = new LinkedList<>();
        IToolDescriptor tool = step.getTool();
        int priority = step.getCommand().getPriority();

        IToolDescriptor currTool;
        int currPriority;
        for (Step currStep : steps) {
            currTool = currStep.getTool();
            currPriority = currStep.getCommand().getPriority();

            if(tool.equals(currTool) && priority > currPriority)
                lessPriority.add(currStep);
        }

        return lessPriority;
    }

    private Collection<Step> extractTos(Collection<Channel> channels) {
        Collection<Step> steps = new LinkedList<>();

        channels.forEach((channel) -> steps.add(channel.getTo()));

        return steps;
    }

    private Collection<Step> extractFroms(Collection<Channel> channels) {
        Collection<Step> steps = new LinkedList<>();

        channels.forEach((channel) -> steps.add(channel.getFrom()));

        return steps;
    }

}
