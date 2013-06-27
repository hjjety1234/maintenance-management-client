package com.wondertek.video.chinanetwifi;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.dkf.wifi.ChinaNetWifi;
import com.dkf.wifi.IChinaNetWifiCallback;
import com.dkf.wifi.ILogger;
import com.dkf.wifi.IMemberOrderPackageCallback;
import com.dkf.wifi.IMemberRegisterCallback;
import com.dkf.wifi.ISmsValidationCodeCallback;
import com.wondertek.video.VenusActivity;
import com.wondertek.video.luatojava.LuaManager;
import com.wondertek.video.luatojava.base.LuaContent;
import com.wondertek.video.luatojava.base.LuaResult;

public class ChinanetWifiObserver extends LuaContent {
	
	private static final String ACTION_SetListen = "setListen";
	private static final String ACTION_GetSmsValidationCode = "getSmsValidationCode";
	private static final String ACTION_ValidateSms = "validateSms";
	private static final String ACTION_IsOrdered = "isOrdered";
	private static final String ACTION_Connect = "connect";
	private static final String ACTION_Disconnect = "disconnect";
	private static final String ACTION_DkfMemberRegister = "dkfMemberRegister";
	private static final String ACTION_DkfMemberOrderPackage = "dkfMemberOrderPackage";
	private static final String ACTION_GetLastMemberId = "getLastMemberId";
	private static final String CLIENT_PASSWORD = "RAEPdA";
	private static ChinanetWifiObserver instance = null;
	public Context context = null;
	private String gmemberId;
	
	private String CBID_ChinaNetWifi = null;
	private String CBID_SmsValidationCode = null;
	private String CBID_dkfMemberRegister = null;
	private String CBID_dkfMemberOrderPackage = null;
	
	public ISmsValidationCodeCallback IsmsCallback = new ISmsValidationCodeCallback() {

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			Log.d("WriteLogs","ISmsValidationCodeCallback onSuccess");
			if(CBID_SmsValidationCode!=null)
			LuaManager.getInstance().nativeAsyncRet(CBID_SmsValidationCode, 
					new LuaResult(LuaResult.Status.OK, "success").getJSONString());
		}

		@Override
		public void onFail(String arg0) {
			// TODO Auto-generated method stub
			Log.d("WriteLogs","ISmsValidationCodeCallback onFail");
			if(CBID_SmsValidationCode!=null)
			LuaManager.getInstance().nativeAsyncRet(CBID_SmsValidationCode, 
					new LuaResult(LuaResult.Status.OK, "fail").getJSONString());
		}
	};

	public IChinaNetWifiCallback IChinaCallback = new IChinaNetWifiCallback() {
		@Override
		public void airplaneModeDetected() {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK, "airplaneModeDetected").getJSONString());
		}

		@Override
		public void chinaNetFound() {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK, "chinaNetFound").getJSONString());
		}

		@Override
		public boolean isAutoScanEnabled() {
			// TODO Auto-generated method stub
			if (isOrdered(gmemberId))
	        {
				if(CBID_ChinaNetWifi!=null)
					LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
							new LuaResult(LuaResult.Status.OK, "autotrue").getJSONString());
				return true;
	        }
	        else
	        {
	        	if(CBID_ChinaNetWifi!=null)
					LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
							new LuaResult(LuaResult.Status.OK, "autofalse").getJSONString());
	            return false;
	        }
		}

		@Override
		public boolean isGoOn() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void onConnectError(int errorCode) {
			// TODO Auto-generated method stub
			if (errorCode == IChinaNetWifiCallback.ERROR_ACCOUNT_OUT || errorCode == IChinaNetWifiCallback.ERROR_NO_ORDER)
	        {
	            // 如果开卡的原因是套餐过期，那么reset订购状态！
	            MemberOrderManager.getInstance().markOrdered(context, gmemberId, false);
	        }
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK, 
								"connecterrormsg|" + ChinaNetWifi.getInstance().codeMessage(errorCode)).getJSONString());
		}

		@Override
		public void onConnectProgress(int percent, String message) {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK,"percent|" + percent + ";" + message).getJSONString());
		}

		@Override
		public void onConnectSuccess() {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK,"connectsuccess").getJSONString());
		}

		@Override
		public void onConnected(long remainMinutes) {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK,"leavingstime|" + remainMinutes).getJSONString());
		}

		@Override
		public void onDisconnectError(String message) {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK,"disconnectError|" + message).getJSONString());
		}

		@Override
		public void onDisconnectSuccess() {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK,"disconnectsuccess").getJSONString());
		}

		@Override
		public void onDisconnectUnexpectedly() {
			// TODO Auto-generated method stub
			if(CBID_ChinaNetWifi!=null)
				LuaManager.getInstance().nativeAsyncRet(CBID_ChinaNetWifi, 
						new LuaResult(LuaResult.Status.OK,"disconnectunexpectedly").getJSONString());
		}
	};

	public IMemberOrderPackageCallback IMeberOrderCallback = new IMemberOrderPackageCallback() {

		@Override
		public void orderedAlready() {
			// TODO Auto-generated method stub
			 MemberOrderManager.getInstance().markOrdered(context, gmemberId, true);
             MemberOrderManager.getInstance().storeLastMemberId(context, gmemberId);
             if(CBID_dkfMemberOrderPackage!=null)
             {
 				LuaManager.getInstance().nativeAsyncRet(CBID_dkfMemberOrderPackage, 
 						new LuaResult(LuaResult.Status.OK,"orderedalready").getJSONString());
             }
		}

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			MemberOrderManager.getInstance().markOrdered(context, gmemberId, true);
            MemberOrderManager.getInstance().storeLastMemberId(context, gmemberId);
			if(CBID_dkfMemberOrderPackage!=null)
            {
				LuaManager.getInstance().nativeAsyncRet(CBID_dkfMemberOrderPackage, 
						new LuaResult(LuaResult.Status.OK,"orderedsuccess").getJSONString());
            }
		}

		@Override
		public void onFail(String message) {
			// TODO Auto-generated method stub
			if(CBID_dkfMemberOrderPackage!=null)
            {
				LuaManager.getInstance().nativeAsyncRet(CBID_dkfMemberOrderPackage, 
						new LuaResult(LuaResult.Status.OK,"orderedfail:" + message).getJSONString());
            }
		}
	};

	public IMemberRegisterCallback IMemberRegisterCallback = new IMemberRegisterCallback()
	{
		@Override
		public void onSuccess()
		{
			MemberOrderManager.getInstance().markRegistered(context, gmemberId, true);
			MemberOrderManager.getInstance().storeLastMemberId(context, gmemberId);
			if(CBID_dkfMemberRegister!=null)
            {
				LuaManager.getInstance().nativeAsyncRet(CBID_dkfMemberRegister, 
						new LuaResult(LuaResult.Status.OK,"registersuccess").getJSONString());
            }
		}

		@Override
		public void onRegisterAlready()
		{
			MemberOrderManager.getInstance().markRegistered(context, gmemberId, true);
			MemberOrderManager.getInstance().storeLastMemberId(context, gmemberId);
			if(CBID_dkfMemberRegister!=null)
            {
				LuaManager.getInstance().nativeAsyncRet(CBID_dkfMemberRegister, 
						new LuaResult(LuaResult.Status.OK,"registeralready").getJSONString());
            }
		}

		@Override
		public void onFail(String message)
		{
			if(CBID_dkfMemberRegister!=null)
            {
				LuaManager.getInstance().nativeAsyncRet(CBID_dkfMemberRegister, 
						new LuaResult(LuaResult.Status.OK,"registerfail:" + message).getJSONString());
            }
		}
	};

	public static ChinanetWifiObserver getInstance()
	{
		if(instance == null)
		{
			instance = new ChinanetWifiObserver();
		}

		return instance;
	}

	/**
	 * Constructor.
	 */
	public ChinanetWifiObserver() {
		instance = this;
		context = VenusActivity.appContext;
		gmemberId = MemberOrderManager.getInstance().getLastMemberId(context);
		onResume();
	}

	@Override
	public LuaResult execute(String action, JSONArray args, String callbackId) {
		// TODO Auto-generated method stub
		LuaResult.Status status = LuaResult.Status.OK;
		String result = "";
		Log.d("WriteLogs", "action =" + action);
		try {
			if (action.equals(ACTION_SetListen)) {
				CBID_ChinaNetWifi = callbackId;
			}else if (action.equals(ACTION_GetSmsValidationCode)) {
				CBID_SmsValidationCode = callbackId;
				getSmsValidationCode(args.getString(0));
				return null;
			}else if(action.equals(ACTION_ValidateSms))
			{
				result = String.valueOf(validateSms(args.getString(0)));
			}else if(action.equals(ACTION_IsOrdered)){
				result = String.valueOf(isOrdered(args.getString(0)));
			}else if(action.equals(ACTION_Connect))
			{
				connect(args.getString(0));
				return null;
			}else if(action.equals(ACTION_Disconnect))
			{
				disconnect();
				return null;
			}else if(action.equals(ACTION_DkfMemberRegister))
			{
				CBID_dkfMemberRegister = callbackId;
				dkfMemberRegister(args.getString(0));
				return null;
			}else if(action.equals(ACTION_DkfMemberOrderPackage))
			{
				CBID_dkfMemberOrderPackage = callbackId;
				dkfMemberOrderPackage(args.getString(0));
				return null;
			}else if(action.equals(ACTION_GetLastMemberId))
			{
				result = getLastMemberId();
			}
			return new LuaResult(status, result);
		} catch (JSONException e) {
			return new LuaResult(LuaResult.Status.JSON_EXCEPTION);
		}
	}

	@Override
	public boolean isSynch(String action) {
		// TODO Auto-generated method stub
		if (action.equals(ACTION_GetSmsValidationCode)) {
			return true;
		}else if(action.equals(ACTION_DkfMemberRegister))
		{
			return true;
		}else if(action.equals(ACTION_DkfMemberOrderPackage))
		{
			return true;
		}
		return false;
	}

	public void setListen()
	{
		
	}
	
	public void getSmsValidationCode(String phonenumber)
	{
		Log.d("WriteLogs","getSmsValidationCode phonenumber =" + phonenumber);
		ChinaNetWifi.getInstance().getSmsValidationCode(phonenumber,IsmsCallback);
	}

	public boolean validateSms(String smsValidationCode)
	{
		Log.d("WriteLogs","validateSms smsValidationCode =" + smsValidationCode);
		return ChinaNetWifi.getInstance().validateSms(smsValidationCode);
	}

	public boolean isOrdered(String memberId)
	{
		Log.d("WriteLogs","isOrdered memberId =" + memberId);
		gmemberId = memberId;
		return MemberOrderManager.getInstance().isOrdered(context, memberId);
	}

	public void connect(String memberId)
	{
		gmemberId = memberId;
		ChinaNetWifi.getInstance().connect(ChinaNetWifi.BUSINESS_TYPE_PACKAGE, CLIENT_PASSWORD, memberId);
	}

	public void disconnect()
	{
		ChinaNetWifi.getInstance().disconnect(CLIENT_PASSWORD);
	}

	public void dkfMemberRegister(String memberId)
	{
		gmemberId = memberId;
		ChinaNetWifi.getInstance().dkf_memberRegister(memberId, "", CLIENT_PASSWORD, IMemberRegisterCallback);
	}

	public void dkfMemberOrderPackage(String memberId)
	{
		gmemberId = memberId;
		ChinaNetWifi.getInstance().dkf_memberOrderPackage(memberId, CLIENT_PASSWORD,IMeberOrderCallback);
	}

	public String getLastMemberId()
	{
		return MemberOrderManager.getInstance().getLastMemberId(context);
	}
	
	protected void onDestroy()
	{
		ChinaNetWifi.getInstance().destroy();
	}

	protected void onPause()
	{
		ChinaNetWifi.getInstance().onPause(context);
	}

	protected void onResume()
	{
		ChinaNetWifi.getInstance().onResume(context, IChinaCallback, CLIENT_PASSWORD, new ILogger()
		{
			@Override
			public void log(String info)
			{
				Log.w("WriteLogs", "ChinanetWifiObserver info =" + info);
			}
		});
	}
}
