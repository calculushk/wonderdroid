
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

	public static class HardwareButton {
		public final Buttons button;
		public final String label;

		public HardwareButton (Buttons button, String label) {
			this.button = button;
			this.label = label;
		}
	}

	public static enum Buttons {
		START, A, B, X1, X2, X3, X4, Y1, Y2, Y3, Y4;
	};

	public static HardwareButton[] buttons;

	public static boolean mButtonStart = false;
	public static boolean mButtonA = false;
	public static boolean mButtonB = false;
	public static boolean mButtonX1 = false;
	public static boolean mButtonX2 = false;
	public static boolean mButtonX3 = false;
	public static boolean mButtonX4 = false;
	public static boolean mButtonY1 = false;
	public static boolean mButtonY2 = false;
	public static boolean mButtonY3 = false;
	public static boolean mButtonY4 = false;
	public static boolean buttonsDirty = false;

	public static final int channelconf = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
	public static final int encoding = AudioFormat.ENCODING_PCM_16BIT;
	public static final int audiofreq = 22050;

	public WonderSwan () {
		throw new UnsupportedOperationException();

	}

	static {
		String[] buttonStrings = new String[] {"Y1", "Y4", "Y2", "Y3", "X3", "X4", "X2", "X1", "A", "B", "START"};

		buttons = new HardwareButton[buttonStrings.length];

		buttons[0] = new HardwareButton(Buttons.Y1, buttonStrings[0]);
		buttons[1] = new HardwareButton(Buttons.Y4, buttonStrings[1]);
		buttons[2] = new HardwareButton(Buttons.Y2, buttonStrings[2]);
		buttons[3] = new HardwareButton(Buttons.Y3, buttonStrings[3]);
		
		buttons[4] = new HardwareButton(Buttons.X3, buttonStrings[4]);
		buttons[5] = new HardwareButton(Buttons.X4, buttonStrings[5]);
		buttons[6] = new HardwareButton(Buttons.X2, buttonStrings[6]);
		buttons[7] = new HardwareButton(Buttons.X1, buttonStrings[7]);
	
		buttons[8] = new HardwareButton(Buttons.A, buttonStrings[8]);
		buttons[9] = new HardwareButton(Buttons.B, buttonStrings[9]);
		buttons[10] = new HardwareButton(Buttons.START, buttonStrings[10]);
		
		System.loadLibrary("wonderswan");
	}

	static public native void load (String rompath, boolean wsc);

	static public native void reset ();

	static public void execute_frame (ShortBuffer framebuffer, boolean skipframe) {
		if (buttonsDirty) {
			WonderSwan.updatebuttons(mButtonY1, mButtonY2, mButtonY3, mButtonY4, mButtonX1, mButtonX2, mButtonX3, mButtonX4,
				mButtonA, mButtonB, mButtonStart);
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
