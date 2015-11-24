package cn.yjt.oa.app.beans;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.activity.AdditionalIcon;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;

/**
 * 
 * @author qinzhe
 */
public class DashBoardItem implements AdditionalIcon {

	public static final String SCHEME_RESOURCE = "res://";
	public static final String SCHEME_FILE = "file://";
	public static final String SCHEME_HTTP = "http://";
	
	public static final String DEVELOPING = "developing";
	
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_DEPRECATED = 1;
	
	
	private int id;

	/**
	 * icon activity packageName
	 */
	private String packageName;
	/**
	 * icon activity className, maybe null
	 */
	private String className;
	/**
	 * icon resource, res://, file://, http://
	 */
	private String iconResUri;
	/**
	 * icon title
	 */
	private String title;
	/**
	 * icon description
	 */
	private String description;

	private boolean installed;
	
	private String requirePermission;
	
	private int status;//0:启用 1：禁用；
	
	private int sdkVersion;
	
	public DashBoardItem() {
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getIconResUri() {
		return iconResUri;
	}

	public void setIconResUri(String iconResUri) {
		this.iconResUri = iconResUri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

	public int getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(int sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getRequirePermission() {
		return requirePermission;
	}

	public void setRequirePermission(String requirePermission) {
		this.requirePermission = requirePermission;
	}
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Drawable getIcon(Context context) {
		if (!TextUtils.isEmpty(iconResUri)) {
			if (iconResUri.startsWith(SCHEME_RESOURCE)) {
				String resName = iconResUri.substring(SCHEME_RESOURCE.length());
				PackageManager pm = context.getPackageManager();
				Resources res = null;
				try {
					res = pm.getResourcesForApplication(packageName);
				} catch (NameNotFoundException e1) {
					e1.printStackTrace();
				}
				//int id = context.getResources().getIdentifier(resName,"drawable", context.getPackageName());
				if(res != null){
					int id = res.getIdentifier(resName,"drawable",packageName);
					if (id != 0) {
						Drawable drawable = res.getDrawable(id);
						int iconSize = context.getResources().getDimensionPixelSize(R.dimen.app_icon_width);
						drawable.setBounds(0, 0, iconSize, iconSize);
						return drawable;
					}
				}
				
			} else if (iconResUri.startsWith(SCHEME_FILE)) {
				String filePath = iconResUri.substring(SCHEME_FILE.length());
				Bitmap bmp = BitmapFactory.decodeFile(filePath);
				return new BitmapDrawable(context.getResources(), bmp);
			} else if (iconResUri.startsWith(SCHEME_HTTP)) {
				

			}
		}
		return null;
	}
	public void getIconRes(Context context,final IconResCallback resCallback) {
		if (!TextUtils.isEmpty(iconResUri)) {
			if (iconResUri.startsWith(SCHEME_RESOURCE)) {
				String resName = iconResUri.substring(SCHEME_RESOURCE.length());
				PackageManager pm = context.getPackageManager();
				Resources res = null;
				try {
					res = pm.getResourcesForApplication(packageName);
				} catch (NameNotFoundException e1) {
					e1.printStackTrace();
				}
				//int id = context.getResources().getIdentifier(resName,"drawable", context.getPackageName());
				if(res != null){
					int id = res.getIdentifier(resName,"drawable",packageName);
					if (id != 0) {
						Drawable drawable = res.getDrawable(id);
						int iconSize = context.getResources().getDimensionPixelSize(R.dimen.app_icon_width);
						drawable.setBounds(0, 0, iconSize, iconSize);
						BitmapDrawable bd=(BitmapDrawable) drawable;
						resCallback.onSuccess(bd.getBitmap());
					}else{
						resCallback.onError();
					}
				}else{
					resCallback.onError();
				}
				
			} else if (iconResUri.startsWith(SCHEME_FILE)) {
				String filePath = iconResUri.substring(SCHEME_FILE.length());
				Bitmap bmp = BitmapFactory.decodeFile(filePath);
				if(bmp!=null){
					resCallback.onSuccess(bmp);
				}else{
					resCallback.onError();
				}
			} else if (iconResUri.startsWith(SCHEME_HTTP)) {
				MainApplication.getImageLoader().get(iconResUri, new ImageLoaderListener() {

					@Override
					public void onSuccess(ImageContainer container) {
						resCallback.onSuccess(container.getBitmap());
					}

					@Override
					public void onError(ImageContainer container) {
						resCallback.onError();
					}
				});
			}
		}else{
			resCallback.onError();
		}
	}
	
	public void getIcon(final Context context,final IconCallback callback) {
		if (TextUtils.isEmpty(iconResUri)) {
			callback.onError();
			return;
		}
		if (iconResUri.startsWith(SCHEME_HTTP)) {
			AsyncRequest.getBitmap(iconResUri, new Listener<Bitmap>() {

				@Override
				public void onResponse(Bitmap bitmap) {
					if (bitmap != null) {

						BitmapDrawable top = new BitmapDrawable(context.getResources(),
								bitmap);
						int iconSize = context.getResources().getDimensionPixelSize(R.dimen.app_icon_width);
						top.setBounds(0, 0, iconSize, iconSize);
						callback.onSuccess(top);
					}
				}

				@Override
				public void onErrorResponse(InvocationError arg0) {
					callback.onError();
				}
			});
		} else {
			Drawable drawable = getIcon(context);
			callback.onSuccess(drawable);
		}
	}
	
	

	public String getTitle(Context context) {
		if (!TextUtils.isEmpty(title)) {
			if (title.startsWith(SCHEME_RESOURCE)) {
				String resName = title.substring(SCHEME_RESOURCE.length());
				int id = context.getResources().getIdentifier(resName,
						"string", context.getPackageName());
				if (id != 0) {
					return context.getResources().getString(id);
				}
			}
		}
		return title;
	}

	public void setIcon(Context context, int iconResId) {
		setIconResUri(SCHEME_RESOURCE
				+ context.getResources().getResourceName(iconResId));
	}

	public void setIcon(String iconResName) {
		setIconResUri(SCHEME_RESOURCE + iconResName);
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DashBoardItem other = (DashBoardItem) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(packageName);
		dest.writeString(className);
		dest.writeString(iconResUri);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(requirePermission);
		dest.writeInt(status);
	}

	public static final Parcelable.Creator<DashBoardItem> CREATOR = new Parcelable.Creator<DashBoardItem>() {
		public DashBoardItem createFromParcel(Parcel in) {
			return new DashBoardItem(in);
		}

		public DashBoardItem[] newArray(int size) {
			return new DashBoardItem[size];
		}
	};

	private DashBoardItem(Parcel in) {
		id=in.readInt();
		packageName = in.readString();
		className = in.readString();
		iconResUri = in.readString();
		title = in.readString();
		description = in.readString();
		requirePermission = in.readString();
		status=in.readInt();
	}
	
	public static interface IconCallback{
		public void onSuccess(Drawable drawable);
		public void onError();
		
	}
	public static interface IconResCallback{
		public void onSuccess(Object obj);
		public void onError();
		
	}
	
	@Override
	public String toString() {
		return packageName+"."+title;
	}

}
