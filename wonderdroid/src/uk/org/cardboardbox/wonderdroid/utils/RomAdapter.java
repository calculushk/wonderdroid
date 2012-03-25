
package uk.org.cardboardbox.wonderdroid.utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import uk.org.cardboardbox.wonderdroid.WonderSwan;
import uk.org.cardboardbox.wonderdroid.views.RomGalleryView;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RomAdapter extends BaseAdapter {

	public static final class Rom {
		public static String[] romExtension = new String[] {"ws", "wsc"};

		public enum Type {
			ZIP, RAW
		}

		public final Type type;
		public final String displayName;
		public final File sourcefile;
		public final String fileName;

		public Rom (Type type, File sourceFile, String fileName, String displayName) {
			this.type = type;
			this.sourcefile = sourceFile;
			this.fileName = fileName;
			this.displayName = displayName;
		}

		public static File getRomFile (Context context, Rom rom) {
			switch (rom.type) {
			case RAW:
				return rom.sourcefile;
			case ZIP:
				try {
					return ZipCache.getFile(context, new ZipFile(rom.sourcefile), rom.fileName, romExtension);
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}

			}
			return null;
		}

		public static WonderSwan.Header getHeader (Context context, Rom rom) {

			File romFile = null;
			try {
				if (rom.type == Type.RAW || rom.type == Type.ZIP && ZipCache.isZipInCache(context, new ZipFile(rom.sourcefile))) {
					romFile = Rom.getRomFile(context, rom);
				} else if (rom.type == Type.ZIP) {
					ZipFile zip = new ZipFile(rom.sourcefile);
					ZipEntry entry = ZipUtils.getEntry(zip, rom.fileName);
					return new WonderSwan.Header(ZipUtils.getBytesFromEntry(zip, entry, entry.getSize() - WonderSwan.Header.HEADERLEN,
						WonderSwan.Header.HEADERLEN));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (romFile != null) {
				WonderSwan.Header header = new WonderSwan.Header(romFile);
				return header;
			}

			return null;
		}
	}

	private static final String TAG = RomAdapter.class.getSimpleName();

	private final HashMap<Integer, WonderSwan.Header> mHeaderCache = new HashMap<Integer, WonderSwan.Header>();
	private final HashMap<String, SoftReference<Bitmap>> mSplashCache = new HashMap<String, SoftReference<Bitmap>>();

	private final AssetManager mAssetManager;
	private final File mRomDir;
	private final Context mContext;
	private final Rom[] mRoms;

	public RomAdapter (Context context, String romdir, AssetManager assetManager) {
		mAssetManager = assetManager;
		mRomDir = new File(romdir);
		mContext = context;
		mRoms = findRoms();
	}

	private Rom[] findRoms () {
		File[] sourceFiles = mRomDir.listFiles(new RomFilter());
		ArrayList<Rom> roms = new ArrayList<Rom>();
		for (int i = 0; i < sourceFiles.length; i++) {

			if (sourceFiles[i].getName().endsWith("zip")) {
				try {
					for (String entry : ZipUtils.getValidEntries(new ZipFile(sourceFiles[i]), Rom.romExtension)) {
						roms.add(new Rom(Rom.Type.ZIP, sourceFiles[i], entry, sourceFiles[i].getName().replaceFirst("\\.zip", "")));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					break;
				}
			} else {
				roms.add(new Rom(Rom.Type.RAW, sourceFiles[i], null, sourceFiles[i].getName().replaceFirst("\\.wsc", "")
					.replaceFirst("\\.ws", "")));
			}

		}

		Rom[] allRoms = roms.toArray(new Rom[0]);

		Arrays.sort(allRoms, new Comparator<Rom>() {
			public int compare (Rom lhs, Rom rhs) {
				return lhs.sourcefile.compareTo(rhs.sourcefile);
			}
		});

		return allRoms;
	}

	public Bitmap getBitmap (int index) {
		WonderSwan.Header header = getHeader(index);
		String internalname = header.getInternalName();
		if (mSplashCache.containsKey(internalname)) {
			Bitmap splash = mSplashCache.get(internalname).get();
			if (splash != null) {
				return splash;
			}
		}

		try {
			Bitmap splash = BitmapFactory.decodeStream(mAssetManager.open("snaps/" + internalname + ".png"));
			if (header.isVertical) {
				Matrix rotationmatrix = new Matrix();
				rotationmatrix.setRotate(270, splash.getWidth() / 2, splash.getHeight() / 2);
				splash = Bitmap.createBitmap(splash, 0, 0, splash.getWidth(), splash.getHeight(), rotationmatrix, false);
			}
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
	public Rom getItem (int arg0) {
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

		view.setTitle(mRoms[index].displayName);

		WonderSwan.Header header = getHeader(index);
		if (header != null) {
			Bitmap shot = getBitmap(index);
			if (shot != null) {
				view.setSnap(shot);
			} else {
				Log.d(TAG, "snap is null for " + mRoms[index].sourcefile);
			}
		}

		return view;
	}

	public synchronized WonderSwan.Header getHeader (int index) {

		if (mHeaderCache.containsKey(index)) {
			return mHeaderCache.get(index);
		}

		Rom rom = (Rom)(this.getItem(index));
		WonderSwan.Header header = Rom.getHeader(mContext, rom);
		if (header != null) {
			mHeaderCache.put(index, header);
		}
		return header;
	}
}
