package cn.yjt.oa.app.http;

import io.luobo.common.Cancelable;
import io.luobo.common.http.ErrorType;
import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ListenerClient;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;
import io.luobo.common.utils.MD5Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import cn.yjt.oa.app.LeaveEnterpriseActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.ReloginAlertActivity;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.utils.FileUtils;
import cn.yjt.oa.app.utils.TelephonyUtil;

/** 访问网络的异步请求类 */
public class AsyncRequest {

	public static final String MODULE_REGISTER = "register";
	public static final String MODULE_LOGIN = "reallogin";
	// public static final String MODULE_LOGIN = "yjtlogin";
	public static final String MODULE_CLIENT_LOGIN = "clientlogin";
	public static final String MODULE_LOGOUT = "yjtlogout";
	public static final String MODULE_TASK = "tasks";
	public static final String MODULE_TASK_ISREAD = "tasks/%s/isread";
	public static final String MODULE_GROUPS = "groups";
	public static final String MODULE_DOCUMENT = "documents";
	public static final String MODULE_SINGNINS = "signins";
	public static final String MODULE_CLOCKIN = "attendance";
	public static final String MODULE_CONTACTS = "contacts";
	//	public static final String MODULE_CONTACTS_LOCAL = "contacts/local";
    /**操作统计*/
    public static final String MODULE_OPERASTATISTICS= "operastatistics";

	/** 请求所有的通讯录联系人 */
	public static final String MODULE_CONTACTS_ALL = "contacts/all";
	public static final String MODULE_CONTACTS_EXTERNAL = "contacts/external";
	public static final String MODULE_CONTACTS_COMMON = "contacts/common";
	public static final String MODULE_PERSONALINFO = "personalInfo";
	public static final String MODULE_FEEDBACK = "feedbacks";
	public static final String MODULE_APPS = "apps";
	public static final String MODULE_APPS_CARDAPP = "apps/cardapps";
	public static final String MODULE_APPS_BUSINESS = "apps/business";
	public static final String MODULE_APPS_PERSONAL = "apps/personal";
	public static final String MODULE_APPS_RECOMMEND = "apps/recommends";
	public static final String MODULE_NOTICE = "notices";

	public static final String MODULE_NEWS = "news";
	public static final String MODULE_CONSUME = "consumes";
	public static final String MODULE_UPDATE = "upload";
	public static final String MESSAGE_CENTER = "messagecenter";
	public static final String MESSAGE_CENTER_UNREAD = "messagecenter/unread";
	// TODO:
	// 将所有未读消息变为已读的接口
	public static final String MODULE_CLEAR_ALLUNREAD = "messagecenter/isread";
	// 获取位置解耦状态的接口：
	public static final String MODULE_AREADE_COUPLING_STATUS = "/custsign/%s/areadecoupling/status";
	// 获取考勤汇总信息的接口
	public static final String MODULE_ATTENDANCE_SUMMARY = "attendance/summary/statis";

	/** 提交会议的请求接口 */
	public static final String MODULE_MEETING = "meetings";
	/** 更新会议 */
	public static final String MODULE_MEETING_UPDATE = "meetings/%s";
	/** 获取个人创建会议列表的请求 */
	public static final String MODULE_MEETING_PUBLIC = "meetings/created";
	/** 获取个人参加会议列表的请求 */
	public static final String MODULE_MEETING_JOIN = "meetings/attended";
	/** 删除会议信息 */
	public static final String MODULE_MEETING_DELETE = "meetings/%s";
	/** 会议考勤 */
	public static final String MODULE_MEETING_SIGNIN = "meetingsign/%s";
	/** 获取会议签到用户列表 */
	public static final String MODULE_MEETING_SIGNIN_LIST = "meetingsign/%s";
	/** 更新会议二维码 */
	public static final String MODULE_MEETING_QRCODE = "meetings/%s/barcode";
	/** 下载会议签到用户列表 */
	public static final String MODULE_MEETING_EXPORT = "meetingsign/%s/export";
	/** 发送签到用户列表到邮箱 */
	public static final String MODULE_MEETING_EMAIL = "meetingsign/%s/sendmail";

	public static final String MODULE_UPLOAD_FILE = "upload/file";
	public static final String MODULE_UPLOAD_IMAGE = "upload/image";

	public static final String MODULE_CUSTS_BYNAME = "custs/byname";
	public static final String MODULE_CUSTS_APPLIES = "custusers/applies";
	public static final String MODULE_CUSTS_APPLIES_ID = "custusers/applies/%s";
	public static final String MODULE_CUSTS_REGISTERINFO_ID = "custs/registerInfo/%s";
	public static final String MODULE_CUSTS_USERS = "custs/%s/users";
	public static final String MODULE_CUSTS = "custs";
	public static final String MODULE_CUSTS_ID_BASEINFO = "custs/%s/baseinfo";
	public static final String MODULE_CUSTS_INVITES = "custusers/invites";
	public static final String MODULE_NFCTAGS = "custsign/%s/tags";
	public static final String MODULE_NFDTAGS_DELETE = "custsign/%s/tags";
	public static final String MODULE_INDUSTRYTAGS = "industrytags";
	public static final String MODULE_CUSTS_ATTENDACE = "custs/attendance";

    /**巡检报表发邮箱*/
    public static final String MODULE_PATROL_SENDEMAIL = "inspectattendance/sendmail";
    /**巡检报表导出*/
    public static final String MODULE_PATROL_EXPORT = "inspectattendance/export";
	public static final String MODULE_CUSTS_SIGNCOMMON = "custsign/%s/areas";
	public static final String MODULE_CUSTS_SIGNCOMMON_LISTS = "custsign/%s/areas/lists";
	public static final String MODULE_CUSTS_CUSTLIST = "custlist";
	public static final String MODULE_SIGNIN_ATTENDACE = "signins/attendances";
	public static final String MODULE_ATTENDANCE_CARDLIST = "attendance/cardlist";
	public static final String MODULE_CUSTS_DEPTS = "custs/%s/depts/detail";
	/** 考勤属性查询（考勤地域和时间段） */
	public static final String MODULE_ATTENDANCE_DUTY = "attendance/duty";
	public static final String MODULE_CUSTS_UPDATETIMES = "custs/updatetimes";
	public static final String MODULE_INVITEUSER = "inviteuser";
	public static final String MODULE_INVITEUSER_INVITEID = "inviteuser/%s";
	public static final String MODULE_SWITCHCUST = "switchcust";
	public static final String MODULE_CONFIG = "modules/config";
	public static final String MODULE_CUSTS_CLOSE = "custs/close";
	public static final String MODULE_CHECKICCID = "checkiccid";
	public static final String MODULE_CUSTSIGNIN_BEACONS = "custsign/%s/beacons";
	public static final String MODULE_CUSTSIGNIN_TIMES = "custsign/%s/times";
	public static final String MODULE_CUSTSIGNIN_AREA_USER = "custsign/%s/areas/%s/users";
	public static final String MODULE_CUSTSIGNIN_TIME_STATUS = "custsign/%s/times/status";
	public static final String MODULE_CUSTSIGNIN_USER_BEACONS = "custsign/%s/users/%s/beacons";
	public static final String MODULE_CUSTSIGNIN_USER_TIMES = "custsign/%s/users/%s/times";
	public static final String MODULE_WHITELIST = "whitelist/phone";
	public static final String MODULE_UPDATEPASSWORD = "";

    /*考勤报表导出*/
    /**考勤汇总表*/
    public static final String MODULE_CUSTSIGN_SUMMARY = "custsign/%s/statis/summary";
    /**考勤明细表*/
    public static final String MODULE_CUSTSIGN_DETAIL = "custsign/%s/statis/detail";
    /**考勤违纪表*/
    public static final String MODULE_CUSTSIGN_DISOBEY = "custsign/%s/statis/detail/disobey";
    /**考勤签到明细表*/
    public static final String MODULE_CUSTSIGN_CARD = "custsign/%s/statis/card";
    /*上面四个无法单独使用，须在后面拼上下面两个中的其中任意一个*/
    /**下载*/
    public static final String ATTENDANCE_DOWNLOAD = "/download";
    /**发送邮箱*/
    public static final String ATTENDANCE_SENDMAIL = "/sendmail";


	public static final int ERROR_CODE_OK = 0;
	public static final int ERROR_CODE_LOGIN_INVALID = 2100;
	public static final int ERROR_CODE_INVALID_USER = 2002;
	public static final int ERROR_CODE_LEAVE_ENTERPRISE = 2004;

	// 定义简单的日期格式
	@SuppressLint("SimpleDateFormat")
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private Request request;

	private AsyncRequest(Request request) {
		this.request = request;
	}

	/** get方式请求 */
	public Cancelable get() {
		final Listener orgListener = request.responseListener;
		Cancelable cancelable = new ReloginTask() {

			@Override
			protected void onLoginFailed(InvocationError error) {
				orgListener.onErrorResponse(error);
			}

			@Override
			protected Cancelable doTask() {
				ListenerClient client = HttpClientCreator.getClientCreator().newListenerClient();
				return client.get(request.url, request.requestHeaders, request.responseType, new LoginCheckListener(
					orgListener, this));
			}

		}.start();
		checkListener(orgListener, cancelable);
		return cancelable;
	}

	public Cancelable post() {
		final Listener orgListener = request.responseListener;
		Cancelable cancelable = new ReloginTask() {

			@Override
			protected void onLoginFailed(InvocationError error) {
				orgListener.onErrorResponse(error);
			}

			@Override
			protected Cancelable doTask() {
				ListenerClient client = HttpClientCreator.getClientCreator().newListenerClientNoRetry();
				return client.post(request.url, request.requestHeaders, request.requestBody, request.responseType,
					new LoginCheckListener(orgListener, this));
			}

		}.start();
		checkListener(orgListener, cancelable);
		return cancelable;
	}

	private void checkListener(final Listener orgListener, Cancelable cancelable) {
		if (orgListener instanceof ProgressDialogResponseListener) {
			ProgressDialogResponseListener<?> dialogResponseListener = (ProgressDialogResponseListener<?>) orgListener;
			dialogResponseListener.onRequest(cancelable);
		}
	}

	public Cancelable put() {
		final Listener orgListener = request.responseListener;
		Cancelable cancelable = new ReloginTask() {

			@Override
			protected void onLoginFailed(InvocationError error) {
				orgListener.onErrorResponse(error);
			}

			@Override
			protected Cancelable doTask() {
				ListenerClient client = HttpClientCreator.getClientCreator().newListenerClientNoRetry();
				return client.put(request.url, request.requestHeaders, request.requestBody, request.responseType,
					new LoginCheckListener(orgListener, this));
			}

		}.start();
		checkListener(orgListener, cancelable);
		return cancelable;
	}

	public Cancelable delete() {
		final Listener orgListener = request.responseListener;
		Cancelable cancelable = new ReloginTask() {

			@Override
			protected void onLoginFailed(InvocationError error) {
				orgListener.onErrorResponse(error);
			}

			@Override
			protected Cancelable doTask() {
				ListenerClient client = HttpClientCreator.getClientCreator().newListenerClientNoRetry();
				return client.delete(request.url, request.requestHeaders, request.responseType, new LoginCheckListener(
					orgListener, this));
			}

		}.start();
		checkListener(orgListener, cancelable);
		return cancelable;
	}

	private class LoginCheckListener implements Listener<Object> {

		private Listener orgListener;
		private ReloginTask task;

		LoginCheckListener(Listener listener, ReloginTask task) {
			this.orgListener = listener;
			this.task = task;
		}

		@Override
		public void onErrorResponse(InvocationError arg0) {
			orgListener.onErrorResponse(arg0);
		}

		@Override
		public void onResponse(Object response) {
			if (response instanceof Response<?>) {
				int errorCode = ((Response<?>) response).getCode();
				if (ERROR_CODE_LOGIN_INVALID == errorCode) {
					task.loginAndRetry();
					return;
				}
			}
			orgListener.onResponse(response);
		}

	}

	private abstract class ReloginTask implements Runnable {
		Cancelable currentTask;
		boolean isCanceled;

		public Cancelable start() {
			run();
			return new Cancelable() {

				@Override
				public boolean cancel() {
					if (currentTask != null)
						return currentTask.cancel();
					isCanceled = true;
					return isCanceled;
				}
			};
		}

		@Override
		public void run() {
			if (isCanceled)
				return;
			Cancelable task = doTask();
			currentTask = task;
		}

		public void loginAndRetry() {
			// Toast.makeText(MainApplication.getAppContext(), "Relogin",
			// Toast.LENGTH_SHORT).show();
			if (isCanceled)
				return;
			Cancelable task = login(new Listener<Response<UserInfo>>() {

				@Override
				public void onErrorResponse(InvocationError error) {
					onLoginFailed(error);
				}

				@Override
				public void onResponse(Response<UserInfo> response) {
					if (response.getCode() == ERROR_CODE_OK) {
						run();
					} else if (response.getCode() == ERROR_CODE_INVALID_USER) {
						ReloginAlertActivity.alertAndClearTask();
						onLoginFailed(new InvocationError(ErrorType.UNKNOWN_ERROR));
					} else if (response.getCode() == ERROR_CODE_LEAVE_ENTERPRISE) {
						LeaveEnterpriseActivity.launch(response.getDescription());
						onLoginFailed(new InvocationError(ErrorType.UNKNOWN_ERROR));
					} else {
						//LeaveEnterpriseActivity.launch(response.getDescription());
						onLoginFailed(new InvocationError(ErrorType.UNKNOWN_ERROR));
					}
				}
			});

			if (task == null) {
				// login cannot execute should login manually here
				loginManually();
				onLoginFailed(new InvocationError(ErrorType.AUTH_FAILURE_ERROR));
			}
			currentTask = task;
		}

		protected abstract void onLoginFailed(InvocationError error);

		protected abstract Cancelable doTask();

	}

	/** 请求对象 */
	public static class Request {
		/** 请求路径 */
		String url;
		/** 请求体 */
		Object requestBody;
		/** 请求类型 */
		Type responseType;
		/** 响应接口 */
		Listener<?> responseListener;
		/** 请求头 */
		Map<String, String> requestHeaders = BusinessConstants.getBusinessHeaders();

		// --------------------get、set方法---------------------
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Object getRequestBody() {
			return requestBody;
		}

		public void setRequestBody(Object requestBody) {
			this.requestBody = requestBody;
		}

		public Type getResponseType() {
			return responseType;
		}

		public void setResponseType(Type responseType) {
			this.responseType = responseType;
		}

		public Listener<?> getResponseListener() {
			return responseListener;
		}

		public void setResponseListener(Listener<?> responseListener) {
			this.responseListener = responseListener;
		}

		public Map<String, String> getRequestHeaders() {
			return requestHeaders;
		}

		public void setRequestHeaders(Map<String, String> requestHeaders) {
			this.requestHeaders = requestHeaders;
		}
		// ------------------------------------------------
	}

	/**
	 * 
	 *
	 */
	public static class Builder {
		/** 自定义模块 */
		private String customModule;
		/** 模块 */
		private String module;
		/** 模块条目 */
		private String moduleItem;
		/** 请求参数 */
		private Map<String, String> queryParameters;
		/** 请求对象 */
		private Request request = new Request();

		/**
		 * 创建AsyncRequest 对象
		 * @return AsyncRequest
		 */
		public AsyncRequest build() {
			// 请求的链接
			StringBuilder url = null;

			// 拼接url
			if (TextUtils.isEmpty(customModule)) {
				url = new StringBuilder(BusinessConstants.getBusinessUrl());
			} else {
				url = new StringBuilder(customModule);
			}
			url.append(module);
			if (!TextUtils.isEmpty(moduleItem)) {
				url.append("/");
				url.append(moduleItem);
			}
			if (queryParameters != null && queryParameters.size() > 0) {
				url.append("?");
				boolean isFirst = true;
				for (String name : queryParameters.keySet()) {
					if (!isFirst)
						url.append("&");
					url.append(name);
					url.append("=");
					try {
						url.append(URLEncoder.encode(queryParameters.get(name), "utf-8"));
					} catch (UnsupportedEncodingException e) {
						// ignore
					}
					isFirst = false;
				}
			}

			// 将url转成字符串赋值给request对象的url属性
			request.url = url.toString();
			request.requestHeaders.put("clientVersion", "android_" + MainApplication.getVersionCode());
			Log.v("AsyncRequest", request.url);
			return new AsyncRequest(request);
		}

		/**
		 * 添加分页加载参数
		 * @param from 显示页
		 * @param max 总页数
		 * @return
		 */
		public Builder addPageQueryParameter(int from, int max) {
			initQueryParameter();
			queryParameters.put("from", String.valueOf(from));
			queryParameters.put("max", String.valueOf(max));
			return this;
		}

		/**
		 * 添加日期类型的参数
		 * @param beginDate 开始日期
		 * @param endDate 结束日期
		 * @return
		 */
		public Builder addDateQueryParameter(Date beginDate, Date endDate) {
			initQueryParameter();
			queryParameters.put("beginDate", DATE_FORMAT.format(beginDate));
			queryParameters.put("endDate", DATE_FORMAT.format(endDate));
			return this;
		}
        /**
         * 添加日期类型的参数
         * @param beginDate 开始日期
         * @param endDate 结束日期
         * @return
         */
        public Builder addDateQueryParameterStartTime(Date beginDate, Date endDate) {
            initQueryParameter();
            queryParameters.put("startDate", DATE_FORMAT.format(beginDate));
            queryParameters.put("endDate", DATE_FORMAT.format(endDate));
            return this;
        }

		/**
		 * 添加参数
		 * @param name 参数名
		 * @param value 参数的值
		 * @return
		 */
		public Builder addQueryParameter(String name, String value) {
			initQueryParameter();
			queryParameters.put(name, value);
			return this;
		}

		/**
		 * 添加多个参数
		 * @param queryParameters 存放参数的map集合
		 * @return
		 */
		public Builder addQueryParameters(Map<String, String> queryParameters) {
			if (queryParameters == null) {
				this.queryParameters = new HashMap<String, String>();
			}
			this.queryParameters = queryParameters;
			return this;
		}

		/** 初始化请求参数 */
		private void initQueryParameter() {
			if (queryParameters == null)
				queryParameters = new HashMap<String, String>();
		}

		// -----------各种属性的设置------------
		public Builder setCustomModule(String customModule) {
			this.customModule = customModule;
			return this;
		}

		public Builder setModule(String module) {
			this.module = module;
			return this;
		}

		public Builder setModuleItem(String moduleItem) {
			this.moduleItem = moduleItem;
			return this;
		}

		public Builder setRequestBody(Object requestBody) {
			request.requestBody = requestBody;
			return this;
		}

		public Builder setResponseType(Type responseType) {
			request.responseType = responseType;
			return this;
		}

		public Builder setResponseListener(final Listener<?> listener) {
			request.responseListener = listener;
			return this;
		}

		public Builder setRequestHeaders(Map<String, String> requestHeaders) {
			request.requestHeaders = requestHeaders;
			return this;
		}

		public Builder addRequestHeaders(Map<String, String> requestHeaders) {
			request.requestHeaders.putAll(requestHeaders);
			return this;
		}
		// ---------------------------------
	}

	public Request getRequest() {
		return request;
	}

	public static Cancelable login(final Listener<Response<UserInfo>> listener) {
		UserLoginInfo info = AccountManager.getCurrentLogiInfo(MainApplication.getAppContext());
		if (info.getUserId() != 0 && !TextUtils.isEmpty(info.getPassword())) {
			info.setIccid(TelephonyUtil.getICCID(MainApplication.getAppContext()));
			AsyncRequest.Builder builder = new AsyncRequest.Builder();
			builder.setModule(AsyncRequest.MODULE_LOGIN);
			builder.setRequestBody(info);
			Type type = new TypeToken<Response<UserInfo>>() {
			}.getType();
			builder.setResponseType(type);
			builder.setResponseListener(new Listener<Response<UserInfo>>() {

				@Override
				public void onResponse(Response<UserInfo> response) {
					if (response.getCode() == ERROR_CODE_OK) {
						UserInfo userInfo = response.getPayload();
						if (userInfo != null) {
							AccountManager.updateUserInfo(MainApplication.getAppContext(), userInfo);
						}
						MainApplication.sendLoginBroadcast(MainApplication.getAppContext());
					}

					listener.onResponse(response);
				}

				@Override
				public void onErrorResponse(InvocationError error) {
					listener.onErrorResponse(error);
				}
			});
			return builder.build().post();
		}

		return null;
	}

	public static void loginManually() {

	}

	// 获取网络图片
	public static void getBitmap(String url, final Listener<Bitmap> listener) {
		// final ImageClient iClient = HttpClientCreator.getClientCreator()
		// .newImageClient();
		// return iClient.getInListener(url, listener);
		MainApplication.getImageLoader().get(url, new ImageLoaderListener() {

			@Override
			public void onSuccess(ImageContainer container) {
				// TODO Auto-generated method stub
				listener.onResponse(container.getBitmap());
			}

			@Override
			public void onError(ImageContainer container) {
				listener.onErrorResponse(new InvocationError(ErrorType.UNKNOWN_ERROR, container.getE()));
			}
		});
	}

	// 获取网络图片
	public static void getInImageView(String url, final ImageView imageView, int defaultImageResId,
		final int errorImageResId) {
		// final ImageClient iClient = HttpClientCreator.getClientCreator()
		// .newImageClient();
		// return iClient.getInImageView(url, imageView, defaultImageResId,
		// errorImageResId);
		imageView.setTag(url);
		imageView.setImageResource(defaultImageResId);
		MainApplication.getImageLoader().get(url, new ImageLoaderListener() {

			@Override
			public void onSuccess(ImageContainer container) {
				if (imageView.getTag().equals(container.getUrl())) {
					imageView.setImageBitmap(container.getBitmap());
				}
			}

			@Override
			public void onError(ImageContainer container) {
				if (imageView.getTag().equals(container.getUrl())) {
					imageView.setImageResource(errorImageResId);
				}
			}
		});
	}

	// 获取网络图片
	public static void getInImageView(String url, final ImageView imageView, int maxWidth, int maxHeight,
		int defaultImageResId, final int errorImageResId) {
		imageView.setTag(url);
		imageView.setImageResource(defaultImageResId);
		MainApplication.getImageLoader().get(url, maxWidth, maxHeight, new ImageLoaderListener() {

			@Override
			public void onSuccess(ImageContainer container) {
				if (imageView.getTag().equals(container.getUrl())) {
					imageView.setImageBitmap(container.getBitmap());
				}
			}

			@Override
			public void onError(ImageContainer container) {
				if (imageView.getTag().equals(container.getUrl())) {
					imageView.setImageResource(errorImageResId);
				}
			}
		});
	}

	public static boolean checkNetwork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = con.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			return activeNetworkInfo.isConnected();
		} else {
			return false;
		}
	}

	public static Cancelable upload(final Context context, final Uri uri, final String url, final String fileType,
		final int width, final int height, final boolean deleteAfterUpload,
		final ProgressListener<Response<String>> listener, final FileClient createSingleThreadFileClient) {
		File file = null;
		if ("file".equals(uri.getScheme())) {
			file = new File(uri.getPath());
		} else if ("content".equals(uri.getScheme())) {

			Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA },
				null, null, null);
			cursor.moveToFirst();
			String string = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
			cursor.close();
			if (string != null) {
				file = new File(string);
				File dist = FileUtils.createFileInUserFolder(file.getName());
				if (FileUtils.copy(file, dist)) {
					file = dist;
				}
			} else {
				final File imageFile = FileUtils.createFileInUserFolder(MD5Utils.md5(url) + ".img");
				if (!imageFile.exists()) {
					final ImageUploadCancelable cancelable = new ImageUploadCancelable();

					new AsyncTask<Void, Void, Void>() {
						private ProgressDialog progressDialog;

						@Override
						protected void onPreExecute() {
							if (context instanceof Activity) {
								if ("image".equals(fileType)) {

									progressDialog = ProgressDialog.show(context, null, "正在读取图片...");
									progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

										@Override
										public void onCancel(DialogInterface dialog) {
											cancel(true);
										}
									});
								}
							}

						}

						@Override
						protected Void doInBackground(Void... params) {
							try {
								FileUtils.copy(context.getContentResolver().openInputStream(uri), imageFile);
								if ("image".equals(fileType)) {

									FileUtils.compressImageFile(imageFile.getAbsolutePath(), width, height);
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
							if (!isCancelled()) {
								Cancelable cancelable2 = upload(createSingleThreadFileClient, url, fileType, imageFile,
									deleteAfterUpload, listener);
								cancelable.setCancelable(cancelable2);
							}
						}

					}.execute();
					return cancelable;
				} else {
					file = imageFile;
				}
			}
		}
		final File uploadFile = file;
		if ("image".equals(fileType)) {
			final ImageUploadCancelable cancelable = new ImageUploadCancelable();

			new AsyncTask<Void, Void, Void>() {
				private ProgressDialog progressDialog;

				@Override
				protected void onPreExecute() {
					if (context instanceof Activity) {

						progressDialog = ProgressDialog.show(context, null, "正在压缩图片...");
						progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								cancel(true);
							}
						});
					}

				}

				@Override
				protected Void doInBackground(Void... params) {
					FileUtils.compressImageFile(uploadFile.getAbsolutePath(), width, height);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					if (progressDialog != null && progressDialog.isShowing()) {
						Activity activity = (Activity) context;
						if (activity.isFinishing()) {
							return;
						}
						progressDialog.dismiss();
					}
					System.out.println("uploadFile:" + uploadFile.getAbsolutePath());
					if (!isCancelled()) {
						Cancelable cancelable2 = upload(createSingleThreadFileClient, url, fileType, uploadFile,
							deleteAfterUpload, listener);
						cancelable.setCancelable(cancelable2);
					}
				}

			}.execute();
			return cancelable;
		} else {
			return upload(createSingleThreadFileClient, url, fileType, uploadFile, deleteAfterUpload, listener);
		}

	}

	private static class ImageUploadCancelable implements Cancelable {
		private Cancelable cancelable;

		public void setCancelable(Cancelable cancelable) {
			this.cancelable = cancelable;
		}

		@Override
		public boolean cancel() {

			if (cancelable != null) {
				return cancelable.cancel();
			}
			return false;
		}

	}

	private static Cancelable upload(FileClient createSingleThreadFileClient, String url, String fileType,
		final File uploadFile, final boolean deleteAfterUpload, final ProgressListener<Response<String>> listener) {
		try {
			return createSingleThreadFileClient.upload(url, fileType, uploadFile, new TypeToken<Response<String>>() {
			}.getType(), new ProgressListener<Response<String>>() {

				@Override
				public void onError(InvocationError arg0) {
					listener.onError(arg0);
				}

				@Override
				public void onProgress(long arg0, long arg1) {
					listener.onProgress(arg0, arg1);
				}

				@Override
				public void onStarted() {
					listener.onStarted();
				}

				@Override
				public void onFinished(Response<String> arg0) {
					listener.onFinished(arg0);
					if (deleteAfterUpload) {
						uploadFile.delete();
					}
				}
			});
		} catch (InvocationError e) {
			e.printStackTrace();
		}
		return null;
	}
}
