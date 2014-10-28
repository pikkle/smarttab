package ch.epfl.sweng.smartTabs.music;

import android.media.SoundPool;

/**
 * @author imani92
 * Ismail Imani
 */

public class NotePlaybackThread extends Thread {
	private SoundPool mPool;
	private SampleMap mSamples;
	private Time mTime;

	/**
	 * Constructor of the NotePlaybackThread.
	 * @param pool is the SoundPool used to play the samples
	 * @param samples is the array of sample IDs
	 * @param time is the Time containing the notes to play
	 */
	public NotePlaybackThread(final SoundPool pool,
			final SampleMap samples, final Time time) {
		mPool = pool;
		mSamples = samples;
		mTime = time;
	}

	@Override
	public final void run() {
		playNote(mTime);
	}

	/**
	 * This method plays the notes contained in the Time object.
	 * @param time is the Time object containing the notes to play
	 */
	public final void playNote(final Time time) {
		for (int i = 0; i < (Instrument.GUITAR).getNumOfStrings(); i++) {
			if (!mTime.getNote(i).equals("")) {
				mPool.play(mSamples.getSampleId(i,
						Integer.parseInt(mTime.getNote(i))), 1, 1, 1, 0, 1);
			}
		}
	}

}