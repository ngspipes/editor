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
package editor.dataAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Uris {

	public static final String SEP = "/";//System.getProperty("file.separator");

	public static final String DEFAULT_TOOL_LOGO = ClassLoader.getSystemResource(Uris.TOOL_LOGO_IMAGE).toExternalForm();

	private static final String IMAGES = 					"images";
	public static final String TOOL_LOGO_IMAGE = 			IMAGES + SEP + "ToolLogo.png";
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
	public static final String URI_BASED_REPOSITORY_IMAGE =	IMAGES + SEP + "UriBasedRepository.png";
	public static final String LOCAL_REPOSITORY_IMAGE =		IMAGES + SEP + "LocalRepository.png";
	public static final String GITHUB_REPOSITORY_IMAGE =	IMAGES + SEP + "GithubRepository.png";
	public static final String LOADING_IMAGE =	 			IMAGES + SEP + "Loading.gif";

	private static final String FXML_FILES = 						"fXML";
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

	public static final String SHORTCUTS_FILE = "Shortcuts.txt";

	public static final String JAR_FILE_PATH = Uris.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	public static final String NGSPIPES_DIRECTORY = 		System.getProperty("user.home") + SEP + "NGSPipes";
	public static final String EDITOR_PATH = 				NGSPIPES_DIRECTORY + SEP + "Editor";
	public static final String LOG_DIR = 					EDITOR_PATH + SEP + "log";
	public static final String PREFERENCES_DIR = 			EDITOR_PATH + SEP + "preferences";
	public static final String DEFAULT_REPOSITORY_DIR = 	EDITOR_PATH + SEP + "repository";
	public static final String CACHE_DIR = 					EDITOR_PATH + SEP + "cache";
	public static final String TUTORIALS_DIR =				EDITOR_PATH + SEP + "tutorials";



	public static void load() throws IOException {
		createNGSPipesDirectory();
			
		createEditorDirectory();

		createLogDirectory();

		createPreferencesDirectory();

		createCacheDirectory();

		createTutorialsDirectory();

		createDefaultRepositoryDirectory();
	}

	private static void createNGSPipesDirectory(){
		File ngsPipesDir = new File(NGSPIPES_DIRECTORY);

		if(!ngsPipesDir.exists())
			ngsPipesDir.mkdirs();
	}

	private static void createEditorDirectory() {
		File editorDir = new File(EDITOR_PATH);

		if(!editorDir.exists())
			editorDir.mkdirs();
	}

	private static void createLogDirectory() {
		File logDir = new File(LOG_DIR);

		if(!logDir.exists())
			logDir.mkdir();
	}

	private static void createPreferencesDirectory() {
		File preferencesDir = new File(PREFERENCES_DIR);

		if(!preferencesDir.exists())
			preferencesDir.mkdir();
	}

	private static void createCacheDirectory() {
		File cacheDir = new File(CACHE_DIR);

		if(!cacheDir.exists())
			cacheDir.mkdirs();
	}

	private static void createTutorialsDirectory() throws IOException {
		File tutorialsDir = new File(TUTORIALS_DIR);

		if(!tutorialsDir.exists()){
			tutorialsDir.mkdirs();
			JarFile jar = new JarFile(new File(JAR_FILE_PATH));
			copyResourcesToDirectory(jar, "tutorials", TUTORIALS_DIR);
			jar.close();
		}
	}

	private static void createDefaultRepositoryDirectory() throws IOException {
		File defaultRepositoryDir = new File(DEFAULT_REPOSITORY_DIR);

		if(!defaultRepositoryDir.exists()){
			defaultRepositoryDir.mkdir();
			JarFile jar = new JarFile(new File(JAR_FILE_PATH));
			copyResourcesToDirectory(jar, "repository", DEFAULT_REPOSITORY_DIR);
			jar.close();
		}
	}

	private static void copyResourcesToDirectory(JarFile fromJar, String jarDir, String destDir) throws IOException {
		Enumeration<JarEntry> entries = fromJar.entries();
		JarEntry entry;
		String entryName;

		while(entries.hasMoreElements()) {
			entry = entries.nextElement();
			entryName = entry.getName();

			if (entryName.startsWith(jarDir + SEP) && !entry.isDirectory()) {
				File dest = new File(destDir + SEP + entryName.substring(jarDir.length() + 1));
				File parent = dest.getParentFile();

				if (!parent.exists()) 
					parent.mkdirs();

				copy(fromJar, entry, dest);
			}
		}
	}

	private static void copy(JarFile fromJar, JarEntry entry, File dest) throws IOException {
		try (FileOutputStream out = new FileOutputStream(dest)){
            try (InputStream in = fromJar.getInputStream(entry)){
                byte[] buffer = new byte[1024];

                int s;
                while ((s = in.read(buffer)) > 0)
                    out.write(buffer, 0, s);
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



