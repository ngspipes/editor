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
package editor.dataAccess.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javafx.util.Pair;
import repository.IRepository;
import configurators.IConfigurator;
import descriptors.IToolDescriptor;
import editor.dataAccess.Uris;
import editor.utils.EditorException;
import exceptions.RepositoryException;

public class EditorRepository implements IRepository{
	
	public static final String DEFAULT_TOOL_LOGO = ClassLoader.getSystemResource(Uris.TOOL_LOGO_IMAGE).toExternalForm();
	
	
	private final Collection<String> toolsName = new LinkedList<>();
	private final Collection<IToolDescriptor> tools = new LinkedList<>();
	private final Map<String, String> toolsLogos = new HashMap<>();
	private final Map<String, IToolDescriptor> toolsDescriptors = new HashMap<>();
	private final Map<Pair<String, String>, IConfigurator> configurators = new HashMap<>();
	private final Map<String, Collection<IConfigurator>> configuratorsOfTool = new HashMap<>();
	private final Map<String, Collection<String>> configuratorsNames = new HashMap<>();
	
	
	private final IRepository repository;
	
	public EditorRepository(IRepository repository) throws EditorException {
		this.repository = repository;
		load();
	}
	
	private void load() throws EditorException {
		loadToolsName();
		loadTools();
		loadToolsLogos();
		loadConfiguratorsNames();
		loadConfigurators();
	}
	
	private void loadToolsName() throws EditorException {
		try{
			toolsName.addAll(repository.getToolsName());
		}catch(RepositoryException ex){
			throw new EditorException("Error loading tools names from Repository!", ex);
		}
	}

	private void loadTools() throws EditorException {
		try{
			EditorToolDescriptor editorTool;
			for(String toolName : toolsName){
				editorTool = new EditorToolDescriptor(repository.getTool(toolName), this);
				tools.add(editorTool);
				toolsDescriptors.put(toolName, editorTool);
			}
		} catch(RepositoryException ex) {
			throw new EditorException("Error loading tools from Repository!", ex);
		}
	}

	private void loadToolsLogos() {
		String logo;
		for(String toolName : toolsName){
			logo = repository.getToolLogo(toolName);
			
			if(logo == null)
				logo = DEFAULT_TOOL_LOGO;
			
			toolsLogos.put(toolName, logo);
		}	
	}

	private void loadConfiguratorsNames() throws EditorException {
		try{
			for(String toolName : toolsName)
				configuratorsNames.put(toolName, repository.getConfiguratorsNameFor(toolName));	
		}catch(RepositoryException ex){
			throw new EditorException("Error loading Configurators names from Repository!", ex);
		}
	}

	private void loadConfigurators() throws EditorException {
		try{
			Collection<IConfigurator> editorConfigs;
			EditorConfigurator editorConfig;
			
			for(String toolName : toolsName){
				editorConfigs = new LinkedList<>();
				
				for(IConfigurator config : repository.getConfigurationsFor(toolName)){
					editorConfig = new EditorConfigurator(config, this);
					editorConfigs.add(editorConfig);
					configurators.put(new Pair<>(toolName, config.getName()), editorConfig);
				}
				
				configuratorsOfTool.put(toolName, editorConfigs);
			}
		}catch(RepositoryException ex){
			throw new EditorException("Error loading Configurators from Repository!", ex);
		}
	}

	

		
	
	@Override
	public String getLocation() {
		return repository.getLocation();
	}
	
	@Override
	public void setLocation(String location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType() {
		return repository.getType();
	}

	@Override
	public String getToolLogo(String toolName) {
		return toolsLogos.get(toolName);
	}

	@Override
	public Collection<String> getToolsName() {
		return toolsName;
	}

	@Override
	public Collection<IToolDescriptor> getAllTools() {
		return tools;
	}

	@Override
	public IToolDescriptor getTool(String toolName) {
		return toolsDescriptors.get(toolName);
	}

	@Override
	public Collection<String> getConfiguratorsNameFor(String toolName) {
		return configuratorsNames.get(toolName);
	}

	@Override
	public Collection<IConfigurator> getConfigurationsFor(String toolName) {
		return configuratorsOfTool.get(toolName);
	}

	@Override
	public IConfigurator getConfigurationFor(String toolName, String configuratorName) {
		return configurators.get(new Pair<>(toolName, configuratorName));
	}

}
