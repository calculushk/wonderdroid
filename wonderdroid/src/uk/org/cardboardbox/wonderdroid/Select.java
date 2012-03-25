
package uk.org.cardboardbox.wonderdroid;

import java.io.File;
import java.util.Random;

import uk.org.cardboardbox.wonderdroid.utils.RomAdapter;
import uk.org.cardboardbox.wonderdroid.utils.RomAdapter.Rom;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Select extends Activity {

	private static final String TAG = Select.class.getSimpleName();

	private Bitmap backgroundOne = null;
	private Runnable bgSwitcher = new Runnable() {

		private Random mRNG = new Random();

		@Override
		public void run () {

			Log.d(TAG, "Switching background");

			int throwcount = 0;
			int newindex = 0;

			// work around for only having one game
			int count = mRAdapter.getCount();
			if (count == 1) {
				newindex = 0;
			} else { // normal path
				while ((newindex = mRNG.nextInt(count - 1)) == splashindex) {
					// I think we're getting a lock up here..
					throwcount++;
					if (throwcount > 10) {
						break;
					}
				}
				handler.postDelayed(this, 4000); // only run again if there is potentially more than one splash
			}

			Bitmap newbitmap = mRAdapter.getBitmap(splashindex);
			if (newbitmap == null) {
				return;
			}

			splashindex = newindex;

			// first run
			if (backgroundOne == null) {
				backgroundOne = newbitmap;
				mBG1.setImageBitmap(backgroundOne);
				return;
			}

			mBG2.setImageBitmap(backgroundOne);
			mBG2.setVisibility(View.VISIBLE); // hide the new splash before switching
			mBG1.setImageBitmap(newbitmap);
			backgroundOne = newbitmap;

			mBG2.startAnimation(fade);
		}
	};
	private Animation fade;
	private Handler handler;
	private AssetManager mAssetManager;
	private ImageView mBG1;

	private ImageView mBG2;

	private RomAdapter mRAdapter;

	private TextView mScreenFormat;

	private String sdpath;
	private int splashindex = 0;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		fade = AnimationUtils.loadAnimation(this, R.anim.splashfade);
		fade.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd (Animation animation) {
				mBG2.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat (Animation animation) {
			}

			@Override
			public void onAnimationStart (Animation animation) {
				mBG2.setVisibility(View.VISIBLE);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.select_exitmi:
			this.finish();
			return true;
		case R.id.select_prefsmi:
			Intent intent = new Intent(this, Prefs.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startEmu (int romid) {
		Intent intent = new Intent(this, Main.class);
		intent.putExtra(Main.ROMPATH, Rom.getRomFile(this.getBaseContext(), mRAdapter.getItem(romid)).getAbsolutePath());
		startActivity(intent);
	}

	@Override
	protected void onPause () {
		super.onPause();
		handler.removeCallbacks(bgSwitcher);
	}

	@Override
	protected void onResume () {
		super.onResume();

		if (Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED) != 0) {
			Toast.makeText(this, R.string.nosdcard, 2000).show();
			return;
		}

		sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();

		setContentView(R.layout.select);
		//
		File romdir = new File(sdpath + WonderDroid.DIRECTORY);
		romdir.mkdir();
		File cartmemdir = new File(sdpath + WonderDroid.CARTMEMDIRECTORY);
		cartmemdir.mkdir();
		//

		mScreenFormat = (TextView)this.findViewById(R.id.select_screenformat);
		mAssetManager = this.getAssets();
		mRAdapter = new RomAdapter(this.getBaseContext(), sdpath + "/wonderdroid/", mAssetManager);

		if (mRAdapter.getCount() != 0) {

			((TextView)this.findViewById(R.id.select_noroms)).setVisibility(View.GONE);

			mScreenFormat.setVisibility(View.VISIBLE);
			Gallery mRomGallery = (Gallery)this.findViewById(R.id.select_gallery);
			mRomGallery.setAdapter(mRAdapter);
			mRomGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected (AdapterView<?> arg0, View arg1, int arg2, long arg3) {

					WonderSwan.Header header = mRAdapter.getHeader(arg2);
					if (header != null) {
						String newtext;
						if (header.isColor) {
							newtext = getString(R.string.colour);
						} else {
							newtext = getString(R.string.mono);
						}

						if (header.isVertical) {
							newtext += getString(R.string.vertical);
						} else {
							newtext += getString(R.string.horizontal);
						}

						mScreenFormat.setText(newtext);
					}
				}

				@Override
				public void onNothingSelected (AdapterView<?> arg0) {
				}

			});

			mRomGallery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick (AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					startEmu(arg2);
				}

			});

			mBG1 = (ImageView)this.findViewById(R.id.select_bg1);
			mBG2 = (ImageView)this.findViewById(R.id.select_bg2);

			backgroundOne = null;

			bgSwitcher.run();

		}

	}

}
