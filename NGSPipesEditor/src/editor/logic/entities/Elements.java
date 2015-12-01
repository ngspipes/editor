package editor.logic.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.Event;


public class Elements {
	
	public final Event<EditorStep> addStepEvent = new Event<>();
	public final Event<EditorStep> removeStepEvent = new Event<>();
	public final Event<EditorChain> addChainEvent = new Event<>();
	public final Event<EditorChain> removeChainEvent = new Event<>();
	
	private final Map<EditorStep, Collection<EditorChain>> chainsFrom = new HashMap<>();
	private final Map<EditorStep, Collection<EditorChain>> chainsTo = new HashMap<>();
	private final Collection<EditorStep> steps = new LinkedList<>();
	private final Collection<EditorChain> chains = new LinkedList<>();

	
	
	public boolean hasSteps(){
		return steps.isEmpty();
	}
	
	public boolean hasChains(){
		return chains.isEmpty();
	}

	public boolean isEmpty(){
		return !hasSteps() && !hasChains();
	}
	
	public boolean containsStep(EditorStep step){
		return steps.contains(step);
	}

	public boolean containsChain(EditorChain chain){
		return chains.contains(chain);
	}

	public boolean hasChainsFrom(EditorStep step){
		return chainsFrom.containsKey(step);
	}
	
	public boolean hasChainsTo(EditorStep step){
		return chainsTo.containsKey(step);
	}
	
	public boolean hasChains(EditorStep step){
		return hasChainsFrom(step) || hasChainsTo(step);
	}
	
	public boolean hasChainsFromAndTo(EditorStep step){
		return hasChainsFrom(step) && hasChainsTo(step);
	}
		
	public Collection<EditorStep> getSteps(){
		return new LinkedList<>(steps);
	}

	public Collection<EditorChain> getChains(){
		return new LinkedList<>(chains);
	}

	public Map<EditorStep, Collection<EditorChain>> getChainsFrom(){
		return chainsFrom;
	}
	 
	public Map<EditorStep, Collection<EditorChain>> getChainsTo(){
		return chainsTo;
	}

	public Collection<EditorChain> getChainsFrom(EditorStep step){
		return chainsFrom.get(step);
	}
	
	public Collection<EditorChain> getChainsTo(EditorStep step){
		return chainsTo.get(step);
	}
	
	public Collection<EditorChain> getChainsOf(EditorStep step){
		List<EditorChain> chains =  new LinkedList<>();
		chains.addAll(chainsFrom.get(step));
		chains.addAll(chainsTo.get(step));
		return chains;
	}
	
	public int getStepsCount(){
		return steps.size();
	}
	
	public int getChainsCount(){
		return chains.size();
	}
	
	
	
	public void addStep(EditorStep step){
		steps.add(step);
		chainsFrom.put(step, new LinkedList<>());
		chainsTo.put(step, new LinkedList<>());
		addStepEvent.trigger(step);	
		setOrder();
	}
	
	public void removeStep(EditorStep step){
		steps.remove(step);
		chainsFrom.remove(step);
		chainsTo.remove(step);	
		removeStepEvent.trigger(step);		
		setOrder();
	}
	
	public void addChain(EditorChain chain){
		chains.add(chain);
		chainsFrom.get(chain.getFrom()).add(chain);
		chainsTo.get(chain.getTo()).add(chain);
		addChainEvent.trigger(chain);
		setOrder();
	}
	
	public void removeChain(EditorChain chain){
		chains.remove(chain);
		chainsFrom.get(chain.getFrom()).remove(chain);
		chainsTo.get(chain.getTo()).remove(chain);
		removeChainEvent.trigger(chain);
		setOrder();
	}

	
		
	private void setOrder(){
		Map<EditorStep, Collection<EditorStep>> chainsFrom = getMapFrom();
		Map<EditorStep, Collection<EditorStep>> chainsTo = getMapTo();
		LinkedList<EditorStep> steps = new LinkedList<>(this.steps);
		
		List<EditorStep> roots = steps.parallelStream().filter((step)->chainsTo.get(step).isEmpty()).collect(Collectors.toList());
		List<EditorStep> orderedSteps = new LinkedList<>();
		orderedSteps.addAll(roots);
		
		
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

	private Map<EditorStep, Collection<EditorStep>> getMapTo() {
		Map<EditorStep, Collection<EditorStep>> to = new HashMap<>();

		for(Map.Entry<EditorStep, Collection<EditorChain>> entry : this.chainsTo.entrySet())
			to.put(entry.getKey(), getFromStepCollection(entry.getValue()));
		return to;
	}

	private Map<EditorStep, Collection<EditorStep>> getMapFrom() {
		Map<EditorStep, Collection<EditorStep>> from = new HashMap<>();
		
		for(Map.Entry<EditorStep, Collection<EditorChain>> entry : this.chainsFrom.entrySet())
			from.put(entry.getKey(), getToStepCollection(entry.getValue()));
		
		return from;
	}

	private Collection<EditorStep> getToStepCollection(Collection<EditorChain> chains) {
		Collection<EditorStep> steps = new LinkedList<>();
		chains.forEach((c) -> addToCollection(steps, c.getTo()));
		return steps;
	}

	private Collection<EditorStep> getFromStepCollection(Collection<EditorChain> chains) {
		Collection<EditorStep> steps = new LinkedList<>();
		chains.forEach((c) -> addToCollection(steps, c.getFrom()));
		return steps;
	}
	
	private void addToCollection(Collection<EditorStep> steps, EditorStep step) {
		if(!steps.contains(step)) 
			steps.add(step);
	}

}