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
 
package io.nirvagi.utils.recorder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
/**
 * A mock recorder for testing purposes
 * 
 * @author bharath
 *
 */
public class MockRecorder implements ScreenVideoRecorder{
	private static final String NO_FILES_MESSAGE = "There are no recorded files!";
	
	private final Set<File> files;
	private static RecorderState state = RecorderState.STOPPED;
	private String key;
	
	public MockRecorder(){
		this.files = new HashSet<File>();		
	}
	
	
	private File generateFile(){
		File f = new File(UUID.randomUUID().toString());
		try {
			f.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException("Error while creating a new file");
		}
		return f;
	}
	

	public synchronized void start(final String key) {
		if(state == RecorderState.RUNNING){
			throw new RuntimeException("Recorder is already running !");
		}
		this.key = key;
		state = RecorderState.RUNNING;
		System.out.println("Started the recording session");
	}

	
	
	public synchronized void stop(final String key) {
		if(state == RecorderState.STOPPED){
			throw new RuntimeException("The recorder is already stopped!");
		}
		if(key.equals(this.key) == false){
			throw new RuntimeException("Cannot stop the recorder as the given key is invalid");
		}
		// stop the recorder 
		System.out.println("Stopped the recording session");
		state = RecorderState.STOPPED;
		File f = this.generateFile();
		files.add(f);
	}

	public Set<File> getRecordedFiles() {
		if(files.isEmpty()){
			throw new RuntimeException(NO_FILES_MESSAGE);
		}
		return files;
	}


	public RecorderState getState() {
		return MockRecorder.state;
	}
	
	

}
