package com.wondertek.engine;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;

import com.wondertek.other.FileUtil;
import com.wondertek.other.StringUtil;
import com.wondertek.vo.IDCard;
import com.ym.idcard.reg.NativeOcr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class OcrEngine {
	private static final int BIDC_ADDRESS = 6;
	private static final int BIDC_BIRTHDAY = 5;
	private static final int BIDC_CARDNO = 3;
	private static final int BIDC_FOLK = 11;
	private static final int BIDC_ISSUE_AUTHORITY = 7;
	private static final int BIDC_MEMO = 99;
	private static final int BIDC_NAME = 1;
	private static final int BIDC_NON = 0;
	private static final int BIDC_SEX = 4;
	private static final int BIDC_VALID_PERIOD = 9;
	private static final int MIN_HEIGHT_LIMIT = 720;
	private static final int MIN_WIDTH_LIMIT = 1024;
	private static final int OCR_CODE_B5 = 2;
	private static final int OCR_CODE_GB = 1;
	private static final int OCR_CODE_GB2B5 = 3;
	private static final int OCR_CODE_NIL = 0;
	private static final int OCR_LAN_CENTEURO = 7;
	private static final int OCR_LAN_CHINESE_SIMPLE = 2;
	private static final int OCR_LAN_CHINESE_TRADITIONAL = 21;
	private static final int OCR_LAN_ENGLISH = 1;
	private static final int OCR_LAN_EUROPEAN = 3;
	private static final int OCR_LAN_JAPAN = 6;
	private static final int OCR_LAN_NIL = 0;
	private static final int OCR_LAN_RUSSIAN = 4;
	private static boolean OPT_CANCEL = false;
	public static final int RECOG_BLUR = 3;
	public static final int RECOG_BLUR_TIP = 5;
	public static final int RECOG_CANCEL = -1;
	public static final int RECOG_FAIL = -2;
	public static final int RECOG_LANGUAGE = 4;
	public static final int RECOG_NONE = 0;
	public static final int RECOG_OK = 1;
	public static final int RECOG_SMALL = 2;
	private static final String TAG = "OcrEngine";
	protected boolean mBeCancel = false;
	protected NativeOcr mNativeOcr = new NativeOcr();
	protected long pEngine = 0L;
	protected long pField = 0L;
	protected long pImage = 0L;
	protected long[] ppEngine = new long[20];
	protected long[] ppField = new long[20];
	protected long[] ppImage = new long[20];

	private void closeBCR() {
		if ((this.ppEngine != null) && (this.mNativeOcr != null)) {
			this.mNativeOcr.closeBCR(this.ppEngine);
			this.ppEngine[0] = 0L;
			this.pEngine = 0L;
		}
	}

	public static void doCancel() {
		OPT_CANCEL = true;
	}

	/**
	 * 格式化图片
	 * @return
	 */
	private boolean doImageBCR() {
		Log.d(TAG, ">>>doImageBCR<<<");
		this.mBeCancel = false;
		this.mNativeOcr.setoption(this.pEngine, StringUtil.convertToUnicode("-fmt"), null);
		int i = this.mNativeOcr.doImageBCR(this.pEngine, this.pImage, this.ppField);
		boolean bool = false;
		if (i == 1) {
			this.pField = this.ppField[0];
			bool = true;
		}
		this.mBeCancel = true;
		return bool;
	}

	/**
	 * 填充识别的字段
	 * @param idCard
	 * @param orcCode
	 * @return
	 */
	private boolean fields2Object(IDCard idCard, int orcCode) {
		Log.d(TAG, ">>>fields2Object<<<");
		if (idCard != null) {
			while (true) {
				int fieldId = getFieldId();
				String fieldValue = getFieldText(orcCode);
				Log.i(TAG, "[fields2Object]" + fieldId + "=" + fieldValue);
				switch (fieldId) {
				case 1:
					idCard.setName(fieldValue);
					break;
				case 3:
					idCard.setCardNo(fieldValue);
					break;
				case 4:
					idCard.setSex(fieldValue);
					break;
				case 11:
					idCard.setEthnicity(fieldValue);
					break;
				case 5:
					idCard.setBirth(fieldValue);
					break;
				case 6:
					idCard.setAddress(fieldValue);
					break;
				case 7:
					idCard.setAuthority(fieldValue);
					break;
				case 9:
					idCard.setPeriod(fieldValue);
					break;
				case 99:
					idCard.setMemo(fieldValue);
					break;
				default:
				}
				getNextField();
				if (isFieldEnd())
					return true;
			}
		}
		return false;
	}

	private void freeBFields() {
		if (this.mNativeOcr != null) {
			this.mNativeOcr.freeBField(this.pEngine, this.ppField[0], 0);
			this.ppField[0] = 0L;
			this.pField = 0L;
		}
	}

	private void freeImage() {
		if (this.mNativeOcr != null) {
			this.mNativeOcr.freeImage(this.pEngine, this.ppImage);
			this.ppImage[0] = 0L;
			this.pImage = 0L;
		}
	}

	private int getFieldId() {
		long l = this.pField;
		return this.mNativeOcr.getFieldId(l);
	}

	private Rect getFieldRect() {
		Rect localRect = new Rect();
		long l = this.pField;
		int[] arrayOfInt = new int[4];
		this.mNativeOcr.getFieldRect(l, arrayOfInt);
		localRect.left = arrayOfInt[0];
		localRect.top = arrayOfInt[1];
		localRect.right = arrayOfInt[2];
		localRect.bottom = arrayOfInt[3];
		return localRect;
	}

	/**
	 * 获取当前field的值
	 * @param ocrCode
	 * @return field的值
	 */
	private String getFieldText(int ocrCode) {
		long fieldId = this.pField;
		byte[] fieldValue = new byte[256];
		this.mNativeOcr.getFieldText(fieldId, fieldValue, 256);
		if ((ocrCode == OCR_CODE_GB2B5)) {
			this.mNativeOcr.codeConvert(this.pEngine, fieldValue, ocrCode);
			return StringUtil.convertBig5ToUnicode(fieldValue);
		}
		return StringUtil.convertGbkToUnicode(fieldValue);
	}

	private void getNextField() {
		if (!isFieldEnd())
			this.pField = this.mNativeOcr.getNextField(this.pField);
	}

	private boolean isBlurImage() {
		NativeOcr localNativeOcr = this.mNativeOcr;
		boolean bool = false;
		if (localNativeOcr != null) {
			int i = this.mNativeOcr.imageChecking(this.pEngine, this.pImage, 0);
			bool = false;
			if (i == 2)
				bool = true;
		}
		return bool;
	}

	private boolean isCancel() {
		return this.mBeCancel;
	}

	private boolean isFieldEnd() {
		return this.pField == 0L;
	}

	/**
	 * 加载图片至内存
	 * @param dataEx
	 * @param width
	 * @param height
	 * @param component
	 * @return
	 */
	private boolean loadImageMem(long dataEx, int width, int height, int component) {
		boolean result = false;
		if (dataEx >= 0) {
			this.pImage = this.mNativeOcr.loadImageMem(this.pEngine, dataEx, width, height, component);
			result = false;
			if (this.pImage >= 0) {
				this.ppImage[0] = this.pImage;
				result = true;
			}
		}
		return result;
	}
	
	public IDCard recognize(Context context, String imagePath) throws IOException {
		return recognize(context, OCR_LAN_CHINESE_SIMPLE, imagePath);
	}

	public IDCard recognize(Context context, byte[] imageDataA) {
		return recognize(context, OCR_LAN_CHINESE_SIMPLE, imageDataA, null);
	}

	public IDCard recognize(Context context, byte[] imageDataA, byte[] imageDataB) {
		return recognize(context, OCR_LAN_CHINESE_SIMPLE, imageDataA, imageDataB);
	}

	private IDCard recognize(Context context, int ocrLang, String imagePath) throws IOException {
		return recognize(context, ocrLang, FileUtil.getBytesFromFile(new File(imagePath)), null);
	}


	/**
	 * 身份证识别入口函数
	 * @param context 当前的context
	 * @param ocrLang 当前的语言
	 * @param imageDataA 身份证正面数据
	 * @param imageDataB 身份证背面数据
	 * @return
	 */
	private IDCard recognize(Context context, int ocrLang, byte[] imageDataA, byte[] imageDataB){
		Log.d(TAG, ">>>recognize<<<");
		IDCard localIDCard1 = new IDCard();
		int ocrCode = OCR_CODE_GB;
		if (ocrLang == OCR_LAN_CHINESE_TRADITIONAL) ocrCode = OCR_CODE_GB2B5;
		OPT_CANCEL = false;
	    byte[] licenseData = new byte[256];
	    AssetManager localAssetManager = context.getAssets();
	    try{
		    InputStream localInputStream = localAssetManager.open("license.info");
		    localInputStream.read(licenseData);
		    localInputStream.close();
		    if (startBCR("", "", ocrLang, licenseData)){
				if (imageDataA != null)
					localIDCard1 = recognizing(imageDataA, false, ocrCode);
				if (imageDataB != null) {
					IDCard localIDCard2 = recognizing(imageDataB, false, ocrCode);
					localIDCard1.setAuthority(localIDCard2.getAuthority());
					localIDCard1.setPeriod(localIDCard2.getPeriod());
				}
				closeBCR();
		    }
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    }
		return localIDCard1;
	}

	/**
	 * 身份证识别实现函数
	 * @param imageData
	 * @param paramBoolean
	 * @param ocrCode
	 * @return
	 */
	private IDCard recognizing(byte[] imageData, boolean paramBoolean,
			int ocrCode) {
		Log.d(TAG, ">>>recognizing<<<");
		IDCard localIDCard = new IDCard();
		ImageEngine localImageEngine = new ImageEngine();
		int i;
		int j;
		int k;
		if ((localImageEngine.init(1, 90)) && (localImageEngine.load(imageData))) {
			i = localImageEngine.getWidth();
			j = localImageEngine.getHeight();
			k = localImageEngine.getComponent();
			if ((Build.MODEL.contains("SO-01"))
					|| (Build.MODEL.contains("X10"))
					|| (Build.MODEL.contains("E10"))
					|| ((i >= 1024) && (j >= 720))) {
				boolean bool = loadImageMem(localImageEngine.getDataEx(), i, j, k);
				if (bool == false) {
					Log.e(TAG, "loadImageMem failed");
					localImageEngine.finalize();
					return null;
				}
				setProgressFunc(true);
				if ((paramBoolean) && (isBlurImage()) && (!OPT_CANCEL)) {
					Log.e(TAG, "isBlurImage");
					localIDCard.setRecogStatus(3);
					return localIDCard;
				}
				if (doImageBCR()) {
					Log.d(TAG, "doImageBCR return true");
					if (fields2Object(localIDCard, ocrCode))
						localIDCard.setRecogStatus(1);
					freeBFields();
					freeImage();
					return localIDCard;
				}
			}
			localIDCard.setRecogStatus(2);
		}
		return localIDCard;
	}

	private void setProgressFunc(boolean paramBoolean) {
		if ((this.pEngine != 0L) && (this.mNativeOcr != null))
			this.mNativeOcr.setProgressFunc(this.pEngine, paramBoolean);
	}

	private boolean startBCR(String paramString1, String paramString2,
			int paramInt, byte[] paramArrayOfByte) {
		int i = this.mNativeOcr.startBCR(this.ppEngine,
				StringUtil.convertUnicodeToAscii(paramString2),
				StringUtil.convertUnicodeToAscii(paramString1), paramInt,
				paramArrayOfByte);
		boolean bool = false;
		if (i == 1) {
			this.pEngine = this.ppEngine[0];
			bool = true;
		}
		return bool;
	}

	public void finalize() {
		this.ppEngine = null;
		this.ppImage = null;
		this.ppField = null;
		this.mNativeOcr = null;
		this.pEngine = 0L;
		this.pImage = 0L;
	}
}