package editor.dataAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Uris {

	public static final String SEP = "/";//System.getProperty("file.separator");
	public static final String RESOURCES = "resources";

	
	private static final String IMAGES = 					RESOURCES + SEP + "images";
	public static final String TOOL_LOGO_IMAGE = 		IMAGES + SEP + "ToolLogo.png";
	public static final String CLOSE_SELECTED_IMAGE = 		IMAGES + SEP + "CloseSelected.png";
	public static final String CLOSE_DESELECTED_IMAGE = 	IMAGES + SEP + "CloseDeselected.png";
	public static final String SMALL_IN_IMAGE = 			IMAGES + SEP + "SmallIn.png";
	public static final String SMALL_OUT_IMAGE = 			IMAGES + SEP + "SmallOut.png";
	public static final String LEFT_EXPAND_IMAGE = 			IMAGES + SEP + "LeftExpand.png";
	public static final String RIGHT_EXPAND_IMAGE = 		IMAGES + SEP + "RightExpand.png";
	public static final String UP_EXPAND_IMAGE = 			IMAGES + SEP + "UpExpand.png";
	public static final String DOWN_EXPAND_IMAGE = 			IMAGES + SEP + "DownExpand.png";
	public static final String SAVED_IMAGE =	 			IMAGES + SEP + "Saved.png";
	public static final String NOT_SAVED_IMAGE = 			IMAGES + SEP + "NotSaved.png";
	public static final String LOGO_IMAGE =		 			IMAGES + SEP + "Logo.png";
	public static final String REMOTE_REPOSITORY_IMAGE =	IMAGES + SEP + "RemoteRepository.png";
	public static final String LOCAL_REPOSITORY_IMAGE =		IMAGES + SEP + "LocalRepository.png";
	public static final String GITHUB_REPOSITORY_IMAGE =	IMAGES + SEP + "GithubRepository.png";


	private static final String FXML_FILES = 						RESOURCES + SEP + "fXML"; 
	public static final String FXML_CHANGE_REPOSITORY = 			FXML_FILES + SEP + "FXMLChangeRepository.fxml";
	public static final String FXML_CREATE_WORKFLOW = 				FXML_FILES + SEP + "FXMLCreateWorkflow.fxml";
	public static final String FXML_STEP = 							FXML_FILES + SEP + "FXMLStep.fxml";
	public static final String FXML_TOOLS_LIST_VIEW_ITEM = 			FXML_FILES + SEP + "FXMLToolsListViewItem.fxml";
	public static final String FXML_DOCUMENT = 						FXML_FILES + SEP + "FXMLDocument.fxml";
	public static final String FXML_COMMANDS_LIST_VIEW_ITEM = 		FXML_FILES + SEP + "FXMLCommandsListViewItem.fxml";
	public static final String FXML_ARGUMENTS_LIST_VIEW_ITEM = 		FXML_FILES + SEP + "FXMLArgumentsListViewItem.fxml";
	public static final String FXML_OUTPUTS_LIST_VIEW_ITEM = 		FXML_FILES + SEP + "FXMLOutputsListViewItem.fxml";
	public static final String FXML_STEP_INFO = 					FXML_FILES + SEP + "FXMLStepInfo.fxml";
	public static final String FXML_REPOSITORY_DESCRIPTION = 		FXML_FILES + SEP + "FXMLRepositoryDescription.fxml";
	public static final String FXML_TAB_HEADER = 					FXML_FILES + SEP + "FXMLTabHeader.fxml";
	public static final String FXML_SHORTCUTS = 					FXML_FILES + SEP + "FXMLShortcuts.fxml";
	public static final String FXML_HELP = 							FXML_FILES + SEP + "FXMLHelp.fxml";
	public static final String FXML_VIDEOS_LIST_VIEW_ITEM = 		FXML_FILES + SEP + "FXMLVideoListViewItem.fxml";
	public static final String FXML_CHAIN =					 		FXML_FILES + SEP + "FXMLChain.fxml";
	public static final String FXML_STEPS_ORDER =					 FXML_FILES + SEP + "FXMLStepsOrder.fxml";

	public static final String SHORCUTS_FILE = RESOURCES + SEP + "Shortcuts.txt";

	public static final String NGSPIPES_DIRECTORY = 		System.getProperty("user.home") + SEP + "NGSPipes";
	public static final String EDITOR_PATH = 				NGSPIPES_DIRECTORY + SEP + "Editor";
	public static final String LOG_DIR = 					EDITOR_PATH + SEP + "log";
	public static final String PREFERENCES_DIR = 			EDITOR_PATH + SEP + "preferences";
	public static final String DEFAULT_REPOSITORY_DIR = 	EDITOR_PATH + SEP + "repository";
	public static final String CACHE_DIR = 					EDITOR_PATH + SEP + "cache";
	public static final String TUTORIALS_DIR =				EDITOR_PATH + SEP + "tutorials";

	public static void load() throws IOException, URISyntaxException{
		File ngsPipesDir = new File(NGSPIPES_DIRECTORY);
		File editorDir = new File(EDITOR_PATH);
		File logDir = new File(LOG_DIR);
		File preferencesDir = new File(PREFERENCES_DIR);
		File defaultRepositoryDir = new File(DEFAULT_REPOSITORY_DIR);
		File cacheDir = new File(CACHE_DIR);
		File tutorialsDir = new File(TUTORIALS_DIR);

		if(!ngsPipesDir.exists())
			ngsPipesDir.mkdirs();
			
		if(!editorDir.exists())
			editorDir.mkdirs();

		if(!logDir.exists())
			logDir.mkdir();

		if(!preferencesDir.exists())
			preferencesDir.mkdir();
		
		if(!cacheDir.exists())
			cacheDir.mkdirs();
		
		if(!tutorialsDir.exists()){
			tutorialsDir.mkdirs();
			final File jarFile = new File(Uris.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			final JarFile jar = new JarFile(jarFile);
			copyResourcesToDirectory(jar, RESOURCES + SEP + "tutorials", TUTORIALS_DIR);
			jar.close();
		}

		if(!defaultRepositoryDir.exists()){
			defaultRepositoryDir.mkdir();
			final File jarFile = new File(Uris.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			final JarFile jar = new JarFile(jarFile);
			copyResourcesToDirectory(jar, RESOURCES + SEP + "repository", DEFAULT_REPOSITORY_DIR);
			jar.close();	
		}
	}


	private static void copyResourcesToDirectory(JarFile fromJar, String jarDir, String destDir) throws IOException {
		for (Enumeration<JarEntry> entries = fromJar.entries(); entries.hasMoreElements();) {

			JarEntry entry = entries.nextElement();

			if (entry.getName().startsWith(jarDir + SEP) && !entry.isDirectory()) {
				File dest = new File(destDir + SEP + entry.getName().substring(jarDir.length() + 1));
				File parent = dest.getParentFile();

				if (!parent.exists()) 
					parent.mkdirs();

				FileOutputStream out = new FileOutputStream(dest);
				InputStream in = fromJar.getInputStream(entry);

				try {
					byte[] buffer = new byte[1024];

					int s = 0;
					while ((s = in.read(buffer)) > 0)
						out.write(buffer, 0, s);

				} finally {
					in.close();
					out.close();
				}
			}
		}
	}
	
	public static String getLogPath(String name) {
		return LOG_DIR  + SEP + name;
	}

	public static String getResource(String path){
		return ClassLoader.getSystemResource(path).toExternalForm();
	}
	
}



