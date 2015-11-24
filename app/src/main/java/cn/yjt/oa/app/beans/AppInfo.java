package cn.yjt.oa.app.beans;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.app.activity.AdditionalIcon;
import cn.yjt.oa.app.app.activity.AdditionalPackage;

public class AppInfo implements Parcelable,Comparable<AppInfo>,AdditionalIcon{

	private String name;
	private String packageName;
	private long size;
	private String type;
	private String versionName;
	private long versionCode;
	private int downCount;
	private String icon;
	private String preview1;
	private String preview2;
	private String preview3;
	private String preview4;
	private String preview5;
	private String description;
	private String downUrl;
	private int showAsBanner;
	private String bannerUrl;
	private AdditionalIcon additionalIcon;
	
	public AppInfo() {
		
	}
	
	public AppInfo(AdditionalIcon item) {
		this.additionalIcon = item;
		this.description = item.getDescription();
		if(item instanceof AdditionalPackage){
			packageName = ((AdditionalPackage)item).getPackageName();
		}
	}
	
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public int getDownCount() {
		return downCount;
	}
	public void setDownCount(int downCount) {
		this.downCount = downCount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public long getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(long versionCode) {
		this.versionCode = versionCode;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getPreview1() {
		return preview1;
	}
	public void setPreview1(String preview1) {
		this.preview1 = preview1;
	}
	public String getPreview2() {
		return preview2;
	}
	public void setPreview2(String preview2) {
		this.preview2 = preview2;
	}
	public String getPreview3() {
		return preview3;
	}
	public void setPreview3(String preview3) {
		this.preview3 = preview3;
	}
	public String getPreview4() {
		return preview4;
	}
	public void setPreview4(String preview4) {
		this.preview4 = preview4;
	}
	public String getPreview5() {
		return preview5;
	}
	public void setPreview5(String preview5) {
		this.preview5 = preview5;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getShowAsBanner() {
		return showAsBanner;
	}
	public void setShowAsBanner(int showAsBanner) {
		this.showAsBanner = showAsBanner;
	}
	public String getBannerUrl() {
		return bannerUrl;
	}
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
	
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packageName=" + packageName
				+ ", size=" + size + ", type=" + type + ", versionName="
				+ versionName + ", versionCode=" + versionCode + ", downCount="
				+ downCount + ", icon=" + icon + ", preview1=" + preview1
				+ ", preview2=" + preview2 + ", preview3=" + preview3
				+ ", preview4=" + preview4 + ", preview5=" + preview5
				+ ", description=" + description + ", downUrl=" + downUrl
				+ ", showAsBanner=" + showAsBanner + ", bannerUrl=" + bannerUrl
				+ "]";
	}

	// 序列化
	public static final Parcelable.Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
		
		@Override
		public AppInfo[] newArray(int size) {
			return new AppInfo[size];
		}
		
		@Override
		public AppInfo createFromParcel(Parcel source) {
			AppInfo appInfo = new AppInfo();
			appInfo.name = source.readString();
			appInfo.packageName = source.readString();
			appInfo.size = source.readLong();
			appInfo.type = source.readString();
			appInfo.versionName = source.readString();
			appInfo.versionCode = source.readLong();
			appInfo.downCount = source.readInt();
			appInfo.icon = source.readString();
			appInfo.preview1 = source.readString();
			appInfo.preview2 = source.readString();
			appInfo.preview3 = source.readString();
			appInfo.preview4 = source.readString();
			appInfo.preview5 = source.readString();
			appInfo.description = source.readString();
			appInfo.downUrl = source.readString();
			appInfo.showAsBanner = source.readInt();
			appInfo.bannerUrl = source.readString();
			return appInfo;
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(packageName);
		dest.writeLong(size);
		dest.writeString(type);
		dest.writeString(versionName);
		dest.writeLong(versionCode);
		dest.writeInt(downCount);
		dest.writeString(icon);
		dest.writeString(preview1);
		dest.writeString(preview2);
		dest.writeString(preview3);
		dest.writeString(preview4);
		dest.writeString(preview5);
		dest.writeString(description);
		dest.writeString(downUrl);
		dest.writeInt(showAsBanner);
		dest.writeString(bannerUrl);
	}
	@Override
	public int compareTo(AppInfo another) {
		return 0;
	}
	@Override
	public Drawable getIcon(Context context) {
		return additionalIcon.getIcon(context);
	}
	@Override
	public String getTitle(Context context) {
		return additionalIcon.getTitle(context);
	}
}
