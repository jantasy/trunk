package cn.yjt.oa.app.utils;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;

public class LocationUtil {
	
	public static LocationClient getLocationClient(Context context,BDLocationListener listener){
		LocationClient mLocClient = new LocationClient(context);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setScanSpan(3600000);// 设置发起定位请求的间隔时间为3600000ms
		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		option.setOpenGps(true); // 打开GPS
		option.setProdName("cn.yjt.oa.app"); // 设置产品线名称
		mLocClient.setLocOption(option);
		mLocClient.registerLocationListener(listener);
		return mLocClient;
	}
	
	// 坐标转换
	public static LatLng changePointGCJ02(LatLng sourceLatLng) {
		// 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.COMMON);
		// sourceLatLng待转换坐标
		converter.coord(sourceLatLng);
		return converter.convert();
	}

}
