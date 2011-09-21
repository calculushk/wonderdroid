
package uk.org.cardboardbox.wonderdroid.utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashMap;

import uk.org.cardboardbox.wonderdroid.WonderSwan;
import uk.org.cardboardbox.wonderdroid.views.RomGalleryView;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RomAdapter extends BaseAdapter {

	private static final String TAG = RomAdapter.class.getSimpleName();

	private HashMap<Integer, WonderSwan.Header> mHeaderCache = new HashMap<Integer, WonderSwan.Header>();
	private HashMap<String, SoftReference<Bitmap>> mSplashCache = new HashMap<String, SoftReference<Bitmap>>();

	private AssetManager mAssetManager;
	private File mRomDir;
	private File[] mRoms;

	public RomAdapter (String romdir, AssetManager assetManager) {
		mAssetManager = assetManager;
		mRomDir = new File(romdir);
		mRoms = mRomDir.listFiles(new RomFilter());
		if (mRoms != null) {
			Arrays.sort(mRoms);
		}
	}

	public Bitmap getBitmap (int index) {
		return getBitmap(getHeader(index).getInternalName());
	}

	public Bitmap getBitmap (String internalname) {

		if (mSplashCache.containsKey(internalname)) {
			Bitmap splash = mSplashCache.get(internalname).get();
			if (splash != null) {
				return splash;
			}
		}

		try {
			Bitmap splash = BitmapFactory.decodeStream(mAssetManager.open("snaps/" + internalname + ".png"));
			mSplashCache.put(internalname, new SoftReference<Bitmap>(splash));
			return splash;
		} catch (IOException e) {
			// e.printStackTrace();
			Log.d(TAG, "No shot for " + internalname);
			return null;
		}

	}

	@Override
	public int getCount () {
		if (mRoms != null) {
			return mRoms.length;
		}
		return 0;
	}

	@Override
	public Object getItem (int arg0) {
		return mRoms[arg0];
	}

	@Override
	public long getItemId (int arg0) {
		return 0;
	}

	@Override
	public View getView (int index, View oldview, ViewGroup arg2) {

		RomGalleryView view;
		if (oldview == null) {
			view = new RomGalleryView(arg2.getContext(), null);
		} else {
			view = (RomGalleryView)oldview;
		}

		view.setTitle(mRoms[index].getName());

		Bitmap shot = getBitmap(getHeader(index).getInternalName());
		if (shot != null) {
			view.setSnap(shot);
		}

		return view;
	}

	private WonderSwan.Header getHeader (int index) {

		if (mHeaderCache.containsKey(index)) {
			return mHeaderCache.get(index);
		}

		WonderSwan.Header header = new WonderSwan.Header((File)this.getItem(index));
		mHeaderCache.put(index, header);
		return header;

	}
}
