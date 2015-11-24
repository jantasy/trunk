package cn.yjt.oa.app.patrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import java.lang.reflect.Type;

import cn.yjt.oa.app.beans.BarCodeAttendanceInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.location.BDLocationServer;
import cn.yjt.oa.app.location.BaseLocationServer;
import cn.yjt.oa.app.patrol.activitys.QrCodeActivity;
import cn.yjt.oa.app.utils.LocationUtil;
import cn.yjt.oa.app.utils.TelephonyUtil;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

/**巡更的后台服务*/
public class QrCodeService extends Service {
	//定义一个handler（静态？）
	Handler handler;

	//-----------------生命周期方法-------------------
	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler(getMainLooper());
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
				final BarCodeAttendanceInfo barCodeAttendanceInfo = intent.getParcelableExtra("barCodeAttendanceInfo");
				if (barCodeAttendanceInfo != null) {
						attendanceListener.onLocating();
                    BDLocationServer.getInstance().startLocation(new BDLocationServer.YjtLocationListener() {
                        @Override
                        public void locating() {

                        }

                        @Override
                        public void locationSuccess(BDLocation bdLocation) {
                            postInspectInfo(barCodeAttendanceInfo, bdLocation);
                        }

                        @Override
                        public void locationFailure(BDLocation bdLocation) {
                            attendanceListener.onLocationFailure();
                        }

                        private void postInspectInfo(final BarCodeAttendanceInfo barCodeAttendanceInfo, BDLocation location) {
                            attendanceListener.onRequesting();
                            postSiginInfoToServer(prepostInspectInfo(barCodeAttendanceInfo, location),
                                    new Listener<Response<BarCodeAttendanceInfo>>() {

                                        @Override
                                        public void onErrorResponse(InvocationError arg0) {
                                            attendanceListener.onError();
                                        }

                                        @Override
                                        public void onResponse(Response<BarCodeAttendanceInfo> arg0) {
                                            attendanceListener.onResponse(arg0);
                                        }
                                    });
                        }
                    });
				}
			}

			@Override
			public void unBind(AttendanceListener listener) {
				this.attendanceListener = new AttendanceListener() {

					@Override
					public void onResponse(Response<BarCodeAttendanceInfo> response) {
						if (response.getCode() == 0) {
							BarCodeAttendanceInfo barCodeAttendanceInfo = response.getPayload();
							if (barCodeAttendanceInfo.getResult() != barCodeAttendanceInfo.RESULT_NORMAL) {
								QrCodeActivity.launchWithState(getApplicationContext(),
										QrCodeActivity.STATE_REQUEST_RESPONSE, barCodeAttendanceInfo.getResultDesc());
							}
						} else {
							QrCodeActivity.launchWithState(getApplicationContext(),
									QrCodeActivity.STATE_REQUEST_RESPONSE, response.getDescription());
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
						QrCodeActivity.launchWithState(getApplicationContext(),
								QrCodeActivity.STATE_LOCATION_FAILURE, null);
					}

					@Override
					public void onLocating() {

					}

					@Override
					public void onError() {
						QrCodeActivity.launchWithState(getApplicationContext(), QrCodeActivity.STATE_ERROR,
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

	private BarCodeAttendanceInfo prepostInspectInfo(BarCodeAttendanceInfo barCodeAttendanceInfo, BDLocation location) {
		if (location != null) {

		    barCodeAttendanceInfo.setPositionDescription(location.getAddrStr());

			final LatLng latLng = LocationUtil.changePointGCJ02(new LatLng(location.getLatitude(),
					location.getLongitude()));

			barCodeAttendanceInfo.setPositionData(latLng.latitude + "," + latLng.longitude);
			barCodeAttendanceInfo.setIccId(TelephonyUtil.getICCID(this));
		}
		return barCodeAttendanceInfo;
	}

	private void postSiginInfoToServer(BarCodeAttendanceInfo barCodeAttendanceInfo, Listener<Response<BarCodeAttendanceInfo>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule("/barcode/attendance");
		builder.setRequestBody(barCodeAttendanceInfo);
		Type type = new TypeToken<Response<BarCodeAttendanceInfo>>() {
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

		public void onResponse(Response<BarCodeAttendanceInfo> response);

		public void onError();
	}

}
