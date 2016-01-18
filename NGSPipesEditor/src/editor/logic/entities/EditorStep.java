package editor.logic.entities;

import java.util.Collection;

import repository.IRepository;
import utils.Event;
import configurators.IConfigurator;
import descriptors.ICommandDescriptor;
import descriptors.IToolDescriptor;
import dsl.entities.Argument;
import dsl.entities.Output;
import dsl.entities.Step;


public class EditorStep{
	
	public final Event<Flow> originFlowEvent;
	private Flow originFlow;
	public Flow getOriginFlow(){
		return originFlow;
	}
	public void setOriginFlow(Flow originFlow){
		this.originFlow = originFlow;
		originFlowEvent.trigger(originFlow);
	}
	
	public final Event<Boolean> saveEvent;
	private boolean saved;
	public boolean getSaved(){ return saved; }
	private void setSaved(boolean saved){ 
		this.saved = saved;
		saveEvent.trigger(saved);
	}

	public final Event<Integer> orderEvent;
	public int getOrder(){ return dslStep.getOrder(); }
	public void setOrder(int order){ 
		dslStep.setOrder(order);
		setSaved(false);
	}

	private double x;
	public double getX(){ return x; }
	public void setX(double x){
		this.x = x;
		setSaved(false);
	}

	private double y;
	public double getY(){ return y; }
	public void setY(double y){
		this.y = y;
		setSaved(false);
	}

	public final Event<IConfigurator> configuratorEvent;
	public IConfigurator getConfigurator(){ return dslStep.getConfigurator(); }
	public void setConfigurator(IConfigurator configurator){
		dslStep.setConfigurator(configurator);
		setSaved(false);
	}
	
	public IRepository getRepository(){ return dslStep.getCommand().getDescriptor().getOriginTool().getOriginRepository(); }
	public IToolDescriptor getToolDescriptor(){ return dslStep.getCommand().getDescriptor().getOriginTool(); }
	public ICommandDescriptor getCommandDescriptor(){ return dslStep.getCommand().getDescriptor(); }

	private final Step dslStep;
	
	public EditorStep(double x, double y, Step dslStep){
		this.saved = true;
		this.saveEvent = new Event<>();
		
		this.orderEvent = new Event<>();
		dslStep.orderEvent.addListner(this.orderEvent::trigger);
		
		this.originFlowEvent = new Event<>();
		
		this.configuratorEvent = new Event<>();
		dslStep.configuratorEvent.addListner(this.configuratorEvent::trigger);
		
		this.dslStep = dslStep;
		
		this.x = x;
		this.y = y;
		registerOnValuesChanges();
	}
	
	private void registerOnValuesChanges(){
		for(Argument argument : dslStep.getCommand().getArguments()) 
			argument.valueChangedEvent.addListner(()->setSaved(false));
		
		for(Output output: dslStep.getCommand().getOutputs()) 
			output.valueChangedEvent.addListner(()->setSaved(false));
	}
	
	public Collection<Argument> getArguments(){
		return dslStep.getCommand().getArguments();
	}
	
	public Collection<Output> getOutputs(){
		return dslStep.getCommand().getOutputs();
	}

	public Argument getArgument(String argumentName){
		return dslStep.getCommand().getArgument(argumentName);
	}

	public Output getOutput(String outputName){
		return dslStep.getCommand().getOutput(outputName);
	}
	
}