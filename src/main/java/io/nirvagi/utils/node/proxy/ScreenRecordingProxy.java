
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
 
package io.nirvagi.utils.node.proxy;

import io.nirvagi.utils.node.helper.HttpGetHelper;

import java.util.Map;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

public class ScreenRecordingProxy extends DefaultRemoteProxy {
	private static final String SCREEN_RECORDING_CAPABILITY = "node_screen_recording";
	private static final String SCREEN_TIMEOUT_CAPABILITY = "node_recording_timeout";
	//180 seconds default 
	private static final int DEFAULT_STOP_TIMEOUT = 180;
	private static final String START_RECORDING_STRING = "%s/extra/RecorderServlet?action=start&key=%s&timeout=%s";
	private static final String STOP_RECORDING_STRING = "%s/extra/RecorderServlet?action=stop&key=%s";
	
	private final String nodeUrl ;
	private boolean hasRecordingStarted = false;
	

	private boolean isScreenRecordingRequested(final Map<String, Object> caps) {
		boolean isScreenRecordingRequested = false;
		try {
			isScreenRecordingRequested = (Boolean) caps.get(SCREEN_RECORDING_CAPABILITY);
		} catch (Exception err) {

		}
		return isScreenRecordingRequested;

	}
	
	private long getTimeout(final Map<String, Object> caps){
		long timeout = 0;
		try{
			timeout = (Long) caps.get(SCREEN_TIMEOUT_CAPABILITY);
		}catch(Exception err){
			timeout = DEFAULT_STOP_TIMEOUT;
		}
		return timeout;
	}

	public ScreenRecordingProxy(RegistrationRequest request, Registry registry) {
		super(request, registry);
		this.nodeUrl = this.getRemoteHost().toString();
		System.out.println("Registering screen recording  proxy ....");
	}

	public void beforeSession(TestSession session) {
		super.beforeSession(session);
		boolean isRecordingRequested = isScreenRecordingRequested(session
				.getRequestedCapabilities());
		if (isRecordingRequested == true) {
			System.out.println("The node url is " + nodeUrl);
			final String key = session.getInternalKey();
			final long timeout = getTimeout(session.getRequestedCapabilities());
			final String startRecordingString = String.format(START_RECORDING_STRING, nodeUrl, key, timeout);
			System.err.println("The start recording string is " + startRecordingString);
			HttpGetHelper helper = new HttpGetHelper(startRecordingString);
			try {
				helper.execute();
				hasRecordingStarted = true;
			} catch (Exception err) {
				System.out
						.println("Error occurred while starting the recording "
								+ err.getMessage());
			}
		}
	}

	public void afterSession(TestSession session) {
		if (this.hasRecordingStarted == true) {
			final String key = session.getInternalKey();
			HttpGetHelper helper = new HttpGetHelper(String.format(
					STOP_RECORDING_STRING, nodeUrl, key));
			try {
				helper.execute();
				hasRecordingStarted = false;
			} catch (Exception err) {
				System.out
						.println("Error occurred while stopping the recording "
								+ err.getMessage());
			}
		}
	}

}
