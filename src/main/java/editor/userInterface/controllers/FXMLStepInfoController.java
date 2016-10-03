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
package editor.userInterface.controllers;

import components.FXMLFile;
import configurators.IConfigurator;
import descriptors.IArgumentDescriptor;
import descriptors.IToolDescriptor;
import editor.dataAccess.Uris;
import editor.logic.workflow.Step;
import editor.logic.workflow.Workflow;
import editor.logic.workflow.WorkflowManager;
import editor.logic.workflow.WorkflowManager.WorkflowEvents;
import editor.transversal.Utils;
import editor.userInterface.utils.Dialog;
import editor.userInterface.utils.UIUtils;
import exceptions.RepositoryException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;
import repository.IRepository;

import java.util.Collection;
import java.util.function.Consumer;


public class FXMLStepInfoController implements IInitializable<FXMLStepInfoController.Data> {

	public static class Data{
		public final Step step;

		public Data(Step step){
			this.step = step;
		}
	}



	private static final String TAG = FXMLStepInfoController.class.getSimpleName();
	private static final Image DEFAULT_TOOL_LOGO = new Image(Uris.TOOL_LOGO_IMAGE);



	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_STEP_INFO;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}

	
	
	@FXML
	private Label lToolName;
	@FXML
	private Label lCommandName;
	@FXML
	private Label lNumberOfArguments;
	@FXML
	private Label lNumberOfOutputs;
	@FXML
	private ImageView iVToolLogo;
	@FXML
	private TabPane propertiesPane;
	@FXML
	private ComboBox<String> cBConfigurators;
	@FXML
	private Label lOrder;

	private WorkflowEvents events;
	private Workflow workflow;
	private  Step step;



	@Override
	public void init(Data data) throws ComponentException {
		this.step = data.step;
		this.workflow = WorkflowManager.getWorkflowOfStep(step);
		this.events = WorkflowManager.getEventsFor(workflow);

		loadUIComponents();
		registerListeners();
	}


	
	private void loadUIComponents() throws ComponentException {
		lToolName.setText(step.getTool().getName());
		lCommandName.setText(step.getCommand().getName());
		lNumberOfOutputs.setText(Integer.toString(step.getCommand().getOutputs().size()));
		
		int totalArguments = step.getCommand().getArguments().size();
		int requiredArgument = getNumberOfRequiredArgument();
		lNumberOfArguments.setText("(" + requiredArgument + ") " + totalArguments);

		String tip = requiredArgument + " Required\n" + totalArguments + " Total";
		Tooltip.install(lNumberOfArguments, new Tooltip(tip));

		iVToolLogo.setImage(DEFAULT_TOOL_LOGO);
		UIUtils.loadLogo(iVToolLogo, step.getTool());

		showOrder(step.getOrder());

		loadConfiguratorsComboBox();
	}

	private void loadConfiguratorsComboBox() throws ComponentException{
		IRepository repository = step.getRepository();
		IToolDescriptor tool = step.getTool();
		
		try {
			Collection<String> configuratorsNames = repository.getConfiguratorsNameFor(tool.getName());
			cBConfigurators.setItems(FXCollections.observableArrayList(configuratorsNames));
			showConfigurator(step.getConfigurator().getName());
		} catch (Exception ex) {
			throw new ComponentException("Error loading configurators from repository!", ex);
		}
	}

	private int getNumberOfRequiredArgument(){
		int count = 0;

		for(IArgumentDescriptor argument : step.getCommand().getArguments())
			if(argument.getRequired())
				count++;

		return count;
	}

	private void registerListeners(){
		registerConfiguratorListeners();
		registerOrderListeners();
	}

	private void registerConfiguratorListeners(){
		registerConfiguratorValueListener();
		registerComboBoxListener();
	}

	private void registerConfiguratorValueListener(){
		Consumer<IConfigurator> listener = (configurator) -> configurator.getName();

		registerConfiguratorValueListenerOnStepAddition(listener);
		unregisterConfiguratorValueListenerOnStepRemoval(listener);

		registerConfiguratorValueListener(listener);
	}

	private void registerConfiguratorValueListenerOnStepAddition(Consumer<IConfigurator> listener) {
		events.stepAdditionEvent.addListener((s) -> {
			if(s == step){
				registerConfiguratorValueListener(listener);
				showConfigurator(step.getConfigurator().getName());
			}
		});
	}

	private void unregisterConfiguratorValueListenerOnStepRemoval(Consumer<IConfigurator> listener) {
		events.stepRemovalEvent.addListener((s) -> {
			if(s == step)
				unregisterConfiguratorValueListener(listener);
		});
	}

	private void registerConfiguratorValueListener(Consumer<IConfigurator> listener){
		events.configuratorEvents.get(step).addListener(listener);
	}

	private void unregisterConfiguratorValueListener(Consumer<IConfigurator> listener){
		events.configuratorEvents.get(step).removeListener(listener);
	}

	private void registerComboBoxListener(){
		cBConfigurators.getSelectionModel().selectedItemProperty().addListener((obs, prev, curr) -> {
			if(WorkflowManager.isAssociatedWithWorkflow(step))
				setConfigurator(curr);
			else
				Dialog.showError("This step is not associated with any workflow!");
		});
	}

	private void setConfigurator(String configuratorName){
		IRepository repository = step.getRepository();
		IToolDescriptor tool = step.getTool();

		try{
			IConfigurator configurator = repository.getConfigurationFor(tool.getName(), configuratorName);
			WorkflowManager.setStepConfigurator(step, configurator);
		}catch(RepositoryException ex){
			Utils.treatException(ex, TAG, "Error loading configurator " + configuratorName +" for tool " + tool.getName() + " from Repository " + repository.getLocation());
		}
	}

	private void showConfigurator(String configuratorName){
		cBConfigurators.getSelectionModel().select(configuratorName);
	}

	private void registerOrderListeners() {
		Consumer<Integer> listener = this::showOrder;

		registerOrderListenerOnStepAddition(listener);
		unregisterOrderListenerOnStepRemoval(listener);

		registerOrderListener(listener);
	}

	private void registerOrderListenerOnStepAddition(Consumer<Integer> listener) {
		events.stepAdditionEvent.addListener((s) -> {
			if(s == step){
				registerOrderListener(listener);
				showOrder(step.getOrder());
			}
		});
	}

	private void unregisterOrderListenerOnStepRemoval(Consumer<Integer> listener) {
		events.stepRemovalEvent.addListener((s) -> {
			if(s == step)
				unregisterOrderListener(listener);
		});
	}

	private void registerOrderListener(Consumer<Integer> listener){
		events.orderEvents.get(step).addListener(listener);
	}

	private void unregisterOrderListener(Consumer<Integer> listener){
		events.orderEvents.get(step).removeListener(listener);
	}

	private void showOrder(int order){
		lOrder.setText(Integer.toString(order));
	}

}
