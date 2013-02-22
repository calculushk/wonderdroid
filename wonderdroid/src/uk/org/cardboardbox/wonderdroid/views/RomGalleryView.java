
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

    private final LinearLayout mLayout;

    private final ImageView iv;

    private final TextView title;

    public RomGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (LinearLayout)((LinearLayout)layoutInflater.inflate(R.layout.romgalleyview, this))
                .getChildAt(0);
        iv = (ImageView)mLayout.getChildAt(1);
        title = ((TextView)mLayout.getChildAt(0));
    }

    public void setSnap(Bitmap bm) {
        iv.setImageBitmap(bm);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

}
