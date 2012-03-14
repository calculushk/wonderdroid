
package uk.org.cardboardbox.wonderdroid;

import java.io.File;
import java.io.IOException;

import uk.org.cardboardbox.wonderdroid.views.EmuView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class Main extends Activity {

	public static final String ROMPATH = "rompath";

	private ProgressBar mPB;
	private EmuView view;
	private String mRomPath;
	private File mCartMem;
	private boolean mControlsVisible = false;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = new EmuView(this);
		setContentView(view);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);

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

		parseKeys();

		mPB = (ProgressBar)this.findViewById(R.id.romloadprogressbar);

		AsyncTask<Void, Void, Void> loader = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute () {
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
		mControlsVisible = !mControlsVisible;
		view.showButtons(mControlsVisible);
	}

	private void parseEmuOptions () {
		// The emu options are all gone for now
		// SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	private void parseKeys () {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		view.setKeyCodes(prefs.getInt("hwcontrolStart", 0), prefs.getInt("hwcontrolA", 0), prefs.getInt("hwcontrolB", 0),
			prefs.getInt("hwcontrolX1", 0), prefs.getInt("hwcontrolX2", 0), prefs.getInt("hwcontrolX3", 0),
			prefs.getInt("hwcontrolX4", 0), prefs.getInt("hwcontrolY1", 0), prefs.getInt("hwcontrolY2", 0),
			prefs.getInt("hwcontrolY3", 0), prefs.getInt("hwcontrolY4", 0));

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
		WonderSwan.storebackupdata(mCartMem.getAbsolutePath());
	}

	@Override
	protected void onResume () {
		super.onResume();
		parseEmuOptions();
		parseKeys();
	}

}
