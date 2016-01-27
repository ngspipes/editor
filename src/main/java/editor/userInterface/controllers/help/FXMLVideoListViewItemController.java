package editor.userInterface.controllers.help;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import jfxutils.ComponentException;
import jfxutils.IInitializable;

import components.FXMLFile;

import editor.dataAccess.Uris;

public class FXMLVideoListViewItemController implements IInitializable<String> {
	
	public static Node mount(String videoName) throws ComponentException {
		
		FXMLFile<Node, String> fxmlFile = new FXMLFile<>(Uris.FXML_VIDEOS_LIST_VIEW_ITEM, videoName);
		fxmlFile.build();
		
		return fxmlFile.getRoot();
	}
	
	public static Node mount() throws ComponentException {
		return mount(null);
	} 

	@FXML
	public Label lVideoName;
	
	@FXML
	public ImageView ivVideoFile;
	
	@Override
	public void init(String name) throws ComponentException {
		lVideoName.setText(name);
	}

}
