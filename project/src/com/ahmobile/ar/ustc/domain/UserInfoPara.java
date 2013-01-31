package com.ahmobile.ar.ustc.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ClassName:ParcelableObject 需要实现三个方法，describeContents()返回0就可以 、
 * writeToParcel(Parcel parcel, int flag) 写入Parcel和public static final
 * Parcelable.Creator<parcelableobject> CREATOR
 * 供外部类反序列化该类 一定要注意顺序后面这个两个函数读和写的顺序要相同，不然数据会出错
 * 
 * @author liuzhaogang
 * 
 */
public class UserInfoPara implements Parcelable {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name;

	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}

	public static final Parcelable.Creator<UserInfoPara> CREATOR = new Creator<UserInfoPara>() {

		@Override
		public UserInfoPara createFromParcel(Parcel source) {
			UserInfoPara userInfo = new UserInfoPara();
			userInfo.id = source.readString();
			userInfo.name = source.readString();
			return userInfo;
		}

		@Override
		public UserInfoPara[] newArray(int size) {
			
			return new UserInfoPara[size];
		}

	};
}
