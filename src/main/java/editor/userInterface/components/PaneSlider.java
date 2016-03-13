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
package editor.userInterface.components;

import java.util.function.Consumer;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import editor.dataAccess.Uris;


public class PaneSlider {

	public enum SlideSide{
		UP, 
		DOWN, 
		LEFT, 
		RIGHT
	}

	private static final Image LEFT_EXPAND_IMAGE = new Image(Uris.LEFT_EXPAND_IMAGE);
	private static final Image RIGHT_EXPAND_IMAGE = new Image(Uris.RIGHT_EXPAND_IMAGE);
	private static final Image UP_EXPAND_IMAGE = new Image(Uris.UP_EXPAND_IMAGE);
	private static final Image DOWN_EXPAND_IMAGE = new Image(Uris.DOWN_EXPAND_IMAGE);
	private static final int DEFAULT_ANIMATIONS_DURATION = 1000;

	private ImageView showImage;
	private ImageView hideImage;
	private SlideSide side;
	private final int animationsDuration;
	private final Button button;
	private final Pane pane;
	private boolean visible;


	public PaneSlider(SlideSide side, Pane pane, Button button, int animationsDuration){
		this.side = side;
		this.pane = pane;
		this.button = button;
		this.animationsDuration = animationsDuration;
		this.visible = true;
		loadImages();
	}

	public PaneSlider(SlideSide side, Pane pane, Button button){
		this(side, pane, button, DEFAULT_ANIMATIONS_DURATION);
	}

	public PaneSlider(SlideSide side, Pane pane){
		this(side, pane, new Button());
	}

	public PaneSlider(SlideSide side, Button button){
		this(side, new AnchorPane(), button);
	}

	public PaneSlider(SlideSide side){
		this(side, new AnchorPane(), new Button());
	} 

	private void loadImages(){
		Image show = null;
		Image hide = null;
		
		switch (side) {
		case UP: 	show = DOWN_EXPAND_IMAGE;  	hide = UP_EXPAND_IMAGE;		break;
		case DOWN:	show = UP_EXPAND_IMAGE; 	hide = DOWN_EXPAND_IMAGE;	break;
		case LEFT:	show = RIGHT_EXPAND_IMAGE;	hide = LEFT_EXPAND_IMAGE;	break;
		case RIGHT:	show = LEFT_EXPAND_IMAGE; 	hide = RIGHT_EXPAND_IMAGE;	break;
		}
		
		showImage = new ImageView(show);
		hideImage = new ImageView(hide);
	}
	

	public void mount(){
		if(side.equals(SlideSide.UP) || side.equals(SlideSide.DOWN)){
			pane.setPrefHeight(pane.getMaxHeight());
			pane.setMinHeight(0);
		}else{
			pane.setPrefWidth(pane.getMaxWidth());
			pane.setMinWidth(0);	
		}
		
		button.setGraphic(hideImage);
		button.setOnMouseClicked(this::onMouseClicked);
	}

	
	public void show(){
		slideIn(0);
	}
	
	public void hide(){
		slideOut(0);
	}
	
	public void slideIn(){
		slideIn(animationsDuration);
	}
	
	public void slideOut(){
		slideOut(animationsDuration);
	}
	
	private void slideIn(double duration){
		if(!visible){
			Animation animation = getShowAnimation(duration);
			pane.setVisible(true);
			button.setGraphic(hideImage);
			animation.play();
			visible = true;
		}
	}
	
	private void slideOut(double duration){
		if(visible){
			Animation animation = getHideAnimation(duration);
			animation.onFinishedProperty().set((e)->pane.setVisible(false));
			button.setGraphic(showImage);
			animation.play();	
			visible = false;
		}
		
	}
	
	private void onMouseClicked(MouseEvent event){
		if(pane.isVisible())
			slideOut();
		else
			slideIn();
	}

	private Animation getHideAnimation(double duration){
		final Consumer<Double> animation = findHideAnimation();
		
		Animation hideAnimation = new Transition() {
			{ setCycleDuration(Duration.millis(duration)); }
			protected void interpolate(double frac) {
				animation.accept(frac);
			}
		};

		return hideAnimation;
	}
	
	public Consumer<Double> findHideAnimation(){
		switch (side) {
		case UP: return this::upHideAnimation;
		case DOWN: return this::downHideAnimation;
		case LEFT: return this::leftHideAnimation;
		case RIGHT: return this::rightHideAnimation;
		default : return null;
		}
	}

	private void upHideAnimation(double frac){		
		double currentHeight = pane.getMaxHeight() * (1.0 - frac);
		pane.setPrefHeight(currentHeight);
		pane.setTranslateY(-pane.getMaxHeight() + currentHeight);
	}

	private void downHideAnimation(double frac){
		double currentHeight = pane.getMaxHeight() * (1.0 - frac);
		pane.setPrefHeight(currentHeight);
		pane.setTranslateY(pane.getMaxHeight() - currentHeight);
	}

	private void leftHideAnimation(double frac){
		double currentWidth = pane.getMaxWidth() * (1.0 - frac);
		pane.setPrefWidth(currentWidth);
		pane.setTranslateX(-pane.getMaxWidth() + currentWidth);
	}

	private void rightHideAnimation(double frac){
		double currentWidth = pane.getMaxWidth() * (1.0 - frac);
		pane.setPrefWidth(currentWidth);
		pane.setTranslateX(pane.getMaxWidth() - currentWidth);
	}

	private Animation getShowAnimation(double duration){
		final Consumer<Double> animation = findShowAnimation();
		
		Animation showAnimation = new Transition() {
			{ setCycleDuration(Duration.millis(duration)); }
			protected void interpolate(double frac) {
				animation.accept(frac);	
			}
		};
		
		return showAnimation;
	}
	
	private Consumer<Double> findShowAnimation(){
		switch (side) {
		case UP: return this::upShowAnimation;
		case DOWN: return this::downShowAnimation;
		case LEFT: return this::leftShowAnimation;
		case RIGHT: return this::rightShowAnimation;
		default:  return null;
		}
	}
	
	private void upShowAnimation(double frac){	
		double currentHeight = pane.getMaxHeight() * frac;
		pane.setPrefHeight(currentHeight);
		pane.setTranslateY(-pane.getMaxHeight() + currentHeight);
	}

	private void downShowAnimation(double frac){
		double currentHeight = pane.getMaxHeight() * frac;
		pane.setPrefHeight(currentHeight);
		pane.setTranslateY(pane.getMaxHeight() - currentHeight);
	}

	private void leftShowAnimation(double frac){
		double currentWidth = pane.getMaxWidth() * frac;
		pane.setPrefWidth(currentWidth);
		pane.setTranslateX(-pane.getMaxWidth() + currentWidth);
	}

	private void rightShowAnimation(double frac){
		double currentWidth = pane.getMaxWidth() * frac;
		pane.setPrefWidth(currentWidth);
		pane.setTranslateX(pane.getMaxWidth() - currentWidth);
	}
	
}
