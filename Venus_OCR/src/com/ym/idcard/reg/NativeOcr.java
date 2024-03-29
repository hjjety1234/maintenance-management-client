package com.ym.idcard.reg;

import android.util.Log;

public class NativeOcr {
	private static final String LIB = "IDCardengine";
	private static final String TAG = "NativeOcr";
	private static boolean mCancel;
	private static int mProgress;

	static {
		try {
			System.loadLibrary("IDCardengine");
			mProgress = 0;
			mCancel = false;
		} catch (Exception localException) {
			Log.e("ocrengine", "", localException);
		}
	}

	public static int Progress(int paramInt1, int paramInt2) {
		if (paramInt2 != 0)
			;
		for (mProgress = paramInt1 + mProgress;; mProgress = paramInt1)
			return mProgress;
	}

	public static int getProgress() {
		return mProgress;
	}

	public native int closeBCR(long[] paramArrayOfLong);

	public native int codeConvert(long paramLong, byte[] paramArrayOfByte, int paramInt);

	public native int doImageBCR(long paramLong1, long paramLong2, long[] paramArrayOfLong);

	public void finalize() {}

	public native void freeBField(long paramLong1, long paramLong2, int paramInt);

	public native int freeImage(long paramLong, long[] paramArrayOfLong);

	public native int getFieldId(long paramLong);

	public native int getFieldRect(long paramLong, int[] paramArrayOfInt);

	public native int getFieldText(long paramLong, byte[] paramArrayOfByte,
			int paramInt);

	public native long getNextField(long paramLong);

	public native int imageChecking(long paramLong1, long paramLong2,
			int paramInt);

	public native long loadImageMem(long paramLong1, long paramLong2,
			int paramInt1, int paramInt2, int paramInt3);

	public native void setProgressFunc(long paramLong, boolean paramBoolean);

	public native int setoption(long paramLong, byte[] paramArrayOfByte1,
			byte[] paramArrayOfByte2);

	public native int startBCR(long[] paramArrayOfLong,
			byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt,
			byte[] paramArrayOfByte3);
}