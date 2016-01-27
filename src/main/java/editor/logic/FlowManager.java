package editor.logic;

import java.io.File;
import java.util.function.BiConsumer;

import editor.logic.entities.Flow;
import editor.utils.EditorException;


public class FlowManager {
	
	public static final String WORK_FLOW_FILE_EXTENSION = ".wf";


	public static void save(Flow flow) throws EditorException {
		FlowSaver.saveFlow(flow);
	}
	
	public static void load(String name, String directory, BiConsumer<Exception, Flow> callback) throws EditorException {
		FlowLoader.load(name, directory, callback);
	}
	
	public static void generate(Flow flow, File inputsDir) throws EditorException {
		DSLWriter.generate(flow, inputsDir);
	}
		
}