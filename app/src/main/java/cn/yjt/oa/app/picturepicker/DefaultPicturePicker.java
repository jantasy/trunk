package cn.yjt.oa.app.picturepicker;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.personalcenter.SelectPicPopupWindow;

public class DefaultPicturePicker implements PicturePicker {
	public static String YJT_DIR = Environment.getExternalStorageDirectory()
			+ "/yijitong/" + MainActivity.userName + "";

	protected static final int REQUEST_CAMERA = 1000;
	protected static final int REQUEST_IMAGE_GALLERY = 2000;

	private File tempFile;

	@Override
	public void pickPicture(final Activity context) {
		tempFile = new File(YJT_DIR, System.currentTimeMillis()
				+ ".jpg");
		SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(context,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = v.getId();
						if (id == R.id.btn_take_photo) {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							if (tempFile.exists()) {
								tempFile.delete();
							}
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tempFile));
							intent.putExtra("return-data", true);
							try {
								context.startActivityForResult(intent,
										REQUEST_CAMERA);
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context, "无法打开您的相机", Toast.LENGTH_SHORT).show();
							}
						} else if (id == R.id.btn_pick_photo) {
							Intent intent1 = new Intent(Intent.ACTION_PICK,
									null);
							intent1.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							try {
								context.startActivityForResult(intent1,
										REQUEST_IMAGE_GALLERY);
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context, "无法打开您的相册", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
		// 显示窗口
		menuWindow.showAtLocation(context.getWindow().getDecorView(),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}
	
	@Override
	public void pickPicture(final Fragment fragment) {
		tempFile = new File(YJT_DIR, System.currentTimeMillis()
				+ ".jpg");
		SelectPicPopupWindow menuWindow = new SelectPicPopupWindow(fragment.getActivity(),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int id = v.getId();
						if (id == R.id.btn_take_photo) {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							if (tempFile.exists()) {
								tempFile.delete();
							}
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tempFile));
							intent.putExtra("return-data", true);
							try {
								fragment.startActivityForResult(intent,
										REQUEST_CAMERA);
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(fragment.getActivity(), "无法打开您的相机", Toast.LENGTH_SHORT).show();
							}
						} else if (id == R.id.btn_pick_photo) {
							Intent intent1 = new Intent(Intent.ACTION_PICK,
									null);
							intent1.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							try {
								fragment.startActivityForResult(intent1,
										REQUEST_IMAGE_GALLERY);
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(fragment.getActivity(), "无法打开您的相册", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
		// 显示窗口
		menuWindow.showAtLocation(fragment.getActivity().getWindow().getDecorView(),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	@Override
	public Uri getPicture(int requestCode,int resultCode,Intent data) {
		Uri uri = null;
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_IMAGE_GALLERY) {
				 uri = data.getData();
			} else if (requestCode == REQUEST_CAMERA) {
				if(tempFile != null){
					uri = Uri.fromFile(tempFile);
				}
			}
		}
		return uri;
	}

	@Override
	public boolean isPickerResult(int requestCode) {
		return requestCode == REQUEST_CAMERA
				|| requestCode == REQUEST_IMAGE_GALLERY;
	}


}
