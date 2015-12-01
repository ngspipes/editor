package editor.userInterface.areas;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import editor.userInterface.components.PaneSlider;
import editor.userInterface.components.PaneSlider.SlideSide;

public abstract class Area {

	protected final PaneSlider slider;
	
	public Area(SlideSide slideSide, AnchorPane areaPane, Button expandButton){
		this.slider = new PaneSlider(slideSide, areaPane, expandButton);
		slider.mount();
	}
	
	public void show(){
		slider.show();
	}
	
	public void hide(){
		slider.hide();
	}
	
	public void slideIn(){
		slider.slideIn();
	}
	
	public void slideOut(){
		slider.slideOut();
	}

}
