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
package editor.utils;

import editor.dataAccess.Uris;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Log {

	public enum MessageType{
		Fatal(),
		Error(),
		Warning(),
		Info(),
		Debug();

		MessageType(){}
	}

	private static class Message{
		public final MessageType type;
		public final String tag;
		public final String msg;
		public final Date time;
		public final long threadID;

		public Message(MessageType type, String tag, String msg, Date time, long threadID) {
			this.type = type;
			this.tag = tag;
			this.msg = msg;
			this.time = time;
			this.threadID = threadID;
		}
	}

	public static String LOG_DIR = getLogDir();
	private static final BlockingQueue<Message> QUEUE = new LinkedBlockingQueue<Log.Message>();
	private static final long POLL_TIMEOUT = 1000;
	private static final AtomicBoolean STOP = new AtomicBoolean(false);


	private static final JSONObject DATA = new JSONObject();
	private static final String START_JSON_KEY = "start";
	private static final String END_JSON_KEY = "end";
	private static final String MESSAGES_JSON_KEY = "messages";
	private static final String TYPE_JSON_KEY = "type";
	private static final String TAG_JSON_KEY = "tag";
	private static final String MESSAGE_JSON_KEY = "message";
	private static final String TIME_JSON_KEY = "time";
	private static final String THREAD_ID_JSON_KEY = "threadID";
	private static final int JSON_INDENTATION = 5;


	static{
		Runnable action = ()->{
			try{
				init();

				run();

				finish();
			} catch(Exception e) {
				e.printStackTrace();
			}};

		WorkQueue.run(action, true, Thread.MIN_PRIORITY);
	}

	@SuppressWarnings("deprecation")
	private static String getLogDir(){
		Date now = new Date();

		return Uris.getLogPath((now.getYear()+1900)+"_" + (now.getMonth()+1)+"_" + now.getDate()+"_" + now.getHours()+"_" + now.getMinutes() + "_" + now.getSeconds())+".json";
	}

	private static void init() throws Exception {
		DATA.put(START_JSON_KEY, new Date());
		DATA.put(MESSAGES_JSON_KEY, new JSONArray());
	}

	private static void finish() throws Exception {
		DATA.put(END_JSON_KEY, new Date());
		printData();
	}

	private static void run() throws Exception {
		Message message;
		while(!STOP.get()){
			message = QUEUE.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);

			if(message != null){
				DATA.getJSONArray(MESSAGES_JSON_KEY).put(getMessageData(message));
				printData();
			}	
		}
	}

	private static JSONObject getMessageData(Message message) throws JSONException {
		JSONObject data = new JSONObject();

		data.put(TAG_JSON_KEY, message.tag);
		data.put(TYPE_JSON_KEY, message.type);
		data.put(TIME_JSON_KEY, message.time);
		data.put(MESSAGE_JSON_KEY, message.msg);
		data.put(THREAD_ID_JSON_KEY, message.threadID);

		return data;
	}

	private static void printData() throws Exception {
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new BufferedWriter(new FileWriter(LOG_DIR)));
			writer.println(DATA.toString(JSON_INDENTATION));
		} finally{
			if(writer!= null)
				writer.close();
		}
	}

	
	public static void log(MessageType type, String tag, String msg){
		QUEUE.add(new Message(type, tag, msg, new Date(), Thread.currentThread().getId()));
	}

	public static void error(String tag, String msg){
		log(MessageType.Error, tag, msg);
	}

	public static void info(String tag, String msg){
		log(MessageType.Info, tag, msg);
	}

	public static void warning(String tag, String msg){
		log(MessageType.Warning, tag, msg);
	}

	public static void debug(String tag, String msg){
		log(MessageType.Debug, tag, msg);
	}
	
	public static void fatal(String tag, String msg){
		log(MessageType.Fatal, tag, msg);
	}

	public static void stop(){
		STOP.set(true);
	}

}
