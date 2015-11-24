package cn.yjt.oa.app.signin;

import cn.yjt.oa.app.location.BDLocationServer;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.ImageBrowser;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.location.BaseLocationServer;
import cn.yjt.oa.app.signin.utils.HttpHelper;
import cn.yjt.oa.app.signin.utils.HttpHelper.UploadCallback;
import cn.yjt.oa.app.utils.BitmapUtils;
import cn.yjt.oa.app.utils.FileUtils;
import cn.yjt.oa.app.utils.LocationUtil;
import cn.yjt.oa.app.utils.TelephonyUtil;
import cn.yjt.oa.app.utils.ViewUtil;
import cn.yjt.oa.app.widget.ViewContainerStub;

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
import com.umeng.analytics.MobclickAgent;

public class SigninPOISearchActivity extends TitleFragmentActivity implements
		OnGetGeoCoderResultListener ,OnClickListener{

	private MapView mMapView; 
	private BaiduMap mBaiduMap;

	// 定位相关
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
	private Button sign_ok;
	private PoiInfo poiInfo, poiOne;
	private int index = 1;
	private int count = 0;
	private int RESULTCODE = 2;
	
	private ImageButton signinCamera;
	private ImageView signinImage;
	private ProgressDialog dialog;
	private static final String SIGNIN_DIR = "/yijitong/signin/temp/";
	
	private static final String TEMP_FILE_NAME = "signin_camera_cache";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {

			clearFile();
			// try {
			// System.loadLibrary("libBaiduMapSDK_v3_2_0_15");
			// System.loadLibrary("liblocSDK4d");
			// } catch (UnsatisfiedLinkError loadUnsatisfiedLinkError) {
			// loadUnsatisfiedLinkError.printStackTrace();
			// }
			// SDKInitializer.initialize(getApplicationContext());
			setContentView(R.layout.signinpoisearch);
			Init();
			// mLoadingProgress.setVisibility(View.GONE);

			signinCamera.setOnClickListener(this);
			signinImage.setOnClickListener(this);
			// 签到
			sign_ok.setOnClickListener(this);
		}
		
		
	}
	
	
	/***
	 * 初始化
	 */
	private void Init() {
		InitTitleBar();
		signinCamera = (ImageButton) findViewById(R.id.signin_camera);
		signinImage = (ImageView) findViewById(R.id.tv_check_in);
		sign_ok = (Button) findViewById(R.id.signin_ok);
		poiInfoList = new ArrayList<PoiInfo>();

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

	/**
	 * 初始化标题栏
	 */
	private void InitTitleBar() {
		String signtype = getIntent().getStringExtra("signtype");
		if (SigninInfo.SINGIN_TYPE_CHECKIN.equals(signtype)) {
			setTitle("上班签到");
		} else if (SigninInfo.SINGIN_TYPE_CHECKOUT.equals(signtype)) {
			setTitle("下班签退");
		} else if (SigninInfo.SINGIN_TYPE_VISIT.equals(signtype)) {
			setTitle("位置签到");
		}else{
			setTitle("位置签到");
		}
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setVisibility(View.GONE);
		setTitleColor(Color.BLACK);
	}

	/**
	 * 返回
	 */
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

//	/**
//	 * 初始化定位
//	 */
//	private void InitLocation() {
//		LocationClientOption option = new LocationClientOption();
//		option.setCoorType("bd09ll");
//		option.setScanSpan(3600000);// 设置发起定位请求的间隔时间为3600000ms
//		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
//		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
//		option.setOpenGps(true); // 打开GPS
//		option.setProdName("cn.yjt.oa.app"); // 设置产品线名称
//		mLocClient.setLocOption(option);
//	}

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
//	public class MyLocationListenner implements BDLocationListener {
//
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			Log.e("msg",
//					"location.getLatitude():" + location.getLatitude() + "\n"
//							+ "location.getLongitude()"
//							+ location.getLongitude());
//			// map view 销毁后不在处理新接收的位置
//			if (location == null || mMapView == null)
//				return;
//
//			poiOne = new PoiInfo();
//			poiOne.name = "当前位置";
//			poiOne.address = location.getAddrStr();
//			poiOne.location = new LatLng(location.getLatitude(),
//					location.getLongitude());
//
//			// 通过纬度经度检索POI
//			latLng = new LatLng(location.getLatitude(), location.getLongitude());
//			GetResult(latLng);
//
//			MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360.direction(100)
//					.latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//			mBaiduMap.setMyLocationData(locData);
//			if (isFirstLoc) {
//				isFirstLoc = false;
//				LatLng ll = new LatLng(location.getLatitude(),
//						location.getLongitude());
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//				mBaiduMap.animateMapStatus(u);
//			}
//		}
//	}

	
	private void startCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri = Uri.fromFile(getImageFile());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, CAMERA_CALL);
	}
	
	private String getImageDir(){
		return Environment.getExternalStorageDirectory()+SIGNIN_DIR;
	}
	
	private File getImageFile(){
		File file = new File(getImageDir(), TEMP_FILE_NAME);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		return file;
	}
	
	private String getFileName(){
		return "image";
	}
	
	private File getUploadFile(){
		if(getImageFile().exists()){
			return getImageFile();
		}
		return null;
	}
	
	class MyAsyncTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			return FileUtils.compressImageFile(params[0], 720, 1280);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				upload(getFileName(),getUploadFile());
			}
		}
	}
	
	
	
	private static final int CAMERA_CALL = 1;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == CAMERA_CALL){
				showImage();
			}
		}
	}
	
	private void showImage(){
		String filepath = getImageFile().getAbsolutePath();
		signinImage.setVisibility(View.VISIBLE);
		BitmapUtils.setBitmapToImageView(filepath, signinImage);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signin_camera:
			startCamera();
			break;
		case R.id.tv_check_in:
			openImage();
			break;
		case R.id.signin_ok:
			signin();
			break;
		default:
			break;
		}
	}
	
	private void clearFile(){
		if(hasAttachment()){
			getUploadFile().delete();
		}
	}

	private void openImage() {
		String[] path = { Uri.fromFile(getImageFile()).toString() };
		ImageBrowser.launch(this,path, 0);
	}

	public boolean hasAttachment(){
		if (getUploadFile() != null) {
			return getUploadFile().exists();
		}
		return false;
	}
	
	private void compressForUpload(){
		//new MyAsyncTask().execute(getImageFile().getAbsolutePath());
		upload(getFileName(),getUploadFile());
	}
	
	private void upload(String uploadName,File uploadFile){
		HttpHelper helper = new HttpHelper(SigninPOISearchActivity.this);
		helper.uploadImage(uploadName, uploadFile, new UploadCallback() {
			
			@Override
			public void onResponse(String url) {
				if(!TextUtils.isEmpty(url)&&getUploadFile().delete()){
					commit(url);// TODO 不能返回
				}else{
					Toast.makeText(getApplicationContext(), "上传失败",	 Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void signin(){
		if(!hasAttachment()){
			commit(null);
		}else{
			compressForUpload();
		}
	}
	
	private void commit(final String imageUrl) {
		
		if (poiOne !=null) {
			
			poiInfo = poiOne;
			final String poiName = poiInfo.address;
			
			
			boolean takePhoto = false;
			if(!TextUtils.isEmpty(imageUrl)){
				takePhoto = true;
			}
			
			Map<String,String> map  = new HashMap<String, String>();
			map.put("take_photo", String.valueOf(takePhoto));
			MobclickAgent.onEvent(this, "attendance_signin_location", map  );
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					signin(poiName, poiInfo.location.latitude, poiInfo.location.longitude, imageUrl);
				}
			});
//			intent.putExtra("poiName", poiName);
//			intent.putExtra("latitude", poiInfo.location.latitude);
//			intent.putExtra("longitude", poiInfo.location.longitude);
//			SigninPOISearchActivity.this.setResult(RESULTCODE, intent);
//			SigninPOISearchActivity.this.finish();
		}else{
			Toast("正在获取当前位置，请等待");
		}
	}
	
	private void  Toast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	private void signin(String poiName, double latitude, double longitude, String attachment) {
		UserInfo userInfo = AccountManager.getCurrent(this);
		SigninInfo signinInfo = new SigninInfo();
		signinInfo.setUserId(userInfo.getId());
		signinInfo.setType(SigninInfo.SINGIN_TYPE_VISIT);
		signinInfo.setContent("check_test");
		signinInfo.setPositionDescription(poiName);
		signinInfo.setPositionData(latitude + "," + longitude);
		signinInfo.setAttachment(attachment);
		signinInfo.setIccId(TelephonyUtil.getICCID(this));

		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setMessage("签到中...");
		}
		dialog.show();

		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_SINGNINS);
		builder.setRequestBody(signinInfo);
		Type type = new TypeToken<Response<SigninInfo>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<SigninInfo>>() {

			@Override
			public void onResponse(Response<SigninInfo> response) {
				dialog.dismiss();
				if (response.getCode() == 0) {
					final Dialog dialog = new Dialog(SigninPOISearchActivity.this,
							R.style.dialogNoTitle);
					View dialogView = View.inflate(getApplicationContext(),
							R.layout.signin_success_dialog, null);
					dialogView.findViewById(R.id.btn_comfirm)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									dialog.dismiss();
									finish();

								}
							});
					dialog.setContentView(dialogView);
					dialog.show();
				} else {
					Toast.makeText(getApplicationContext(),
							response.getDescription(),
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dialog.dismiss();
				Toast.makeText(getApplicationContext(),
						"签到失败！InvocationError" + error.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
		builder.build().post();
	}
	



	/**
	 * 初始化定位
	 */
//	private void InitLocation() {
//		LocationClientOption option = new LocationClientOption();
//		option.setCoorType("bd09ll");
//		option.setScanSpan(3600000);// 设置发起定位请求的间隔时间为3600000ms
//		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
//		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
//		option.setOpenGps(true); // 打开GPS
//		option.setProdName("cn.yjt.oa.app"); // 设置产品线名称
//		mLocClient.setLocOption(option);
//	}



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
			clearFile();
			if (mBaiduMap != null && latLng != null) {
				SharedPreferences mSharedPreferences = getSharedPreferences(
						"LastLocation", Context.MODE_PRIVATE);
				Editor editor = mSharedPreferences.edit();
				editor.putString("lastlocation", latLng.latitude + ","
						+ latLng.longitude);
				editor.commit();
			}
			super.onDestroy();
			// 退出时销毁定位
//			mLocClient.stop();
			// 关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
			mGeoCoder.destroy();
		}else {
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
		}

	}

	static class ViewHolder {
		TextView poiname;
		CheckedTextView checked;
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
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(context,
						R.layout.listview_item_signinpoisearch, null);
				holder.poiname = (TextView) convertView
						.findViewById(R.id.listview_item_poiname);
				convertView.setTag(holder);
				holder.checked = (CheckedTextView) convertView
						.findViewById(R.id.listview_item_checked);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PoiInfo poiInfo = (PoiInfo) getItem(position);
			if (position == 0) {
				holder.poiname.setText(poiInfo.name + ":\t" + poiInfo.address);
			} else {
				holder.poiname.setText(poiInfo.name);
			}
			return convertView;
		}

	}
	
	public static void launch(Context context){
		Intent intent = new Intent(context, SigninPOISearchActivity.class);
		context.startActivity(intent );
	}
	
}
