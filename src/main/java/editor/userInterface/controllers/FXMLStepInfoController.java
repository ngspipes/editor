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
import editor.logic.entities.EditorStep;
import editor.transversal.Utils;
import editor.userInterface.utils.UIUtils;
import exceptions.RepositoryException;
import javafx.beans.value.ObservableValue;
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



public class FXMLStepInfoController implements IInitializable<FXMLStepInfoController.Data> {
	
	private static final String TAG = "FXMLStepInfoController";
	
	public static Node mount(Data data) throws ComponentException {
		String fXMLPath = Uris.FXML_STEP_INFO;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath, data);

		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}

	
	
	public static class Data{
		public final EditorStep step;
		
		public Data(EditorStep step){
			this.step = step;
		}
	}
	
	private static final Image DEFAULT_TOOL_LOGO = new Image(Uris.TOOL_LOGO_IMAGE);
	
	
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
	
	private  EditorStep step;
	
	@Override
	public void init(Data data) throws ComponentException {
		this.step = data.step;
		load();
	}
	
	private void load() throws ComponentException {
		lToolName.setText(step.getToolDescriptor().getName());
		lCommandName.setText(step.getCommandDescriptor().getName());
		lNumberOfOutputs.setText(Integer.toString(step.getCommandDescriptor().getOutputs().size()));
		
		int totalArguments = step.getCommandDescriptor().getArguments().size();
		int requiredArgument = getNumberOfRequiredArgument();
		String tip = requiredArgument + " Required\n" + totalArguments + " Total";
		lNumberOfArguments.setText("(" + requiredArgument + ") " + totalArguments);
		Tooltip.install(lNumberOfArguments, new Tooltip(tip));
		
		setOrder(step.getOrder());
		
		iVToolLogo.setImage(DEFAULT_TOOL_LOGO);
		UIUtils.loadLogo(iVToolLogo, step.getToolDescriptor());
		
		loadConfiguratorsComboBox();
		step.orderEvent.addListener(this::setOrder);
	}
	
	private void setOrder(int order){
		lOrder.setText(Integer.toString(order));
	}
	
	private void loadConfiguratorsComboBox() throws ComponentException{
		IRepository repository = step.getRepository();
		IToolDescriptor tool = step.getToolDescriptor();
		
		try {
			Collection<String> configuratorsNames = repository.getConfiguratorsNameFor(tool.getName());
			cBConfigurators.setItems(FXCollections.observableArrayList(configuratorsNames));
			cBConfigurators.getSelectionModel().select(step.getConfigurator().getName());
			cBConfigurators.getSelectionModel().selectedItemProperty().addListener(this::onConfiguratorSelected);
		} catch (Exception ex) {
			throw new ComponentException("Error loading configurators from repository!", ex);
		}
	}
	
	private void onConfiguratorSelected(ObservableValue<? extends String> obs, String prev, String curr){
		IRepository repository = step.getRepository();
		IToolDescriptor tool = step.getToolDescriptor();
		
		try{
			IConfigurator config = repository.getConfigurationFor(tool.getName(), curr);
			step.setConfigurator(config);	
		}catch(RepositoryException ex){
			Utils.treatException(ex, TAG, "Error loading configurator " + curr +" for tool " + tool.getName() + " from Repository " + repository.getLocation());
		}
	}
	
	private int getNumberOfRequiredArgument(){
		int count = 0;
		
		for(IArgumentDescriptor argument : step.getCommandDescriptor().getArguments())
			if(argument.getRequired())
				count++;
			
		return count;
	}
	
}
