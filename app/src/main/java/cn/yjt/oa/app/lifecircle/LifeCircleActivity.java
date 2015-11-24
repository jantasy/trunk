package cn.yjt.oa.app.lifecircle;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.lifecircle.adapter.SearchFavoriteAdapter;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.net.NetConnection;
import cn.yjt.oa.app.lifecircle.utils.Constants;
import cn.yjt.oa.app.lifecircle.utils.PreferfenceUtils;
import cn.yjt.oa.app.lifecircle.view.LinearView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

//public class LifeCircleFragment extends BaseFragment implements OnClickListener, OnEditorActionListener {
public class LifeCircleActivity extends TitleFragmentActivity implements OnClickListener, OnEditorActionListener {

	private final static String tag = "LifeCircleActivity"; 
	private TextView mTVLocation, mTVArea, mTVType, mTVOrder, mTVSu1, mTVSu2, mTVSu3, mTVSu4 ,mTVSu5, mTVSu6, mTVSu7, mTVSu8;
	private ImageView mIVMap, mIVSearch, mIVSu1, mIVSu2, mIVSu3, mIVSu4, mIVSu5, mIVSu6, mIVSu7, mIVSu8, mIVPhoneTraffic, mIVPhoneWallet, mIVNFCLable;
	private EditText mEditSearch;
	private RelativeLayout mRLNearBy, mRLNearBySix;
	private LinearLayout mLLSu1, mLLSu2, mLLSu3, mLLSu4, mLLSu5, mLLSu6, mLLSu7, mLLSu8, mLLPhoneTraffic, mLLPhoneWallet, mLLNFCLable, mLLLike;
	private ProgressBar mProgressBar;
	private LinearView mLVLike;
	
	private NetConnection mNetConnection;
	private Handler mHandler;
	
    private boolean isCityChange = false;
    private boolean isHasData = false;
    private boolean isNotHasCity = false;
    
    private ArrayList<Netable> localList = new ArrayList();
    private Netable likeNetable = new Netable();
    private Netable sallNetable = new Netable();
    private ArrayList<Netable> favoriteList = new ArrayList();
    
    private Animation animationHide;
    private Animation animationShow;
     
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    
    private SearchFavoriteAdapter favoriteAdapter;
    
    private static final int REQUEST_CHOOSE_CITY = 1;    
    private boolean mIsInitDone = false;

    public class MyLocationListener implements BDLocationListener {
        public void onReceiveLocation(BDLocation bDLocation) {
            StringBuffer stringBuffer = new StringBuffer(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
            stringBuffer.append("\n地图中当前位置来源 : ");
            switch (bDLocation.getLocType()) {
                case BDLocation.TypeGpsLocation /*61*/:
                    stringBuffer.append("GPS定位结果");
                    break;
                case BDLocation.TypeCriteriaException /*62*/:
                    stringBuffer.append("扫描整合定位依据失败。此时定位结果无效。");
                    break;
                case BDLocation.TypeNetWorkException /*63*/:
                    stringBuffer.append("网络异常，没有成功向服务器发起请求。此时定位结果无效。");
                    break;
                case BDLocation.TypeCacheLocation /*65*/:
                    stringBuffer.append("定位缓存的结果。");
                    break;
                case BDLocation.TypeOffLineLocation /*66*/:
                    stringBuffer.append("离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果");
                    break;
                case BDLocation.TypeOffLineLocationFail /*67*/:
                    stringBuffer.append("离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果");
                    break;
                case BDLocation.TypeOffLineLocationNetworkFail /*68*/:
                    stringBuffer.append("网络连接失败时，查找本地离线定位时对应的返回结果");
                    break;
                case BDLocation.TypeNetWorkLocation /*161*/:
                    stringBuffer.append("表示网络定位结果");
                    break;
            }
            stringBuffer.append("time : ");
            stringBuffer.append(bDLocation.getTime());
            stringBuffer.append("\nerror code : ");
            stringBuffer.append(bDLocation.getLocType());
            stringBuffer.append("\nlatitude : ");
            stringBuffer.append(bDLocation.getLatitude());
            stringBuffer.append("\nlontitude : ");
            stringBuffer.append(bDLocation.getLongitude());
            stringBuffer.append("\nradius : ");
            stringBuffer.append(bDLocation.getRadius());
            if (bDLocation.getLocType() == 61) {
                stringBuffer.append("\nspeed : ");
                stringBuffer.append(bDLocation.getSpeed());
                stringBuffer.append("\nsatellite : ");
                stringBuffer.append(bDLocation.getSatelliteNumber());
                stringBuffer.append("\ndirection : ");
                stringBuffer.append("\naddr : ");
                stringBuffer.append(bDLocation.getAddrStr());
                stringBuffer.append(bDLocation.getDirection());
            } else if (bDLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                stringBuffer.append("\naddr : ");
                stringBuffer.append(bDLocation.getAddrStr());
                stringBuffer.append("\noperationers : ");
                stringBuffer.append(bDLocation.getOperators());
            }
            Constants.latitude = bDLocation.getLatitude();
            Constants.longitude = bDLocation.getLongitude();
            Log.d(tag, stringBuffer.toString());

            Constants.city = bDLocation.getCity();
            Constants.locationCity = bDLocation.getCity();
            Constants.addrStr = bDLocation.getAddrStr();
            Constants.locationProvince = bDLocation.getProvince();
            mLocationClient.stop();
            Log.d(tag, "bDLocation city:" + Constants.city);
            sendRequest();
        }
    }	
    
    private void sendRequest() {
    	mIsInitDone = false;
        new Thread() {
            public void run() {
                super.run();                
                Constants.proList = mNetConnection.getProvince(); 
                if(Constants.proList == null)
                	return;
                if (Constants.city != null) {
	                if (Constants.city.equals(PreferfenceUtils.getCityPreferences(LifeCircleActivity.this))) {
	                    Log.d(tag, "当前定位与历史记录相同");                                
	                    if (isHasCity(Constants.city)) {
	                        Constants.isLocationCurrentCityEqueals = true;
	                    } else {
	                    	Constants.city = "北京市";
	                        PreferfenceUtils.setCityPreferences(LifeCircleActivity.this, "北京市");
	                        isNotHasCity = true;
	                    }
	                } else {
	                    Log.d(tag, "当前定位与历史记录不同");
	                    if (isHasCity(Constants.city)) {
	                    	Log.d(tag, "如果服务器中有定位的城市");
	                        if (isHasCity(PreferfenceUtils.getCityPreferences(LifeCircleActivity.this))) {	                            
	                            isCityChange = true;
	                        } else {
	                            PreferfenceUtils.setCityPreferences(LifeCircleActivity.this, Constants.city);
	                            Constants.isLocationCurrentCityEqueals = true;
	                        }
	                    } else {
	                        isNotHasCity = true;
	                        Log.d(tag, "如果服务器中没有定位的城市");
	                        if (isHasCity(PreferfenceUtils.getCityPreferences(LifeCircleActivity.this))) {
	                        	Constants.city = PreferfenceUtils.getCityPreferences(LifeCircleActivity.this);
	                        } else {
	                        	Constants.city = "北京市";
	                            PreferfenceUtils.setCityPreferences(LifeCircleActivity.this, "北京市");
	                        }
	                    }
	                }
	                for (int i = 0; i < Constants.proList.size(); i++) {
	                    String[] cities = ((Netable) Constants.proList.get(i)).getCities();
	                    for (String city : cities) {
	                        if (PreferfenceUtils.getCityPreferences(LifeCircleActivity.this).equals(city)) {
//	                            Constants.YJT_IP = ((Netable) Constants.proList.get(i)).getIp();
	                            Constants.PRO_IP = ((Netable) Constants.proList.get(i)).getIp();
	                            Log.d(tag, "Location.PRO_IP2:" + Constants.PRO_IP);
	                            Constants.PRO_NAME = ((Netable) Constants.proList.get(i)).getName();
	                            Constants.business = ((Netable) Constants.proList.get(i)).getBussiness();
	                        }
	                    }
	                }

		            Log.d(tag, "首页等待定位");
		            try {
		            	Thread.sleep(500);
		            } catch (InterruptedException e) {
		            	e.printStackTrace();
		            }
                }
	            
                mHandler.post(new Runnable() {
                    public void run() {
                    	mTVLocation.setText(Constants.choose(LifeCircleActivity.this, PreferfenceUtils.getCityPreferences(LifeCircleActivity.this)));
                    	sendGetOtherData();
                    	if(isNotHasCity) {
                    		showToastCityNotHas();
                    	} else if(isCityChange)
                    		showChangeCity();
                    	}
                });
            }
        }.start();
    }    
    
    private void sendGetOtherData() {   
    	mIsInitDone = false;
//        mRLNearBySix.setVisibility(View.INVISIBLE);
//        mRLNearBySix.startAnimation(animationHide);
        mProgressBar.setVisibility(View.VISIBLE);
        
        new Thread() {            
            class getDataAndDisplayRunnable implements Runnable {
                private final Netable cityAllInfo;

                getDataAndDisplayRunnable(Netable netable) {
                    cityAllInfo = netable;
                }

                public void run() {
                	if(localList == null) {
                		return;
                	}
                    for (int i = 0; i < localList.size(); i++) {
                        Netable netable = (Netable) localList.get(i);
                        switch (i) {
                            case 0: 
                            	String url1 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu1.setTag(url1);
                            	Log.d(tag, "su1 icon url:" + url1);
                            	MainApplication.getImageLoader().get(url1, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu1.getTag())){
                        					mIVSu1.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu1.setText(netable.getFirstType());
                                } else {
                                	mTVSu1.setText(netable.getSecondType());
                                }
                                mLLSu1.setVisibility(View.VISIBLE);
                                break;
                            case 1:        
                            	String url2 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu2.setTag(url2);
                            	Log.d(tag, "su2 icon url:" + url2);
                            	MainApplication.getImageLoader().get(url2, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu2.getTag())){
                        					mIVSu2.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu2.setText(netable.getFirstType());
                                } else {
                                	mTVSu2.setText(netable.getSecondType());
                                }
                                mLLSu2.setVisibility(View.VISIBLE);
                                break;
                            case 2:              
                            	String url3 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu3.setTag(url3);
                            	Log.d(tag, "su3 icon url:" + url3);
                            	MainApplication.getImageLoader().get(url3, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu3.getTag())){
                        					mIVSu3.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu3.setText(netable.getFirstType());
                                } else {
                                	mTVSu3.setText(netable.getSecondType());
                                }
                                mLLSu3.setVisibility(View.VISIBLE);
                                break;
                            case 3:                       
                            	String url4 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu4.setTag(url4);
                            	Log.d(tag, "su4 icon url:" + url4);
                            	MainApplication.getImageLoader().get(url4, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu4.getTag())){
                        					mIVSu4.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu4.setText(netable.getFirstType());
                                } else {
                                	mTVSu4.setText(netable.getSecondType());
                                }
                                mLLSu4.setVisibility(View.VISIBLE);
                                break;
                            case 4:        
                            	String url5 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu5.setTag(url5);
                            	Log.d(tag, "su5 icon url:" + url5);
                            	MainApplication.getImageLoader().get(url5, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu5.getTag())){
                        					mIVSu5.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu5.setText(netable.getFirstType());
                                } else {
                                	mTVSu5.setText(netable.getSecondType());
                                }
                                mLLSu5.setVisibility(View.VISIBLE);
                                break;
                            case 5:      
                            	String url6 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu6.setTag(url6);
                            	Log.d(tag, "su6 icon url:" + url6);
                            	MainApplication.getImageLoader().get(url6, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu6.getTag())){
                        					mIVSu6.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu6.setText(netable.getFirstType());
                                } else {
                                	mTVSu6.setText(netable.getSecondType());
                                }
                                mLLSu6.setVisibility(View.VISIBLE);
                                break;
                            case 6:    
                            	String url7 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu7.setTag(url7);
                            	Log.d(tag, "su7 icon url:" + url7);
                            	MainApplication.getImageLoader().get(url7, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu7.getTag())){
                        					mIVSu7.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu7.setText(netable.getFirstType());
                                } else {
                                	mTVSu7.setText(netable.getSecondType());
                                }
                                mLLSu7.setVisibility(View.VISIBLE);
                                break;
                            case 7:     
                            	String url8 = "https://" + Constants.PRO_IP + "/" + netable.getIconUrl();
                            	mIVSu8.setTag(url8);
                            	Log.d(tag, "su8 icon url:" + url8);
                            	MainApplication.getImageLoader().get(url8, new ImageLoaderListener() {
                        			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals( mIVSu8.getTag())){
                        					mIVSu8.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});

                                if (TextUtils.isEmpty(netable.getSecondType())){
                                    mTVSu8.setText(netable.getFirstType());
                                } else {
                                	mTVSu8.setText(netable.getSecondType());
                                }
                                mLLSu8.setVisibility(View.VISIBLE);
                                break;

                            default:
                                break;
                        }
                    }
                    
                    if (cityAllInfo.getGuessLike() == 1) {
                        mLLLike.setVisibility(View.VISIBLE);
                        mLVLike.removeAllViews();
                        favoriteList.clear();
                        if (likeNetable != null) {
                            favoriteList = likeNetable.getMerchants();
                            Log.d(tag, "favoriteList:" + favoriteList.size());
                            if (favoriteList.size() > 0) {
                                favoriteAdapter = new SearchFavoriteAdapter(LifeCircleActivity.this, favoriteList);
                                mLVLike.setAdapter(favoriteAdapter);
                            } else {
                                mLLLike.setVisibility(View.GONE);
                            }
                        } else {
                            mLLLike.setVisibility(View.GONE);
                        }
                    } else {
                        mLLLike.setVisibility(View.GONE);
                    }
                    
//                    mRLNearBySix.setVisibility(View.VISIBLE);
//                    mRLNearBySix.startAnimation(animationShow);
                    mProgressBar.setVisibility(View.GONE);
                    mIsInitDone = true;
                }
            }

            public void run() {
                super.run();
                if(Constants.locationCity == null)
                	return;
                if (Constants.locationCity.equals(PreferfenceUtils.getCityPreferences(LifeCircleActivity.this))) {
                	Constants.isLocationCurrentCityEqueals = true;
                } else {
                	Constants.isLocationCurrentCityEqueals = false;
                }
                mNetConnection.refreshTokenPrivate();
                Netable cityAllInfo = mNetConnection.getCityAllInfo(PreferfenceUtils.getCityPreferences(LifeCircleActivity.this));
                if(cityAllInfo == null){
                	return;
                }
                Constants.juliNetable.setOptions(cityAllInfo.getDistanceOptions());
                Constants.defaultPress = 100;
                if (localList != null) {
                    localList.clear();
                }
                localList = cityAllInfo.getLocals();
                Netable netable = new Netable();
                netable.setAreas(cityAllInfo.getAreas());
                Netable netable2 = new Netable();
                netable2.setFirstTypes(cityAllInfo.getFirstTypes());
                Constants.setArea(netable);
                Constants.setFirstTypes(netable2);
                if (cityAllInfo.getPromotionArea() == 1) {
                    sallNetable = mNetConnection.getGoods("1", "0", PreferfenceUtils.getCityPreferences(LifeCircleActivity.this), "5");
                }
                if (cityAllInfo.getGuessLike() == 1) {
                    likeNetable = mNetConnection.guessLikeMerchant(PreferfenceUtils.getCityPreferences(LifeCircleActivity.this), 
                    		Constants.longitude+"", Constants.latitude+"");
                }
                mHandler.post(new getDataAndDisplayRunnable(cityAllInfo));
            }
        }.start();
    }
    
    private void showToastCityNotHas() {
        new AlertDialog.Builder(LifeCircleActivity.this).setTitle("提示:")
        	.setMessage("您所在的城市尚未开通周边商户服务，敬请期待!")
        	.setIcon(R.drawable.ic_launcher)
        	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialogInterface, int i) {
        			
        		}
        }).create().show();
    }
    
    private void showChangeCity() {
  
        new AlertDialog.Builder(LifeCircleActivity.this)
        	.setTitle("提示:")
        	.setMessage("当前城市与定位城市不同,是否切换?")
        	.setIcon(R.drawable.ic_launcher)
        	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialogInterface, int i) {
	                PreferfenceUtils.setCityPreferences(LifeCircleActivity.this, Constants.city);
	                for (int i2 = 0; i2 < Constants.proList.size(); i2++) {
	                    String[] cities = ((Netable) Constants.proList.get(i2)).getCities();
	                    for (String city : cities) {
	                        if (PreferfenceUtils.getCityPreferences(LifeCircleActivity.this).equals(city)) {
	                        	Constants.PRO_IP = ((Netable) Constants.proList.get(i2)).getIp();
	                            Log.d(tag, "Location.PRO_IP2:" + Constants.PRO_IP);
	                            Constants.PRO_NAME = ((Netable) Constants.proList.get(i2)).getName();
	                            Constants.business = ((Netable) Constants.proList.get(i2)).getBussiness();
	                        }
	                    }
	                }
	                mTVLocation.setText(PreferfenceUtils.getCityPreferences(LifeCircleActivity.this));
	                sendGetOtherData();
	            }
        	}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialogInterface, int i) {
	            }
        }).setCancelable(false).create().show();
    }    
	
    private boolean isHasCity(String str) {
        Log.d(tag, "city:" + str);
        boolean z = false;
        for (int i = 0; i < Constants.proList.size(); i++) {
            String[] cities = ((Netable) Constants.proList.get(i)).getCities();
            for (String equals : cities) {
                if (equals.equals(str)) {
                    z = true;
                    break;
                }
            }
        }
        return z;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "onCreate");
		mNetConnection = new NetConnection(LifeCircleActivity.this);
		mHandler = new Handler();
		mMyLocationListener = new MyLocationListener();
		mLocationClient = new LocationClient(MainApplication.getAppContext());
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption opt = new LocationClientOption();
		opt.setIsNeedAddress(true);
		opt.setOpenGps(true);
		mLocationClient.setLocOption(opt);
		mLocationClient.start();
		// 获取位置信息
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.d("LocSDK4", "locClient is null or not started");
		
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		setTitle("周边商户");
		setContentView(R.layout.fragment_life_circle);
		initView();
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
		
	private void initView() {
		mTVLocation = (TextView) findViewById(R.id.tv_location);
		mTVArea = (TextView)findViewById(R.id.tv_area);
		mTVType = (TextView)findViewById(R.id.tv_type);
		mTVOrder = (TextView)findViewById(R.id.tv_order);
		mTVSu1 = (TextView)findViewById(R.id.tv_su1);
		mTVSu2 = (TextView)findViewById(R.id.tv_su2);
		mTVSu3 = (TextView)findViewById(R.id.tv_su3);
		mTVSu4 = (TextView)findViewById(R.id.tv_su4);
		mTVSu5 = (TextView)findViewById(R.id.tv_su5);
		mTVSu6 = (TextView)findViewById(R.id.tv_su6);
		mTVSu7 = (TextView)findViewById(R.id.tv_su7);
		mTVSu8 = (TextView)findViewById(R.id.tv_su8);
		
		
		mIVMap = (ImageView)findViewById(R.id.iv_map);
		mIVSearch = (ImageView)findViewById(R.id.iv_search);
		mIVSu1 = (ImageView)findViewById(R.id.iv_su1);
		mIVSu2 = (ImageView)findViewById(R.id.iv_su2);
		mIVSu3 = (ImageView)findViewById(R.id.iv_su3);
		mIVSu4 = (ImageView)findViewById(R.id.iv_su4);
		mIVSu5 = (ImageView)findViewById(R.id.iv_su5);
		mIVSu6 = (ImageView)findViewById(R.id.iv_su6);
		mIVSu7 = (ImageView)findViewById(R.id.iv_su7);
		mIVSu8 = (ImageView)findViewById(R.id.iv_su8);
		mIVPhoneTraffic = (ImageView)findViewById(R.id.iv_phone_traffic);
		mIVPhoneWallet = (ImageView)findViewById(R.id.iv_phone_wallet);
		mIVNFCLable = (ImageView)findViewById(R.id.iv_nfc_lable);			
		
		mEditSearch = (EditText)findViewById(R.id.edit_search);
		
		mRLNearBy = (RelativeLayout)findViewById(R.id.rl_nearby);
		mRLNearBySix = (RelativeLayout)findViewById(R.id.rl_nearbysix);
		
		mLLSu1 = (LinearLayout)findViewById(R.id.ll_su1);
		mLLSu2 = (LinearLayout)findViewById(R.id.ll_su2);
		mLLSu3 = (LinearLayout)findViewById(R.id.ll_su3);
		mLLSu4 = (LinearLayout)findViewById(R.id.ll_su4);
		mLLSu5 = (LinearLayout)findViewById(R.id.ll_su5);
		mLLSu6 = (LinearLayout)findViewById(R.id.ll_su6);
		mLLSu7 = (LinearLayout)findViewById(R.id.ll_su7);
		mLLSu8 = (LinearLayout)findViewById(R.id.ll_su8);
		mLLPhoneTraffic = (LinearLayout)findViewById(R.id.ll_phone_traffic);
		mLLPhoneWallet = (LinearLayout)findViewById(R.id.ll_phone_wallet);
		mLLNFCLable = (LinearLayout)findViewById(R.id.ll_nfc_lable);
		mLLLike = (LinearLayout) findViewById(R.id.like_ll);
		mLVLike = (LinearView) findViewById(R.id.like_lv);
		
		mProgressBar = (ProgressBar)findViewById(R.id.progress_center);
		
		mTVLocation.setOnClickListener(this);
		mTVArea.setOnClickListener(this);
		mTVType.setOnClickListener(this);
		mTVOrder.setOnClickListener(this);
		mLLSu1.setOnClickListener(this);
		mLLSu2.setOnClickListener(this);
		mLLSu3.setOnClickListener(this);
		mLLSu4.setOnClickListener(this);
		mLLSu5.setOnClickListener(this);
		mLLSu6.setOnClickListener(this);
		mLLSu7.setOnClickListener(this);
		mLLSu8.setOnClickListener(this);
		mIVMap.setOnClickListener(this);
		mIVSearch.setOnClickListener(this);
		mIVPhoneTraffic.setOnClickListener(this);
		mIVPhoneWallet.setOnClickListener(this);
		mIVNFCLable.setOnClickListener(this);
		mLLPhoneTraffic.setOnClickListener(this);
		mLLPhoneWallet.setOnClickListener(this);
		mLLNFCLable.setOnClickListener(this);
		mEditSearch.setOnEditorActionListener(this);
        mLVLike.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
        		if(!mIsInitDone) {
        			Toast.makeText(LifeCircleActivity.this, "正在初始化,请稍等!", Toast.LENGTH_SHORT).show();
        			return;
        		}
                Bundle bundle = new Bundle();
                bundle.putSerializable("netable", (Serializable) likeNetable.getMerchants().get(view.getId()));
                bundle.putSerializable("list", localList);
                Intent intent = new Intent();
                intent.setClass(LifeCircleActivity.this, StoreDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);	                    
            }
        });
		
		animationShow = AnimationUtils.loadAnimation(LifeCircleActivity.this, R.anim.fragement_show);
		animationHide = AnimationUtils.loadAnimation(LifeCircleActivity.this, R.anim.fragement_hide);
	}

	@Override
	public void onClick(View v) {
		if(!mIsInitDone) {
			Toast.makeText(this, "正在初始化,请稍等!", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (v.getId()) {
		case R.id.tv_location:			
			Intent intent = new Intent(LifeCircleActivity.this, ChooseCityActivity.class);			
			startActivityForResult(intent, REQUEST_CHOOSE_CITY);
			break;
		case R.id.tv_area:

			break;
		case R.id.tv_type:

			break;
		case R.id.tv_order:

			break;
		case R.id.ll_su1:
			toSurroundActivity(0);
			break;
		case R.id.ll_su2:
			toSurroundActivity(1);
			break;
		case R.id.ll_su3:
			toSurroundActivity(2);
			break;
		case R.id.ll_su4:
			toSurroundActivity(3);
			break;
		case R.id.ll_su5:
			toSurroundActivity(4);
			break;
		case R.id.ll_su6:
			toSurroundActivity(5);
			break;
		case R.id.ll_su7:
			toSurroundActivity(6);
			break;
		case R.id.ll_su8:
			toSurroundActivity(7);
			break;
		case R.id.iv_map:
            Bundle bundle = new Bundle();
            bundle.putSerializable("action", "1");
            bundle.putSerializable("list", localList);
            Intent intent4 = new Intent();
            intent4.setClass(LifeCircleActivity.this, MapSearchActivity.class);
            intent4.putExtras(bundle);
            startActivity(intent4);
			break;
		case R.id.iv_search:

			break;
		case R.id.iv_phone_traffic:
		case R.id.ll_phone_traffic:
			Intent intent1 = new Intent();
			intent1.setComponent(new ComponentName("cn.yjt.oa.app", "cn.yjt.oa.app.ReadBusCard"));
			startActivity(intent1);			
			break;
		case R.id.iv_phone_wallet:
		case R.id.ll_phone_wallet:
			Intent intent2 = new Intent();
			intent2.setAction(Intent.ACTION_MAIN);
			intent2.addCategory(Intent.CATEGORY_LAUNCHER);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.setPackage("chinatelecom.mwallet");
			intent2.setComponent(new ComponentName("chinatelecom.mwallet", "chinatelecom.mwallet.activity.WelcomeActivity"));
			try {				 
				startActivity(intent2);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
				String package1 = intent2.getPackage();
				if (!AppUtils.open(LifeCircleActivity.this, package1)) {
					startBrowerDownload("http://nfc.189.cn");
				}
			}
			break;
		case R.id.iv_nfc_lable:
		case R.id.ll_nfc_lable:
			Intent intent3 = new Intent();
			intent3.setComponent(new ComponentName("cn.yjt.oa.app", "cn.yjt.oa.app.nfctools.NFCActivity"));
			startActivity(intent3);
			break;
	
		default:
			break;
		}
	}
	
	private void startBrowerDownload(String url) {
		if (!TextUtils.isEmpty(url)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
	}
	
    public void toSurroundActivity(int i) {
    	Netable netable = (Netable) localList.get(i);    	
        Intent intent = new Intent();
        intent.setClass(LifeCircleActivity.this, SurroundActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("action", 0);
        bundle.putString("first", netable.getFirstType());
        bundle.putString("second", netable.getSecondType());
        bundle.putSerializable("list", localList);
        intent.putExtras(bundle);
        startActivity(intent);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String city = data.getStringExtra(Intent.EXTRA_INTENT);
			if(TextUtils.equals(city, PreferfenceUtils.getCityPreferences(LifeCircleActivity.this))) {
				Log.d(tag, "city not changed!");
			} else {
				Log.d(tag, "city changed!");
              PreferfenceUtils.setCityPreferences(LifeCircleActivity.this, city);                

              for (int i = 0; i < Constants.proList.size(); i++) {
                  String[] cities = ((Netable) Constants.proList.get(i)).getCities();
                  for (String city1 : cities) {
                      if (city.equals(city1)) {
                          Constants.PRO_IP = ((Netable) Constants.proList.get(i)).getIp();
                          Log.d(tag, "onActivityResult Location.PRO_IP2:" + Constants.PRO_IP);
                          Constants.PRO_NAME = ((Netable) Constants.proList.get(i)).getName();
                          Constants.business = ((Netable) Constants.proList.get(i)).getBussiness();
                      }
                  }
              }
              mTVLocation.setText(Constants.choose(LifeCircleActivity.this, city));
              sendGetOtherData();
			}
        } else {
        	
        }
	}	

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			sendSearch();
			return true;
		}
		return false;
	}
	
    private void sendSearch() {
		InputMethodManager inputMethodManager = (InputMethodManager)LifeCircleActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(LifeCircleActivity.this.getCurrentFocus().getApplicationWindowToken(), 2);
        String editMsg = mEditSearch.getText().toString();
        Intent intent = new Intent();
        intent.setClass(LifeCircleActivity.this, SurroundActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("action", 1);
        bundle.putString("msg", editMsg);
        bundle.putSerializable("list", localList);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}