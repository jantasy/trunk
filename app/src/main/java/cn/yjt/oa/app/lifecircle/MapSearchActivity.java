package cn.yjt.oa.app.lifecircle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.net.NetConnection;
import cn.yjt.oa.app.lifecircle.utils.Constants;
import cn.yjt.oa.app.lifecircle.utils.PreferfenceUtils;

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
import java.io.Serializable;
import java.util.ArrayList;

public class MapSearchActivity extends TitleFragmentActivity implements OnClickListener {
	private static final String tag = "MapSearchActivity";    
    public String first;
    public String second;
    private String action = "";
    private String areaPress;    
    private BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);
    private BitmapDescriptor bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
    private RelativeLayout body_fl;    
    private NetConnection conn;
    private ArrayList<Netable> currList;
    private String inc = "0";
    private boolean isDone = false;
    private boolean isLocation = false;
    private boolean isPaging_price = true;
    private String juliPress = "默认";
    private LayoutParams layoutParam;
    private BaiduMap mBaiduMap;
    private Handler mHandler = new Handler();
    private InfoWindow mInfoWindow;
    private MapView mMapView;
    private LinearLayout mapTypell;
    private ImageView map_show_next;
    private TextView map_show_num;
    private ImageView map_show_up;
    private double maxLat;
    private double maxLon;
    private double minLat;
    private double minLon;
    private ImageButton mylocation;
    private ArrayList<Netable> netableList;
    private int pag_price = 1;
    private int pager = 1;
    private ProgressDialog pd;
    private Netable sNetable = new Netable();;
    private ArrayList<Netable> tempList;
    private ArrayList<Netable> sixList = new ArrayList<Netable>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);        
        setContentView(R.layout.activity_map);     
		setTitle("地图模式");
		getLeftbutton().setImageResource(R.drawable.navigation_back);
        mMapView = (MapView) findViewById(R.id.bmapsView);
        mBaiduMap = mMapView.getMap();        
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));        
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
        if(pd != null) {
        	pd.dismiss();
            pd = null;
        }        	
        super.onDestroy();
    }
	
    private void atMine() {
        Marker marker = (Marker) mBaiduMap.addOverlay(
        		new MarkerOptions().position(new LatLng(Constants.latitude, Constants.longitude))
        		.zIndex(Integer.MAX_VALUE)
        		.icon(bdB)
        		.draggable(true));
    }

    private void init() {
        body_fl = (RelativeLayout) findViewById(R.id.body_fl);
        map_show_up = (ImageView) findViewById(R.id.map_show_up);
        map_show_num = (TextView) findViewById(R.id.map_show_num);
        map_show_next = (ImageView) findViewById(R.id.map_show_next);
        mylocation = (ImageButton) findViewById(R.id.mylocation);
        pd = new ProgressDialog(this);
        pd.setTitle("提示");
        pd.setMessage("正在定位中");
        netableList = new ArrayList();
        tempList = new ArrayList();
        currList = new ArrayList();
        conn = new NetConnection(this);
        action = getIntent().getExtras().getString("action");
        sixList = (ArrayList) getIntent().getSerializableExtra("list");
        first = "";
        second = "";
        pag_price = 1;
    }

    private void location() {
        pd.show();
        new Thread() {
            public void run() {
                super.run();
//                while (!isLocation) {
                    if (!(Constants.longitude == 0.0d || Constants.latitude == 0.0d)) {
                        isLocation = true;
                        if (TextUtils.equals(action, "1")) {
                            mHandler.post(new Runnable() {
                                public void run() {
                                    atMine();
                                    body_fl.setVisibility(View.VISIBLE);
                                    pd.cancel();
                                    pd.setTitle("提示");
                                    pd.setMessage("加载数据中");
                                    pd.show();
                                    sendRequest();
                                }
                            });
                        }
                    }
                    try {
                    	Thread.sleep(500);
//                        AnonymousClass6.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                }
            }
        }.start();
    }

    private void sendRequest() {
        isDone = false;
        new Thread() {
            public void run() {
                sNetable = conn.searchNearBy(PreferfenceUtils.getCityPreferences(MapSearchActivity.this), first, second, Constants.longitude+"", Constants.latitude+"", "全城", juliPress, new StringBuilder(String.valueOf(pag_price)).toString(), inc);
                if(sNetable == null) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            pd.cancel();
                            isDone = true;
                        }
                    });
                    return;
                }
                	
                tempList = sNetable.getMerchants();
                for (int i = 0; i < tempList.size(); i++) {
                    netableList.add((Netable) tempList.get(i));
                }
                mHandler.post(new Runnable() {
                    public void run() {
                        if (tempList.size() != 10) {
                            isPaging_price = false;
                            if (pag_price == 1 && tempList.size() == 0) {
                                Toast.makeText(MapSearchActivity.this, "提示:暂无数据,请尝试更改检索条件!", Toast.LENGTH_SHORT);
                            }
                        }
                        
                        if (tempList.size() != 0) {
                            pager = (int) Math.floor((double) ((netableList.size() + 9) / 10));
                            if (tempList.size() != 10) {
                                map_show_num.setText("第" + (((pager - 1) * 10) + 1) + "-" + (((pager - 1) * 10) + tempList.size()) + "家");
                            } else {
                                map_show_num.setText("第" + (((pager - 1) * 10) + 1) + "-" + ((((pager - 1) * 10) + 1) + 9) + "家");
                            }
                            try {
                                viewTypePage(tempList);
                            } catch (Exception e) {
                                Log.d(tag, "!!!!!!!!!!!!!!!!!!!!!");
                            }
                        }
                        pd.cancel();
                        isDone = true;
                    }
                });
            }
        }.start();
    }

    private void setListener() {
        map_show_next.setOnClickListener(this);
        map_show_up.setOnClickListener(this);
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

                @Override
                public void onInfoWindowClick() {
                    Log.d(tag, "点击button");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("netable",myNetable);
                    bundle.putSerializable("list", sixList);
                    Intent intent = new Intent();
                    intent.setClass(MapSearchActivity.this, StoreDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    mBaiduMap.hideInfoWindow();
                }
            }

            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(tag, marker.getZIndex()+"");
                if (currList != null && currList.size() > marker.getZIndex()) {
                    Netable netable = (Netable) currList.get(marker.getZIndex());
                    Button button = new Button(MapSearchActivity.this);
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
        mHandler.post(new Runnable() {
            public void run() {
            }
        });
        currList.clear();
        Log.d(tag, "ssssssssssssssssssssssssssssssss3");
        Builder builder = new Builder();
        for (int i = 0; i < arrayList.size(); i++) {
            double y = ((Netable) arrayList.get(i)).getY();
            double x = ((Netable) arrayList.get(i)).getX();
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
            builder.include(latLng2);
            latLng2 = new LatLng(minLat, minLon);
            builder.include(latLng2);
            LatLngBounds build = builder.build();
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(build));
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(build.getCenter()));
        } catch (Exception e) {
            Log.d(tag, "!!!!!!!!!!!!!!!!!!!!!!");
        }
        atMine();
        Log.d(tag, "ssssssssssssssssssssssssssssssss4");
    }
    
    @Override
    public void onClick(View view) {
        int i;
        int i2;
        int i3;
        switch (view.getId()) {
            case R.id.map_show_up:
                if (pager == 1) {
                    Toast.makeText(this, "提示:已经是第一页了!", Toast.LENGTH_SHORT).show();
                    return;
                }
                i = pager - 1;
                i2 = (i - 1) * 10;
                i3 = ((i - 1) * 10) + 9;
                ArrayList arrayList = new ArrayList();
                for (i = i2; i <= i3; i++) {
                    arrayList.add((Netable) netableList.get(i));
                }
                viewTypePage(arrayList);
                pager--;
                map_show_num.setText("第" + (((pager - 1) * 10) + 1) + "-" + ((((pager - 1) * 10) + 1) + 9) + "家");
                break;
            case R.id.map_show_next:
                i3 = (int) Math.floor((double) ((netableList.size() + 9) / 10));
                if (!isPaging_price && pager >= i3) {                    
                    Toast.makeText(this, "提示:没有更多数据了!", Toast.LENGTH_SHORT).show();
                } else if (!isDone) {                    
                    Toast.makeText(this, "提示:已经在加载了!", Toast.LENGTH_SHORT).show();
                } else if (pager < i3) {
                    i = pager + 1;
                    i2 = (i - 1) * 10;
                    int i4 = ((i - 1) * 10) + 9;
                    ArrayList arrayList2 = new ArrayList();
                    i = i2;
                    while (i <= i4 && i < netableList.size()) {
                        arrayList2.add((Netable) netableList.get(i));
                        i++;
                    }
                    viewTypePage(arrayList2);
                    pager++;
                    if (pager == i3) {
                        map_show_num.setText("第" + (((pager - 1) * 10) + 1) + "-" + ((((pager - 1) * 10) + (netableList.size() % 10)) - 1) + "家");
                    } else {
                        map_show_num.setText("第" + (((pager - 1) * 10) + 1) + "-" + ((((pager - 1) * 10) + 1) + 9) + "家");
                    }
                } else {
                    pag_price++;
                    pd.show();
                    pd.setTitle("提示");
                    pd.setMessage("加载数据中");
                    sendRequest();
                }
                break;
            default:
                break;
        }
    }
}
