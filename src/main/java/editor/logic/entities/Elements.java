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
package editor.logic.entities;

import jfxwfutils.Event;

import java.util.*;


public class Elements {
	
	public final Event<EditorStep> addStepEvent = new Event<>();
	public final Event<EditorStep> removeStepEvent = new Event<>();
	public final Event<EditorChain> addChainEvent = new Event<>();
	public final Event<EditorChain> removeChainEvent = new Event<>();
	
	private final Map<EditorStep, Collection<EditorChain>> chainsFrom = new HashMap<>();
	private final Map<EditorStep, Collection<EditorChain>> chainsTo = new HashMap<>();
	private final Collection<EditorStep> steps = new LinkedList<>();
	private final Collection<EditorChain> chains = new LinkedList<>();

	private final StepsSorter stepsOrdered = new StepsSorter();
	
	public boolean hasSteps(){
		return !steps.isEmpty();
	}
	
	public boolean hasChains(){
		return !chains.isEmpty();
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
		stepsOrdered.order(steps, chainsFrom, chainsTo);
	}
	
	public void removeStep(EditorStep step){
		steps.remove(step);
		chainsFrom.remove(step);
		chainsTo.remove(step);	
		removeStepEvent.trigger(step);		
		stepsOrdered.order(steps, chainsFrom, chainsTo);
	}
	
	public void addChain(EditorChain chain){
		chains.add(chain);
		chainsFrom.get(chain.getFrom()).add(chain);
		chainsTo.get(chain.getTo()).add(chain);
		addChainEvent.trigger(chain);
		stepsOrdered.order(steps, chainsFrom, chainsTo);
	}
	
	public void removeChain(EditorChain chain){
		chains.remove(chain);
		chainsFrom.get(chain.getFrom()).remove(chain);
		chainsTo.get(chain.getTo()).remove(chain);
		removeChainEvent.trigger(chain);
		stepsOrdered.order(steps, chainsFrom, chainsTo);
	}

}