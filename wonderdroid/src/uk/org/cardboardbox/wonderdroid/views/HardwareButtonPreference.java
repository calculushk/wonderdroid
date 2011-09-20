
package uk.org.cardboardbox.wonderdroid.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HardwareButtonPreference extends Preference {
	private LinearLayout layout;

	public HardwareButtonPreference (Context context) {
		super(context);
	}

	public HardwareButtonPreference (Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HardwareButtonPreference (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView (ViewGroup parent) {
		layout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
		params1.gravity = Gravity.LEFT;
		params1.weight = 1.0f;
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT);
		params2.gravity = Gravity.RIGHT;

		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(30, LinearLayout.LayoutParams.WRAP_CONTENT);
		params3.gravity = Gravity.CENTER;

		layout.setPadding(15, 5, 10, 5);
		layout.setOrientation(LinearLayout.HORIZONTAL);

		TextView view = new TextView(getContext());
		view.setText(getTitle());
		view.setTextSize(18);
		view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		view.setGravity(Gravity.LEFT);
		view.setLayoutParams(params1);

		Button clear = new Button(parent.getContext());
		clear.setText("Clear");
		clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				SharedPreferences.Editor editor = getEditor();
				editor.putInt(getKey(), 0);
				editor.commit();
			}
		});

		Button button = new Button(parent.getContext());
		button.setText("Set button");
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick (View v) {
				KeyCaptureAlert alert = new KeyCaptureAlert(v.getContext());
				alert.setTitle("Press a key");
				alert.show();

			}
		});
		this.layout.addView(view);
		layout.addView(button);
		layout.addView(clear);

		return layout;
	}

	@Override
	public View getView (View convertView, ViewGroup parent) {

		convertView = this.layout == null ? onCreateView(parent) : this.layout;
		return convertView;
	}

	public class KeyCaptureAlert extends AlertDialog {
		public KeyCaptureAlert (Context arg0) {
			super(arg0);
		}

		public boolean onKeyDown (int keyCode, KeyEvent event) {
			Log.d("key capture", ((Integer)keyCode).toString());

			SharedPreferences.Editor editor = getEditor();
			editor.putInt(getKey(), keyCode);
			editor.commit();
			this.dismiss();
			return true;

		}

	}
}
