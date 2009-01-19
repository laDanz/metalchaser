package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import main.MainMenu;
import main.SuperMain;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 * Provides OpenAl sound.
 * 
 * @author ladanz
 * 
 */
public class OpenAlClip {

	LinkedList<Runnable> startAction;
	LinkedList<Runnable> endAction;
	boolean forcedstop;
	double minus_per_tick = 0;
	long elapsed_ticks = 0;
	float myGain = 1;

	/** Buffers holding sound data. */
	IntBuffer buffer = BufferUtils.createIntBuffer(1);

	/** Sources are points producing sound. */
	IntBuffer source = BufferUtils.createIntBuffer(1);

	/** Position of the source sound. */
	FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/*
	 * These are 3D cartesian vector coordinates. A structure or class would be
	 * a more flexible of handling these, but for the sake of simplicity we will
	 * just leave it as is.
	 */

	/** Velocity of the source sound. */
	FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/** Position of the listener. */
	FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/** Velocity of the listener. */
	FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });

	/**
	 * Orientation of the listener. (first 3 elements are "at", second 3 are
	 * "up") Also note that these should be units of '1'.
	 */
	FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });

	private boolean failed = false;

	/**
	 * Prepares & Loads all necessary sound data from the file.
	 * 
	 * @param filename
	 */
	public OpenAlClip(String filename) {

		startAction = new LinkedList<Runnable>();
		endAction = new LinkedList<Runnable>();
		forcedstop = false;

		// CRUCIAL!
		// any buffer that has data added, must be flipped to establish its
		// position and limits
		sourcePos.flip();
		sourceVel.flip();
		listenerPos.flip();
		listenerVel.flip();
		listenerOri.flip();

		// Load the wav data.
		if (loadALData(filename) == AL10.AL_FALSE) {
			System.out.println("Error loading data.");
			// kill this instance
			failed = true;
			return;
		}

		setListenerValues();
		new StateWatcher().start();
		failed = false;
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
	 * boolean LoadALData()
	 * 
	 * This function will load our sample data from the disk using the Alut
	 * utility and send the data into OpenAL as a buffer. A source is then also
	 * created to play that buffer.
	 */
	int loadALData(String filename) {
		if (!OPENAL_INIT) {
			return AL10.AL_FALSE;
		}
		// Load wav data into a buffer.
		AL10.alGenBuffers(buffer);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		// Fileformat depending
		if (filename.toLowerCase().endsWith(".wav")) {

			WaveData waveFile = WaveData.create(filename);
			AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();

		} else if (filename.toLowerCase().endsWith("ogg")) {
			// Erst dekodieren
			AudioInputStream in;
			try {
				in = AudioSystem.getAudioInputStream(new FileInputStream(new File(filename)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("FileNotFound!");

			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("UnsupportedAudioFile");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("IOException");
			}

			if (in != null) {
				AudioFormat baseFormat = in.getFormat();
				AudioFormat af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 2 * 2, baseFormat
						.getSampleRate(), false);
				// das eigentliche umkodieren
				AudioInputStream din = null;
				try {
					din = AudioSystem.getAudioInputStream(af, in);
				} catch (IllegalArgumentException e) {
					// bei konvertierungsfehlern! falsche channel oder so
					e.printStackTrace();
					return AL10.AL_FALSE;
				}

				int totalFramesRead = 0;

				try {
					AudioInputStream audioInputStream = din;
					int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
					// Set an arbitrary buffer size of 1024 frames.
					int numBytes = 1024 * bytesPerFrame;
					byte[] audioBytes = new byte[numBytes];
					ByteBuffer audioBuffer = null;
					try {
						int numBytesRead = 0;
						int numFramesRead = 0;

						LinkedList<byte[]> zwischen = new LinkedList<byte[]>();
						LinkedList<Integer> numBread = new LinkedList<Integer>();

						// Stream zwischenspeichern
						// Try to read numBytes bytes from the file.
						while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
							// Calculate the number of frames actually read.
							numFramesRead = numBytesRead / bytesPerFrame;
							totalFramesRead += numFramesRead;

							zwischen.add(Arrays.copyOf(audioBytes, audioBytes.length));
							numBread.add(numBytesRead);
						}

						// und wieder in einmzelnen ByteBuffer zurückformen
						audioBuffer = ByteBuffer.allocate(totalFramesRead * bytesPerFrame);
						int count = 0;
						for (byte[] b : zwischen) {

							audioBuffer.put(b, 0, numBread.get(count));
							count++;
						}
						// der 1337h4x0r trick
						audioBuffer.rewind();
						int format = AL10.AL_FORMAT_MONO16;
						if (baseFormat.getChannels() == 1) {
							if (baseFormat.getSampleSizeInBits() == 16) {
								format = AL10.AL_FORMAT_MONO16;
							} else {
								format = AL10.AL_FORMAT_MONO8;
							}
						} else {
							if (baseFormat.getSampleSizeInBits() == 16) {
								format = AL10.AL_FORMAT_STEREO16;
							} else {
								format = AL10.AL_FORMAT_STEREO8;
							}
						}
						format = AL10.AL_FORMAT_STEREO16;
						AL10.alBufferData(buffer.get(0), format, audioBuffer, 44100);
					} catch (Exception ex) {
						// Handle the error...
						ex.printStackTrace();
					}
				} catch (Exception e) {
					// Handle the error...
					e.printStackTrace();
				}

			}
		} else {
			throw new RuntimeException("Unsupported filetype");
		}

		// Bind the buffer with the source.
		AL10.alGenSources(source);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1.0f);
		AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
		AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVel);

		// Do another error check and return.
		if (AL10.alGetError() == AL10.AL_NO_ERROR)
			return AL10.AL_TRUE;

		return AL10.AL_FALSE;
	}

	/**
	 * void killALData()
	 * 
	 * We have allocated memory for our buffers and sources which needs to be
	 * returned to the system. This function frees that memory.
	 */
	void killALData() {
		if (failed) {
			return;
		}
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}

	/**
	 * void setListenerValues()
	 * 
	 * We already defined certain values for the Listener, but we need to tell
	 * OpenAL to use that data. This function does just that.
	 */
	void setListenerValues() {
		if (failed) {
			return;
		}
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}

	static public boolean OPENAL_INIT = false;

	public static void initOpenAL() {
		// Initialize OpenAL and clears the error bit.
		try {
			AL.create(null, 15, 22050, true);
			OPENAL_INIT = true;
		} catch (LWJGLException le) {
			le.printStackTrace();
			OPENAL_INIT = false;
			Sys
					.alert("Sound Error (#OpenALCl299)",
							"Couldn't initialize OpenAL.\nPlease close all other applications\nand restart the game to play with sound.");
			return;
		}

		if (AL10.alIsExtensionPresent("AL_EXT_vorbis")) {
			System.out.println("Vorbis extension found.");

		} else {
			System.out.println("Vorbis extension not supported.");
		}

		AL10.alGetError();
	}

	public void play() {
		if (failed) {
			return;
		}
		myGain = 1;
		minus_per_tick = 0;
		AL10.alSourcePlay(source.get(0));

	}

	public boolean isPlaying() {
		if (failed) {
			return false;
		}
		return AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void pause() {
		if (failed) {
			return;
		}
		AL10.alSourcePause(source.get(0));
	}

	public void stop() {
		if (failed) {
			return;
		}
		forcedstop = true;
		AL10.alSourceStop(source.get(0));
	}

	public void fadeOut(int seconds) {
		if (failed) {
			return;
		}
		sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.1f });
		sourceVel.flip();
		AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVel);

		// workaround!
		elapsed_ticks = 0;
		minus_per_tick = 1. / (10 * seconds);
	}

	void actualisePosition() {
		if (failed) {
			return;
		}
		elapsed_ticks++;

		sourcePos.put(0, sourcePos.get(0) + sourceVel.get(0));
		sourcePos.put(1, sourcePos.get(1) + sourceVel.get(1));
		sourcePos.put(2, sourcePos.get(2) + sourceVel.get(2));

		AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
		if (minus_per_tick != 0) {
			myGain = (float) (Math.max(Math.min(1.0 - elapsed_ticks * minus_per_tick, 1), 0));
		}
		if (myGain == 0) {
			stop();
		}

	}

	/**
	 * Sets the Gain of this Sound. Must be between 0 and 1.
	 * 
	 * @param myGain
	 */
	public void setMyGain(float myGain) {
		this.myGain = myGain;
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

		for (Runnable r : endAction) {
			r.run();
		}

	}

	class StateWatcher extends Thread {
		int last_state;

		public StateWatcher() {
			super();
			last_state = AL10.AL_INITIAL;
		}

		@Override
		public void run() {
			while (true) {
				actualisePosition();

				// gain an globalen gain anpassen!

				float newgain = myGain;
				try {
					if (MainMenu.intern_state == MainMenu.Einstellungen) {
						newgain = (float) (myGain * MainMenu.temp_profil.getSound_gain() / 10.);
					} else {
						newgain = (float) (myGain * SuperMain.profil.getSound_gain() / 10.);
					}

				} catch (NullPointerException e) {
					newgain = 1;
				}
				AL10.alSourcef(source.get(0), AL10.AL_GAIN, newgain);

				int current_state = AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE);

				if (current_state == AL10.AL_STOPPED && last_state != current_state) {
					// es wurde gerade gestoppt
					if (forcedstop) {
						// gewolltes ende
						forcedstop = false;
					} else {
						// von alleine geendet
						onClipEnded();
					}
				}
				if (current_state == AL10.AL_PLAYING && last_state != current_state) {
					// es wurde gerade gestartet
					onClipStarted();
				}

				last_state = current_state;
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
