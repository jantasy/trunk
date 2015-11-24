package cn.yjt.oa.app.attendance;

import cn.yjt.oa.app.location.BDLocationServer;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.location.BaseLocationServer;
import cn.yjt.oa.app.signin.AttendanceActivity;
import cn.yjt.oa.app.utils.LocationUtil;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.TelephonyUtil;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;


/**考勤的后台服务*/
public class AttendanceService extends Service {
	//定义一个handler（静态？）
	Handler handler;
	private SharedPreferences mSp;

	//-----------------生命周期方法-------------------
	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler(getMainLooper());
		mSp = getSharedPreferences("areadeCoupling", MODE_PRIVATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(final Intent intent) {

		return new AttendanceBinder() {

			//
			private AttendanceListener attendanceListener;

			@Override
			public void onBind(AttendanceListener listener) {
				this.attendanceListener = listener;
				final SigninInfo signinInfo = intent.getParcelableExtra("SigninInfo");
				if (signinInfo != null) {
					String[] temp = mSp.getString("areade_coupling", "0,false,-1").split(",");
					//当是定位考勤或者企业和考勤区域位置没有解耦的情况下，获取位置信息
					if (!Boolean.valueOf(temp[1])
							|| SigninInfo.SINGIN_TYPE_VISIT.equals(signinInfo.getType())
							|| SigninInfo.SIGNIN_TYPE_QR.equals(signinInfo.getType())) {
						attendanceListener.onLocating();
                        BDLocationServer.getInstance().startLocation(new BDLocationServer.YjtLocationListener() {
                            @Override
                            public void locating() {

                            }

                            @Override
                            public void locationSuccess(final BDLocation bdLocation) {
                                handler.post(new Runnable() {
									@Override
									public void run() {
										if (bdLocation != null&& (bdLocation.getLatitude() != 0 && bdLocation
														.getLongitude() != 0)) {
											postSigninInfo(signinInfo, bdLocation);
										} else {
											attendanceListener.onLocationFailure();
										}
									}
								});
                            }

                            @Override
                            public void locationFailure(BDLocation bdLocation) {
                                attendanceListener.onLocationFailure();
                            }

                            private void postSigninInfo(final SigninInfo signinInfo, BDLocation location) {
								attendanceListener.onRequesting();
								postSiginInfoToServer(prepostSigninInfo(signinInfo, location),
										new Listener<Response<SigninInfo>>() {

											@Override
											public void onErrorResponse(InvocationError arg0) {
												attendanceListener.onError();
											}

											@Override
											public void onResponse(Response<SigninInfo> arg0) {
												attendanceListener.onResponse(arg0);
											}
										});
							}
                        });
//						BaseLocationServer.startLocation(new MULLocationListener() {
//
//							@Override
//							public void onResponse(final MULLocation location) {
//								handler.post(new Runnable() {
//									@Override
//									public void run() {
//										if (location != null
//												&& location.getLocationResponse() != null
//												&& (location.getLatitude() != 0 && location
//														.getLongitude() != 0)) {
//											postSigninInfo(signinInfo, location);
//										} else {
//											attendanceListener.onLocationFailure();
//										}
//									}
//								});
//							}
//
//							private void postSigninInfo(final SigninInfo signinInfo, MULLocation location) {
//								attendanceListener.onRequesting();
//								postSiginInfoToServer(prepostSigninInfo(signinInfo, location),
//										new Listener<Response<SigninInfo>>() {
//
//											@Override
//											public void onErrorResponse(InvocationError arg0) {
//												attendanceListener.onError();
//											}
//
//											@Override
//											public void onResponse(Response<SigninInfo> arg0) {
//												attendanceListener.onResponse(arg0);
//											}
//										});
//							}
//						});
						//					}
					} else {
						attendanceListener.onRequesting();
						postSiginInfoToServer(prepostSigninInfo(signinInfo, null),
                                new Listener<Response<SigninInfo>>() {

                                    @Override
                                    public void onErrorResponse(InvocationError arg0) {
                                        attendanceListener.onError();
                                    }

                                    @Override
                                    public void onResponse(Response<SigninInfo> arg0) {
                                        attendanceListener.onResponse(arg0);
                                    }
                                });
					}

				}
			}

			@Override
			public void unBind(AttendanceListener listener) {
				this.attendanceListener = new AttendanceListener() {

					@Override
					public void onResponse(Response<SigninInfo> response) {
						if (response.getCode() == 0) {
							SigninInfo signinInfo = response.getPayload();
							if (signinInfo.getSignResult() != SigninInfo.RESULT_NORMAL) {
								AttendanceActivity.launchWithState(getApplicationContext(),
										AttendanceActivity.STATE_REQUEST_RESPONSE, signinInfo.getSignResultDesc());
							}
						} else {
							AttendanceActivity.launchWithState(getApplicationContext(),
									AttendanceActivity.STATE_REQUEST_RESPONSE, response.getDescription());
						}

					}

					@Override
					public void onRequesting() {

					}

					@Override
					public void onLocationSuccess() {

					}

					@Override
					public void onLocationFailure() {
						AttendanceActivity.launchWithState(getApplicationContext(),
								AttendanceActivity.STATE_LOCATION_FAILURE, null);
					}

					@Override
					public void onLocating() {

					}

					@Override
					public void onError() {
						AttendanceActivity.launchWithState(getApplicationContext(), AttendanceActivity.STATE_ERROR,
								null);
					}
				};
			}
		};
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private SigninInfo prepostSigninInfo(SigninInfo signinInfo, BDLocation location) {
		signinInfo.setUserId(AccountManager.getCurrent(this).getId());
		if (location != null) {
			if (SigninInfo.SIGNIN_TYPE_NFC.equals(signinInfo.getType())
					|| SigninInfo.SIGNIN_TYPE_BEACON.equals(signinInfo.getType())) {
				signinInfo.setActrualPOI(location.getAddrStr());
			} else {
				signinInfo.setPositionDescription(location.getAddrStr());
			}
			final LatLng latLng = LocationUtil.changePointGCJ02(new LatLng(location.getLatitude(),
					location.getLongitude()));

			signinInfo.setPositionData(latLng.latitude + "," + latLng.longitude);
			signinInfo.setIccId(TelephonyUtil.getICCID(this));
		} else {
			LogUtils.e("AttendanceService", "开启位置解耦");
		}
		return signinInfo;
	}

	private void postSiginInfoToServer(SigninInfo signinInfo, Listener<Response<SigninInfo>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_SIGNIN_ATTENDACE);
		builder.setRequestBody(signinInfo);
		Type type = new TypeToken<Response<SigninInfo>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(listener);
		builder.build().post();
	}

	public abstract static class AttendanceBinder extends Binder {
		public abstract void onBind(AttendanceListener listener);

		public abstract void unBind(AttendanceListener listener);
	}

	/**
	 * 考勤的请求接口
	 *
	 */
	public static interface AttendanceListener {
		public void onLocating();

		public void onLocationSuccess();

		public void onLocationFailure();

		public void onRequesting();

		public void onResponse(Response<SigninInfo> response);

		public void onError();
	}

}
