
package uk.org.cardboardbox.wonderdroid.utils;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

public class EmuThread extends Thread {

	public static interface Renderer {
		public void start ();

		public void update (boolean skip);

		public void render (Canvas c);
	}

	private Renderer renderer;

	private static final boolean debug = false;
	private static final String TAG = EmuThread.class.getSimpleName();
	private static final int TARGETFRAMETIME = (int) Math.round(1000 / 75.47);

	private boolean mIsRunning = false;
	private boolean isPaused = false;
	private boolean showFps = false;

	private SurfaceHolder mSurfaceHolder;

	private Canvas c;

	private int frame;
	private long frameStart;
	private long frameEnd;
	private int realRuntime;
	private int emulatedRuntime;
	private int frametime;

	boolean skip = false;
	boolean behind = false;

	public EmuThread (Renderer renderer) {

		this.renderer = renderer;

	}

	public void setSurfaceHolder (SurfaceHolder sh) {
		mSurfaceHolder = sh;
	}

	public void pause () {
		isPaused = true;
	}

	public void unpause () {
		isPaused = false;
		// if (WonderSwan.audio.getState() == AudioTrack.PLAYSTATE_PAUSED) {
		// WonderSwan.audio.play();
		// }
	}

	@Override
	public void run () {

		while (mSurfaceHolder == null) {
			SystemClock.sleep(20);
		}

		// benchmark
		/*
		 * long start = System.currentTimeMillis(); for (int frame = 0; frame < 60; frame++) {
		 * 
		 * c = null; try { c = mSurfaceHolder.lockCanvas(); synchronized (mSurfaceHolder) { c.drawARGB(0x00, frame, frame, frame); }
		 * } finally { if (c != null) { mSurfaceHolder.unlockCanvasAndPost(c); } }
		 * 
		 * } float fps = (float)(1f / (((System.currentTimeMillis() - start) / 1000f) / 60f)); Log.d(TAG, String.format("%f fps",
		 * fps)); //
		 */

		while (mIsRunning) {

			if (isPaused) {
				SystemClock.sleep(TARGETFRAMETIME);
			} else {

				skip = behind || frame % 3 == 0;

				frameStart = System.currentTimeMillis();
				renderer.update(skip);

				if (!skip) {
					c = null;
					try {
						c = mSurfaceHolder.lockCanvas();
						synchronized (mSurfaceHolder) {
							renderer.render(c);
						}
					} finally {
						if (c != null) {
							mSurfaceHolder.unlockCanvasAndPost(c);
						}
					}
				}

				frameEnd = System.currentTimeMillis();
				frametime = (int)(frameEnd - frameStart);
				realRuntime += frametime;
				emulatedRuntime += TARGETFRAMETIME;

				if (realRuntime <= emulatedRuntime) {
					behind = false;
				} else {
					behind = true;
				}

				frame++;
			}

		}

		synchronized (this) {
			notifyAll();
		}

	}

	public boolean isRunning () {
		return mIsRunning;
	}

	public void setRunning () {
		mIsRunning = true;
		renderer.start();
	}

	public void clearRunning () {
		mIsRunning = false;
	}

}
