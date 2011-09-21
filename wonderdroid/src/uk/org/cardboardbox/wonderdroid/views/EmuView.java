
package uk.org.cardboardbox.wonderdroid.views;

import uk.org.cardboardbox.wonderdroid.R;
import uk.org.cardboardbox.wonderdroid.WonderSwan;
import uk.org.cardboardbox.wonderdroid.utils.EmuThread;
import android.content.Context;
import android.graphics.Matrix;

import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class EmuView extends SurfaceView implements SurfaceHolder.Callback {

	private final static String TAG = EmuView.class.getSimpleName();
	@SuppressWarnings("unused")
	private final static boolean debug = true;
	private boolean mPaused = false;

	public static enum Buttons {
		START(R.id.button_start), A(R.id.button_a), B(R.id.button_b), X1(R.id.button_x1), X2(R.id.button_x2), X3(R.id.button_x3), X4(
			R.id.button_x4), Y1(R.id.button_y1), Y2(R.id.button_y2), Y3(R.id.button_y3), Y4(R.id.button_y4);

		private final int id;

		Buttons (int id) {
			this.id = id;
		}

		public int getId () {
			return id;
		}

		public static Buttons findForId (int id) {
			for (Buttons button : Buttons.values()) {
				if (button.getId() == id) {
					return button;
				}
			}
			return null;
		}
	};

	private EmuThread mThread;

	public EmuView (Context context) {
		this(context, null);
	}

	public EmuView (Context context, AttributeSet attrs) {
		super(context, attrs);

		setZOrderOnTop(true); // FIXME any advantage to this?

		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);

		mThread = new EmuThread();
	}

	@Override
	public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {

		float postscale = (float)width / (float)WonderSwan.SCREEN_WIDTH;

		if (height * postscale > height) {
			postscale = (float)height / (float)WonderSwan.SCREEN_HEIGHT;

		}

		Matrix scale = mThread.getMatrix();

		scale.reset();
		scale.postScale(postscale, postscale);
		scale.postTranslate((width - (WonderSwan.SCREEN_WIDTH * postscale)) / 2, 0);

	}

	@Override
	public void surfaceCreated (SurfaceHolder holder) {
		mThread.setSurfaceHolder(holder);
	}

	@Override
	public void surfaceDestroyed (SurfaceHolder holder) {
		mThread.clearRunning();
	}

	public void start () {
		Log.d(TAG, "emulation started");
		mThread.setRunning();
		mThread.start();
	}

	public void togglepause () {
		if (mPaused) {
			mPaused = false;
			mThread.unpause();
		} else {
			mPaused = true;
			mThread.pause();
		}
	}

	public void onResume () {
		mThread = new EmuThread();
		start();
	}

	public void stop () {

		if (mThread.isRunning()) {
			Log.d(TAG, "shutting down emulation");

			mThread.clearRunning();

			synchronized (mThread) {
				try {
					mThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}

		}
	}

	public static void changeButton (Buttons which, boolean newstate) {
		switch (which) {
		case START:
			WonderSwan.mButtonStart = newstate;
			break;
		case A:
			WonderSwan.mButtonA = newstate;
			break;
		case B:
			WonderSwan.mButtonB = newstate;
			break;
		case X1:
			WonderSwan.mButtonX1 = newstate;
			break;
		case X2:
			WonderSwan.mButtonX2 = newstate;
			break;
		case X3:
			WonderSwan.mButtonX3 = newstate;
			break;
		case X4:
			WonderSwan.mButtonX4 = newstate;
			break;
		case Y1:
			WonderSwan.mButtonY1 = newstate;
			break;
		case Y2:
			WonderSwan.mButtonY2 = newstate;
			break;
		case Y3:
			WonderSwan.mButtonY3 = newstate;
			break;
		case Y4:
			WonderSwan.mButtonY4 = newstate;
			break;
		}

		WonderSwan.buttonsDirty = true;

	}

	public void setButton (Buttons which) {
		changeButton(which, true);
	}

	public void clearButton (Buttons which) {
		changeButton(which, false);

	}

	public EmuThread getThread () {
		return mThread;
	}

}
