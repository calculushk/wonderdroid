
package uk.org.cardboardbox.wonderdroid;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import uk.org.cardboardbox.wonderdroid.utils.ZipCache;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "wonderdroidcrash@0x0f.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_string)
public class WonderDroid extends Application {

	public static final String DIRECTORY = "/wonderdroid/";
	public static final String CARTMEMDIRECTORY = DIRECTORY + "cartmem/";
	public static final String SAVESTATEDIRECTORY = DIRECTORY + "savestates/";

	@Override
	public void onCreate () {
		super.onCreate();
		ACRA.init(this);
		WonderSwan.outputDebugShizzle();
		ZipCache.dumpInfo(this.getBaseContext());
		ZipCache.clean(this.getBaseContext());
	}

}
