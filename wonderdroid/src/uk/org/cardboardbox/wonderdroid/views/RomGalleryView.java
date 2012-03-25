
package uk.org.cardboardbox.wonderdroid.views;

import uk.org.cardboardbox.wonderdroid.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RomGalleryView extends LinearLayout {

	LinearLayout mLayout;

	public RomGalleryView (Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = (LinearLayout)((LinearLayout)layoutInflater.inflate(R.layout.romgalleyview, this)).getChildAt(0);
	}

	public void setSnap (Bitmap bm) {
		ImageView iv = (ImageView)mLayout.getChildAt(1);
		iv.setImageBitmap(bm);
	}

	public void setTitle (String title) {
		((TextView)mLayout.getChildAt(0)).setText(title);
	}
}
