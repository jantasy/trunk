package cn.yjt.oa.app.lifecircle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.utils.Constants;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import java.io.Serializable;
import java.util.ArrayList;

public class MapModeActivity extends TitleFragmentActivity implements OnClickListener, OnGetRoutePlanResultListener {
	private static final String tag = "MapModeActivity";
	private static final int FLAG_DRIVE = 0;
	private static final int FLAG_BUS = 1;
//    private Applications application;
	private BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);;
	private BitmapDescriptor bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
    private FrameLayout body_fl;
    private ImageButton bus_btn;
//    private Button button;
    private ImageButton clear_btn;    
    private ArrayList<Netable> currList;
    private double currLo;
    private double currla;
    private TextView frame_top_title;
    private LinearLayout hometype_ll;
    private boolean isLocation = false;
    private BaiduMap mBaiduMap;
    private Handler mHandler = new Handler();;
    private InfoWindow mInfoWindow;
    private double mLat1 = 39.915291d;
    private double mLon1 = 116.403857d;
    private MapView mMapView;
    private RoutePlanSearch mSearch;
    private double maxLat;
    private double maxLon;
    private double minLat;
    private double minLon;
    private ImageButton mylocation;
    private ArrayList<Netable> netableList;
    private ArrayList<Netable> sixList;
    private int nodeIndex;
    private ProgressDialog pd;
    private RouteLine route;
    private OverlayManager routeOverlay;
    private int searchType;
    boolean useDefaultIcon = false;;
    private ImageButton zijia_btn;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mapnearyby);
		setTitle("地图模式");
		getLeftbutton().setImageResource(R.drawable.navigation_back);
        mMapView = (MapView) findViewById(R.id.bmapsView);
        mBaiduMap = mMapView.getMap();
//        LatLng latLng = new LatLng(39.915291d, 116.403857d);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        init();
        setListener();
        location();
    }

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

	@Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }    
    
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        public BitmapDescriptor getStartMarker() {
            return useDefaultIcon ? BitmapDescriptorFactory.fromResource(R.drawable.icon_st) : null;
        }

        public BitmapDescriptor getTerminalMarker() {
            return useDefaultIcon ? BitmapDescriptorFactory.fromResource(R.drawable.icon_en) : null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        public BitmapDescriptor getStartMarker() {
            return useDefaultIcon ? BitmapDescriptorFactory.fromResource(R.drawable.icon_st) : null;
        }

        public BitmapDescriptor getTerminalMarker() {
            return useDefaultIcon ? BitmapDescriptorFactory.fromResource(R.drawable.icon_en) : null;
        }
    }

    private void atMine() {
        Marker marker = (Marker) mBaiduMap.addOverlay(
        		new MarkerOptions()
        		.position(new LatLng(Constants.latitude, Constants.longitude))
        		.zIndex(Integer.MAX_VALUE)
        		.icon(bdB)
        		.draggable(true));
    }

    private void init() {
        body_fl = (FrameLayout) findViewById(R.id.body_fl);
        mylocation = (ImageButton) findViewById(R.id.mylocation);
        zijia_btn = (ImageButton) findViewById(R.id.zijia_btn);
        bus_btn = (ImageButton) findViewById(R.id.bus_btn);
        clear_btn = (ImageButton) findViewById(R.id.clear_btn);
//        button = new Button(this);
//        button.setBackgroundResource(R.drawable.icon_ar_popup_normal);
        pd = new ProgressDialog(this);
        pd.setTitle("定位中...");
        netableList = new ArrayList();
        currList = new ArrayList();   
        sixList = new ArrayList();
        netableList.clear();
        netableList = (ArrayList) getIntent().getSerializableExtra("list");
        sixList = (ArrayList) getIntent().getSerializableExtra("sixlist");
        for (int i = 0; i < netableList.size(); i++) {
            Log.d(tag, "****************************" + ((Netable) netableList.get(i)).getShortName());
        }
    }

    private void location() {
        body_fl.setVisibility(View.VISIBLE);
        viewTypePage(netableList);        
    }

    private void setListener() {
        zijia_btn.setOnClickListener(this);
        bus_btn.setOnClickListener(this);
        clear_btn.setOnClickListener(this);
//        button.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(LocaleUtil.INDONESIAN, (Serializable) currList.get(view.getId()));
//                bundle.putSerializable("list", SearchActivity.localList);
//                Intent intent = new Intent();
//                intent.setClass(this, StoreDetailActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                Log.d(tag, "id:" + view.getId());
//            }
//        });
        mylocation.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                pd.setTitle("提示");
                pd.setMessage("定位中");
                pd.show();
                new Thread() {
                    public void run() {
                        if (Constants.longitude != 0.0d && Constants.latitude != 0.0d) {
                            mHandler.post(new Runnable() {
                                public void run() {
                                    pd.cancel();
                                    atMine();
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(Constants.latitude, Constants.longitude)));
                                }
                            });
                        }
                    }
                }.start();
            }
        });
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {            
            class myOnInfoWindowClickListener implements OnInfoWindowClickListener {
                private final Netable myNetable;

                myOnInfoWindowClickListener(Netable netable) {
                    myNetable = netable;
                }

                public void onInfoWindowClick() {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("netable", myNetable);
                    bundle.putSerializable("list", sixList);
                    Intent intent = new Intent();
                    intent.setClass(MapModeActivity.this, StoreDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    mBaiduMap.hideInfoWindow();
                }
            }

            public boolean onMarkerClick(Marker marker) {
                Log.d(tag, marker.getZIndex()+"");
                if (currList != null && currList.size() > marker.getZIndex()) {
                    Netable netable = (Netable) currList.get(marker.getZIndex());
                    currLo = netable.getY();
                    currla = netable.getX();
                    Button button = new Button(MapModeActivity.this);
                    button.setBackgroundResource(R.drawable.icon_ar_popup_normal);
                    button.setText(netable.getShortName());
                    Log.d(tag, "设置button点击事件");
                    OnInfoWindowClickListener myOnInfoWindowClickListener = new myOnInfoWindowClickListener(netable);
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), marker.getPosition(), -45, myOnInfoWindowClickListener);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    private void viewTypePage(ArrayList<Netable> arrayList) {
        mBaiduMap.clear();
        currList.clear();
        Log.d(tag, "ssssssssssssssssssssssssssssssss3");
        Builder builder = new Builder();
        for (int i = 0; i < arrayList.size(); i++) {
            double y = ((Netable) arrayList.get(i)).getY();
            double x = ((Netable) arrayList.get(i)).getX();
            Log.d(tag, "y: " + y + "x: " + x);
            if (!(y == 0.0d || x == 0.0d)) {
                if (maxLat == 0.0d) {
                    maxLat = y;
                }
                if (minLat == 0.0d) {
                    minLat = y;
                }
                if (maxLon == 0.0d) {
                    maxLon = x;
                }
                if (minLon == 0.0d) {
                    minLon = x;
                }
                maxLat = maxLat > y ? maxLat : y;
                minLat = minLat < y ? minLat : y;
                maxLon = maxLon > x ? maxLon : x;
                minLon = minLon < x ? minLon : x;
                currList.add((Netable) arrayList.get(i));
                LatLng latLng = new LatLng(y, x);
                Marker marker = (Marker) mBaiduMap.addOverlay(new MarkerOptions().position(latLng).icon(bdA).zIndex(currList.size() - 1).draggable(true));
                builder.include(latLng);
                if (!TextUtils.isEmpty(((Netable) arrayList.get(i)).getIconUrl())) {
//                    SelectedImageCache.getInstance().loadMapURL(Constants.PRO_IP + ((Netable) arrayList.get(i)).getIconUrl(), marker);
                }
            }
        }
        try {
            LatLng latLng2 = new LatLng(maxLat, maxLon);
            latLng2 = new LatLng(minLat, minLon);
            LatLngBounds build = builder.build();
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(build));
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(build.getCenter()));
        } catch (Exception e) {
            Log.d(tag, "!!!!!!!!!!!!!!!!!!!!!!");
        }
        atMine();
        currLo = ((Netable) currList.get(0)).getY();
        currla = ((Netable) currList.get(0)).getX();
        Log.d(tag, "ssssssssssssssssssssssssssssssss4");
        mMapView.refreshDrawableState();
    }

    public void SearchButtonProcess(int type) {
        route = null;
        LatLng latLng = new LatLng(Constants.latitude, Constants.longitude);
        LatLng latLng2 = new LatLng(currLo, currla);
        PlanNode withLocation = PlanNode.withLocation(latLng);
        PlanNode withLocation2 = PlanNode.withLocation(latLng2);
        if (type == FLAG_DRIVE) {
            mSearch.drivingSearch(new DrivingRoutePlanOption().from(withLocation).to(withLocation2));
        } else if (type == FLAG_BUS) {
            mSearch.transitSearch(new TransitRoutePlanOption().from(withLocation).city(Constants.city).to(withLocation2));
        }
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zijia_btn:
                SearchButtonProcess(FLAG_DRIVE);
                break;
            case R.id.bus_btn:
                SearchButtonProcess(FLAG_BUS);
                break;
            case R.id.clear_btn:
                viewTypePage(netableList);
                break;
            default:
                break;
        }
    }
	
	@Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", 0).show();
        }
        if (drivingRouteResult.error != ERRORNO.AMBIGUOUS_ROURE_ADDR && drivingRouteResult.error == ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = (RouteLine) drivingRouteResult.getRouteLines().get(0);
            MyDrivingRouteOverlay myDrivingRouteOverlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = myDrivingRouteOverlay;
            myDrivingRouteOverlay.setData((DrivingRouteLine) drivingRouteResult.getRouteLines().get(0));
            myDrivingRouteOverlay.addToMap();
            myDrivingRouteOverlay.zoomToSpan();
        }
    }

	@Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        if (transitRouteResult == null || transitRouteResult.error != ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", 0).show();
        }
        if (transitRouteResult.error != ERRORNO.AMBIGUOUS_ROURE_ADDR && transitRouteResult.error == ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = (RouteLine) transitRouteResult.getRouteLines().get(0);
            MyTransitRouteOverlay myTransitRouteOverlay = new MyTransitRouteOverlay(mBaiduMap);
            routeOverlay = myTransitRouteOverlay;
            myTransitRouteOverlay.setData((TransitRouteLine) transitRouteResult.getRouteLines().get(0));
            myTransitRouteOverlay.addToMap();
            myTransitRouteOverlay.zoomToSpan();
        }
    }

	@Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
    }
}
