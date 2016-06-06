
/*
 * A Recorder based on Monte Media Library --  http://www.randelshofer.ch/monte/
 * 
 * The Monte Media Library is licensed under the Creative Commons BY 3.0, Please refer 
 * http://www.randelshofer.ch/monte/license.html . To view a copy of this license please look at
 * http://creativecommons.org/licenses/by/3.0/legalcode 
 * 
 */


 
package io.nirvagi.utils.recorder;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.VideoFormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

public class MonteScreenRecorder implements ScreenVideoRecorder {
	private final ScreenRecorder screenRecorder;
	private final Set<File> files;
	private static RecorderState state = RecorderState.STOPPED;
	private String key;

	public MonteScreenRecorder() {
		GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		this.files = new TreeSet<File>();
		try {
			this.screenRecorder = new ScreenRecorder(gc, new Format(
					MediaTypeKey, MediaType.FILE, MimeTypeKey, FormatKeys.MIME_QUICKTIME),
					new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey,
							VideoFormatKeys.ENCODING_QUICKTIME_ANIMATION,
							CompressorNameKey,
							ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey,
							24, FrameRateKey, Rational.valueOf(15), QualityKey,
							1.0f, KeyFrameIntervalKey, 15 * 60), new Format(
							MediaTypeKey, MediaType.VIDEO, EncodingKey,
							"black", FrameRateKey, Rational.valueOf(30)), null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public synchronized void start(String key) {
		if (state == RecorderState.RUNNING) {
			throw new RuntimeException("Recorder is already running !");
		}
		try {
			screenRecorder.start();
			this.key = key;
			state = RecorderState.RUNNING;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public synchronized void stop(String key) {
		if (state == RecorderState.STOPPED) {
			throw new RuntimeException("The recorder is already stopped!");
		}
		if (key.equals(this.key) == false) {
			throw new RuntimeException(
					"Cannot stop the recorder as the given key is invalid");
		}
		try {
			screenRecorder.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		state = RecorderState.STOPPED;
		List<File> createdFiles = screenRecorder.getCreatedMovieFiles();
		File createdFile = createdFiles.get(createdFiles.size() - 1);
		files.add(createdFile);
	}

	public Set<File> getRecordedFiles() {
		return this.files;
	}

	public RecorderState getState() {
		return MonteScreenRecorder.state;
	}

}
