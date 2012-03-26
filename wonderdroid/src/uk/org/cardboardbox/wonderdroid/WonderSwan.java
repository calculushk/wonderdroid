
package uk.org.cardboardbox.wonderdroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

import uk.org.cardboardbox.wonderdroid.utils.CpuUtils;

import android.media.AudioFormat;
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
		if (CpuUtils.getArch() == CpuUtils.Arch.ARMv7 && CpuUtils.hasNeon()) {
			System.loadLibrary("wonderswan-neon");
		} else {
			System.loadLibrary("wonderswan");
		}
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

	public static class Header implements Serializable {
		/**
		 * 
		 */

		private static final long serialVersionUID = 1L;
		public static final int HEADERLEN = 10;
		private final int developer;
		private final int cartid;
		private final int checksum;
		private final int romsize;
		public final boolean isColor;
		public final boolean isVertical;
		public final String internalname;

		private static byte[] getHeaderFromFile (File rom) {
			byte header[] = new byte[HEADERLEN];
			try {
				FileInputStream fis;
				fis = new FileInputStream(rom);
				FileChannel fc = fis.getChannel();
				fc.read(ByteBuffer.wrap(header), fc.size() - HEADERLEN);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
			return header;
		}

		public Header (File rom) {
			this(getHeaderFromFile(rom));
		}

		public Header (byte[] header) {
			if (header == null || header.length != HEADERLEN) {
				throw new IllegalArgumentException("Header must be " + HEADERLEN + " bytes");
			}

			developer = (header[0] & 0xFF);
			isColor = (header[1] == 1);
			cartid = (header[2] & 0xFF);
			switch (header[4]) {
			default:
				romsize = 0;
			}
			isVertical = ((header[6] & 0x01) == 1);
			checksum = (header[8] & 0xFF) + ((header[9] << 8) & 0xFFFF);
			internalname = ((Integer)developer).toString() + "-" + ((Integer)cartid).toString() + "-"
				+ ((Integer)checksum).toString();

		}

	}

	public static native void storebackupdata (String filename);

	public static native void loadbackupdata (String filename);
}
