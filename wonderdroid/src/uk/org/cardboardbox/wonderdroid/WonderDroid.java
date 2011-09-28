
package uk.org.cardboardbox.wonderdroid;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "wonderdroidcrash@0x0f.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_string)
public class WonderDroid extends Application {

	public static final String DIRECTORY = "/wonderdroid/";
	public static final String CARTMEMDIRECTORY = DIRECTORY + "cartmem/";

	@Override
	public void onCreate () {
		super.onCreate();
		ACRA.init(this);
		WonderSwan.outputDebugShizzle();
	}

}
