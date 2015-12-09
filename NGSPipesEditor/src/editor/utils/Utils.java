package editor.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
	
	@SafeVarargs
	public static <T> Collection<T> distinct(Collection<T>... colls){
		List<T> elems = new LinkedList<>();
		
		for(Collection<T> coll : colls)
			for(T elem : coll)
				if(!elems.contains(elem))
					elems.add(elem);
		
		return elems;
	} 
	
}
