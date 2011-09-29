
package uk.org.cardboardbox.wonderdroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class WonderSwan {

	private static final String TAG = WonderSwan.class.getSimpleName();

	static public final int SCREEN_WIDTH = 224;
	static public final int SCREEN_HEIGHT = 144;
	static public final int FRAMEBUFFERSIZE = (SCREEN_WIDTH * SCREEN_HEIGHT) * 2;
	static public int samples;
	static final int audiobufferlen = 2000;
	static public short[] audiobuffer = new short[audiobufferlen];

	public static enum WonderSwanButton {
		Y1, Y4, Y2, Y3, X3, X4, X2, X1, A, B, START; // FIXME the is screen rendering order
		public boolean hardwareKeyDown = false;
		public boolean down = false;
		public int keyCode = 0;
	};

	public static boolean buttonsDirty = false;

	public static final int channelconf = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
	public static final int encoding = AudioFormat.ENCODING_PCM_16BIT;
	public static final int audiofreq = 22050;

	public WonderSwan () {
		throw new UnsupportedOperationException();

	}

	static {
		System.loadLibrary("wonderswan");
	}

	static public native void load (String rompath, boolean wsc);

	static public native void reset ();

	static public void execute_frame (ShortBuffer framebuffer, boolean skipframe) {
		if (buttonsDirty) {
			WonderSwan.updatebuttons(WonderSwanButton.Y1.down, WonderSwanButton.Y2.down, WonderSwanButton.Y3.down,
				WonderSwanButton.Y4.down, WonderSwanButton.X1.down, WonderSwanButton.X2.down, WonderSwanButton.X3.down,
				WonderSwanButton.X4.down, WonderSwanButton.A.down, WonderSwanButton.B.down, WonderSwanButton.START.down);
			buttonsDirty = false;
		}

		samples = _execute_frame(skipframe, framebuffer, audiobuffer);
		synchronized (audiobuffer) {
			audiobuffer.notify();
		}
	}

	static private native int _execute_frame (boolean skipframe, ShortBuffer framebuffer, short[] audiobuffer);

	static public native void updatebuttons (boolean y1, boolean y2, boolean y3, boolean y4, boolean x1, boolean x2, boolean x3,
		boolean x4, boolean a, boolean b, boolean start);

	static public void outputDebugShizzle () {
		Log.d(TAG, "Audio buffer min " + AudioTrack.getMinBufferSize(audiofreq, channelconf, encoding));
	}

	public static class Header {
		private int developer = 0;
		private int cartid = 0;
		private int checksum = 0;
		public boolean isColor = false;
		public boolean isVertical = false;

		public String getInternalName () {
			return ((Integer)developer).toString() + "-" + ((Integer)cartid).toString() + "-" + ((Integer)checksum).toString();
		}

		public Header (File rom) {

			byte header[] = new byte[10];
			FileInputStream fis;
			try {
				fis = new FileInputStream(rom);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

			FileChannel fc = fis.getChannel();
			try {
				fc.read(ByteBuffer.wrap(header), fc.size() - 10);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

			developer = (header[0] & 0xFF);
			cartid = (header[2] & 0xFF);

			if (header[1] == 1) {
				isColor = true;
			}

			if ((header[6] & 0x01) == 1) {
				isVertical = true;
			}

			checksum = (header[8] & 0xFF) + ((header[9] << 8) & 0xFFFF);
		}
	}

	public static native void storebackupdata (String filename);

	public static native void loadbackupdata (String filename);
}
