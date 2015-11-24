package cn.yjt.oa.app.location;

import android.content.Context;
import android.location.LocationListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.utils.ToastUtils;

/**
 * Created by xiong on 2015/10/26.
 */
public class BDLocationServer {

    private static BDLocationServer mLocationServer;
    private LocationClient mLocationClient;

    private final int LOCATION_OVERTIME = 3;
    private int mLocationTimes = 0;

    private List<YjtLocationListener> mList = new ArrayList<>();

    private LocationMode mLocationMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";//国测局加密经纬度坐标
    private String tempcoor2 = "bd09ll";//百度加密经纬度
    private String tempcoor3 = "bd09";//百度加密墨卡托坐标



    private BDLocationServer(){
       initLocation();
    }

    public static BDLocationServer getInstance(){
        if(mLocationServer == null){
            synchronized (BDLocationServer.class){
                mLocationServer = new BDLocationServer();
            }
        }
        return mLocationServer;
    }

    public void initLocation(){
        mLocationClient = new LocationClient(MainApplication.getApplication());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("gcj02");
        option.setScanSpan(0);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new MyLocationListener());
    }

    public void startLocation(YjtLocationListener listener){
        if (mList == null){
            mList = new ArrayList<>();
        }
        if(mLocationClient == null){
            initLocation();
        }
        mList.add(listener);
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLocationClient.stop();
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    ||bdLocation.getLocType() == BDLocation.TypeNetWorkLocation
                    ||bdLocation.getLocType() == BDLocation.TypeOffLineLocation){
                if(!mList.isEmpty()){
                    YjtLocationListener listener = mList.remove(0);
                    listener.locationSuccess(bdLocation);
                }
                mLocationTimes = 0;
            }else{
                ToastUtils.shortToastShow(bdLocation.getLocType()+"");
                if(mList.isEmpty()){
                    return ;
                }
//                if(mLocationTimes<LOCATION_OVERTIME){
//                    mLocationClient.start();
//                    mLocationTimes++;
//                }else{
                    mLocationTimes = 0;
                    YjtLocationListener listener = mList.remove(0);
                    listener.locationFailure(bdLocation);
//                }
            }
            if(!mList.isEmpty()){
                mLocationClient.start();
            }
        }
    }

    public interface YjtLocationListener{
        public void locating();
        public void locationSuccess(BDLocation bdLocation);
        public void locationFailure(BDLocation bdLocation);
    }
}
