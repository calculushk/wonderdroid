
package uk.org.cardboardbox.wonderdroid;

import java.io.File;
import java.io.IOException;

import uk.org.cardboardbox.wonderdroid.views.EmuView;
import uk.org.cardboardbox.wonderdroid.views.EmuView.Buttons;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Main extends Activity {

	public static final String ROMPATH = "rompath";

	private TextView fpscounter;
	private Handler handler;

	private static int[] buttonIds = {R.id.button_y1, R.id.button_y2, R.id.button_y3, R.id.button_y4, R.id.button_x1,
		R.id.button_x2, R.id.button_x3, R.id.button_x4, R.id.button_a, R.id.button_b, R.id.button_start};

	private ProgressBar mPB;
	private EmuView view;

	private int mButtonStartCode = 0;
	private int mButtonACode = 0;
	private int mButtonBCode = 0;
	private int mButtonX1Code = 0;
	private int mButtonX2Code = 0;
	private int mButtonX3Code = 0;
	private int mButtonX4Code = 0;
	private int mButtonY1Code = 0;
	private int mButtonY2Code = 0;
	private int mButtonY3Code = 0;
	private int mButtonY4Code = 0;

	private String mRomPath;

	private File mCartMem;

	private Boolean mRomLoaded = false;
	private boolean mControlsVisible = true;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handler = new Handler();

		mRomPath = this.getIntent().getExtras().getString(ROMPATH);

		WonderSwan.Header header = new WonderSwan.Header(new File(mRomPath));
		mCartMem = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wonderdroid/cartmem/"
			+ header.getInternalName() + ".mem");
		try {
			mCartMem.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

		setContentView(R.layout.main);

		fpscounter = (TextView)findViewById(R.id.fpscounter);

		for (int button : buttonIds) {

			((Button)this.findViewById(button)).setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch (View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						view.setButton(Buttons.findForId(v.getId()));

					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						view.clearButton(Buttons.findForId(v.getId()));
					}

					return false;
				}

			});

		}

		view = (EmuView)this.findViewById(R.id.gameview);

		parseKeys();
		mControlsVisible = true;
		toggleControls();

		mPB = (ProgressBar)this.findViewById(R.id.romloadprogressbar);

		AsyncTask<Void, Void, Void> loader = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute () {
				mRomLoaded = false;
			}

			@Override
			protected Void doInBackground (Void... params) {
				WonderSwan.load(mRomPath, ((WonderSwan.Header)(new WonderSwan.Header(new File(mRomPath)))).isColor);
				return null;
			}

			@Override
			protected void onPostExecute (Void result) {
				if (mPB != null) {
					mPB.setVisibility(ProgressBar.GONE);
				}

				WonderSwan.reset();
				if (mCartMem.isFile() && (mCartMem.length() > 0)) {
					WonderSwan.loadbackupdata(mCartMem.getAbsolutePath());
				}
				mRomLoaded = true;
				view.start();
			}
		};

		loader.execute((Void[])null);
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.main_exitmi:
			mRomLoaded = false;
			this.finish();
			return true;

		case R.id.main_pausemi:
			view.togglepause();
			return true;

		case R.id.main_resetmi:
			WonderSwan.reset();
			return true;

		case R.id.main_prefsmi:
			Intent intent = new Intent(this, Prefs.class);
			startActivity(intent);
			return true;

		case R.id.main_togcntrlmi:
			toggleControls();
			return true;
			// case R.id.quit:
			// quit();
			// return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		return true;
	}

	private void toggleControls () {
		if (mControlsVisible) {

			for (int button : buttonIds) {
				this.findViewById(button).setVisibility(View.GONE);
			}

		}

		else {

			for (int button : buttonIds) {
				this.findViewById(button).setVisibility(View.VISIBLE);
			}

		}
		mControlsVisible = !mControlsVisible;
	}

	private void parseEmuOptions () {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		fpscounter.setVisibility(prefs.getBoolean("emufpscounter", false) ? View.VISIBLE : View.GONE);
	}

	void parseKeys () {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		mButtonStartCode = prefs.getInt("hwcontrolStart", 0);

		mButtonACode = prefs.getInt("hwcontrolA", 0);
		mButtonBCode = prefs.getInt("hwcontrolB", 0);

		mButtonX1Code = prefs.getInt("hwcontrolX1", 0);
		mButtonX2Code = prefs.getInt("hwcontrolX2", 0);
		mButtonX3Code = prefs.getInt("hwcontrolX3", 0);
		mButtonX4Code = prefs.getInt("hwcontrolX4", 0);

		mButtonY1Code = prefs.getInt("hwcontrolY1", 0);
		mButtonY2Code = prefs.getInt("hwcontrolY2", 0);
		mButtonY3Code = prefs.getInt("hwcontrolY3", 0);
		mButtonY4Code = prefs.getInt("hwcontrolY4", 0);
	}

	private Buttons decodeKey (int keycode) {

		if (keycode == mButtonStartCode) {
			return Buttons.START;
		}

		else if (keycode == mButtonACode) {
			return Buttons.A;
		}

		else if (keycode == mButtonBCode) {
			return Buttons.B;
		}

		else if (keycode == mButtonX1Code) {
			return Buttons.X1;
		}

		else if (keycode == mButtonX2Code) {
			return Buttons.X2;
		}

		else if (keycode == mButtonX3Code) {
			return Buttons.X3;
		}

		else if (keycode == mButtonX4Code) {
			return Buttons.X4;
		}

		else if (keycode == mButtonY1Code) {
			return Buttons.Y1;
		}

		else if (keycode == mButtonY2Code) {
			return Buttons.Y2;
		}

		else if (keycode == mButtonY3Code) {
			return Buttons.Y3;
		}

		else if (keycode == mButtonY4Code) {
			return Buttons.Y4;
		}

		return null;

	}

	public boolean onKeyDown (int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// disable back key
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!mRomLoaded) {
				return true;
			}
			return false;
		}

		Buttons pressed = decodeKey(keyCode);
		if (pressed != null) {
			view.setButton(pressed);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp (int keyCode, KeyEvent event) {

		Buttons pressed = decodeKey(keyCode);
		if (pressed != null) {
			view.clearButton(pressed);
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onRestart () {
		super.onRestart();
		view.onResume();
	}

	@Override
	public void onPause () {
		super.onPause();
		view.stop();
		handler.removeCallbacks(fpsUpdater);
		WonderSwan.storebackupdata(mCartMem.getAbsolutePath());
	}

	Runnable fpsUpdater = new Runnable() {
		@Override
		public void run () {
			fpscounter.setText(Float.toString(view.getFps()));
			handler.postDelayed(this, 500);
		}
	};

	@Override
	protected void onResume () {
		super.onResume();
		parseEmuOptions();
		fpsUpdater.run();
	}

}
