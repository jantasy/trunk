package cn.yjt.oa.app.personalcenter;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.enterprise.CreateEnterpriseActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.FileClientFactory;
import cn.yjt.oa.app.utils.BitmapUtils;

public class PersonalHomeActivity extends TitleFragmentActivity implements OnClickListener {
	MediaScannerConnection scannerConn;
	public static String FILE_PATH = Environment.getExternalStorageDirectory() + "/yijitong/" + MainActivity.userName
			+ "/headpic.jpg";
	public static String FILE_PATH_TEMP = Environment.getExternalStorageDirectory() + "/yijitong/"
			+ MainActivity.userName + "/headpictemp.jpg";

	SelectPicPopupWindow menuWindow;
	public static final int FLAG_CAMERA = 1;
	public static final int FLAG_IMAGE_GALLERY = 2;
	public static final int FLAG_IMAGE_CUT = 3;
	public static final int FLAG_EXIT = 4;
	File tempFile = new File(FILE_PATH_TEMP);
	private String avatar = "";
	private int custId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        /*记录操作 0106*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_PERSON_CENTER);

		setContentView(R.layout.personal_home_activity_layout);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		headPhotoIv = (ImageView) findViewById(R.id.icon);
		headPhotoIv.setOnClickListener(this);
		phoneMunber = (TextView) findViewById(R.id.phone_munber);
		nameEt = (EditText) findViewById(R.id.name);
		companyEt = (EditText) findViewById(R.id.company);
		//		departmentEt = (EditText) findViewById(R.id.department);
		//		positionEt = (EditText) findViewById(R.id.position);
		emailEt = (EditText) findViewById(R.id.email);
		telEt = (EditText) findViewById(R.id.tel);
		info = AccountManager.getCurrent(this);
		maleIv = (ImageView) findViewById(R.id.male);
		femaleIv = (ImageView) findViewById(R.id.female);
		maleIv.setOnClickListener(this);
		femaleIv.setOnClickListener(this);
		saveBt = (Button) findViewById(R.id.save);
		saveBt.setOnClickListener(this);
		from = getIntent().getStringExtra("from");
		if (info != null) {
			avatar = info.getAvatar();
			initInfo();
		}
		// else {
		// if ("regist".equals(from)){
		// setTitle("完成注册");
		// saveBt.setText("提交");
		// String registPhoneMunber =
		// getIntent().getStringExtra("regist_phone_munber");
		// phoneMunber.setText(registPhoneMunber);
		// }
		// }
		BitmapUtils.setLoginHeadIcon(this, avatar, headPhotoIv, 0, R.drawable.contactlist_contact_icon_default);
		custId = getIntent().getIntExtra("custId", 1);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initInfo() {
		phoneMunber.setText(info.getPhone());
		if (TextUtils.isEmpty(info.getPosition()) || TextUtils.isEmpty(info.getDepartment())) {
			setTitle("个人信息");
			saveBt.setText("提交");
			phoneMunber.setText(info.getPhone());
		}
		if (0 == info.getSex()) {
			maleIv.setSelected(true);
		} else if (1 == info.getSex()) {
			femaleIv.setSelected(true);
		}
		if (!TextUtils.isEmpty(info.getName()) && (info.getIsYjtUser() == 1)) {
			nameEt.setInputType(EditorInfo.TYPE_NULL);
			nameEt.setKeyListener(null);
			nameEt.setTextColor(Color.GRAY);
			nameEt.setAlpha(0.4f);
			nameEt.setText(info.getName() + "(不可修改)");
		} else {
			nameEt.setText(info.getName());
		}
		companyEt.setText(info.getCustName() + "(不可修改)");
		//		departmentEt.setText(info.getDepartment());
		//		positionEt.setText(info.getPosition());
		emailEt.setText(info.getEmail());
		telEt.setText(info.getTel());
	}

	ProgressDialog dialog;
	private ImageView headPhotoIv;

	public void showDialog(String message) {
		dialog = new ProgressDialog(this);
		dialog.setMessage(message);
		dialog.setCancelable(false);
		dialog.show();
	}

	public void dismissDialog() {
		if (dialog != null) {
			dialog.setCancelable(true);
			dialog.dismiss();

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.icon:
			showPop();
			break;
		case R.id.save:
			saveInfo();

            /*记录操作 0107*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_MODIFY_INFO);

			break;
		case R.id.male:
			maleIv.setSelected(true);
			femaleIv.setSelected(false);
			break;
		case R.id.female:
			femaleIv.setSelected(true);
			maleIv.setSelected(false);
			break;
		default:
			break;
		}
	}

	private void showPop() {
		File parent = tempFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdir();
		}
		if (menuWindow == null) {
			menuWindow = new SelectPicPopupWindow(this, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					menuWindow.dismiss();
					int id = v.getId();
					if (id == R.id.btn_take_photo) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						if (tempFile.exists()) {
							tempFile.delete();
						}
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
						intent.putExtra("return-data", true);
						try {
							startActivityForResult(intent, FLAG_CAMERA);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), "无法打开您的相机", Toast.LENGTH_SHORT).show();
						}
					} else if (id == R.id.btn_pick_photo) {
						Intent intent1 = new Intent(Intent.ACTION_PICK, null);
						intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
						try {
							startActivityForResult(intent1, FLAG_IMAGE_GALLERY);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), "无法打开您的相册", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}
		// 显示窗口
		menuWindow.showAtLocation(findViewById(R.id.container), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// 如果用户取消
		if (resultCode == this.RESULT_CANCELED) {
			return;
		}

		// 拍照后返回
		if (requestCode == FLAG_CAMERA) {
			// 处理拍照
			// 为了图片在图库里面能看见,发送一个广播
			broadcastNewPicture(this, Uri.fromFile(tempFile));
			startPhotoZoom(Uri.fromFile(tempFile));
			return;
		} else if (requestCode == FLAG_IMAGE_GALLERY) {
			if (intent != null) {
				startPhotoZoom(intent.getData());
			}
			return;
		} else if (requestCode == FLAG_IMAGE_CUT) {
			if (intent != null) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					Bitmap photo = bundle.getParcelable("data");
					FileOutputStream fop;
					try {
						fop = new FileOutputStream(PersonalHomeActivity.FILE_PATH_TEMP);
						// 实例化FileOutputStream，参数是生成路径
						photo.compress(Bitmap.CompressFormat.JPEG, 100, fop);
						File tempFile = new File(PersonalHomeActivity.FILE_PATH_TEMP);
						fop.close();
						if (tempFile.exists()) {
							uploadHeadPhoto(photo);
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					if (tempFile.exists()) {
						// showDialog(DIALOG_HEADPIC_UPLOADING);
					}
				}
			}
			return;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == UPLOAD_SUCCESS) {
				Toast.makeText(PersonalHomeActivity.this, R.string.upload_headicon_success, Toast.LENGTH_SHORT).show();
				BitmapUtils.setLoginHeadIcon(PersonalHomeActivity.this, avatar, headPhotoIv, 0,
						R.drawable.contactlist_contact_icon_default);
			} else {
				Toast.makeText(PersonalHomeActivity.this, R.string.upload_headicon_fail, Toast.LENGTH_SHORT).show();
			}
		};
	};

	private static final int UPLOAD_SUCCESS = 1;
	private static final int UPLOAD_FAIL = 2;
	private UserInfo info;
	private EditText nameEt;
	private EditText companyEt;
	//	private EditText departmentEt;
	//	private EditText positionEt;
	private EditText emailEt;
	private EditText telEt;
	private ImageView maleIv;
	private ImageView femaleIv;
	private TextView phoneMunber;
	private Button saveBt;
	private String from;

	private void uploadHeadPhoto(final Bitmap b) {
		checkNetWork();
		final PersonalHomeActivity activity = (PersonalHomeActivity) this;
		activity.showDialog(getString(R.string.saving_headicon));
		FileClient client = FileClientFactory.createSingleThreadFileClient(this);
		try {
			Type type = new TypeToken<Response<String>>() {
			}.getType();
			String uri = BusinessConstants.buildUrl(AsyncRequest.MODULE_PERSONALINFO + "/avatar");
			client.upload(uri, "avatar", new File(PersonalHomeActivity.FILE_PATH_TEMP), type,
					new Listener<Response<String>>() {

						@Override
						public void onResponse(Response<String> response) {
							activity.dismissDialog();
							if (response.getCode() == 0) {
								String url = response.getPayload();
								uploadSaveInfo(url);

								// Toast.makeText(this,
								// R.string.upload_headicon_success, 0).show();
							} else {
								File fileTemp = new File(PersonalHomeActivity.FILE_PATH_TEMP);
								fileTemp.delete();
								handler.sendEmptyMessage(UPLOAD_FAIL);
								// Toast.makeText(this,
								// R.string.upload_headicon_fail, 0).show();
							}
						}

						@Override
						public void onErrorResponse(InvocationError error) {
							activity.dismissDialog();
							File fileTemp = new File(PersonalHomeActivity.FILE_PATH_TEMP);
							fileTemp.delete();
							handler.sendEmptyMessage(UPLOAD_FAIL);
							// Toast.makeText(this,
							// R.string.upload_headicon_fail, Toast.LENGTH_SHORT).show();
						}
					});
		} catch (InvocationError e) {
			activity.dismissDialog();
			Toast.makeText(this, R.string.upload_headicon_fail, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	/**
	 * 缩放图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		if (uri == null || "".equals(uri.toString())) {
			return;
		}
		MediaScannerConnectionClient scannerClient = new MediaScannerConnectionClient() {
			@Override
			public void onScanCompleted(String path, Uri uri) {
				try {
					if (scannerConn.isConnected()) {
						scannerConn.disconnect();
					}
				} catch (Exception e) {
				}
				scannerConn = null;
			}

			@Override
			public void onMediaScannerConnected() {
				try {
					scannerConn.scanFile(tempFile.getPath(), null);
				} catch (Exception e) {
				}
			}
		};
		if (scannerConn == null) {
			scannerConn = new MediaScannerConnection(this, scannerClient);
		}
		scannerConn.connect();
		startPhotoZoom(uri, 100);
	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		try {
			startActivityForResult(intent, FLAG_IMAGE_CUT);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "打开系统裁剪失败", Toast.LENGTH_SHORT).show();
		}
	}

	// 为了图库中能看见图片，发送了一个广播
	private void broadcastNewPicture(Context context, Uri uri) {
		context.sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", uri));
		// Keep compatibility
		context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
		// ACTION_MEDIA_SCANNER_SCAN_FILE
		Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		i.setData(uri);
		context.sendBroadcast(i);

	}

	private void checkNetWork() {
		if (!hasNetWork()) {
			Toast.makeText(this, R.string.connect_network_fail, Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private boolean hasNetWork() {
		ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	private UserInfo getSaveUserInfo() {
		UserInfo recordiInfo = AccountManager.getCurrent(this);
		UserInfo info = null;
		if (recordiInfo == null) {
			info = new UserInfo();
		} else {
			info = recordiInfo.clone();
		}
		if (maleIv.isSelected()) {
			info.setSex(0);
		} else if (femaleIv.isSelected()) {
			info.setSex(1);
		} else {
			info.setSex(2);
		}
		if (!TextUtils.isEmpty(avatar)) {
			info.setAvatar(avatar);
		}
		String name = nameEt.getText().toString();
		//		String department = departmentEt.getText().toString();
		//		String position = positionEt.getText().toString();
		String email = emailEt.getText().toString();
		if(name.contains("(不可修改)")){
			name = info.getName();
		}
		info.setName(name);
		//		info.setDepartment(department);
		//		info.setPosition(position);
		info.setEmail(email);
		info.setTel(telEt.getText().toString().trim());
		return info;
	}
	
	private UserInfo getOldUserInfo() {
		UserInfo recordiInfo = AccountManager.getCurrent(this);
		UserInfo info = null;
		if (recordiInfo == null) {
			info = new UserInfo();
		} else {
			info = recordiInfo.clone();
		}
		return info;
	}

	private void uploadSaveInfo(final String url) {
		UserInfo tempInfo = AccountManager.getCurrent(this);
		
		if(tempInfo==null){
			tempInfo = getSaveUserInfo();
		}
		final UserInfo requestInfo = tempInfo;
		Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_PERSONALINFO);
		builder.setRequestBody(requestInfo);
		builder.setResponseType(new TypeToken<Response<Object>>() {
		}.getType());

		builder.setResponseListener(new Listener<Response<Object>>() {

			@Override
			public void onResponse(Response<Object> response) {
				dismissDialog();
				if (response.getCode() == 0) {
					avatar = url;
					AccountManager.updateUserAvatar(PersonalHomeActivity.this, url);
					handler.sendEmptyMessage(UPLOAD_SUCCESS);
					AccountManager.updateUserInfo(PersonalHomeActivity.this, requestInfo);
				} else {
					handler.sendEmptyMessage(UPLOAD_FAIL);
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dismissDialog();
				Toast.makeText(PersonalHomeActivity.this, R.string.edit_info_fail, Toast.LENGTH_SHORT).show();
			}
		});
		builder.build().put();

	}

	private void saveInfo() {
		final UserInfo info = getSaveUserInfo();
		if (TextUtils.isEmpty(info.getName())) {
			Toast.makeText(this, "姓名为必填，请填写后保存", Toast.LENGTH_SHORT).show();
			return;
		}
		//		showDialog(getString(R.string.saving_info));
		Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_PERSONALINFO);
		builder.setRequestBody(info);
		builder.setResponseType(new TypeToken<Response<Object>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<Object>>() {

			@Override
			public void onResponse(Response<Object> response) {
				if (response.getCode() == 0) {
					AccountManager.updateUserInfo(PersonalHomeActivity.this, info);
					Toast.makeText(PersonalHomeActivity.this, R.string.edit_info_success, Toast.LENGTH_SHORT).show();
					int applyStatus = getIntent().getIntExtra("apply_status", -1);
					String custId = getIntent().getStringExtra("cust_id");
					if ("1".equals(custId) && applyStatus == 0) {
						CreateEnterpriseActivity.launch(PersonalHomeActivity.this);
						finish();
					} else {
						Intent i = new Intent(PersonalHomeActivity.this, MainActivity.class);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						}
						i.putExtra("info", info);
						startActivity(i);
						finish();
					}
				} else {
					Toast.makeText(PersonalHomeActivity.this, R.string.edit_info_fail, Toast.LENGTH_SHORT).show();
				}
				dismissDialog();
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dismissDialog();
				Toast.makeText(PersonalHomeActivity.this, R.string.edit_info_fail, Toast.LENGTH_SHORT).show();
			}
		});
		builder.build().put();

	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	public static void launchWithApplyStatus(Context context, String custId, int applyStatus) {
		Intent intent = new Intent(context, PersonalHomeActivity.class);
		intent.putExtra("apply_status", applyStatus);
		intent.putExtra("cust_id", custId);
		context.startActivity(intent);
	}
}
