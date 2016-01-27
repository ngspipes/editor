package editor.userInterface.controllers.help;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import org.json.JSONException;

import jfxutils.ComponentException;
import jfxutils.IInitializable;
import editor.dataAccess.Uris;
import editor.userInterface.utils.pallet.Pallet;
import editor.utils.Utils;

public class FXMLHelpController implements IInitializable<Void> {
	
	private static final String TAG = "FXMLHelpController";		
	
	private static class HelpPallet extends Pallet<IVideoDescriptor> {

		private static final String TAG = "HelpPallet";		
		final Consumer<IVideoDescriptor> componentUpdater;
		
		public HelpPallet(TextField tf, ListView<IVideoDescriptor> lv, Consumer<IVideoDescriptor> c) {
			super(tf, lv);
			this.componentUpdater = c;
		}

		@Override
		protected boolean filter(IVideoDescriptor item, String pattern) {
			return item.getName().toLowerCase().contains(pattern.toLowerCase());
		}

		@Override
		protected Node getCellRoot(IVideoDescriptor video) {
			try {
				Node videoCell = FXMLVideoListViewItemController.mount(video.getName());
				videoCell.setOnMouseClicked((event) -> componentUpdater.accept(video));
				return videoCell;
			} catch (Exception e) {
				Utils.treatException(e, TAG, "Error loading HelpPallet item!");
			}
			
			return null;
		}
	}

	
	
	
	@FXML
	public TextField tFSearchVideo;

	@FXML
	public ListView<IVideoDescriptor> lvVideo;

	@FXML
	public Label lVideoName;

	@FXML
	public Label lVideoDuration;

	@FXML
	public TextArea tAVideoDescription;

	@FXML
	public MediaView mvVideo;
	
	@FXML
	public HBox hbMediaBar;
	
	@Override
	public void init(Void data) throws ComponentException {
		HelpPallet pallet = new HelpPallet(tFSearchVideo, lvVideo, this::updateComponents);
		pallet.load(getVideoList());

		if(!lvVideo.getItems().isEmpty())
			initComponents(lvVideo.getItems().get(0));
	}

	public void updateComponents(IVideoDescriptor video) {
		hbMediaBar.getChildren().clear();
		initComponents(video);
	}
	
	private void initComponents(IVideoDescriptor video) {
		lVideoName.setText(video.getName());
		lVideoDuration.setText(video.getDuration().toString());
		tAVideoDescription.setText(video.getDescription());
		initMedia(video);
	}

	private void initMedia(IVideoDescriptor video) {
		try {
			File f = new File(video.getLocation());
			if(f != null) {
				Media media = new Media(f.toURI().toString());
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				MediaControl mediaControl = new MediaControl(mediaPlayer, hbMediaBar);	
				mediaControl.load(mvVideo);
			}
		} catch (Exception e) {
			Utils.treatException(e, TAG, "Error loading video: " + video.getName());
		}
	}

	private Collection<IVideoDescriptor> getVideoList() {

		Collection<File> descriptorsFiles = getDescriptorsFiles();

		return getVideoDescriptors(descriptorsFiles);
	}

	private Collection<IVideoDescriptor> getVideoDescriptors(Collection<File> descriptorsFiles) {
		Collection<IVideoDescriptor> videos = new LinkedList<>();

		descriptorsFiles.forEach((f) -> createDescriptor(videos, f));

		return videos;
	}

	private void createDescriptor(Collection<IVideoDescriptor> videos, File f) {
		try {
			videos.add(new JSONVideoDescriptor(f.getAbsolutePath()));
		} catch (IOException | JSONException e) {
			Utils.treatException(e, TAG, "Error loading descriptor" + f.getAbsolutePath());
		}
	}

	private Collection<File> getDescriptorsFiles() {

		Collection<File> subFiles = new LinkedList<>();
		getSubFiles(Uris.TUTORIALS_DIR, subFiles);
		Collection<File> descriptorsFiles = new LinkedList<>();

		for (File file : subFiles)
			if(file.isFile() && isDescriptor(file.getAbsolutePath()))
				descriptorsFiles.add(file);

		return descriptorsFiles;
	}

	private void getSubFiles(String directoryPath, Collection<File> files) {
		File directory = new File(directoryPath);
		
		File[] fList = directory.listFiles();
		
		if(fList == null)
			return;
		
		for (File file : fList) {
			if (file.isFile())
				files.add(file);
			else
				if (file.isDirectory())
					getSubFiles(file.getAbsolutePath(), files);
		}
	}

	private boolean isDescriptor(String absolutePath) {
		String extension = absolutePath.substring(absolutePath.lastIndexOf(".") + ".".length());
		return extension.equals("json");
	}
}