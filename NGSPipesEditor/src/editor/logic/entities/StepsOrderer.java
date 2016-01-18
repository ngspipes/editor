package editor.logic.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import descriptors.IToolDescriptor;
import editor.utils.Utils;

public class StepsOrderer {
	
	private LinkedList<EditorStep> steps;
	private Map<EditorStep, Collection<EditorStep>> chainsFrom;
	private Map<EditorStep, Collection<EditorStep>> chainsTo;
	

	public void order(Collection<EditorStep> steps, Map<EditorStep, Collection<EditorChain>> chainsFrom, Map<EditorStep, Collection<EditorChain>> chainsTo){
		//With Step we can get all Chain from a to it self, but we only want Steps so we obtain then from getMapFrom() and getMapTo()
		//There possibly are multiple chains between 2 steps, due to the fact that 1 files originates 2 chains we also resolve it with same methods
		this.steps = new LinkedList<>(steps);
		this.chainsFrom = getMapFrom(chainsFrom);
		this.chainsTo = getMapTo(chainsTo);
		
		setOrder();
	}
	
	private void setOrder(){
		List<EditorStep> roots = getRoots();
		List<EditorStep> orderedSteps = new LinkedList<>(roots);
		
		while(!roots.isEmpty()){
			EditorStep root = roots.remove(0);

			for(EditorStep step : chainsFrom.get(root)){				
				chainsTo.get(step).remove(root);

				if(chainsTo.get(step).isEmpty()) {
					roots.add(0, step);
					if(!orderedSteps.contains(step))
						orderedSteps.add(step);
				}
			}
		}
		
		for (int idx = 0; idx < orderedSteps.size(); ++idx)
			orderedSteps.get(idx).setOrder(idx + 1);
	}
	
	private List<EditorStep> getRoots(){
		List<EditorStep> roots = new LinkedList<>();
		
		for (EditorStep step : steps) {
			if(chainsTo.get(step).isEmpty() && isHighestPriorityOfTool(step))
				roots.add(step);
		}
		
		return roots;
	}

	private boolean isHighestPriorityOfTool(EditorStep step) {
		IToolDescriptor toolDescriptor = step.getToolDescriptor();
		int priority = step.getCommandDescriptor().getPriority();
		IToolDescriptor currToolDescriptor;
		int currPriority;
		
		for (EditorStep currStep : steps) {
			currToolDescriptor = currStep.getToolDescriptor();
			currPriority = currStep.getCommandDescriptor().getPriority();
			
			if(toolDescriptor.equals(currToolDescriptor) && priority < currPriority)
				return false;
		}
		
		return true;
	}

	private Map<EditorStep, Collection<EditorStep>> getMapTo(Map<EditorStep, Collection<EditorChain>> originalChainsTo) {
		Map<EditorStep, Collection<EditorStep>> chainsTo = new HashMap<>();
		
		Collection<EditorChain> chainsToStep;
		Collection<EditorStep> stepsFrom;
		Collection<EditorStep> morePriority;
		
		for(EditorStep step : steps){
			chainsToStep = originalChainsTo.get(step);
			stepsFrom = extractFroms(chainsToStep);
			morePriority = getStepWithMorePriority(step);
			chainsTo.put(step, Utils.distinct(stepsFrom, morePriority));
		}
	
		return chainsTo;
	}
	
	private Map<EditorStep, Collection<EditorStep>> getMapFrom(Map<EditorStep, Collection<EditorChain>> originalChainsFrom) {
		Map<EditorStep, Collection<EditorStep>> chainsFrom = new HashMap<>();
		
		Collection<EditorChain> chainsFromStep;
		Collection<EditorStep> stepsTo;
		Collection<EditorStep> lessPriority;
		
		for(EditorStep step : steps){
			chainsFromStep = originalChainsFrom.get(step);
			stepsTo = extractTos(chainsFromStep);
			lessPriority = getStepWithLessPriority(step);
			chainsFrom.put(step, Utils.distinct(stepsTo, lessPriority));
		}
		
		return chainsFrom;
	}
	
	private Collection<EditorStep> getStepWithMorePriority(EditorStep step){
		Collection<EditorStep> morePriority = new LinkedList<>();
		IToolDescriptor toolDescriptor = step.getToolDescriptor();
		int priority = step.getCommandDescriptor().getPriority();
		IToolDescriptor currToolDescriptor;
		int currPriority;
		
		for (EditorStep currStep : steps) {
			currToolDescriptor = currStep.getToolDescriptor();
			currPriority = currStep.getCommandDescriptor().getPriority();
			
			if(toolDescriptor.equals(currToolDescriptor) && priority < currPriority)
				morePriority.add(currStep);
		}
		
		return morePriority;
	}
	
	private Collection<EditorStep> getStepWithLessPriority(EditorStep step){
		Collection<EditorStep> lessPriority = new LinkedList<>();
		IToolDescriptor toolDescriptor = step.getToolDescriptor();
		int priority = step.getCommandDescriptor().getPriority();
		IToolDescriptor currToolDescriptor;
		int currPriority;
		
		for (EditorStep currStep : steps) {
			currToolDescriptor = currStep.getToolDescriptor();
			currPriority = currStep.getCommandDescriptor().getPriority();
			
			if(toolDescriptor.equals(currToolDescriptor) && priority > currPriority)
				lessPriority.add(currStep);
		}
		
		return lessPriority;
	}

	private Collection<EditorStep> extractTos(Collection<EditorChain> chains) {
		Collection<EditorStep> steps = new LinkedList<>();
		
		chains.forEach((chain) -> steps.add(chain.getTo()));
		
		return steps;
	}

	private Collection<EditorStep> extractFroms(Collection<EditorChain> chains) {
		Collection<EditorStep> steps = new LinkedList<>();
		
		chains.forEach((chain) -> steps.add(chain.getFrom()));
		
		return steps;
	}
	
}
