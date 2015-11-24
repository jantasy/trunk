package cn.yjt.oa.app;

import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.beans.OperaStatistics;
import cn.yjt.oa.app.beans.OperaStatisticsRequest;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.utils.ToastUtils;
import io.luobo.common.http.ErrorType;
import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.app.utils.UpdateHelper;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.component.UmengBaseActivity;
import cn.yjt.oa.app.enterprise.CreateEnterpriseActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.FileClientFactory;
import cn.yjt.oa.app.personalcenter.LoginActivity;
import cn.yjt.oa.app.personalcenter.PersonalHomeActivity;
import cn.yjt.oa.app.push.Utils;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.TimeUse;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

/** 启动Activity，程序的入口闪屏页面 */
public class LaunchActivity extends UmengBaseActivity {

	private static String TAG = "LaunchActivity";

	public static final int INTO_GUIDE = 1;
	public static String LOGO_ICON = "";

	private SharedPreferences mSharedPrefs;
	private Handler mHandler;

	/** 闪屏位图对象 */
	private Bitmap mSplashBitmap;

	/** 闪屏界面的imageview控件 */
	private ImageView mIvLogo;

    private boolean isBlackLists = false;

	// -----------Activity生命周期函数-------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		TimeUse.start("launch");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch_activity_layout);

		try{
			commitOpera();
		}catch (Exception e){
			LogUtils.e(TAG,"同步数据出错"+e.getMessage());
		}

		initParam();
		initView();
		fillData();
		setListener();
		businessLogic();
	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(task);
		mSplashBitmap.recycle();
		mSplashBitmap = null;
	}

	// ------------------END-------------------

	// ---------------oncreate方法中的初始化--------------
	/**
	 * 初始化参数变量
	 */
	private void initParam() {
		// 初始化sp
		mSharedPrefs = getSharedPreferences("account", MODE_PRIVATE);
		// 初始化Bitmap
		mSplashBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.splash);
	}

    private void commitOpera() {
        List<OperaStatistics> list = new Select().from(OperaStatistics.class).execute();
        List<OperaStatisticsRequest> requestList = new ArrayList<>();
        if(list == null){
            return ;
        }
        if(list.isEmpty()){
            return;
        }
        for(OperaStatistics info:list){
            LogUtils.i("OperaStatistics",info.getOperaEventNo()+":::::"+info.getTime());
            requestList.add(new OperaStatisticsRequest(info));
        }

        ApiHelper.commitOpera(new Listener<Response<Object>>() {
            @Override
            public void onResponse(Response<Object> objectResponse) {
//                ToastUtils.shortToastShow(objectResponse.getCode() + "上传操作成功");
                if(objectResponse.getCode() == 0){
                    new Delete().from(OperaStatistics.class).execute();
                }
            }

            @Override
            public void onErrorResponse(InvocationError invocationError) {
//                ToastUtils.shortToastShow(invocationError.getMessage()+"上传操作失败");
            }
        }, requestList);
    }

	/** 初始化控件 */
	private void initView() {
		mIvLogo = (ImageView) findViewById(R.id.logo);
	}

	/** 数据填充 */
	@SuppressWarnings("deprecation")
	private void fillData() {
		mIvLogo.setBackgroundDrawable(new BitmapDrawable(getResources(),
			mSplashBitmap));
	}

	/** 设置监听 */
	private void setListener() {

	}

	/** 业务逻辑 */
	private void businessLogic() {
		switchPageDelay();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				onCreateAsync();
				return null;
			}
		}.execute();
	}

	// --------------------END-----------------------

	/** 闪屏界面下一步跳转的任务，根据不同的条件跳转到不同的页面 */
	Runnable task = new Runnable() {
		@Override
		public void run() {
			// 从sp中获取是否为第一次登录的标识符
			boolean firstLaunch = mSharedPrefs.getBoolean("first_launch", true);

			if (firstLaunch && !hasLogin()) {

				// 如果是第一次登录的话，
				// 将第一次登录的标识符置为false，并跳到引导页面
				Editor edit = mSharedPrefs.edit();
				edit.putBoolean("first_launch", false);
				edit.commit();
				Intent guideIntent = new Intent(LaunchActivity.this,
					GuideActivity.class);
				startActivityForResult(guideIntent, INTO_GUIDE);
			} else if (!hasLogin()) {
				// 如果不是第一次登录，
				// 但并不是已经登录（自动登录），跳转到登录界面，并关闭当前页面
				Intent guideIntent = new Intent(LaunchActivity.this,
					LoginActivity.class);
				startActivity(guideIntent);
				finish();

			} else if(isBlackLists){

            }else {
				// 如果处于已经登录状态（自动登录），
				// 获取当前登录的用户信息
				UserInfo info = AccountManager.getCurrent(LaunchActivity.this);

				if (info == null) {
					// 如果用户信息为空跳转到登录页面
					Intent intent = new Intent(LaunchActivity.this,
						LoginActivity.class);
					startActivity(intent);

				} else if (TextUtils.isEmpty(info.getName())) {
					// 如果用户的名字为空，跳转到个人中心页面
					Intent personalIntent = new Intent(LaunchActivity.this,
						PersonalHomeActivity.class);
					// 做好是从启动页面跳转过来的标识，并带上企业id
					personalIntent.putExtra("from", "launch");
					personalIntent.putExtra("custId", info.getCustId());
					startActivity(personalIntent);

				} else if ("1".equals(info.getCustId())
					&& info.getHasApplyCust() == 0) {
					// 如果企业的id为1，或者用户没有申请加入过企业
					// 则跳转到创建企业的界面
					CreateEnterpriseActivity.launch(LaunchActivity.this);

				} else {
					// 除了以上各种情况，
					// 直接跳转到主界面
					Intent personalIntent = new Intent(LaunchActivity.this,
						MainActivity.class);
					startActivity(personalIntent);
					// MainActivity.launchAndSelectTab(LaunchActivity.this, 1);
				}
				// 只要处于已经登录状态，任何一种情况最后都要关闭当前界面
				finish();
			}
		}
	};

	private void onCreateAsync() {
		// mContext = this;
		// 打开友盟推送消息
		/*
		 * boolean isOpenPushMessage =
		 * StorageUtils.getSystemSettings(mContext).getBoolean
		 * (Config.IS_OPEN_PUSH_MESSAGE, true);
		 * LogUtils.i("==lan isOpenPushMessage = " + isOpenPushMessage);
		 */
		configPush();
		getUserInfo();
		UpdateHelper.reset();
	}

	private void configPush() {
		boolean isOpenPushMessage = StorageUtils.getSystemSettings(this)
			.getBoolean(Config.IS_OPEN_PUSH_MESSAGE, true);

		/*
		 * PushAgent mPushAgent = MainApplication.getPushAgent(); if
		 * (isOpenPushMessage) { mPushAgent.enable(); } else {
		 * mPushAgent.disable(); }
		 */
		// 开启百度推送功能
		// 调用百度推送的api
		PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
			Utils.getMetaValue(this, "api_key"));
		if (isOpenPushMessage) {
			PushManager.resumeWork(this);
		} else {
			// 关闭百度推送
			PushManager.stopWork(this);
		}

	}

	private void getUserInfo() {
		mSharedPrefs = getSharedPreferences("account", MODE_PRIVATE);
		boolean autoLogin = AccountManager.isAutoLogin(this);
		if (!autoLogin) {
			return;
		}
		UserLoginInfo info = AccountManager.getCurrentLogiInfo(this);
		if (info.getUserId() != 0 && !TextUtils.isEmpty(info.getPhone())
			&& !TextUtils.isEmpty(info.getPassword())) {
			MainActivity.userName = info.getPhone();
			LOGO_ICON = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/yijitong/"
				+ MainActivity.userName
				+ "/company_logo.jpg";
			File file = new File(LOGO_ICON);
			UserInfo userInfo = AccountManager.getCurrent(this);
			if (userInfo == null
				|| (TextUtils.isEmpty(userInfo.getSplash480()) && TextUtils
					.isEmpty(userInfo.getSplash720()))) {
				// logoIv.post(new Runnable() {
				//
				// @Override
				// public void run() {
				// logoIv.setImageResource(R.drawable.splash);
				// }
				// });

			} else {
				if (file.exists()) {
					final Drawable d = new BitmapDrawable(getResources(),
						file.getAbsolutePath());
					mIvLogo.post(new Runnable() {

						@Override
						public void run() {
							mIvLogo.setBackgroundDrawable(d);
						}
					});
				}
			}
			login(info);
		}
	}

	private void login(final UserLoginInfo loginInfo) {
		ApiHelper.realLogin(new Listener<Response<UserInfo>>() {

			@Override
			public void onResponse(Response<UserInfo> response) {
				if (response.getCode() == 0) {
                    isBlackLists = false;
					UserInfo info = response.getPayload();
					if (info != null) {
						final long userId = info.getId();
						final String custId = info.getCustId();
						// 百度推送需要的phone tag
						final String phone = info.getPhone();
						new Thread() {
							public void run() {
								// 友盟使用的setAlias
								// MainApplication.setAlias("" + userId);
								// 百度推送使用的setAlias
								MainApplication.setAlias("" + userId, phone);
								MainApplication.addTag("cust" + custId);
								MainApplication
									.sendLoginBroadcast(getApplicationContext());
							};
						}.start();
					}
					if (info != null) {
						savaLogo(info, LaunchActivity.this,
							loginInfo.getPhone(),
							getResources().getString(R.string.screen_width));
					}
				}else if (response.getCode() == Response.RESPONSE_NOT_YOUR_ICCID||response.getCode() == Response.RESPONSE_OTHER_ICCID) {
					isBlackLists = false;
                    LeaveEnterpriseActivity.launch(response.getDescription());
				}else{
                    showDialogLogin(response.getDescription());
                }
			}

			@Override
			public void onErrorResponse(InvocationError arg0) {
				// TODO Auto-generated method stub

			}
		}, loginInfo);
	}

    private void showDialogLogin(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        finish();
        isBlackLists = true;
        LoginActivity.launch(LaunchActivity.this);
//        AlertDialogBuilder
//                .newBuilder(this)
//                .setTitle(getResources().getString(R.string.dialog_title))
//                .setMessage(message)
//                .setPositiveButton(
//                        getResources().getString(R.string.dialog_sure), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                                isBlackLists = true;
//                                LoginActivity.launch(LaunchActivity.this);
//                            }
//                        })
//                .show();
    }

	public static void savaLogo(UserInfo info, Activity activity,
		String loginName, String width) {
		String newLogoPath = null;
		String oldLogoPath = null;
		String old720 = null;
		String old480 = null;
		String key = "720p";
		MainActivity.userName = info.getPhone();
		UserInfo oldUserInfo = AccountManager.getCurrent(activity);
		if (oldUserInfo != null) {
			old720 = oldUserInfo.getSplash720();
			old480 = oldUserInfo.getSplash480();
			if ("360dip".equals(width)) {
				oldLogoPath = old720;
				key = "720p";
			} else {
				oldLogoPath = old480;
				key = "4800p";
			}
		}
		if ("360dip".equals(width)) {
			newLogoPath = info.getSplash720();
			key = "720p";
			info.setSplash480(info.getSplash480());
		} else {
			newLogoPath = info.getSplash480();
			info.setSplash720(info.getSplash720());
			key = "4800p";
		}
		AccountManager.updateUserInfo(activity, info);
		if (TextUtils.isEmpty(oldLogoPath)) {
			try {
				downloadLogoIcon(key, oldLogoPath, newLogoPath, activity);
			} catch (Exception e) {
			}
		} else {
			File logoFile = new File(LOGO_ICON);
			if (!oldLogoPath.equals(newLogoPath) || !logoFile.exists()) {
				try {
					downloadLogoIcon(key, oldLogoPath, newLogoPath, activity);
				} catch (Exception e) {
				}
			}
		}
	}

	private static void downloadLogoIcon(final String key, String oldPath,
		final String newPath, final Context context) throws Exception {
		File logoFile = new File(LOGO_ICON);
		File parent = logoFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		FileClient client = FileClientFactory
			.createSingleThreadFileClient(context);
		client.download(newPath, logoFile, new ProgressListener<File>() {

			@Override
			public void onError(InvocationError arg0) {
				UserInfo info = AccountManager.getCurrent(context);
				info.setSplash720("");
				info.setSplash480("");
				AccountManager.updateUserInfo(context, info);
			}

			@Override
			public void onFinished(File arg0) {
				UserInfo info = AccountManager.getCurrent(context);
				if ("720p".equals(key)) {
					info.setSplash720(newPath);
				} else {
					info.setSplash480(newPath);
				}
				AccountManager.updateUserInfo(context, info);
			}

			@Override
			public void onProgress(long arg0, long arg1) {

			}

			@Override
			public void onStarted() {

			}

		}).start();
		;
	}

	private boolean hasLogin() {
		return AccountManager.isAutoLogin(this);
	}

	/**
	 * 根据条件，选择延迟一段时间后跳转到下一个界面
	 */
	private void switchPageDelay() {
		mHandler = new Handler();
		mHandler.postDelayed(task, 2000);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == INTO_GUIDE) {
			if (hasLogin()&&!isBlackLists) {
				Intent guideIntent = new Intent(LaunchActivity.this,
					MainActivity.class);
				startActivity(guideIntent);
				// MainActivity.launchAndSelectTab(LaunchActivity.this, 1);
			} else {
				Intent guideIntent = new Intent(LaunchActivity.this,
					LoginActivity.class);
				startActivity(guideIntent);
			}
		}
		finish();
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, LaunchActivity.class);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}
}
