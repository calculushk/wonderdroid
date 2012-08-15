
package uk.org.cardboardbox.wonderdroid;

import android.app.Activity;

public class BaseActivity extends Activity {

    WonderDroid getWonderDroidApplication() {
        return (WonderDroid)getApplication();
    }

}
