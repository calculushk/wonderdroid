
package uk.org.cardboardbox.wonderdroid.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.util.Log;

public class ZipCache {

	private static final String TAG = ZipCache.class.getSimpleName();

	public static File getFile (Context context, ZipFile zip, String wantedFile, String[] extensionsToUnpack) {

		Log.d(TAG, "Someone is asking for " + wantedFile + " from " + zip.getName());

		String shortName = zip.getName().replaceAll(".*/", "");

		File cacheDir = getCacheDir(context);
		File zipDir = new File(cacheDir, shortName);
		File cachedFile;

		if (zipDir.exists()) {
			cachedFile = new File(zipDir, wantedFile);
			if (!cachedFile.exists()) {
				throw new IllegalStateException();
			}
			Log.d(TAG, "Returning file from cache");
			zipDir.setLastModified(System.currentTimeMillis());
			return cachedFile;
		} else {
			Log.d(TAG, shortName + " hasn't been unpacked yet.. doing it now");
			zipDir.mkdir();
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File target = new File(zipDir, entry.getName());
				for (String extension : extensionsToUnpack) {
					if (entry.getName().endsWith(extension)) {
						extract(zip, entry, target);
						break;
					}
				}
			}

			cachedFile = new File(zipDir, wantedFile);
			if (cachedFile.exists()) {
				return cachedFile;
			}
		}

		return null;
	}

	private static boolean extract (ZipFile zip, ZipEntry entry, File target) {
		try {
			Log.d(TAG, "extracting " + entry.getName());
			if (entry.getSize() > (4 * 1024 * 1024)) {
				Log.d(TAG, "File is bigger than 4MB");
				return false;
			}

			byte[] buffer = new byte[1024];
			InputStream is = zip.getInputStream(entry);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
			int len;
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
			Log.d(TAG, "Done!");
			return true;
		} catch (Exception ex) {
			Log.d(TAG, "Failed!");
			ex.printStackTrace();
			return false;
		}
	}

	public static void clean (Context context) {
		File cacheDir = getCacheDir(context);
		File[] list = cacheDir.listFiles();
		long oneWeekAgo = System.currentTimeMillis() - (1000 * (60 * 60 * 24 * 7));
		for (File file : list) {
			if (file.lastModified() < oneWeekAgo) {
				Log.d(TAG, "Deleting " + file.getName());
				file.delete();
			}
		}
	}

	public static void dumpInfo (Context context) {
		File cacheDir = getCacheDir(context);
		String[] list = cacheDir.list();
		Log.d(TAG, "Have " + list.length + " extracted zips in cache");
		for (String file : list) {
			Log.d(TAG, file);
		}
	}

	private static File getCacheDir (Context context) {
		File cacheDir = new File(context.getFilesDir(), "zipcache");
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

}
