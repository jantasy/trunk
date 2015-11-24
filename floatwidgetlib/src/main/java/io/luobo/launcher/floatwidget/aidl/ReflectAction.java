package io.luobo.launcher.floatwidget.aidl;

import io.luobo.common.utils.MyThreadUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

public class ReflectAction implements Parcelable {
	int viewId;
	String methodName;
	Class<?>[] paramClasses;
	Object[] paramValues;
	
	public ReflectAction(int viewId, String methodName,
			Class<?>[] paramClasses, Object[] paramValues) {
		this.viewId = viewId;
		this.methodName = methodName;
		this.paramClasses = paramClasses;
		this.paramValues = paramValues;
	}
	
	private ReflectAction(Parcel in) {
		viewId = in.readInt();
		methodName = in.readString();
		paramValues = in.readArray(ReflectAction.class.getClassLoader());
		int size = in.readInt();
		if (size >= 0) {
			paramClasses = new Class<?>[size];
			for (int i=0; i<size; ++i) {
				paramClasses[i] = (Class<?>) in.readSerializable();
			}
		}
	}
	
	public void doAction(final ViewGroup root) {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				View v = viewId != View.NO_ID ? root.findViewById(viewId) : root.getChildAt(0);
				if (v != null) {
					try {
						Method m = v.getClass().getDeclaredMethod(methodName, paramClasses);
						m.invoke(v, paramValues);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		};
		if (MyThreadUtils.isOnMainThread()) {
			run.run();
		} else {
			root.post(run);
		}
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(viewId);
		dest.writeString(methodName);
		dest.writeArray(paramValues);
		if (paramClasses == null) {
			dest.writeInt(-1);
		} else {
			dest.writeInt(paramClasses.length);
			for (Class<?> clazz:paramClasses) {
				dest.writeSerializable(clazz);
			}
		}
	}
	
	public static final Creator<ReflectAction> CREATOR = new Creator<ReflectAction>() {
		public ReflectAction createFromParcel(Parcel in) {
			return new ReflectAction(in);
		}

		public ReflectAction[] newArray(int size) {
			return new ReflectAction[size];
		}
	};

	@Override
	public String toString() {
		return "ReflectAction [viewId=" + viewId + ", methodName=" + methodName
				+ ", paramValues=" + Arrays.toString(paramValues) + "]";
	}
}