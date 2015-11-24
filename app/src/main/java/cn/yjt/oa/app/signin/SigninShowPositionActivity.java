package cn.yjt.oa.app.signin;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.ImageBrowser;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.UmengBaseActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.Config;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

public class SigninShowPositionActivity extends UmengBaseActivity implements OnClickListener{

	private MapView map;
	private BaiduMap mBaiduMap;

	private String positionData;
	private String positionDes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.signin_show_position);
		initViewData();
		initview();
	}
	
	private View signinView;
	private ImageView signinImage;
	private TextView signinText;
	private ProgressBar progressBar;
	private String imageUrl;
	private void initViewData(){
		Intent intent = getIntent();
		positionData = intent.getStringExtra(Config.POSITION_DATA);
		System.out.println("positionData:"+positionData);
		positionDes = intent.getStringExtra(Config.POSITION_DES);
		imageUrl = intent.getStringExtra("imageUrl");
	}
	
	private void initSigninView(){
		signinView = findViewById(R.id.show_signin_view);
		signinImage = (ImageView) findViewById(R.id.show_signin_image);
		progressBar = (ProgressBar) findViewById(R.id.signin_image_progress);
		signinImage.setOnClickListener(this);
		if(!TextUtils.isEmpty(imageUrl)){
			signinView.setVisibility(View.VISIBLE);
			//AsyncRequest.getInImageView(imageUrl, signinImage, 0, 0);
			AsyncRequest.getBitmap(imageUrl, new Listener<Bitmap>() {
				
				@Override
				public void onResponse(Bitmap bitmap) {
					progressBar.setVisibility(View.GONE);
					signinImage.setImageBitmap(bitmap);
				}
				
				@Override
				public void onErrorResponse(InvocationError error) {
					progressBar.setVisibility(View.GONE);
				}
			});
		}
		signinText = (TextView) findViewById(R.id.show_signin_text);
		signinText.setText("签到图片");
	}
	
	@Override
	public void onClick(View v) {
		showImage();
	}
	
	private void showImage(){
		ImageBrowser.launch(this, new String[]{imageUrl}, 0);
	}

	private void initview() {

		initSigninView();
		
		map = (MapView) findViewById(R.id.signin_baidu_map);

		mBaiduMap = map.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));

		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMyLocationEnabled(true);
		
		setPoint();

	}

	private void setPoint() {
		if(TextUtils.isEmpty(positionDes)){
			positionDes="未知";
		}
	    String [] position = null; 
	    position = positionData.split(",");
		// 定义Maker坐标点
		LatLng point = null;
		try {
			point = new LatLng(Double.parseDouble(position[0]), Double.parseDouble(position[1]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (point == null) {
			Toast.makeText(this,
					getResources().getString(R.string.get_location_fail),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.signin_icon_location);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap).title(positionDes);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(18)
				.build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);

		OverlayOptions textOption = new TextOptions().bgColor(Color.TRANSPARENT)
				.fontSize(getResources().getDimensionPixelSize(R.dimen.dimen_16dp)).fontColor(Color.rgb(0, 141, 234)).text(positionDes)
				.position(point);
		// 在地图上添加该文字对象并显示
		mBaiduMap.addOverlay(textOption);

		// PopupOverlay
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		map.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		map.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mBaiduMap.setMyLocationEnabled(false);
		map.onDestroy();
		map = null;
		super.onDestroy();
	}

}
