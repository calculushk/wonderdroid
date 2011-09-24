
package uk.org.cardboardbox.wonderdroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import uk.org.cardboardbox.wonderdroid.utils.EmuThread;

public class WonderSwanRenderer implements EmuThread.Renderer {

	private final Bitmap framebuffer;

	private final Matrix scale = new Matrix();
	private final Paint paint = new Paint();
	private final Paint textPaint = new Paint();

	public WonderSwanRenderer () {
		textPaint.setColor(0xFFFFFFFF);
		textPaint.setTextSize(35);
		textPaint.setShadowLayer(3, 1, 1, 0x99000000);
		textPaint.setAntiAlias(true);
		framebuffer = Bitmap.createBitmap(WonderSwan.SCREEN_WIDTH, WonderSwan.SCREEN_HEIGHT, Bitmap.Config.RGB_565);
	}

	@Override
	public void render (Canvas c, boolean frameskip, boolean showFps, String fpsString) {

		c.drawARGB(0xff, 0, 0, 0);
		c.drawBitmap(framebuffer, scale, paint);

		// if (showFps) {
		c.drawText(fpsString, 30, 50, textPaint);
		// }

	}

	public Matrix getMatrix () {
		return scale;
	}

	public Paint getPaint () {
		return paint;
	}

	@Override
	public void update (boolean skip) {
		WonderSwan.execute_frame(skip);
		if (!skip) {
			framebuffer.copyPixelsFromBuffer(WonderSwan.framebuffer);
		}
	}

}
