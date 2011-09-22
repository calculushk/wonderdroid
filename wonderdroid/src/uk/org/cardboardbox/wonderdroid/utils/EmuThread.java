
package uk.org.cardboardbox.wonderdroid.utils;

import uk.org.cardboardbox.wonderdroid.WonderSwan;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

public class EmuThread extends Thread {

	private static final boolean debug = false;
	private static final String TAG = EmuThread.class.getSimpleName();
	private static final int TARGETFRAMETIME = 1000 / 71;
	private static final int mustSkipFrames = 5;

	private boolean mIsRunning = false;
	private boolean isPaused = false;
	private boolean showFps = false;

	private final Bitmap framebuffer;
	private Bitmap overlay;
	private final Paint paint = new Paint();
	private final Paint textPaint = new Paint();
	private SurfaceHolder mSurfaceHolder;
	private final Matrix scale;
	private Canvas c;

	private short framecounter = 1; // dont skip the first frame
	private long thisFrame;
	private long lastFrame;
	private int averageFrameTime = TARGETFRAMETIME;
	private int worstFrameTime = TARGETFRAMETIME;

	public EmuThread () {
		framebuffer = Bitmap.createBitmap(WonderSwan.SCREEN_WIDTH, WonderSwan.SCREEN_HEIGHT, Bitmap.Config.RGB_565);
		textPaint.setColor(0xFFFFFFFF);
		textPaint.setTextSize(35);
		textPaint.setShadowLayer(3, 1, 1, 0x99000000);
		textPaint.setAntiAlias(true);
		scale = new Matrix();
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

		WonderSwan.audio.play();

		lastFrame = SystemClock.uptimeMillis();

		while (mIsRunning) {

			if (isPaused) {

				SystemClock.sleep(200);

			} else {

				thisFrame = SystemClock.uptimeMillis();
				// boolean skip = framecounter % mustSkipFrames == 0 || lastFrame > thisFrame + TARGETFRAMETIME;

				boolean skip = false;
				render(c, mSurfaceHolder, framebuffer, overlay, scale, paint, textPaint, skip, showFps, fpsString);

				int frametime = (int)(averageFrameTime + (thisFrame - lastFrame));

				averageFrameTime = frametime / 2;
				lastFrame = thisFrame;

				if (frametime > worstFrameTime) {
					worstFrameTime = frametime;
				}

				if (averageFrameTime < TARGETFRAMETIME) {
					SystemClock.sleep((int)(TARGETFRAMETIME - averageFrameTime));
					updateFPSString();
				}

				framecounter++;

			}

		}

		WonderSwan.audio.stop();

		synchronized (this) {
			notifyAll();
		}

	}

	private static void render (Canvas c, SurfaceHolder sh, Bitmap framebuffer, Bitmap overlay, Matrix scale, Paint paint,
		Paint textPaint, boolean frameskip, boolean showFps, String fpsString) {
		WonderSwan.execute_frame(frameskip);
		if (!frameskip) {
			framebuffer.copyPixelsFromBuffer(WonderSwan.framebuffer);

			c = null;
			try {
				c = sh.lockCanvas();
				synchronized (sh) {
					c.drawBitmap(framebuffer, scale, paint);

					if (overlay != null) {
						c.drawBitmap(overlay, 0, 0, null);
					}

					if (showFps) {
						c.drawText(fpsString, 30, 50, textPaint);
					}
				}
			} finally {
				if (c != null) {
					sh.unlockCanvasAndPost(c);
				}
			}
		}
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

	private String fpsString = new String();

	private void updateFPSString () {
		fpsString = String.format("%d: %03d fps", worstFrameTime, Math.round(getFps()));
	}

	public float getFps () {
		return 1000 / averageFrameTime;
	}

	public Paint getPaint () {
		return paint;
	}

	public Matrix getMatrix () {
		return scale;
	}

	public void showFps (boolean show) {
		showFps = show;
	}

	public void setOverlay (Bitmap overlay) {
		this.overlay = overlay;
	}

}
