/*---------------------------------------------------------------------------------------------------------
 * Copyright 2016 Nirvagi project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * \*-------------------------------------------------------------------------------------------------------------------*/
 
package io.nirvagi.utils.servlet;

import io.nirvagi.node.task.RecorderMonitorTask;
import io.nirvagi.utils.recorder.MonteScreenRecorder;
import io.nirvagi.utils.recorder.ScreenVideoRecorder;
import io.nirvagi.utils.recorder.ScreenVideoRecorder.RecorderState;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

/**
 * 
 * A simple servlet to handle the recording action. This servlet is meant to be
 * hosted in the selenium proxy. This servlet starts and stops the screen
 * recorder based on the url parameter . This accepts a unique key to start and
 * stop the screen recording . The reason for having the key is that since the
 * recording happens at OS level , running multiple sessions in the same node
 * can result in the recorder being stopped by a test session that is other than
 * the one that started the recording session
 * 
 * @author bharath
 *
 */
public class RecorderServlet extends HttpServlet {
	private static final String ACTION_PARAM = "action";
	private static final String KEY_PARAM = "key";
	private static final String FILENAME_PARAM = "filename";
	private static final String MAX_TIMEOUT_PARAM = "timeout";

	private static final String CONTENT_TYPE = "application/octet-stream";
	private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	private static final String FILE_NAME = "filename=%s";

	// html fragment for rendering files
	private static final String FILE_ELEMENT_HTML_FRAGMENT = "<li><a href=\"?action=download&filename=%s\">%s</a></li>";
	private ScheduledExecutorService executorService;
	private ScreenVideoRecorder recorder;
	private ScheduledFuture<?> futureHandle;
	private static String recorderKey;

	public void init() {
		recorder = new MonteScreenRecorder();
		executorService = Executors.newScheduledThreadPool(1);
	}

	public enum Action {
		START, STOP, SHOWFILES, DOWNLOAD;
	}

	private Action getActionFromParam(final String action) {
		try {
			return Action.valueOf(action.trim().toUpperCase());
		} catch (Exception err) {
			return Action.SHOWFILES;
		}
	}

	private String buildFileListHtml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><ul>");
		Set<File> files = recorder.getRecordedFiles();
		for (File file : files) {
			sb.append(String.format(FILE_ELEMENT_HTML_FRAGMENT, file.getPath(),
					file.getName()));
		}
		sb.append("</ul></html>");
		return sb.toString();
	}
	
	private long getTimeout(String timeout){
		if(timeout == null || timeout.isEmpty()){
			throw new RuntimeException("Timeout for starting the recorder cannot be null or empty!");
		}
		try{
		return Long.parseLong(timeout);
		}catch(Exception err){
			throw new RuntimeException("timeout should be in int ");
		}
	}

	private void process(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Action action = this.getActionFromParam(req.getParameter(ACTION_PARAM));
		try {
			switch (action) {
			case START:
			case STOP:
				final String key = req.getParameter(KEY_PARAM);
				if (key == null || key.isEmpty()) {
					action = Action.SHOWFILES;
				}
				if (action == Action.START) {
					// Store the key , IN case if the node is killed cleanup (stop) the recorder using this key
					recorderKey = key;
					recorder.start(key);
					long timeout = getTimeout(req.getParameter(MAX_TIMEOUT_PARAM));
					System.err.println("The recorder timeout is " + timeout + " seconds!");
					futureHandle = executorService.schedule(new RecorderMonitorTask(recorder,key), timeout, TimeUnit.SECONDS);
					return;
				}
				if (action == Action.STOP) {
					System.err.println("Stopping the monitor thread");
					futureHandle.cancel(true);
					recorder.stop(key);
					return;
				}
			case SHOWFILES:
				String htmlString = buildFileListHtml();
				resp.getWriter().print(htmlString);
				return;
			case DOWNLOAD:
				String filename = req.getParameter(FILENAME_PARAM);
				resp.setContentType(CONTENT_TYPE);
				resp.setHeader(CONTENT_DISPOSITION_HEADER,
						String.format(FILE_NAME, filename));
				FileUtils.copyFile(new File(filename), resp.getOutputStream());
				return;
			}
		} catch (Exception err) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, err.getMessage());
			return;
		}
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		process(req, resp);
	}
	
	/**
	 * Cleanup and delete all the files on shutting down this servlet 
	 * Stop the executorservice if it is actively running 
	 * Stop the recorder if it is running already 
	 * 
	 */

	public void destroy() {
		if(this.recorder.getState() == RecorderState.RUNNING){
			System.err.println("stopping the recorder , It is running !");
			recorder.stop(recorderKey);
		}
		// shutdown the executor service , We dont care about the task that has been waiting !
		System.err.println("Shutting down the executor service !");
		executorService.shutdownNow();
		System.err.println("Deleting all the recorder files... Cleanup..");
		Set<File> files = recorder.getRecordedFiles();
		for (File f : files) {
			f.delete();
		}
	}

}
