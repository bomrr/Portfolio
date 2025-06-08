package com.mycompany.a3;

import java.io.InputStream;

import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.Display;

/**
 * A class to play looped sound.
 */
public class BGSound implements Runnable {
	private Media m;

	public BGSound(String fileName) {
		/*if (Display.getInstance().getCurrent() == null) {
			System.out.println("Error: Create sound objects after calling show()!");
			System.exit(0);
		}*/

		try {
			InputStream is = Display.getInstance().getResourceAsStream(getClass(), "/" + fileName);
			// attach a runnable to run when media has finished playing as the last parameter
			m = MediaManager.createMedia(is, "audio/wav", this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		m.pause();
	} // pause playing the sound

	public void play() {
		m.play();
	} // continue playing from where we have left off
		// entered when media has finished playing

	public void run() {
		// start playing from time zero (beginning of the sound file)
		try {
			if (m != null) {
				m.setTime(0);
				m.play();
			} else {
				System.out.println("Loading sounds...");
			}

		} catch (Exception e) {

		}
	}

	public Media getSound() {
		return m;
	}
}
