package cn.yjt.oa.app.signin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.location.BDLocationServer;
import cn.yjt.oa.app.location.BaseLocationServer;
import cn.yjt.oa.app.utils.LocationUtil;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.ViewContainerStub;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class POIActivity extends TitleFragmentActivity implements
		OnGetGeoCoderResultListener, OnItemClickListener, OnLoadMoreListner, OnRefreshListener {

	private MapView mMapView;
	private BaiduMap mBaiduMap;

	// 定位相关
	private BaseLocationServer mLocationServer;
	private MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;// 定位模式
	private BitmapDescriptor mCurrentMarker;
	private boolean isFirstLoc = true;// 是否首次定位

	// POI相关
	private GeoCoder mGeoCoder;
	private ReverseGeoCodeOption mReverseGeoCodeOption;
	private LatLng latLng;
	private List<PoiInfo> poiInfoList;
	private List<String> poiInfoUids;
	private boolean issucsess;// 检索是否成功
	private PoiInfo poiOne;
	private int index = 1;
	private int count = 0;
	private SigninListViewAdapter adapter;
	private PullToRefreshListView listView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi);
		Init();
	}

	/***
	 * 初始化
	 */
	private void Init() {
		InitTitleBar();
		poiInfoList = new ArrayList<PoiInfo>();
		listView = (PullToRefreshListView) findViewById(R.id.poiResultListView);
		adapter = new SigninListViewAdapter(this, poiInfoList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnLoadMoreListner(this);
		listView.setOnRefreshListener(this);
		
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(this);
		mReverseGeoCodeOption = new ReverseGeoCodeOption();

		BaiduMapOptions options = new BaiduMapOptions();
		options.zoomControlsEnabled(false);
		options.zoomGesturesEnabled(false);
		mMapView = new MapView(this, options);
		((ViewContainerStub) findViewById(R.id.bmapView)).setView(mMapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));

		mCurrentMode = LocationMode.NORMAL;
		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.signin_icon_location);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位到上次定位的地点
		SharedPreferences mSharedPreferences = getSharedPreferences(
				"LastLocation", Context.MODE_PRIVATE);
		String lastlocation = mSharedPreferences
				.getString("lastlocation", null);
		if (lastlocation != null) {
			String[] latlng = lastlocation.split(",");
			LatLng ll = new LatLng(Double.parseDouble(latlng[0]),
					Double.parseDouble(latlng[1]));
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
//		BaseLocationServer.startLocation(myListener);
        BDLocationServer.getInstance().startLocation(myListener);
		showProgressBar();

	}

	private void InitTitleBar() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	/**
	 * 返回
	 */
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationServer.YjtLocationListener {

        @Override
        public void locating() {

        }

        @Override
        public void locationSuccess(BDLocation location) {
            LogUtils.i("===mulLocation = " + location);
            dismissProgressBar();
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            latLng = LocationUtil.changePointGCJ02(new LatLng(
                    location.getLatitude(), location.getLongitude()));
            poiOne = new PoiInfo();
            poiOne.name = "当前位置";
            poiOne.address = location.getAddrStr();
            poiOne.location = new LatLng(latLng.latitude, latLng.longitude);
            GetResult(latLng);

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360.direction(100)
                    .latitude(latLng.latitude).longitude(latLng.longitude)
                    .build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(latLng.latitude, latLng.longitude);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }

        @Override
        public void locationFailure(BDLocation bdLocation) {
            LogUtils.i("===mulLocation = " + bdLocation);
            dismissProgressBar();
            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }
        }
    }

	/**
	 * 通过latLng检索
	 * 
	 * @param latLng
	 */
	private void GetResult(LatLng latLng) {
		showProgressBar();
		mReverseGeoCodeOption.location(latLng);
		issucsess = mGeoCoder.reverseGeoCode(mReverseGeoCodeOption);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		if (!ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			// 将最后定位的位置经纬度保存
			if (mBaiduMap != null && latLng != null) {
				SharedPreferences mSharedPreferences = getSharedPreferences(
						"LastLocation", Context.MODE_PRIVATE);
				Editor editor = mSharedPreferences.edit();
				editor.putString("lastlocation", latLng.latitude + ","
						+ latLng.longitude);
				editor.commit();
			}
			super.onDestroy();
			// 关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
			mGeoCoder.destroy();
		} else {
			super.onDestroy();
		}

	}

	/**
	 * POI检索回调
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
	}

	/**
	 * POI检索回调
	 */
	@Override
	public void onGetReverseGeoCodeResult(
			ReverseGeoCodeResult reverseGeoCodeResult) {
		dismissProgressBar();
		if (issucsess) {
			if (poiInfoList.size() == 0) {
				if (reverseGeoCodeResult.getPoiList() != null) {
					poiInfoList.addAll(reverseGeoCodeResult.getPoiList());
					// 将定位位置信息放入第一项
					poiInfoList.add(0, poiOne);
				}
			} else {
				Filter(reverseGeoCodeResult.getPoiList());
			}
			adapter.notifyDataSetChanged();
			for (int i = 0; i < poiInfoList.size(); i++) {
				Log.i("msg", "poiname:----------" + poiInfoList.get(i).name);
			}
		} else {
			Toast.makeText(getApplicationContext(), "检索数据失败！",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 根据poiInfo中的UID过滤重复POI
	 * 
	 * @param ResultPoiInfoList
	 * @return
	 */
	private void Filter(List<PoiInfo> ResultPoiInfoList) {
		poiInfoUids = new ArrayList<String>();
		// 取出poiInfoList所有元素UID
		for (int i = 0; i < poiInfoList.size(); i++) {
			poiInfoUids.add(i, poiInfoList.get(i).uid);
		}
		// 新得到的poiInfoList元素的UID如果不存在当前poiInfoUids，存入poiInfoList
		for (int i = 0; i < ResultPoiInfoList.size(); i++) {
			if (!poiInfoUids.contains(ResultPoiInfoList.get(i).uid)) {
				poiInfoList.add(ResultPoiInfoList.get(i));
				count++;
			}
		}
		if (count < 10) {
			GetResult(poiInfoList.get(index++).location);
		} else {
			count = 0;
			listView.onLoadMoreComplete();

		}

	}

	static class SigninListViewAdapter extends BaseAdapter {

		private Context context;
		private List<PoiInfo> poiInfoList;

		public SigninListViewAdapter(Context context, List<PoiInfo> poiInfoList) {
			this.context = context;
			this.poiInfoList = poiInfoList;
		}

		@Override
		public int getCount() {
			return poiInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return poiInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.poi_list_item, null);
			} 

			TextView poiname = (TextView) convertView
					.findViewById(R.id.listview_item_poiname);
			PoiInfo poiInfo = (PoiInfo) getItem(position);
			if (position == 0) {
				poiname.setText(poiInfo.name + ":\t" + poiInfo.address);
			} else {
				poiname.setText(poiInfo.name);
			}
			return convertView;
		}

	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, POIActivity.class);
		context.startActivity(intent);
	}
	
	public static void startForResult(Activity activity , int requestCode){
		Intent intent = new Intent(activity,POIActivity.class);
		activity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PoiInfo item = (PoiInfo) parent.getAdapter().getItem(position);
		setResult(item);
	}
	
	private void setResult(PoiInfo info){
		Intent data = new Intent();
		if("当前位置".equals(info.name)){
			data.putExtra("poi_name", info.address);
		}else{
			data.putExtra("poi_name", info.name);
		}
		data.putExtra("latlng", info.location.latitude + "," + info.location.longitude);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public void onLoadMore() {
		GetResult(poiInfoList.get(index++).location);
		if (poiInfoList.size() > 100) {
			listView.enableFooterView(false);
		}
		
	}

	@Override
	public void onRefresh() {
		listView.onRefreshComplete();
	}
}
