
package uk.org.cardboardbox.wonderdroid.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {

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

}
