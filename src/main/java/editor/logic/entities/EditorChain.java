package editor.logic.entities;

import jfxwfutils.Event;
import dsl.entities.Argument;
import dsl.entities.Chain;
import dsl.entities.Output;



public class EditorChain {
	
	private final EditorStep from;
	public EditorStep getFrom() { return from; }
	
	private final EditorStep to;
	public EditorStep getTo() { return to; }
	
	private final Argument argument;
	public Argument getArgument(){ return argument; }
	
	private final Output output;
	public Output getOutput(){ return output; }
	
	public final Event<Boolean> connectEvent;
	
	private final Chain dslChain;
	
	public EditorChain(EditorStep from, Output output, EditorStep to, Argument argument){
		this.from = from;
		this.to = to;
		this.argument = argument;
		this.output = output;
		this.dslChain = new Chain(argument, output);
		this.connectEvent = new Event<>();
		
		dslChain.connectEvent.addListner(connectEvent::trigger);
		
		argument.valueChangedEvent.addListner((newValue)->{
			if(!newValue.equals(output.getValue()))
				disconnect();
		});
	}
	
	public void connect(){
		dslChain.connect();
	}
	
	public void disconnect(){
		dslChain.disconnect();
	}

}
