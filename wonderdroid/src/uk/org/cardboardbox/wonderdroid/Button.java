
package uk.org.cardboardbox.wonderdroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Button {

	Rect rect;
	Bitmap normal, pressed;

	public Button (Drawable base, Paint textPaint, String text) {

		rect = base.getBounds();

		normal = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(normal);

		float textLen = (textPaint.measureText(text)) / 2;
		base.draw(canvas);
		canvas.drawText(text, (rect.left + rect.width() / 2) - textLen, (rect.top + rect.height() / 2)
			+ (textPaint.getTextSize() / 2), textPaint);

		canvas.setBitmap(normal);

		textLen = (textPaint.measureText(text)) / 2;
		base.draw(canvas);
		canvas.drawText(text, (rect.left + rect.width() / 2) - textLen, (rect.top + rect.height() / 2)
			+ (textPaint.getTextSize() / 2), textPaint);
	}

}
