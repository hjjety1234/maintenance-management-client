package com.wondertek.video.appmanager.weixinopen;

import java.io.ByteArrayOutputStream;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

/**
 * WeiXin OpenApi
 * @author yuhongwei
 */

public class IWXManager {
	private static final String TAG = "IWXManager";
	private static final String APP_ID = "wxba93d1e964e1b509";
	private static IWXManager instance = null;
	private Context mContext;
	private IWXAPI iWXApi;
	
	private IWXManager(Context cxt) {
		mContext = cxt;
		iWXApi = WXAPIFactory.createWXAPI(mContext, APP_ID, true);
		iWXApi.registerApp(APP_ID);
	}
	
	public static IWXManager getInstance(Context cxt) {
		if (instance == null) {
			instance = new IWXManager(cxt);
		}
		return instance;
	}
	
	public void shareText(String text, boolean isFriendCircle) {
		WXTextObject txtObj = new WXTextObject();
		txtObj.text = text;
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = txtObj;
		msg.description = text;
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text");
		req.message = msg;
		req.scene = isFriendCircle ? SendMessageToWX.Req.WXSceneTimeline : 
			SendMessageToWX.Req.WXSceneSession;
		iWXApi.sendReq(req);
	}
	
	public void shareImage(String path, boolean isFriendCircle) {
		WXImageObject image = new WXImageObject();
		image.setImagePath(path);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = image;
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
		bitmap.recycle();
		msg.thumbData = bmpToByteArray(thumbBitmap, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("image");
		req.message = msg;
		req.scene = isFriendCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		
		iWXApi.sendReq(req);
	}
	
	public void shareWebPage(String url, String title, String imagePath, String desc, boolean isFriendCircle) {
		Log.d(TAG, "[shareNews]  url: " + url + " title: " + title + " imagePath: " + imagePath + " desc: " + desc + " isFriendCircle: "
				+ isFriendCircle);
		WXWebpageObject web = new WXWebpageObject();
		web.webpageUrl = url;
		WXMediaMessage msg = new WXMediaMessage(web);
		msg.title = title;
		msg.description = desc;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		Bitmap thumBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
		bitmap.recycle();
		msg.thumbData = bmpToByteArray(thumBitmap, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = isFriendCircle ? SendMessageToWX.Req.WXSceneTimeline : 
			SendMessageToWX.Req.WXSceneSession;
		iWXApi.sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
