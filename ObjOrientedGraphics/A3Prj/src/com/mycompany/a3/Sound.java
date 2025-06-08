package com.mycompany.a3;

import java.io.InputStream;

import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.Display;

/**
 * Encapsulates sound files for easier use
 */
public class Sound {
	private Media m;

	public Sound(String fileName) {
		// Ensure the display exists (aka show() has been called)
		/*if (Display.getInstance().getCurrent() == null) {
			System.out.println("Error: Create sound objects after calling show()!");
			System.exit(0);
		}*/

		// Set the sound file
		try {
			InputStream is = Display.getInstance().getResourceAsStream(getClass(), "/" + fileName);
			m = MediaManager.createMedia(is, "audio/wav");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		// start playing the sound from time zero (beginning of the sound file)
		m.setTime(0);
		m.play();
	}
}
