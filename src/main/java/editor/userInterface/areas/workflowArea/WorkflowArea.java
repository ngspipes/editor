package editor.userInterface.areas.workflowArea;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import editor.EditorOperations;
import editor.logic.entities.Flow;
import editor.utils.EditorException;
import editor.utils.Log;
import editor.utils.Utils;

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
	
		WorkflowPaper paper = null;
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
