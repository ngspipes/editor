package editor.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import editor.userInterface.utils.Dialog;


public class Utils {

	public static String getStackTrace(Throwable t){
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static void treatException(Exception e, String tag, String msg){
		if(e instanceof EditorException){
			Log.error(tag, msg+"\n"+getStackTrace(e));
			Dialog.showError(e.getMessage());
		}else{
			Log.error(tag, msg+"\n"+getStackTrace(e));
			Dialog.showError(msg);
		}
		
		e.printStackTrace();
	}
	
}
