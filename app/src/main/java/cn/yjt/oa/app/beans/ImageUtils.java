package cn.yjt.oa.app.beans;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * 图片工具类
 * @author 熊岳岳
 * @since 20150830
 */
public class ImageUtils {

	public static void openImageFile(Context context, File file) {
		if (file.exists()) {
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			if (!(context instanceof Activity)) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "图片文件不存在", Toast.LENGTH_SHORT).show();
		}
	}
}
