
package uk.org.cardboardbox.wonderdroid.views;

import uk.org.cardboardbox.wonderdroid.R;
import uk.org.cardboardbox.wonderdroid.WonderSwan;
import uk.org.cardboardbox.wonderdroid.WonderSwan.Buttons;
import uk.org.cardboardbox.wonderdroid.utils.EmuThread;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class EmuView extends SurfaceView implements SurfaceHolder.Callback {

	private final static String TAG = EmuView.class.getSimpleName();
	@SuppressWarnings("unused")
	private final static boolean debug = true;
	private boolean mPaused = false;

	private EmuThread mThread;
	private String[] buttonStrings = new String[] {"Y1", "Y4", "Y2", "Y3", "X1", "X4", "X2", "X3", "A", "B", "START"};
	private GradientDrawable[] buttons;

	public EmuView (Context context) {
		this(context, null);
	}

	public EmuView (Context context, AttributeSet attrs) {
		super(context, attrs);

		buttons = new GradientDrawable[11];

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = (GradientDrawable)getResources().getDrawable(R.drawable.button);
		}

		setZOrderOnTop(true); // FIXME any advantage to this?

		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);

		mThread = new EmuThread();

	}

	@Override
	public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
		int spacing = height / 50;
		int buttonsize = (int)(height / 6.7);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setSize(buttonsize, buttonsize);

			int updownleft = buttonsize / 2 + (spacing / 2);
			int updownright = buttonsize + (buttonsize / 2) + (spacing / 2);
			int bottomrowtop = height - buttonsize;

			switch (i) {
			// Y
			case 0:
				buttons[i].setBounds(updownleft, 0, updownright, buttonsize);
				break;
			case 1:
				buttons[i].setBounds(0, buttonsize + spacing, buttonsize, (buttonsize * 2) + spacing);
				break;
			case 2:
				buttons[i].setBounds(buttonsize + spacing, buttonsize + spacing, (buttonsize * 2) + spacing, (buttonsize * 2)
					+ spacing);
				break;
			case 3:
				buttons[i].setBounds(updownleft, (buttonsize * 2) + (spacing * 2), updownright, (buttonsize * 3) + (spacing * 2));
				break;
			// X
			case 4:
				buttons[i].setBounds(updownleft, height - buttonsize, updownright, height);
				break;
			case 5:
				buttons[i].setBounds(0, height - (buttonsize * 2) - spacing, buttonsize, height - buttonsize - spacing);
				break;
			case 6:
				buttons[i].setBounds(buttonsize + spacing, height - (buttonsize * 2) - spacing, (buttonsize * 2) + spacing, height
					- buttonsize - spacing);
				break;
			case 7:
				buttons[i].setBounds(updownleft, (height - (buttonsize * 3)) - (2 * spacing), updownright,
					(height - (buttonsize * 2)) - (2 * spacing));
				break;
			// A,B
			case 8:
				buttons[i].setBounds(width - (buttonsize * 2) - spacing, bottomrowtop, (width - buttonsize) - spacing, height);
				break;
			case 9:
				buttons[i].setBounds(width - buttonsize, bottomrowtop, width, height);
				break;
			// Start
			case 10:
				buttons[i].setSize(buttonsize * 2, buttonsize);
				buttons[i].setBounds((width / 2) - buttonsize, bottomrowtop, (width / 2) + buttonsize, height);
				break;
			}
		}

		float postscale = (float)width / (float)WonderSwan.SCREEN_WIDTH;

		if (height * postscale > height) {
			postscale = (float)height / (float)WonderSwan.SCREEN_HEIGHT;

		}

		mThread.setOverlay(renderControlsLayer(width, height, buttons, buttonStrings));

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

	GradientDrawable primaryButton;
	GradientDrawable secondaryButton;

	@Override
	public boolean onTouchEvent (MotionEvent event) {

		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();

		if (event.getPointerCount() != 1) {
			return true;
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if ((primaryButton = checkButtons(event.getX(), event.getY())) != null) {
				Log.d(TAG, "down");
			}
			break;
		case MotionEvent.ACTION_UP:
			primaryButton = null;
			Log.d(TAG, "up");
			break;
		}
		// else if(action == MotionEvent.ACTION_MOVE){
		// if(primaryButton != null){
		// if(!primaryButton.getBounds().contains((int) x, (int)y)){
		// primaryButton = null;
		// Log.d(TAG, "out");
		// //
		// }
		// }
		// }

		return true;
	}

	private GradientDrawable checkButtons (float x, float y) {
		for (GradientDrawable button : buttons) {
			if (button.getBounds().contains((int)x, (int)y)) {
				return button;
			}
		}

		return null;
	}

	private static Bitmap renderControlsLayer (int width, int height, Drawable[] buttons, String[] strings) {
		if (buttons != null) {
			Paint textPaint = new Paint();
			textPaint.setColor(0xFFFFFFFF);
			textPaint.setTextSize(height/30);
			textPaint.setShadowLayer(3, 1, 1, 0x99000000);
			textPaint.setAntiAlias(true);
			Bitmap controls = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(controls);

			for (int i = 0; i < buttons.length; i++) {
				Rect rect = buttons[i].getBounds();
				float textLen = (textPaint.measureText(strings[i])) / 2;

				buttons[i].draw(canvas);
				canvas.drawText(strings[i], (rect.left + rect.width() / 2) - textLen,
					(rect.top + rect.height() / 2) + (textPaint.getTextSize() /2 ), textPaint);
			}
			return controls;
		}

		return null;
	}
}
