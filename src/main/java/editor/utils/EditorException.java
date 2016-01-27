package editor.utils;

public class EditorException extends Exception{

	private static final long serialVersionUID = 1L;

	public EditorException(){}
	
	public EditorException(String msg){ super(msg); }
	
	public EditorException(String msg, Throwable cause){ super(msg, cause); }

}
