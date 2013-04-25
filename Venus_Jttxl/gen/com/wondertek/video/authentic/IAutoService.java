/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\project\\Venus_Jttxl\\src\\com\\wondertek\\video\\authentic\\IAutoService.aidl
 */
package com.wondertek.video.authentic;
public interface IAutoService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.wondertek.video.authentic.IAutoService
{
private static final java.lang.String DESCRIPTOR = "com.wondertek.video.authentic.IAutoService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.wondertek.video.authentic.IAutoService interface,
 * generating a proxy if needed.
 */
public static com.wondertek.video.authentic.IAutoService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.wondertek.video.authentic.IAutoService))) {
return ((com.wondertek.video.authentic.IAutoService)iin);
}
return new com.wondertek.video.authentic.IAutoService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getApps:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getApps();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCertInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getCertInfo(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_isStarted:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isStarted();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setServerAddr:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.setServerAddr(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_start:
{
data.enforceInterface(DESCRIPTOR);
this.start();
reply.writeNoException();
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_upgrade:
{
data.enforceInterface(DESCRIPTOR);
this.upgrade();
reply.writeNoException();
return true;
}
case TRANSACTION_getAppStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getAppStatus(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setPin:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setPin(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setCheckAppInterval:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setCheckAppInterval(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDisplayToast:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setDisplayToast(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.wondertek.video.authentic.IAutoService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/*
	鑾峰彇搴旂敤
	杩斿洖鍊硷細
		搴旂敤鍚嶇О1=鐩戝惉鍦板潃:鐩戝惉绔彛
		搴旂敤鍚嶇О2=鐩戝惉鍦板潃:鐩戝惉绔彛
		...
		搴旂敤鍚嶇Оn=鐩戝惉鍦板潃:鐩戝惉绔彛
	*/
@Override public java.lang.String getApps() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getApps, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	鑾峰彇璇佷功椤逛俊鎭紙鎴愬姛鍚姩鏈嶅姟鍚庢墠鑳借幏鍙栵級
	[IN]opt: 1=SN锛�=CN锛�=DN
		DN涓殑瀵瑰簲椤癸細
			"CN"	濮撳悕
			"T" 	TF鍗℃爣璇嗗彿
			"G"		璀﹀彿
			"ALIAS"	韬唤璇佸彿鐮�
			"S"		鐪�
			"L"		甯�
			"O"		缁勭粐
			"OU"	鏈烘瀯
			"E"		鐢靛瓙閭欢
			"I"		瀹瑰櫒鍚嶇О
	杩斿洖鍊硷細鎸囧畾椤逛俊鎭�
	*/
@Override public java.lang.String getCertInfo(int opt) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(opt);
mRemote.transact(Stub.TRANSACTION_getCertInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	鏈嶅姟鏄惁宸插惎鍔�
	杩斿洖鍊硷細true=宸插惎鍔紝false=鏈惎鍔�
	*/
@Override public boolean isStarted() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isStarted, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	璁剧疆鏈嶅姟鍣ㄥ湴鍧�
	[IN]ip锛�鏈嶅姟鍣ㄥ湴鍧�
	[IN]port锛氭湇鍔″櫒绔彛
	*/
@Override public void setServerAddr(java.lang.String ip, java.lang.String port) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ip);
_data.writeString(port);
mRemote.transact(Stub.TRANSACTION_setServerAddr, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	鍚姩鏈嶅姟
	*/
@Override public void start() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_start, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	鍋滄鏈嶅姟
	*/
@Override public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	鑷姩鍗囩骇
	*/
@Override public void upgrade() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_upgrade, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	鑾峰彇搴旂敤鏈嶅姟鐘舵�
	[IN]appName锛�搴旂敤鏈嶅姟鍚嶇О锛堝嵆鏈湴IP锛屼緥濡傦細127.0.0.1:10001锛�
	杩斿洖鍊硷細0 = 鏈摼鎺ワ紝1=宸查摼鎺�
	*/
@Override public int getAppStatus(java.lang.String appName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(appName);
mRemote.transact(Stub.TRANSACTION_getAppStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*
	璁剧疆PIN鐮�
	[IN]pin锛�pin鐮�
	*/
@Override public void setPin(java.lang.String pin) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pin);
mRemote.transact(Stub.TRANSACTION_setPin, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	璁剧疆鏈嶅姟妫�煡闂撮殧锛堢锛�
	[IN]seconds锛�闂撮殧鏃堕棿(绉�锛�鍒欎笉妫�煡杩滅鏈嶅姟
	*/
@Override public void setCheckAppInterval(int seconds) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(seconds);
mRemote.transact(Stub.TRANSACTION_setCheckAppInterval, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*
	璁剧疆鏄剧ずtoast娑堟伅
	[IN]display锛�true=鏄剧ず锛沠alse=涓嶆樉绀�
	*/
@Override public void setDisplayToast(boolean display) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((display)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setDisplayToast, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getApps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getCertInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isStarted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setServerAddr = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_start = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_upgrade = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getAppStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_setPin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_setCheckAppInterval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_setDisplayToast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
/*
	鑾峰彇搴旂敤
	杩斿洖鍊硷細
		搴旂敤鍚嶇О1=鐩戝惉鍦板潃:鐩戝惉绔彛
		搴旂敤鍚嶇О2=鐩戝惉鍦板潃:鐩戝惉绔彛
		...
		搴旂敤鍚嶇Оn=鐩戝惉鍦板潃:鐩戝惉绔彛
	*/
public java.lang.String getApps() throws android.os.RemoteException;
/*
	鑾峰彇璇佷功椤逛俊鎭紙鎴愬姛鍚姩鏈嶅姟鍚庢墠鑳借幏鍙栵級
	[IN]opt: 1=SN锛�=CN锛�=DN
		DN涓殑瀵瑰簲椤癸細
			"CN"	濮撳悕
			"T" 	TF鍗℃爣璇嗗彿
			"G"		璀﹀彿
			"ALIAS"	韬唤璇佸彿鐮�
			"S"		鐪�
			"L"		甯�
			"O"		缁勭粐
			"OU"	鏈烘瀯
			"E"		鐢靛瓙閭欢
			"I"		瀹瑰櫒鍚嶇О
	杩斿洖鍊硷細鎸囧畾椤逛俊鎭�
	*/
public java.lang.String getCertInfo(int opt) throws android.os.RemoteException;
/*
	鏈嶅姟鏄惁宸插惎鍔�
	杩斿洖鍊硷細true=宸插惎鍔紝false=鏈惎鍔�
	*/
public boolean isStarted() throws android.os.RemoteException;
/*
	璁剧疆鏈嶅姟鍣ㄥ湴鍧�
	[IN]ip锛�鏈嶅姟鍣ㄥ湴鍧�
	[IN]port锛氭湇鍔″櫒绔彛
	*/
public void setServerAddr(java.lang.String ip, java.lang.String port) throws android.os.RemoteException;
/*
	鍚姩鏈嶅姟
	*/
public void start() throws android.os.RemoteException;
/*
	鍋滄鏈嶅姟
	*/
public void stop() throws android.os.RemoteException;
/*
	鑷姩鍗囩骇
	*/
public void upgrade() throws android.os.RemoteException;
/*
	鑾峰彇搴旂敤鏈嶅姟鐘舵�
	[IN]appName锛�搴旂敤鏈嶅姟鍚嶇О锛堝嵆鏈湴IP锛屼緥濡傦細127.0.0.1:10001锛�
	杩斿洖鍊硷細0 = 鏈摼鎺ワ紝1=宸查摼鎺�
	*/
public int getAppStatus(java.lang.String appName) throws android.os.RemoteException;
/*
	璁剧疆PIN鐮�
	[IN]pin锛�pin鐮�
	*/
public void setPin(java.lang.String pin) throws android.os.RemoteException;
/*
	璁剧疆鏈嶅姟妫�煡闂撮殧锛堢锛�
	[IN]seconds锛�闂撮殧鏃堕棿(绉�锛�鍒欎笉妫�煡杩滅鏈嶅姟
	*/
public void setCheckAppInterval(int seconds) throws android.os.RemoteException;
/*
	璁剧疆鏄剧ずtoast娑堟伅
	[IN]display锛�true=鏄剧ず锛沠alse=涓嶆樉绀�
	*/
public void setDisplayToast(boolean display) throws android.os.RemoteException;
}
