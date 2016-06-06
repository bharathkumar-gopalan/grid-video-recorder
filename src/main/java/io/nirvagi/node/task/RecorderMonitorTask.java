
/*---------------------------------------------------------------------------------------------------------
 * Copyright 2016 - Nirvagi project
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
 
package io.nirvagi.node.task;

import io.nirvagi.utils.recorder.ScreenVideoRecorder;
import io.nirvagi.utils.recorder.ScreenVideoRecorder.RecorderState;

public class RecorderMonitorTask implements Runnable{
	private final ScreenVideoRecorder recorder;
	private final String key;
	
	public RecorderMonitorTask(final ScreenVideoRecorder recorder, final String key){
		this.recorder = recorder;
		this.key = key;
	}

	public void run() {
		if(recorder.getState() == RecorderState.RUNNING){
			System.err.println("The recorder is still running despite timeout, Stopping it ..");
			recorder.stop(key);
		}
	}

}
