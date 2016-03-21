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
		
		dslChain.connectEvent.addListener(connectEvent::trigger);
		
		argument.valueChangedEvent.addListener((newValue)->{
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
