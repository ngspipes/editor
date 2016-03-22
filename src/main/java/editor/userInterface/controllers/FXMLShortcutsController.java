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
import editor.dataAccess.Uris;
import editor.userInterface.controllers.FXMLArgumentListViewCellController.Data;
import editor.userInterface.utils.pallet.Pallet;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;

public class FXMLShortcutsController implements IInitializable<Void>{
	
	private static class ShortcutsPallet extends Pallet<Shortcut>{

		public ShortcutsPallet(TextField textfield, ListView<Shortcut> listView) {
			super(textfield, listView);
		}

		@Override
		protected boolean filter(Shortcut item, String pattern) {
			pattern = pattern.toLowerCase();
			return item.legend.toLowerCase().contains(pattern);
		}

		@Override
		protected Node getCellRoot(Shortcut item) {
			Label label = new Label(item.toString());
			label.setStyle("-fx-background-color : transparent");
			return label;
		}
		
	}
	
	public static Node mount() throws ComponentException {
		String fXMLPath = Uris.FXML_SHORTCUTS;
		FXMLFile<Node, Data> fxmlFile = new FXMLFile<>(fXMLPath);

		fxmlFile.build();
		
		return fxmlFile.getRoot();
	} 
	
	private static String readFile() throws IOException{
		BufferedReader txtReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(Uris.SHORTCUTS_FILE)));
		
		StringBuilder text = new StringBuilder();
		String line;
	
		while((line=txtReader.readLine())!=null)
			text.append(line).append("\n");
	
		if(text.length()==0)
			return text.toString();
		else
			return text.toString().substring(0, text.length()-"\n".length()); // REMOVE LAST '\n'
	}
	
	private static class Shortcut{
		public final String shortcut;
		public final String legend;
		
		public Shortcut(String shortcut, String legend){
			this.shortcut = shortcut;
			this.legend = legend;
		}
		
		@FXML 
		public String toString(){
			return legend + " -> " + "(" + shortcut + ")";
		}
	}
	
	@FXML
	private TextField tFFilter;
	
	@FXML
	private ListView<Shortcut> lVShortcuts;

	@Override
	public void init(Void a) throws ComponentException {
		Collection<Shortcut> shortcuts = getShortcuts();
		new ShortcutsPallet(tFFilter, lVShortcuts).load(shortcuts);
	}
	
	private Collection<Shortcut> getShortcuts() throws ComponentException {
		String rawShortcuts;
		try{
			rawShortcuts = readFile();
		}catch(Exception ex){
			throw new ComponentException("Error reading file " + Uris.SHORTCUTS_FILE, ex);
		}
		
		Collection<Shortcut> shortcuts = new LinkedList<>();
		
		for(String rawShortcut : rawShortcuts.split("\n"))
			shortcuts.add(getShortcut(rawShortcut));
			
		return shortcuts;
	}
	
	private Shortcut getShortcut(String content){
		String[] splitContent = content.split(":");
		String shortcut = splitContent[0];
		String legend = splitContent[1];
		return new Shortcut(shortcut, legend);
	}
		
}
