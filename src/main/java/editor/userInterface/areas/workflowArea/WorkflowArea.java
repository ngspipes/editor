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
package editor.userInterface.areas.workflowArea;

import editor.EditorOperations;
import editor.logic.entities.Flow;
import editor.utils.EditorException;
import editor.utils.Log;
import editor.utils.Utils;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.*;

public class WorkflowArea {
	private static final String TAG = "WorkflowTable";
	
	private final TabPane workflowPane;
	private final Map<Flow, WorkflowPaper> papers;
	private final Map<Tab, Flow> tabs;
	private final List<Flow> workflows;

	public WorkflowArea(TabPane workflowPane){
		this.workflowPane = workflowPane;
		this.papers = new HashMap<>();
		this.tabs = new HashMap<>();
		this.workflows = new LinkedList<>();
		workflowPane.getSelectionModel().selectedItemProperty().addListener((obs, prev, curr)->{
			if(curr == null)
				return;
			
			Flow flow = tabs.get(curr);
			
			if(flow.getRepository()!=null)
				EditorOperations.loadRepositoryArea(flow.getRepository());
		});
	}	

	public void open(Flow workflow){
		Log.debug(TAG, "Opening Workflow name : " + workflow.getName() + " directory : " + workflow.getDirectory());
	
		WorkflowPaper paper;
		try{
			paper = new WorkflowPaper(workflow);
		}catch(EditorException ex){
			Utils.treatException(ex, TAG, ex.getMessage());
			return;
		}
		
		Tab tab = paper.getTab();
		
		papers.put(workflow, paper);
		tabs.put(paper.getTab(), workflow);
		workflows.add(workflow);
		
		workflowPane.getTabs().add(tab);
		workflowPane.getSelectionModel().select(tab);
	}

	public void close(Flow workflow) {
		Log.debug(TAG, "Closing Workflow name : " + workflow.getName() + " directory : " + workflow.getDirectory());
		
		workflowPane.getTabs().removeAll(papers.get(workflow).getTab());
		tabs.remove(papers.get(workflow).getTab());
		papers.remove(workflow);
		workflows.remove(workflow);
	}

	public Collection<Flow> getAllWorkflows(){
		return workflows;
	}

	public Flow getSelectedWorkflow(){
		return tabs.get(workflowPane.getSelectionModel().getSelectedItem());
	}
	
}

