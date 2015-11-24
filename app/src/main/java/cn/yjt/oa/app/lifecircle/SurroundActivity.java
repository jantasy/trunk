package cn.yjt.oa.app.lifecircle;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.lifecircle.adapter.SurroundAdapter;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.net.NetConnection;
import cn.yjt.oa.app.lifecircle.utils.Constants;
import cn.yjt.oa.app.lifecircle.utils.PreferfenceUtils;
import cn.yjt.oa.app.lifecircle.view.FooterListView;
import cn.yjt.oa.app.lifecircle.view.FooterListView.OnAdapterListener;
import cn.yjt.oa.app.lifecircle.view.FooterListView.OnPagingListener;
import cn.yjt.oa.app.lifecircle.view.SpinerDialog;

public class SurroundActivity extends TitleFragmentActivity implements OnClickListener {	
	private static final String tag = "SurroundActivity";
	public TextView mTVArea, mTVType, mTVOrder;
	private LinearLayout mSelBody;
	private ProgressBar mProgress;
	private FooterListView mList;	
	
    private Animation animationHide;
    private Animation animationShow;
    private NetConnection mNetConnection;
    private Handler mHandler;
    
    public static final int TYPE_SEARCH = 1;
    public static final int TYPE_SURROUND = 0;
    private int actionType;
    private String firstPress = "";
    private String secondPress = "";
    public String areaPress = "";
    public String zonePress = "";
    private String orderPress = "";
    private String word = "";
    private ArrayList<Netable> sixList = new ArrayList<Netable>();
    private ArrayList<Netable> juliPressList = new ArrayList<Netable>();
    private String[] arrays;
    private SpinerDialog dialog;
    private SpinerDialog dialog2;
    private View loadMoreView;
    private int headContentHeight = 0;
    private int pag_price = 1;
    private String inc = "0";
    private ArrayList<Netable> netableSurroundList = new ArrayList<Netable>();
    private ArrayList<Netable> tempSurroundList = new ArrayList<Netable>();
    String[] arrays2 = new String[]{"默认", "距离", "评分", "人气"};
    String[] array2 = new String[]{"默认排序", "距离 (由近到远)", "评分 (由高到低)", "人气 (由高到低)"};
    private SurroundAdapter adapterSurround;
    private boolean isPaging_price = true;
    private boolean jlT_tag = false;
    private boolean type_tag = false;
    
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_surround);
		setTitle("附近");
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.fragment_finder_title_location_white);
		
		mTVArea = (TextView) findViewById(R.id.tv_area);
		mTVType = (TextView) findViewById(R.id.tv_type);
		mTVOrder = (TextView) findViewById(R.id.tv_order);
		mSelBody = (LinearLayout) findViewById(R.id.select_body);
		mProgress = (ProgressBar) findViewById(R.id.progress_center);
		mList = (FooterListView) findViewById(R.id.list);
		
		mTVArea.setOnClickListener(this);
		mTVType.setOnClickListener(this);
		mTVOrder.setOnClickListener(this);
		mList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("netable", (Serializable)netableSurroundList.get(i));
                bundle.putSerializable("list", sixList);
                Intent intent = new Intent();
                intent.setClass(SurroundActivity.this, StoreDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mList.setOnAdapterListener(new OnAdapterListener() {
            public BaseAdapter onAdapter() {
                return adapterSurround;
            }
        });
        mList.setonPagingListener(new OnPagingListener() {
            public void onPaging() {
                if (isPaging_price) {
                	pag_price++;
                    sendRequest();
                }
            }
        });
		
		mHandler = new Handler();
		mNetConnection = new NetConnection(this);
        animationShow = AnimationUtils.loadAnimation(this, R.anim.fragement_show);
        animationHide = AnimationUtils.loadAnimation(this, R.anim.fragement_hide);
        
        init();
        sendRequest();
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	@Override
	public void onRightButtonClick() {
        if (netableSurroundList == null || netableSurroundList.size() <= 0) {            
            Toast.makeText(this, "提示:暂无数据,请尝试更改检索条件!", Toast.LENGTH_SHORT).show();
        } else {            
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", netableSurroundList);
            bundle.putSerializable("sixlist", sixList);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClass(this, MapModeActivity.class);
            startActivity(intent);
        }
	}
	@Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 4 && (type_tag || jlT_tag)) {
            changeArrowState(View.GONE, false, false, false);
            return true;
        }
        finish();
        return super.onKeyDown(i, keyEvent);
    }
	
	@Override
	public void onClick(View v) {
        FragmentTransaction beginTransaction;
        switch (v.getId()) {
            case R.id.tv_area /*2131099940*/:
                if (actionType == 0) {
                    if (!Constants.isLocationCurrentCityEqueals) {                        
                        Toast.makeText(this, "提示:异地城市不支持该选项!", Toast.LENGTH_LONG).show();
                    } else if (arrays == null || arrays.length == 0) {                        
                        Toast.makeText(this, "提示:数据正在加载中!", Toast.LENGTH_LONG).show();
                    } else {
                        dialog.adapter.notifyDataSetChanged();
                        dialog.show();
                    }
                } else if (jlT_tag) {
                    changeArrowState(View.GONE, false, false, false);
                } else {
                    beginTransaction = getSupportFragmentManager().beginTransaction();
                    beginTransaction.replace(R.id.select_body, new AreaActivity());
                    beginTransaction.commit();
                    changeArrowState(View.VISIBLE, true, false, false);
                }
                break;
            case R.id.tv_type /*2131099941*/:
                if (type_tag) {
                    changeArrowState(View.GONE, false, false, false);
                    return;
                }
                Constants.typeFrom = TYPE_SEARCH;
                beginTransaction = getSupportFragmentManager().beginTransaction();
                beginTransaction.replace(R.id.select_body, new TypeActivity());
                beginTransaction.commit();
                changeArrowState(View.VISIBLE, false, true, false);
                break;
            case R.id.tv_order /*2131099942*/:
                int i = Constants.defaultOrderPress != 100 ? Constants.defaultOrderPress : 0;
                dialog2 = new SpinerDialog(this, i);
                juliTxt(array2[i], arrays2[i]);
                dialog2.setView(array2);
                if (i > TYPE_SEARCH) {
                    inc = "1";
                }
                dialog2.show();
                dialog2.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    	Constants.defaultOrderPress = i;
                        dialog2.selectItem = i;
                        if (i > TYPE_SEARCH) {
                            inc = "1";
                        } else {
                            inc = "0";
                        }
                        juliTxt(array2[i], arrays2[i]);
                        loadMoreView.setPadding(0, 0, 0, headContentHeight * 0);
                        mProgress.setVisibility(View.VISIBLE);
                        mList.setVisibility(View.GONE);
                        pag_price = 1;                        
                        isPaging_price = true;
                        netableSurroundList.clear();
                        sendRequest();
                        dialog2.cancel();
                    }
                });
                changeArrowState(View.GONE, false, false, true);
                dialog2.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialogInterface) {
                        changeArrowState(View.GONE, false, false, false);
                    }
                });
                break;
            default:
                break;
        }
    }
	
	private void init() {
		actionType = getIntent().getExtras().getInt("action");
        sixList = (ArrayList) getIntent().getSerializableExtra("list");
        if (actionType == 0) {
            setTitle("附近商户");
            firstPress = getIntent().getExtras().getString("first");
            secondPress = getIntent().getExtras().getString("second");
            Log.d(tag, firstPress + "," + secondPress);
            if (!TextUtils.isEmpty(secondPress)) {
                mTVType.setText(secondPress);
            } else if (!TextUtils.isEmpty(firstPress)) {
            	mTVType.setText(firstPress);
            }
            juliPressList = Constants.juliNetable.getOptions();
            arrays = new String[juliPressList.size()];
            int i = 0;
            for (int i2 = 0; i2 < juliPressList.size(); i2 += TYPE_SEARCH) {
                arrays[i2] = ((Netable) juliPressList.get(i2)).getDistance();
                if (((Netable) juliPressList.get(i2)).isDefaultOption()) {
                    if (Constants.defaultPress != 100) {
                        i = Constants.defaultPress;
                    } else {
                    	Constants.defaultPress = i2;
                        i = i2;
                    }
                }
            }
            setOrderTxt(arrays[i]);
            dialog = new SpinerDialog(this, i);
            dialog.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    Constants.defaultPress = i;                    
                    loadMoreView.setPadding(0, 0, 0, headContentHeight * 0);
                    mProgress.setVisibility(View.VISIBLE);
                    mList.setVisibility(View.GONE);
                    pag_price = 1;
                    netableSurroundList.clear();
                    setOrderTxt(arrays[i]);
                    sendRequest();
                    dialog.cancel();
                }
            });
            dialog.setView(arrays);
            dialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    changeArrowState(View.GONE, false, false, false);
                }
            });
        } else {
            word = getIntent().getExtras().getString("msg");
            setTitle("商户列表");
        }
        
        int i3 = Constants.defaultOrderPress != 100 ? Constants.defaultOrderPress : 0;
        dialog2 = new SpinerDialog(this, i3);
        juliTxt(array2[i3], arrays2[i3]);
        dialog2.setView(array2);
        if (i3 > TYPE_SEARCH) {
            inc = "1";
        }
        loadMoreView = getLayoutInflater().inflate(R.layout.loadmoreview, null);
        measureView(loadMoreView);
        headContentHeight = loadMoreView.getMeasuredHeight();
        adapterSurround = new SurroundAdapter(this, netableSurroundList);
        mList.addFooterView(loadMoreView, null, false);
        mList.setAdapter(adapterSurround);
		
	}
	
    private void setOrderTxt(String str) {
        mTVArea.setText(str);
        areaPress = str;
    }
    
    private void juliTxt(String str, String str2) {
        mTVOrder.setText(str);
        orderPress = str2;
    }

    private void measureView(View view) {
        LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(-1, -2);
        }
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(0, 0, layoutParams.width);
        int i = layoutParams.height;
        view.measure(childMeasureSpec, i > 0 ? MeasureSpec.makeMeasureSpec(i, 1073741824) : MeasureSpec.makeMeasureSpec(0, 0));
    }
    
    public void send() {
        mList.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        pag_price = 1;
        isPaging_price = true;
        loadMoreView.setPadding(0, 0, 0, headContentHeight * 0);
        netableSurroundList.clear();
        sendRequest();
    }
    
    private void sendRequest() {
        new Thread() {
            public void run() {
                double d;
                double d2;
                if (Constants.isLocationCurrentCityEqueals) {                    
                    d = Constants.latitude;
                    d2 = Constants.longitude;
                } else {
                    d = 0.0d;
                    d2 = 0.0d;
                }
                Netable sSurroundNetable;
                if (actionType == 0) {
                    sSurroundNetable = mNetConnection.searchNearBy(PreferfenceUtils.getCityPreferences(SurroundActivity.this), firstPress, secondPress, d2+"", d+"", areaPress, orderPress, new StringBuilder(String.valueOf(pag_price)).toString(), inc);
                } else {
                    if ("全城".equals(areaPress)) {
                        areaPress = "";
                    }
                    sSurroundNetable = mNetConnection.search(PreferfenceUtils.getCityPreferences(SurroundActivity.this), areaPress, zonePress, firstPress, secondPress, d2+"", d+"", word, orderPress, pag_price+"", inc, Constants.FLAG);
                }
            	if(sSurroundNetable == null) 
            		return;
                tempSurroundList.clear();
                tempSurroundList = sSurroundNetable.getMerchants();
                
                mHandler.post(new Runnable() {
                    public void run() {
                        for (int i = 0; i < tempSurroundList.size(); i++) {
                        	netableSurroundList.add(tempSurroundList.get(i));                            
                        }
                        if (tempSurroundList.size() != 10) {
                            loadMoreView.setPadding(0, 0, 0, headContentHeight * -1);
                            isPaging_price = false;
                            if (pag_price == 1 && netableSurroundList.size() == 0) {
                                Toast.makeText(SurroundActivity.this, "提示:暂无数据,请尝试更改检索条件!", Toast.LENGTH_LONG).show();
                            }
                        }
                        adapterSurround.setList(netableSurroundList);
                        adapterSurround.notifyDataSetChanged();
                        mProgress.setVisibility(View.GONE);
                        mList.setVisibility(View.VISIBLE);
                        Log.d(tag, "pag_price:" + pag_price);

                        if (pag_price == 1) {                            
                            mList.setSelection(0);
                            mList.startAnimation(animationShow);
                        }                        
                    }
                });
            }
        }.start();
    
    	
    }
    
    public void changeArrowState(int i, boolean z, boolean z2, boolean z3) {
        mSelBody.isShown();
        mSelBody.setVisibility(i);
        if (i == View.VISIBLE) {
        	mSelBody.startAnimation(animationShow);
        }
        type_tag = z2;
        jlT_tag = z;

        if (!z && !z2 && !z3) {
            mTVArea.setTextColor(getResources().getColor(R.color.dark_normal));
            mTVType.setTextColor(getResources().getColor(R.color.dark_normal));
            mTVOrder.setTextColor(getResources().getColor(R.color.dark_normal));
        } else if (z) {
            mTVArea.setTextColor(getResources().getColor(R.color.widget_selected_front));
            mTVType.setTextColor(getResources().getColor(R.color.dark_normal));
            mTVOrder.setTextColor(getResources().getColor(R.color.dark_normal));
        } else if (z2) {
            mTVArea.setTextColor(getResources().getColor(R.color.dark_normal));
            mTVType.setTextColor(getResources().getColor(R.color.widget_selected_front));
            mTVOrder.setTextColor(getResources().getColor(R.color.dark_normal));
        } else if (z3) {
            mTVArea.setTextColor(getResources().getColor(R.color.dark_normal));
            mTVType.setTextColor(getResources().getColor(R.color.dark_normal));
            mTVOrder.setTextColor(getResources().getColor(R.color.widget_selected_front));
        }
    }
    
    public void setAreaText(String str, String str2) {
        areaPress = str;
        zonePress = str2;
        mTVArea.setText(str2);
    }
    public void setTypeTxtAndSend(String str, String str2, boolean z, boolean z2) {
        firstPress = str;
        secondPress = str2;
        if (z2) {
            secondPress = "";
        }
        if (z) {
            mTVType.setText("全行业");
        } else {
        	mTVType.setText(str2);
        }
        loadMoreView.setPadding(0, 0, 0, headContentHeight * 0);
        mProgress.setVisibility(View.VISIBLE);
        mList.setVisibility(View.GONE);
        pag_price = 1;        
        isPaging_price = true;
        netableSurroundList.clear();
        sendRequest();
    }
}
