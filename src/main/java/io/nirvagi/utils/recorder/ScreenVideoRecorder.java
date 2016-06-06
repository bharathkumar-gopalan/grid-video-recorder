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
import java.util.Set;

public interface ScreenVideoRecorder {
	
	public enum RecorderState{
		STOPPED, RUNNING;
	}
	
	public void start(final String key);
	
	public void stop(final String key);
	
	public RecorderState getState();
	
	public Set<File> getRecordedFiles();

}
