
package uk.org.cardboardbox.wonderdroid.utils;

import uk.org.cardboardbox.wonderdroid.WonderSwan;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

@SuppressWarnings("unused")
public class EmuThread extends Thread {

	private static final boolean debug = false;
	private static final String TAG = EmuThread.class.getSimpleName();
	private static final float TARGETFRAMETIME = 1000 / 71;
	private int mustSkipFrames = 5;
	private boolean mIsRunning = false;
	private boolean isPaused = false;

	private final Bitmap framebuffer;
	private final Paint paint = new Paint();
	private SurfaceHolder mSurfaceHolder;
	private Matrix scale;

	public EmuThread () {
		framebuffer = Bitmap.createBitmap(WonderSwan.SCREEN_WIDTH, WonderSwan.SCREEN_HEIGHT, Bitmap.Config.RGB_565);
	}

	public void setSurfaceHolder (SurfaceHolder sh) {
		mSurfaceHolder = sh;
	}

	public void pause () {
		isPaused = true;
		WonderSwan.audio.pause();
	}

	public void unpause () {
		isPaused = false;
		if (WonderSwan.audio.getState() == AudioTrack.PLAYSTATE_PAUSED) {
			WonderSwan.audio.play();
		}
	}

	private Canvas c;

	private int framecounter;
	private long thisFrame;
	private long lastFrame;
	private long averageFrameTime = 1;

	@Override
	public void run () {

		while (mSurfaceHolder == null) {
			SystemClock.sleep(20);
		}

		// benchmark
		long start = System.currentTimeMillis();
		for (int frame = 0; frame < 60; frame++) {

			c = null;
			try {
				c = mSurfaceHolder.lockCanvas();
				synchronized (mSurfaceHolder) {
					c.drawARGB(0x00, frame, frame, frame);
				}
			} finally {
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}

		}
		float fps = (float)(1f / (((System.currentTimeMillis() - start) / 1000f) / 60f));
		Log.d(TAG, String.format("%f fps", fps));
		//

		WonderSwan.audio.play();

		while (mIsRunning) {

			if (isPaused) {

				SystemClock.sleep(200);

			} else {

				render(framecounter % mustSkipFrames == 0 || averageFrameTime > TARGETFRAMETIME);
				
				if (averageFrameTime < TARGETFRAMETIME) {
					SystemClock.sleep((int)(TARGETFRAMETIME - averageFrameTime));
				}
				
				thisFrame = SystemClock.uptimeMillis();
				averageFrameTime = (averageFrameTime + (thisFrame - lastFrame)) / 2;
				lastFrame = thisFrame;

				

			}

			framecounter++;
		}

		WonderSwan.audio.stop();

		synchronized (this) {
			notifyAll();
		}

	}

	private void render (boolean frameskip) {
		WonderSwan.execute_frame(frameskip);
		if (!frameskip) {
			framebuffer.copyPixelsFromBuffer(WonderSwan.framebuffer);

			c = null;
			try {
				c = mSurfaceHolder.lockCanvas();
				synchronized (mSurfaceHolder) {
					c.drawBitmap(framebuffer, scale, paint);
				}
			} finally {
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
		//WonderSwan.execute_vblank();
	}

	public boolean isRunning () {
		return mIsRunning;
	}

	public void setRunning () {
		mIsRunning = true;
	}

	public void clearRunning () {
		mIsRunning = false;
	}

	public void setScale (Matrix scale) {
		this.scale = scale;
	}

	public float getFps () {
		return 1000 / averageFrameTime;
	}

	public Paint getPaint () {
		return paint;
	}
}
