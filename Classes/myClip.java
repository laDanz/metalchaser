package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import main.SuperMain;

/**
 * Represents a sound clip.
 * 
 * @deprecated Lieber OpenAlClip nutzen!
 * @author ladanz
 * 
 */
public class myClip {

	public AudioInputStream din;
	AudioInputStream in;
	LinkedList<Runnable> startAction;
	LinkedList<Runnable> endAction;
	public boolean wannaStop;
	public long bytes_read;
	String filename;
	private boolean playing;
	static int clip_count = 0;

	/**
	 * Default constructor.<br>
	 * Creates a sound clips out of a given audio file.
	 * 
	 * @param filename
	 *            The source filename.
	 */
	public myClip(String filename) {
		this.filename = filename;
		wannaStop = false;
		playing = false;
		bytes_read = 0;
		startAction = new LinkedList<Runnable>();
		endAction = new LinkedList<Runnable>();
		// nur wenn audio provided ist!

		initStream(filename);
		clip_count++;
		System.out.println("ClipCount: " + clip_count);
	}

	/**
	 * Add an action which will be performed when the clip starts.
	 * 
	 * @param action
	 *            An action.
	 */
	public void addStartAction(Runnable action) {
		startAction.add(action);
	}

	/**
	 * Add an action which will be performed when the clip ends.
	 * 
	 * @param action
	 *            An action.
	 */
	public void addEndAction(Runnable action) {
		endAction.add(action);
	}

	/**
	 * Plays the clip.
	 */
	public void play() {
		// System.out.println("Clip play: " + System.currentTimeMillis());
		myAudio.play(this);
		playing = true;
	}

	/**
	 * Stops playing this clip.
	 */
	public void stop() {
		wannaStop = true;
		playing = false;
	}

	/**
	 * Closes this Stream.
	 */
	public void close() {

		try {
			din.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Rewind this Clip.
	 */
	public void rewind() {
		initStream(this.filename);
	}

	private void initStream(String filename) {
		File file = new File(filename);
		try {
			in = AudioSystem.getAudioInputStream(new FileInputStream(file));

			if (in != null) {
				AudioFormat baseFormat = in.getFormat();
				myAudio.setDecodedFormat(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						44100/* baseFormat.getSampleRate() */, 16, /* baseFormat.getChannels() */
						2,
						/* baseFormat.getChannels() */2 * 2, baseFormat.getSampleRate(), false));
				din = AudioSystem.getAudioInputStream(myAudio.getDecodedFormat(), in);

			}
		} catch (IOException e) {
			if (!SuperMain.dev)
				org.lwjgl.Sys.alert(OGL.GAME_TITLE, "Konnt Audio nicht initialisieren.\nDatei nicht gefunden: "
						+ filename);
			e.printStackTrace();
			SuperMain.out(e);
		} catch (UnsupportedAudioFileException e) {
			if (!SuperMain.dev)
				org.lwjgl.Sys.alert(OGL.GAME_TITLE,
						"Konnt Audio nicht initialisieren.\nDatei wird nicht unterstützt.\nDatei: " + filename);
			e.printStackTrace();
			SuperMain.out(e);
		} catch (IllegalArgumentException e) {
			// Unsupported conversion
			e.printStackTrace();
			SuperMain.out(e);
		}

	}

	/**
	 * Perform the start actions.
	 */
	public void onClipStarted() {
		for (Runnable r : startAction) {
			r.run();
		}

	}

	/**
	 * Perform the end actions.
	 */
	public void onClipEnded() {
		playing = false;
		// FIXME Q&D
		if (!wannaStop) {

			for (Runnable r : endAction) {
				r.run();
			}
		}
		// Stream hart zuruecksetzten
		initStream(filename);

		wannaStop = false;

	}

	/**
	 * Is this clip playing?
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return playing;
	}

}
