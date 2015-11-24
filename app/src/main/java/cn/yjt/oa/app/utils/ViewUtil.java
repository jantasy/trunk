package cn.yjt.oa.app.utils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.dashboard.config.DashBoardManager;

public class ViewUtil {
	public static final String TAG_CHECKED = String.valueOf('\u221A');
	public static final String TAG_UNCHECKED = String.valueOf('\u25A1');
	private static final int SHORTCUT_ICON_TITLE_MAX_LEN = 10;
	private static final ThreadLocal<Paint> sPaint = new ThreadLocal<Paint>();
	private static final ThreadLocal<Canvas> sCanvas = new ThreadLocal<Canvas>();
	private static final ThreadLocal<Rect> sRect1 = new ThreadLocal<Rect>();
	private static final ThreadLocal<Rect> sRect2 = new ThreadLocal<Rect>();

	public static Paint getPaint() {
		if (sPaint.get() == null) {
			Paint paint = new Paint();
			paint.setFilterBitmap(true);
			sPaint.set(paint);
		}
		return sPaint.get();
	}

	public static Canvas getCanvas() {
		if (sCanvas.get() == null) {
			Canvas canvas = new Canvas();
			sCanvas.set(canvas);
		}
		return sCanvas.get();
	}

	public static Rect getRect1() {
		if (sRect1.get() == null) {
			Rect rc = new Rect();
			sRect1.set(rc);
		}
		return sRect1.get();
	}

	public static Rect getRect2() {
		if (sRect2.get() == null) {
			Rect rc = new Rect();
			sRect2.set(rc);
		}
		return sRect2.get();
	}

	public static Bitmap createViewCacheBitmap(View v, float scale,
			boolean hasAlpha) {
		int width = (int) (v.getWidth() * scale);
		int height = (int) (v.getHeight() * scale);
		float scaleX = width / (float) v.getWidth();
		float scaleY = height / (float) v.getHeight();
		final Bitmap.Config c = hasAlpha ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap thumb = null;
		try {
			thumb = Bitmap.createBitmap(width, height, c);
		} catch (OutOfMemoryError e) {
			return null;
		}
		final Canvas canvas = getCanvas();
		final Paint paint = getPaint();
		canvas.setBitmap(thumb);
		paint.setDither(false);
		paint.setFilterBitmap(true);

		canvas.save();
		if (scaleX != 1.0f && scaleY != 1.0)
			canvas.scale(scaleX, scaleY);
		canvas.translate(-v.getScrollX(), -v.getScrollY());

		v.draw(canvas);

		canvas.restore();

		// clear bitmap reference
		ApiAdapter.setCanvasNullBitmap(canvas);

		return thumb;
	}

	/**
	 * 判断桌面是否已添加快捷方式
	 * 
	 * @param titleName
	 *            快捷方式名称
	 * @return
	 */
	public static boolean IfaddShortCut(Context context, String titleName) {
		boolean isInstallShortcut = false;
		final ContentResolver cr = context.getContentResolver();
		final String AUTHORITY = "com.sec.android.app.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { titleName }, null);
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}
		return isInstallShortcut;
	}

	/**
	 * 发送快捷方式到桌面
	 */
	public static void sendToDesktop(Context context, String className,
			String packageName, String shortcutName, String iconUri,
			Object shortcutIcon) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		shortcut.putExtra("duplicate", false);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				makeShortcutIconTitle(shortcutName));

		if (shortcutIcon != null) {
			if (TextUtils.isEmpty(iconUri)) {
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
						Intent.ShortcutIconResource.fromContext(context,
								(Integer) shortcutIcon));
			} else {

				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON,
						(Bitmap) shortcutIcon);
			}
		}

		Intent intent = null;
		if (TextUtils.isEmpty(className)) {
			PackageManager pm = context.getPackageManager();
			intent = pm.getLaunchIntentForPackage(packageName);
		} else {
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			ComponentName comp = new ComponentName(context.getPackageName(),
					className);
			intent.setComponent(comp);
		}

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		context.sendBroadcast(shortcut);
	}

	/**
	 * EXTRA_SHORTCUT_NAME对应的String需要转换下，不然系统不认
	 */
	private static String makeShortcutIconTitle(String content) {
		content = content.replace(TAG_CHECKED, "");
		content = content.replace(TAG_UNCHECKED, "");
		return content.length() > SHORTCUT_ICON_TITLE_MAX_LEN ? content
				.substring(0, SHORTCUT_ICON_TITLE_MAX_LEN) : content;
	}

	public static boolean noLoginToLaunchAndDashBoardAllowed(Context context) {
		UserInfo current = AccountManager.getCurrent(context);
		boolean allowed = DashBoardManager.isAllowed(context);
		if (allowed) {
			if (current == null || current.getId() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

    public static boolean noLoginToLaunch(Context context){
        UserInfo current = AccountManager.getCurrent(context);
        if (current == null || current.getId() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
