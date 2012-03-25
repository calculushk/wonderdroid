
package uk.org.cardboardbox.wonderdroid.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.util.Log;

public class ZipUtils {

	private static final String TAG = ZipUtils.class.getSimpleName();

	public static String[] getValidEntries (ZipFile zip, String[] validExtensions) {

		ArrayList<String> validEntries = new ArrayList<String>();

		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			String thisEntry = entries.nextElement().getName();
			for (String extension : validExtensions) {
				if (thisEntry.endsWith(extension)) {
					validEntries.add(thisEntry);
				}
			}
		}

		return validEntries.toArray(new String[0]);
	}

	public static byte[] getBytesFromFile (ZipFile zip, String wantedFile, long offset, int len) {

		Log.d(TAG, "Someone is asking for bytes from " + wantedFile + " from zip " + zip.getName());
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getName().compareTo(wantedFile) == 0) {
				Log.d(TAG, "found the file");

				byte[] bytes = new byte[len];

				try {
					InputStream is = zip.getInputStream(entry);
					is.skip(offset);
					is.read(bytes);
					return bytes;
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				break;
			}
		}

		return null;
	}

	public static boolean extractFile (ZipFile zip, ZipEntry entry, File target) {
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

}
