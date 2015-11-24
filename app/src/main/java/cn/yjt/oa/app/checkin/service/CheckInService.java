package cn.yjt.oa.app.checkin.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.checkin.CheckInActivity;
import cn.yjt.oa.app.checkin.binder.CheckInBinder;
import cn.yjt.oa.app.checkin.interfaces.ICheckInType;
import cn.yjt.oa.app.checkin.interfaces.OnCheckInListener;
import cn.yjt.oa.app.location.BDLocationServer;
import cn.yjt.oa.app.location.BaseLocationServer;
import cn.yjt.oa.app.utils.LocationUtil;
import cn.yjt.oa.app.utils.TelephonyUtil;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

/**
 * Created by xiong on 2015/9/30.
 */
public abstract class CheckInService extends Service {

    private Handler mHander;

    private OnCheckInListener onCheckInListener;
    private ICheckInType mInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        mHander = new Handler(getMainLooper());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(final Intent intent) {

        mInfo = intent.getParcelableExtra(CheckInActivity.CHECKIN_INFO);

        return new CheckInBinder() {
            @Override
            public void onBind(OnCheckInListener listener) {
                onCheckInListener = listener;
                if (mInfo != null) {
                    onCheckInListener.onLocating();
                    startLocating();
                }
            }

            @Override
            public void unBind() {
                unBindMethod(intent);
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    /** 开启定位 */
    private void startLocating() {
        BDLocationServer.getInstance().startLocation(new BDLocationServer.YjtLocationListener() {
            @Override
            public void locating() {

            }

            @Override
            public void locationSuccess(BDLocation bdLocation) {
                //定位成功
                locatedSuccess(bdLocation);
            }

            @Override
            public void locationFailure(BDLocation bdLocation) {
                //定位失败
                onCheckInListener.onLocationFailure();
            }
        });
//        BaseLocationServer.startLocation(new MULLocationListener() {
//            @Override
//            public void onResponse(final MULLocation location) {
//                mHander.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (location != null
//                                && location.getLocationResponse() != null
//                                && (location.getLocationResponse().getLat() != 0
//                                && location.getLocationResponse().getLon() != 0)) {
//                            //定位成功
//                            locatedSuccess(location);
//
//                        } else {
//                            //定位失败
//                            onCheckInListener.onLocationFailure();
//                        }
//                    }
//                });
//            }
//        });
    }

    /** 定位成功 */
    private void locatedSuccess(BDLocation location) {

        onCheckInListener.onRequesting();
        packagingInfo(location);
        requestCheckInInfo(mInfo, new Listener<Response<ICheckInType>>() {
            @Override
            public void onResponse(Response<ICheckInType> response) {
                onCheckInListener.onResponse(response);
            }

            @Override
            public void onErrorResponse(InvocationError invocationError) {
                onCheckInListener.onError();
            }
        });
    }

    /** 封装签到对象 */
    private void packagingInfo(BDLocation location) {
        mInfo.setUserId(AccountManager.getCurrent(this).getId());
        if (location != null) {
            if (mInfo.SIGNIN_TYPE_NFC.equals(mInfo.getType())) {
                mInfo.setActrualPOI(location.getAddrStr());
            } else {
                mInfo.setPositionDesc(location.getAddrStr());
            }
            final LatLng latLng = LocationUtil.changePointGCJ02(new LatLng(location.getLatitude(),
                    location.getLongitude()));
            mInfo.setPositionData(latLng.latitude + "," + latLng.longitude);
            mInfo.setIccId(TelephonyUtil.getICCID(this));
        }
    }


    private void unBindMethod(Intent intent) {
        onCheckInListener = new OnCheckInListener() {
            @Override
            public void onResponse(Response<ICheckInType> response) {
                if (response.getCode() == 0) {
                    ICheckInType info = response.getPayload();
                    if (info.getResultCode() != info.RESULT_NORMAL) {
                        requestSuccessUnBind(info);
                    }
                } else {
                    requestOtherCodeUnBind(response);
                }

            }

            @Override
            public void onRequesting() {

            }

            @Override
            public void onLocationFailure() {
                onLocationFailureUnBind();
            }

            @Override
            public void onLocating() {

            }

            @Override
            public void onError() {
                onErrorUnbind();
            }
        };
    }
    /*-----------------------抽象方法----------------------*/

    /** 向服务器提交签到信息 */
    protected abstract void requestCheckInInfo(ICheckInType info, Listener<Response<ICheckInType>> listener);

    /** 未绑定Activity情况下签到失败 */
    protected abstract void onErrorUnbind();

    /** 未绑定Activity情况下定位成功 */
    protected abstract void onLocationFailureUnBind();

    /** 未绑定Activity情况下响应的code不为0 */
    protected abstract void requestOtherCodeUnBind(Response<ICheckInType> response);

    /** 未绑定Activity情况下签到成功 */
    protected abstract void requestSuccessUnBind(ICheckInType info);

}
