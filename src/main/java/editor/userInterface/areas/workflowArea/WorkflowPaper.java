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

import components.DoubleClickable;
import editor.EditorOperations;
import editor.logic.entities.Flow;
import editor.userInterface.controllers.FXMLTabHeaderController;
import editor.userInterface.controllers.FXMLTabHeaderController.Data;
import editor.transversal.EditorException;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jfxutils.ComponentException;

public class WorkflowPaper {

	protected static final double MAX_ZOOM = 3;
	protected static final double MIN_ZOOM = .5;
	protected static final int WIDTH = 4000;
	protected static final int HEIGHT = 4000;

	
	private final Flow flow;
	private final Tab tab;
	private final TabPane tabContent;
	private final Tab orderTab;
	private final Tab chainTab;
	private final OrderPaper orderPaper;
	private final ChainPaper chainPaper;


	public WorkflowPaper(Flow flow) throws EditorException {
		this.flow = flow;
		this.tab = new Tab();
		this.tabContent = new TabPane();
		this.orderTab = new Tab();
		this.chainTab = new Tab();
		this.orderPaper = new OrderPaper(flow);
		this.chainPaper = new ChainPaper(flow);
		setTabHeader();
		loadTabs();
	}

	private void setTabHeader() throws EditorException {
		try{
			Node root = FXMLTabHeaderController.mount(new Data(flow, ()->EditorOperations.closeWorkflow(flow)));
			new DoubleClickable<>(root, EditorOperations::slideOutAllAreas).mount();
			tab.setGraphic(root);
		} catch(ComponentException ex) {
			throw new EditorException("Error loading Tab header!", ex);
		}
	}

	private void loadTabs() {
		tab.setContent(tabContent);
				
		tabContent.getTabs().add(chainTab);
		tabContent.getTabs().add(orderTab);
		
		tabContent.getSelectionModel().selectedItemProperty().addListener((ev)->EditorOperations.slideOutStepArea());
		
		chainTab.setClosable(false);
		orderTab.setClosable(false);
		
		chainTab.setGraphic(new Label("Chain"));
		orderTab.setGraphic(new Label("Order"));
		
		chainTab.getGraphic().setStyle("-fx-background-color : transparent");
		orderTab.getGraphic().setStyle("-fx-background-color : transparent");
		
		chainTab.setContent(chainPaper.getContent());
		orderTab.setContent(orderPaper.getContent());
	}
	
	public Tab getTab(){
		return tab;
	}
	
}
