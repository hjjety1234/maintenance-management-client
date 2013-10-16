package com.wondertek.other;

import java.io.UnsupportedEncodingException;

public class StringUtil {
	public static String convertAscIIToUnicode(byte[] paramArrayOfByte) {
		// String str1;
		// try
		// {
		// byte[] arrayOfByte = filterAndCut(paramArrayOfByte);
		// String str2;
		// if (arrayOfByte != null)
		// str2 = new String(arrayOfByte, "ISO-8859-1");
		// for (str1 = str2; ; str1 = "")
		// return str1.trim();
		// }
		// catch (UnsupportedEncodingException
		// localUnsupportedEncodingException)
		// {
		// return null;
		// }
		return null;
	}

	public static String convertBig5ToUnicode(byte[] paramArrayOfByte) {
		// try
		// {
		// byte[] arrayOfByte = filterAndCut(paramArrayOfByte);
		// String str2;
		// if (arrayOfByte != null)
		// str2 = new String(arrayOfByte, "big5");
		// for (str1 = str2; ; str1 = "")
		// return str1.trim();
		// }
		// catch (UnsupportedEncodingException
		// localUnsupportedEncodingException)
		// {
		// while (true)
		// String str1 = null;
		// }
		return null;
	}

	public static String convertGbkToUnicode(byte[] paramArrayOfByte) {
		String str = "";
		try {
			str = new String(paramArrayOfByte, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
//		StringBuffer result = new StringBuffer();
//		for (int i = 0; i < str.length(); i++) {
//			char chr1 = (char) str.charAt(i);
//			if (!isNeedConvert(chr1)) {
//				result.append(chr1);
//				continue;
//			}
//			result.append("\\u" + Integer.toHexString((int) chr1));
//		}
		return str;
	}
	
	public static boolean isNeedConvert(char para) {
        return ((para & (0x00FF)) != para);
    }


	public static byte[] convertToUnicode(String paramString) {
		try {
			byte[] arrayOfByte1 = paramString.getBytes("utf-8");
			int i = arrayOfByte1.length;
			byte[] arrayOfByte2 = new byte[i + 1];
			for (int j = 0; j < i; j++) {
				if (j >= i) {
					arrayOfByte2[i] = 0;
				}
				arrayOfByte2[j] = arrayOfByte1[j];
			}
			return arrayOfByte2;
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		return (byte[]) null;
	}

	public static byte[] convertUnicodeToAscii(String paramString) {
		try {
			int i = paramString.length();
			byte[] arrayOfByte1 = paramString.getBytes("US-ASCII");
			byte[] arrayOfByte2 = new byte[i + 1];
			for (int j = 0;; j++) {
				if (j >= i) {
					arrayOfByte2[i] = 0;
					return arrayOfByte2;
				}
				arrayOfByte2[j] = arrayOfByte1[j];
			}
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		return  null;
	}

	public static byte[] convertUnicodeToGbk(String paramString) {
		try {
			byte[] arrayOfByte1 = paramString.getBytes("GBK");
			int i = arrayOfByte1.length;
			byte[] arrayOfByte2 = new byte[i + 1];
			for (int j = 0;; j++) {
				if (j >= i) {
					arrayOfByte2[i] = 0;
					return arrayOfByte2;
				}
				arrayOfByte2[j] = arrayOfByte1[j];
			}
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		return (byte[]) null;
	}

	public static byte[] filter(byte[] paramArrayOfByte) {
		int i = paramArrayOfByte.length;
		byte[] arrayOfByte = new byte[i];
		int j = 0;
		int k = 0;
		if (j >= i)
			return arrayOfByte;
		int m = 0;
		if (paramArrayOfByte[j] == 13)
			m = k;
		while (true) {
			j++;
			k = m;
			m = k + 1;
			arrayOfByte[k] = paramArrayOfByte[j];
			break;
		}
		return arrayOfByte;
	}

	public static byte[] filterAndCut(byte[] paramArrayOfByte) {
//		 int i = strlen(paramArrayOfByte);
//		 byte[] arrayOfByte = new byte[i];
//		 if (i < 1) {
//			 arrayOfByte = null;
//			 return arrayOfByte;
//		 }
//		 int j = 0;
//		 int k = 0;
//		 int m;
//		 if (j < i) {
//			 if (paramArrayOfByte[j] != 13)
//			 break label50;
//			 m = k;
//		 }
//		 while (true)
//		 {
//			 j++;
//			 k = m;
//			 break label23;
//			 label50: m = k + 1;
//			 arrayOfByte[k] = paramArrayOfByte[j];
//		 }
		return null;
	}

	public static int strlen(byte[] paramArrayOfByte) {
		// int i = -1;
		// if (paramArrayOfByte != null)
		// {
		// if (paramArrayOfByte.length != 0)
		// break label15;
		// i = 0;
		// }
		// while (true)
		// {
		// return i;
		// label15: for (int j = 0; j < paramArrayOfByte.length; j++)
		// if (paramArrayOfByte[j] == 0)
		// return j;
		// }
		return 0;
	}

	public void finalize() {
	}
}