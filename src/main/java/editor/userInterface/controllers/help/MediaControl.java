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
package editor.userInterface.controllers.help;


import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaControl {
	
	private MediaPlayer mp;	
	private HBox hbMediaBar;
	private final boolean repeat = false;
	private boolean stopRequested = false;
	private boolean atEndOfMedia = false;
	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private Slider volumeSlider;
	final Button playButton;

	public MediaControl(MediaPlayer mp, HBox hbMediaBar) {
		this.mp = mp;
		this.hbMediaBar = hbMediaBar;
		playButton  = new Button();
		playTime = new Label();
		volumeSlider = new Slider();   
	}
	
	public void load(MediaView mediaView) {

		configureComponents(); 
		mp.setAutoPlay(true);
		mediaView.setMediaPlayer(mp);
	}

	private void configureComponents() {
		
		configurePlayButton();		
		configureMediaPlayer();
		configureTimeSlider();
		configureVolumeSlider();
		addComponentsToMediaBar();
	}

	private void addComponentsToMediaBar() {
		hbMediaBar.getChildren().add(playButton);

		// Add spacer
		Label spacer = new Label("   ");
		hbMediaBar.getChildren().add(spacer);

		// Add Time label
		Label timeLabel = new Label("Time: ");
		hbMediaBar.getChildren().add(timeLabel);

		hbMediaBar.getChildren().add(timeSlider);

		// Add Play label
		playTime.setPrefWidth(130);
		playTime.setMinWidth(50);
		hbMediaBar.getChildren().add(playTime);

		// Add the volume label
		Label volumeLabel = new Label("Vol: ");
		hbMediaBar.getChildren().add(volumeLabel);
		hbMediaBar.getChildren().add(volumeSlider);
	}

	private void configureVolumeSlider() {
		// Add Volume slider     
		volumeSlider.setPrefWidth(70);
		volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (volumeSlider.isValueChanging())
					mp.setVolume(volumeSlider.getValue() / 100.0);
			}
		});
		volumeSlider.setMinWidth(30);
	}

	private void configureTimeSlider() {
		// Add time slider
		timeSlider = new Slider();
		HBox.setHgrow(timeSlider,Priority.ALWAYS);
		timeSlider.setMinWidth(50);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		timeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (timeSlider.isValueChanging())
					mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
			}
		});
	}

	private void configureMediaPlayer() {
		mp.currentTimeProperty().addListener(new InvalidationListener()  {
			public void invalidated(Observable ov) {
				updateValues();
			}
		});

		mp.setOnPlaying(() -> {
				if (stopRequested) {
					mp.pause();
					stopRequested = false;
				} 
				else
					playButton.setText("||");
		});

		mp.setOnPaused(() -> playButton.setText(">"));

		mp.setOnReady(()-> {
				duration = mp.getMedia().getDuration();
				updateValues();
		});

		mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
		mp.setOnEndOfMedia(() -> {
				if (!repeat) {
					playButton.setText(">");
					stopRequested = true;
					atEndOfMedia = true;
				}
			});
	}

	private void configurePlayButton() {
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				Status status = mp.getStatus();

				if (status == Status.UNKNOWN  || status == Status.HALTED) 
					return;

				if ( status == Status.PAUSED
						|| status == Status.READY
						|| status == Status.STOPPED) {
					if (atEndOfMedia) {
						mp.seek(mp.getStartTime());
						atEndOfMedia = false;
					}
					mp.play();
				} 
				else
					mp.pause();
			}
		});
	}

	protected void updateValues() {
		if (playTime != null && timeSlider != null && volumeSlider != null) {
			Platform.runLater(() -> {
					Duration currentTime = mp.getCurrentTime();
					playTime.setText(formatTime(currentTime, duration));
					timeSlider.setDisable(duration.isUnknown());
					if (!timeSlider.isDisabled() 
							&& duration.greaterThan(Duration.ZERO) 
							&& !timeSlider.isValueChanging())
						timeSlider.setValue(currentTime.divide(duration).toMillis()	* 100.0);

					if (!volumeSlider.isValueChanging())
						volumeSlider.setValue((int)Math.round(mp.getVolume() * 100));
			});
		}
	}

	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int)Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0)
			intElapsed -= elapsedHours * 60 * 60;
		
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int)Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0)
				intDuration -= durationHours * 60 * 60;
			
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, 
									elapsedMinutes, elapsedSeconds, durationHours, 
									durationMinutes, durationSeconds);
			} 
			else
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, 
									elapsedSeconds,durationMinutes, durationSeconds);
		} else {
			if (elapsedHours > 0)
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			else
				return String.format("%02d:%02d",elapsedMinutes, elapsedSeconds);
		}
	}
}