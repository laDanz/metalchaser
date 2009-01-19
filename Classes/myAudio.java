package Classes;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import main.SuperMain;

import org.tritonus.share.sampled.FloatSampleBuffer;

/**
 * A class for audio handling.
 * 
 * @deprecated use OpenAlClip instead.
 * @author laDanz
 * 
 */
public class myAudio {

	private static SourceDataLine line = null;

	private static AudioFormat decodedFormat;

	private static boolean first_try = true;

	private static LinkedList<myClip> streams2;
	private static LinkedList<myClip> waiters = new LinkedList<myClip>();
	static FloatSampleBuffer mixBuffer = null;

	static FloatSampleBuffer readBuffer = new FloatSampleBuffer();

	static byte[] tempBuffer;

	/**
	 * Attenuate the stream by how many dB per mixed stream. For example, if
	 * attenuationPerStream is 2dB, and 3 streams are mixed together, the mixed
	 * stream will be attenuated by 6dB. Set to 0 to not attenuate the signal,
	 * which will usually give good results if only 2 streams are mixed
	 * together.
	 */
	static float attenuationPerStream = 0.1f;

	/**
	 * The linear factor to apply to all samples (derived from
	 * attenuationPerStream). This is a factor in the range of 0..1 (depending
	 * on attenuationPerStream and the number of streams).
	 */
	static float attenuationFactor = 1.0f;

	private static int gain = 50;

	private static int oldsize = 0;

	/**
	 * Prints the supported audio types.
	 */
	public static void print_supported() {
		for (AudioFileFormat.Type t : AudioSystem.getAudioFileTypes()) {
			SuperMain.out(t.toString());
		}
		SuperMain.out("MixerInfo: ");
		for (Mixer.Info t : AudioSystem.getMixerInfo()) {
			SuperMain.out(t.toString());
		}

	}

	/**
	 * Tries to play the clip.
	 * 
	 * @param clip
	 *            A <code>myClip</code>.
	 */
	static public void play(myClip clip) {
		if (clip == null)
			return;

		// wenn line nach dem ersten versuch nit da ist, dann nicht wiedergebwen
		if (line == null && !first_try) {
			clip.close();
			return;
		}
		// System.out.println("myAudio.play: " + System.currentTimeMillis());
		try {

			if (streams2 == null)
				streams2 = new LinkedList<myClip>();

			if (line == null && first_try)
				initLine(getDecodedFormat());

			// System.out.println("Add to write: " +
			// System.currentTimeMillis());
			addToWrite(clip);

		} catch (LineUnavailableException e) {
			if (!SuperMain.dev)
				org.lwjgl.Sys.alert(OGL.GAME_TITLE, "Konnt Audio nicht initialisieren.\nSoundtreiber aktualisieren.");
			// e.printStackTrace();SuperMain.out(e);
			System.err.println("AudioLineUnavaiable");
		} catch (IllegalArgumentException e) {
			if (!SuperMain.dev)
				org.lwjgl.Sys.alert(OGL.GAME_TITLE,
						"Konnt Audio nicht starten.\nAndere Audiogeräte bitte schließen und " + OGL.GAME_TITLE
								+ " neu starten.");
			// Anderes Device im Hintergrund an!
			// e.printStackTrace();SuperMain.out(e);
			first_try = false;
			System.err.println("AudioLineUnavaiable-other Device running");
		}

	}

	private static void addToWrite(myClip din) {
		// System.out.println("AddToWaiters: " + System.currentTimeMillis());
		waiters.add(din);
	}

	private static void writetoLine() throws IOException {

		checkForWaiters();
		// berechne maximalen anteil pro clip

		if (oldsize < streams2.size()) {
			// System.out.println("Stream added(" + streams2.size() + "): " +
			// System.currentTimeMillis());
		}
		oldsize = streams2.size();

		int count = Math.max(1, streams2.size());
		int piece = line.getBufferSize() / count;
		boolean eins_groesser = false;
		while (!eins_groesser || count * piece > line.getBufferSize() || piece % 4 != 0) {
			count++;
			piece = line.getBufferSize() / count;
			if (piece % 4 == 0 && !eins_groesser) {
				eins_groesser = true;
				count++;
			}
		}

		int MaxnumberOfBytesToRead = line.getBufferSize() / (count);
		// int read = 0;
		byte[] abData = new byte[MaxnumberOfBytesToRead];
		int nLength = MaxnumberOfBytesToRead;
		// AudioFormat decodedFormat=myAudio.decodedFormat;
		int nChannels = getDecodedFormat().getChannels();
		int nFrameSize = getDecodedFormat().getFrameSize();
		// System.out.println("Channels/Framesize:"+nChannels+"/"+nFrameSize);
		int nOffset = 0;
		if (mixBuffer == null)
			mixBuffer = new FloatSampleBuffer(getDecodedFormat().getChannels(), 0, getDecodedFormat().getSampleRate());

		mixBuffer.changeSampleCount(nLength / getDecodedFormat().getFrameSize(), false);

		// initialize the mixBuffer with silence
		mixBuffer.makeSilence();

		// remember the maximum number of samples actually mixed
		int maxMixed = 0;

		Iterator streamIterator = streams2.iterator();
		while (streamIterator.hasNext()) {
			myClip clip = ((myClip) streamIterator.next());
			AudioInputStream stream = (AudioInputStream) clip.din;

			// calculate how many bytes we need to read from this stream
			int needRead = mixBuffer.getSampleCount() * stream.getFormat().getFrameSize();

			// set up the temporary byte buffer
			if (tempBuffer == null || tempBuffer.length < needRead) {
				tempBuffer = new byte[needRead];
			}

			// read from the source stream
			int bytesRead = stream.read(tempBuffer, 0, needRead);
			clip.bytes_read += bytesRead;
			if (bytesRead == -1 || clip.wannaStop) {
				// end of stream: remove it from the list of streams.
				clip.onClipEnded();
				streamIterator.remove();

				continue;
			}
			// now convert this buffer to float samples
			readBuffer.initFromByteArray(tempBuffer, 0, bytesRead, stream.getFormat());
			if (maxMixed < readBuffer.getSampleCount()) {
				maxMixed = readBuffer.getSampleCount();
			}

			// the actual mixing routine: add readBuffer to mixBuffer
			// can only mix together as many channels as available
			int maxChannels = Math.min(mixBuffer.getChannelCount(), readBuffer.getChannelCount());
			for (int channel = 0; channel < maxChannels; channel++) {
				// get the arrays of the normalized float samples
				float[] readSamples = readBuffer.getChannel(channel);
				float[] mixSamples = mixBuffer.getChannel(channel);
				// Never use readSamples.length or mixSamples.length: the length
				// of the array may be longer than the actual buffer ("lazy"
				// deletion).
				int maxSamples = Math.min(mixBuffer.getSampleCount(), readBuffer.getSampleCount());
				// in a loop, add each "read" sample to the mix buffer
				// can only mix as many samples as available. Also apply the
				// attenuation factor.

				// Note1: the attenuation factor could also be applied only once
				// in a separate loop after mixing all the streams together,
				// saving processor time in case of many mixed streams.

				// Note2: adding everything together here will not cause
				// clipping, because all samples are in float format.
				for (int sample = 0; sample < maxSamples; sample++) {
					mixSamples[sample] += attenuationFactor * readSamples[sample];
				}
			}

		} // loop over streams

		if (maxMixed == 0) {
			// nothing written to the mixBuffer
			if (streams2.size() == 0) {
				// nothing mixed, no more streams available: end of stream
				return;
			}
			// nothing written, but still streams to read from
			return;
		}
		// finally convert the mix Buffer to the requested byte array.
		// This routine will handle clipping, i.e. if there are samples > 1.0f
		// in the mix buffer, they will be clipped to 1.0f and converted to the
		// specified audioFormat's sample format.
		mixBuffer.convertToByteArray(/* 0, maxMixed, */abData, nOffset, getDecodedFormat());

		// System.out.println("Write to SoundLine: " +
		// System.currentTimeMillis());
		// System.out.println("______________________________");
		line.write(abData, 0, MaxnumberOfBytesToRead);

		// line.drain();
	}

	private static void checkForWaiters() {
		for (myClip as : waiters) {
			// System.out.println("CheckForWaiters: " +
			// System.currentTimeMillis());
			streams2.add(as);
			as.onClipStarted();
		}
		waiters.clear();

	}

	private static void initLine(AudioFormat format) throws LineUnavailableException {

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format);
		line.start();
		System.out.println("LineBufferSize: " + line.getBufferSize());
		setGain(gain);
		// Writing starten
		new Thread() {
			@Override
			public void run() {

				super.run();
				while (line.isOpen())
					try {
						writetoLine();
						// this.sleep(20);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						SuperMain.out(e);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// SuperMain.out(e);
					}
			}
		}.start();
	}

	private static void closeLine() {
		line.close();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		closeLine();
	}

	/**
	 * Sets the gain of the sound.<br>
	 * 
	 * @param i
	 *            The gain in percent (0-100).
	 */
	public static void setGain(int i) {
		double value = i / 100.;
		gain = i;
		if (line == null)
			return;
		FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		float dB = (float) (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
		gainControl.setValue(dB);
	}

	/**
	 * Is audio available on this hardware???
	 * 
	 * @return true if it is available.
	 */
	public static boolean isAudiAvaiable() {
		return (line != null);
	}

	/**
	 * @param decodedFormat
	 *            the decodedFormat to set
	 */
	public static void setDecodedFormat(AudioFormat decodedFormat) {
		myAudio.decodedFormat = decodedFormat;
	}

	/**
	 * @return the decodedFormat
	 */
	public static AudioFormat getDecodedFormat() {
		return decodedFormat;
	}

}
