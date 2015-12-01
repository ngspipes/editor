package editor.userInterface.utils;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;

public class Utils {

	public static FXMLLoader getLoader(String fXMLWindowPath){
        URL location = ClassLoader.getSystemResource(fXMLWindowPath);
        FXMLLoader loader = new FXMLLoader(location);
        return loader;
    }
	
	public static Tooltip createTooltip(String text, boolean wrap, double maxWidth, double maxHeight){
		Tooltip tip = new Tooltip(text);
		tip.setWrapText(true);
		tip.setMaxWidth(maxWidth);
		tip.setMaxHeight(maxHeight);
		
		return tip;
	}

	public static void set3DEffect(Node node, boolean setShadow, boolean setReflection){
		DropShadow shadow = new DropShadow();
		shadow.setOffsetY(5.0);
	    shadow.setOffsetX(5.0);
	    shadow.setColor(Color.GRAY);
	    
	    Reflection reflection = new Reflection();
	    reflection.setTopOffset(-15);
	    reflection.setBottomOpacity(0.15);
		
	    if(setShadow && !setReflection)
	    	node.setEffect(shadow);
	    else if(!setShadow && setReflection)
	    	node.setEffect(reflection);
	    else if(setShadow && setReflection){
	    	shadow.setInput(reflection);
	    	node.setEffect(shadow);
	    }
	}
	
	
}
