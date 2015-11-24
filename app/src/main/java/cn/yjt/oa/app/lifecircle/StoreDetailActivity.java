package cn.yjt.oa.app.lifecircle;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.lifecircle.adapter.AllGoodsAdapter;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.net.NetConnection;
import cn.yjt.oa.app.lifecircle.utils.Constants;
import cn.yjt.oa.app.lifecircle.utils.PreferfenceUtils;
import cn.yjt.oa.app.lifecircle.view.LinearView;

public class StoreDetailActivity extends TitleFragmentActivity implements OnClickListener {
	
	private static final String tag = "StoreDetailActivity";
	private Netable hisNetable = new Netable();
	private ArrayList<Netable> sixList = new ArrayList<Netable>();
	
    private AllGoodsAdapter adapter;
    private TextView address_txt;
    private Netable allGoods = new Netable();;
    private TextView attr_iv;
    private LinearLayout attr_ll;
    private TextView attr_tv;
    private View attr_v;
    private LinearLayout attradd_ll;
    private ImageView attrapp_iv;
    private LinearLayout bot_ll;
    private ProgressBar center_progress;
    private ScrollView center_scroll;
    private LinearLayout comments_ll;
    private NetConnection conn;
    private long currTime;
    private TextView cuxiao_txt;
    private TextView detail_txt;
    private TextView fullname_txt;
    private TextView goodname_tv;
    private TextView goodpricetv;
    private LinearLayout goods_body;
    private LinearLayout goods_ll;
    private ImageView goodstip_iv;
    private TextView goodstip_tv;
    private ImageView icon;
    private String id;
    boolean isT;
    private TextView jc_ll;
    private ImageView left;
    private View line;
    private View line2;
    private Handler mHandelr = new Handler();;
    String message;
    private LinearView more_lv;
    private ImageView new_iv;
    private LinearLayout otherstore_ll;
    private ProgressDialog pd;
    private TextView phone_txt;
    private TextView pl_text;
    private TextView pls_ll;
    private TextView priceold_tv;
    private PopupWindow pwMyPopWindow;
    private int qNum;
    private Netable sNetable = new Netable();;
    private ImageView sall_iv;
    private LinearLayout sall_ll;
    private TextView sc_ll;
    private TextView share_ll;
    private RatingBar shop_power;
    private LinearLayout showgoods_ll;
    private int tNum;
    private boolean tempAttrIv = false;
    private boolean tempMoreLv;
    private TextView time_txt;
    private TextView travel_txt;
    private TextView type1_btn;
    private TextView type2_btn;
    private TextView type3_btn;
    private TextView type4_btn;
    private TextView type5_btn;
    private TextView type6_btn;
    private LinearLayout type_ll;
    WindowManager wm;
    private TextView xx_text;
    private TextView yhdetail_txt;
    private LinearLayout yhq_ll;
    private TextView yhq_text;	

	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_storedetail);
		setTitle("商户详情");
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		init();
		setListener();
		sendRequest();
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
//	@Override
//    protected void onRestoreInstanceState(Bundle bundle) {
//        if (bundle != null) {
//            hisNetable = (Netable) bundle.getSerializable("hisNetable");
//            sixList = (ArrayList) bundle.getSerializable("sixList");
//        }
//        super.onRestoreInstanceState(bundle);
//    }

	@Override
    protected void onResume() {
        super.onResume();
    }

//	@Override
//    protected void onSaveInstanceState(Bundle bundle) {
//        bundle.putSerializable("hisNetable", hisNetable);
//        bundle.putSerializable("sixList", sixList);
//        super.onSaveInstanceState(bundle);
//    }
	
    private void init() {
        wm = (WindowManager) getSystemService("window");
        attr_tv = (TextView) findViewById(R.id.attr_tv);
        share_ll = (TextView) findViewById(R.id.share_ll);
        sc_ll = (TextView) findViewById(R.id.sc_ll);
        pls_ll = (TextView) findViewById(R.id.pls_ll);
        goods_ll = (LinearLayout) findViewById(R.id.goods_ll);
        attr_v = findViewById(R.id.attr_v);
        type_ll = (LinearLayout) findViewById(R.id.type_ll);
        jc_ll = (TextView) findViewById(R.id.jc_ll);
        left = (ImageView) findViewById(R.id.left);
        showgoods_ll = (LinearLayout) findViewById(R.id.showgoods_ll);
        otherstore_ll = (LinearLayout) findViewById(R.id.otherstore_ll);
        yhq_ll = (LinearLayout) findViewById(R.id.yhq_ll);
        goods_body = (LinearLayout) findViewById(R.id.goods_body);
        
        fullname_txt = (TextView) findViewById(R.id.fullname_txt);
        time_txt = (TextView) findViewById(R.id.time_txt);
        address_txt = (TextView) findViewById(R.id.address_txt);        
        goodname_tv = (TextView) findViewById(R.id.goodname_tv);
        goodpricetv = (TextView) findViewById(R.id.goodpricetv);
        priceold_tv = (TextView) findViewById(R.id.priceold_tv);
        priceold_tv.getPaint().setFlags(16);
        priceold_tv.getPaint().setAntiAlias(true);
        bot_ll = (LinearLayout) findViewById(R.id.bot_ll);
        goodstip_tv = (TextView) findViewById(R.id.goodstip_tv);
        goodstip_iv = (ImageView) findViewById(R.id.goodstip_iv);
        pl_text = (TextView) findViewById(R.id.pl_text);
        phone_txt = (TextView) findViewById(R.id.phone_txt);
        cuxiao_txt = (TextView) findViewById(R.id.cuxiao_txt);
        travel_txt = (TextView) findViewById(R.id.travel_txt);
        detail_txt = (TextView) findViewById(R.id.detail_txt);
        yhdetail_txt = (TextView) findViewById(R.id.yhdetail_txt);
        new_iv = (ImageView) findViewById(R.id.new_iv);
        sall_iv = (ImageView) findViewById(R.id.sall_iv);
        center_progress = (ProgressBar) findViewById(R.id.center_progress);
        center_scroll = (ScrollView) findViewById(R.id.center_scroll);
        shop_power = (RatingBar) findViewById(R.id.shop_power);
        attr_ll = (LinearLayout) findViewById(R.id.attr_ll);
        attradd_ll = (LinearLayout) findViewById(R.id.attradd_ll);
        attr_iv = (TextView) findViewById(R.id.attr_iv);
        attrapp_iv = (ImageView) findViewById(R.id.attrapp_iv);
        line = findViewById(R.id.tv_line);
        line2 = findViewById(R.id.line2);
        icon = (ImageView) findViewById(R.id.icon);
        type1_btn = (TextView) findViewById(R.id.type1_btn);
        type2_btn = (TextView) findViewById(R.id.type2_btn);
        type3_btn = (TextView) findViewById(R.id.type3_btn);
        type4_btn = (TextView) findViewById(R.id.type4_btn);
        type5_btn = (TextView) findViewById(R.id.type5_btn);
        type6_btn = (TextView) findViewById(R.id.type6_btn);
        xx_text = (TextView) findViewById(R.id.xx_text);
        yhq_text = (TextView) findViewById(R.id.yhq_text);
        more_lv = (LinearView) findViewById(R.id.more_lv);
        sall_ll = (LinearLayout) findViewById(R.id.sall_ll);
        comments_ll = (LinearLayout) findViewById(R.id.comments_ll);
        pd = new ProgressDialog(this);
        pd.setTitle("提示:");
        pd.setMessage("操作中,请稍后");
//        options = new Builder().showImageForEmptyUri((int) R.drawable.item_defout).showImageOnFail((int) R.drawable.item_defout).cacheInMemory().cacheOnDisc().bitmapConfig(Config.RGB_565).delayBeforeLoading(500).build();
        conn = new NetConnection(this);
//        asyncImageLoader = SelectedImageCache.getInstance();
//        if (bundle != null) {
//            hisNetable = (Netable) bundle.getSerializable("hisNetable");
//            sixList = (ArrayList) bundle.getSerializable("sixList");
//            Log.d(tag, "bundle != null sixList:" + sixList.toString());
//        } else {
//            hisNetable = (Netable) getIntent().getSerializableExtra("netable");
//            sixList = (ArrayList) getIntent().getSerializableExtra("list");
//            Log.d(tag, "bundle == null sixList:" + sixList.toString());
//        }
		hisNetable = (Netable) getIntent().getSerializableExtra("netable");
		sixList = (ArrayList) getIntent().getSerializableExtra("list");
		Log.d(tag, "oncreate sixList size:" + sixList.size() + "; sixList: " + sixList.toString());
        id = new StringBuilder(String.valueOf(hisNetable.getId())).toString();
        Log.d(tag, "id:" + id);
        currTime = new Date().getTime();
//        iniPopupWindow();
    }
    
    private void setListener() {
        share_ll.setOnClickListener(this);
        otherstore_ll.setOnClickListener(this);
        type1_btn.setOnClickListener(this);
        type2_btn.setOnClickListener(this);
        type3_btn.setOnClickListener(this);
        type4_btn.setOnClickListener(this);
        type5_btn.setOnClickListener(this);
        type6_btn.setOnClickListener(this);
        sall_ll.setOnClickListener(this);
        sc_ll.setOnClickListener(this);
        attr_iv.setOnClickListener(this);
        comments_ll.setOnClickListener(this);
        pls_ll.setOnClickListener(this);
        showgoods_ll.setOnClickListener(this);
        yhq_ll.setOnClickListener(this);
        icon.setOnClickListener(this);
        jc_ll.setOnClickListener(this);
        goods_ll.setOnClickListener(this);
        phone_txt.setOnClickListener(this);
        address_txt.setOnClickListener(this);
        xx_text.setOnClickListener(this);
        yhq_text.setOnClickListener(this);
        time_txt.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.comments_ll: 
                final Intent intent3 = new Intent();
                intent3.setClass(this, CommentListActivity.class);
                final Bundle bundle3 = new Bundle();
                bundle3.putString("id", this.id);
                bundle3.putBoolean("isStore", false);
                intent3.putExtras(bundle3);
                startActivity(intent3);
                break;        
            case R.id.phone_txt: 
                if(!TextUtils.isEmpty(sNetable.getTel())) {
                	final String[] split = sNetable.getTel().split("、");
                    if (split.length > 1) {
                        new AlertDialog.Builder((Context)this).setTitle((CharSequence)"联系商户").setItems((CharSequence[])split, (DialogInterface.OnClickListener)new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialogInterface, final int n) {
                                final Intent intent = new Intent("android.intent.action.DIAL");
                                intent.setData(Uri.parse("tel:" + split[n]));
                                startActivity(intent);
                            }
                        }).show();                        
                    } else {
                        final Intent intent7 = new Intent("android.intent.action.DIAL");
                        intent7.setData(Uri.parse("tel:" + split[0]));
                        startActivity(intent7);
                    }
                }                
                break;           
            case R.id.address_txt:             	
                if (sNetable.getX() == 0.0 || sNetable.getY() == 0.0) {
                    Toast.makeText(this, "提示:尚未设置经纬度信息!", Toast.LENGTH_SHORT).show();                    
                } else {
                    final ArrayList<Netable> list = new ArrayList<Netable>();
                    list.add(sNetable);
                    final Bundle bundle7 = new Bundle();
                    bundle7.putSerializable("list", (Serializable)list);
                    final Intent intent8 = new Intent();
                    intent8.putExtras(bundle7);
                    intent8.setClass(this, MapModeActivity.class);
                    startActivity(intent8);
                }
                break;        
            case R.id.attr_iv: 
            	if(tempAttrIv) {
                    tempAttrIv = false;
                    attradd_ll.setVisibility(View.GONE);
                    attr_iv.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, getResources().getDrawable(R.drawable.ic_down), (Drawable)null, (Drawable)null);
            	} else {
                    tempAttrIv = true;
                    attradd_ll.setVisibility(View.VISIBLE);
                    attr_iv.setCompoundDrawablesWithIntrinsicBounds((Drawable)null, getResources().getDrawable(R.drawable.ic_up), (Drawable)null, (Drawable)null);
            	}
            	break;
            case R.id.showgoods_ll: 
                if (tempMoreLv) {
                    tempMoreLv = false;
                    more_lv.setVisibility(View.GONE);
                    goodstip_tv.setVisibility(View.VISIBLE);
                    goodstip_iv.setImageResource(R.drawable.ic_down);
                } else {
                    tempMoreLv = true;
                    more_lv.setVisibility(View.VISIBLE);
                    goodstip_tv.setVisibility(View.GONE);
                    goodstip_iv.setImageResource(R.drawable.ic_up);                    
                }
                break;            
        }
    }    
	
    public void sendRequest() {
        center_progress.setVisibility(View.VISIBLE);
        center_scroll.setVisibility(View.GONE);
        new Thread() {
            public void run() {
                sNetable = conn.getStoreDetail(id);
                if(sNetable == null)
                	return;
                if (sNetable.getResult() == 0) {
                    allGoods = conn.getGoods("0", id, "", "50");         
                    if(allGoods == null)
                    	return;
                    mHandelr.post(new Runnable() {
                        public void run() {
                            fullname_txt.setText(sNetable.getFullName());
                            address_txt.setText(sNetable.getAddress().trim());
                            phone_txt.setText(sNetable.getTel());
                            cuxiao_txt.setText(sNetable.getDiscount().trim());
                            travel_txt.setText(sNetable.getTravel().trim());
                            detail_txt.setText(sNetable.getDescription().trim());
                            center_progress.setVisibility(View.GONE);
                            center_scroll.setVisibility(View.VISIBLE);
                            pl_text.setText("评论 (共" + sNetable.getCommentCount() + "条)");
                            Log.d(tag, "tNum:" + tNum + ",qNum:" + qNum);
                            if (sNetable.getCollectStatus() == 0) {
                                bot_ll.setVisibility(View.GONE);
                            } else {
                                if (tNum != 0) {
                                    xx_text.setText("消息(" + tNum + ")");
                                }
                                if (qNum != 0) {
                                    yhq_text.setText("定向优惠券(" + qNum + ")");
                                }
                            }
                            try {
                                String enterTime = sNetable.getEnterTime();
                                String discountTime = sNetable.getDiscountTime();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Log.d(tag, "------------新促:-----------" + enterTime + "," + discountTime);
                                if (TextUtils.isEmpty(enterTime)) {
                                    new_iv.setVisibility(View.GONE);
                                } else {
                                    long during = currTime - simpleDateFormat.parse(enterTime).getTime();
                                    long j = (((during / 1000) / 60) / 60) / 24;
                                    Log.d(tag, "------------新:-" + during + "," + j);
                                    if (j <= 7) {
                                        new_iv.setVisibility(View.VISIBLE);
                                    } else {
                                        new_iv.setVisibility(View.GONE);
                                    }
                                }
                                ArrayList attributes;
                                LayoutInflater from;
                                int i;
                                View inflate;
                                TextView textView;
                                ArrayList merchandises;
                                ArrayList arrayList;
                                int i2;
                                Drawable bDrawable;
                                int i3;
                                Netable netable;
                                int length;
                                long saveTags;
                                long currentTimeMillis;
                                long j2;
                                
                                saveTags = currTime - simpleDateFormat.parse(discountTime).getTime();
                                currentTimeMillis = (((saveTags / 1000) / 60) / 60) / 24;
                                Log.d(tag, "------------促:-" + saveTags + "," + currentTimeMillis);
                                if (currentTimeMillis <= 7) {
                                    sall_iv.setVisibility(View.VISIBLE);
                                } else {
                                    sall_iv.setVisibility(View.GONE);
                                }
                                attributes = sNetable.getAttributes();
                                attradd_ll.removeAllViews();
                                if (attributes.size() > 0) {
                                    attr_ll.setVisibility(View.VISIBLE);
                                    String imgUrl = ((Netable) attributes.get(0)).getImgUrl();
                                    String url = "https://" + Constants.PRO_IP + "/" + imgUrl;
                                    attrapp_iv.setTag(url);
                                	Log.d(tag, "attrapp_iv icon url:" + url);
                                	MainApplication.getImageLoader().get(url, new ImageLoaderListener() {                    			
                            			@Override
                            			public void onSuccess(ImageContainer container) {
                            				if(container.getUrl().equals((attrapp_iv).getTag())){
                            					attrapp_iv.setImageBitmap(container.getBitmap());
                            				}
                            			}
                            			
                            			@Override
                            			public void onError(ImageContainer container) {
                            				// TODO Auto-generated method stub
                            				
                            			}
                            		});                                                        
                                
//                                    imageLoader.displayImage(Applications.PRO_IP + ((Netable) attributes.get(0)).getImgUrl(), attrapp_iv, options);
                                    attr_tv.setText(((Netable) attributes.get(0)).getAttr());
                                    if (attributes.size() <= 1) {
                                        attr_v.setVisibility(View.VISIBLE);
                                    } else {
                                        from = LayoutInflater.from(StoreDetailActivity.this);
                                        for (i = 1; i < attributes.size(); i++) {
                                            inflate = from.inflate(R.layout.item_attr, null);
                                            textView = (TextView) inflate.findViewById(R.id.attr_tv);
                                            final ImageView img = (ImageView) inflate.findViewById(R.id.attrapp_iv);
                                            String imgUrlMore = ((Netable) attributes.get(i)).getImgUrl();
                                            String urlMore = "https://" + Constants.PRO_IP + "/" + imgUrlMore;
                                            img.setTag(urlMore);
                                        	Log.d(tag, "img icon url:" + urlMore);
                                        	MainApplication.getImageLoader().get(urlMore, new ImageLoaderListener() {                    			
                                    			@Override
                                    			public void onSuccess(ImageContainer container) {
                                    				if(container.getUrl().equals((img).getTag())){
                                    					img.setImageBitmap(container.getBitmap());
                                    				}
                                    			}
                                    			
                                    			@Override
                                    			public void onError(ImageContainer container) {
                                    				// TODO Auto-generated method stub
                                    				
                                    			}
                                    		});  
//                                            imageLoader.displayImage(Applications.PRO_IP + ((Netable) attributes.get(i)).getImgUrl(), (ImageView) inflate.findViewById(R.id.attrapp_iv), options);
                                            textView.setText(((Netable) attributes.get(i)).getAttr());
                                            attradd_ll.addView(inflate);
                                        }
                                        attr_iv.setVisibility(View.VISIBLE);
                                    }
                                }
                                merchandises = allGoods.getMerchandises();
                                adapter = null;
                                if (more_lv != null) {
                                    more_lv.removeAllViews();
                                }
                                if (merchandises.size() > 0) {
//                                    goods_body.setVisibility(View.VISIBLE);
                                    goodname_tv.setText(((Netable) merchandises.get(0)).getName());
                                    goodpricetv.setText("￥" + (((Netable) merchandises.get(0)).getPrice() / 100.0f));
                                    priceold_tv.setText("原价￥" + (((Netable) merchandises.get(0)).getMoney() / 100.0f));
                                    
                                    String imgUrl = ((Netable) merchandises.get(0)).getImageUrl();
                                    String url = "https://" + Constants.PRO_IP + "/" + imgUrl;
                                    left.setTag(url);
                                	Log.d(tag, "merchandises icon url:" + url);
                                	MainApplication.getImageLoader().get(url, new ImageLoaderListener() {                    			
                            			@Override
                            			public void onSuccess(ImageContainer container) {
                            				if(container.getUrl().equals((left).getTag())){
                            					left.setImageBitmap(container.getBitmap());
                            				}
                            			}
                            			
                            			@Override
                            			public void onError(ImageContainer container) {
                            				// TODO Auto-generated method stub
                            				
                            			}
                            		});  
//                                    imageLoader.displayImage(Applications.PRO_IP + ((Netable) merchandises.get(0)).getImageUrl(), left, options);
                                    if (merchandises.size() > 1) {
                                        showgoods_ll.setVisibility(View.VISIBLE);
                                        arrayList = new ArrayList();
                                        for (i2 = 1; i2 < merchandises.size(); i2++) {
                                            arrayList.add((Netable) merchandises.get(i2));
                                        }
                                        adapter = new AllGoodsAdapter(StoreDetailActivity.this, arrayList);
                                        more_lv.setAdapter(adapter);
                                    }
                                }
                                time_txt.setText("营业时间:" + sNetable.getOpenTime());
                             // 0表示该用户没有收藏，1表示收藏但没收听，3表示收藏又收听
                                if (sNetable.getCollectStatus() == 0) {
//                                    bDrawable = getBDrawable(R.drawable.save_selector);
//                                    bDrawable.setBounds(0, 0, bDrawable.getMinimumWidth(), bDrawable.getMinimumHeight());
//                                    sc_tv.setCompoundDrawables(null, bDrawable, null, null);
                                } else {
//                                    bDrawable = getBDrawable(R.drawable.ic_normal_pressed);
//                                    bDrawable.setBounds(0, 0, bDrawable.getMinimumWidth(), bDrawable.getMinimumHeight());
//                                    sc_tv.setCompoundDrawables(null, bDrawable, null, null);
                                }
                                if (sNetable.getCoupons() != null) {
                                }
                                yhdetail_txt.setText("该商户暂无优惠券");
                                shop_power.setRating(sNetable.getScore());
                                
                                String imgUrl = sNetable.getImg();
                                String url = "https://" + Constants.PRO_IP + "/" + imgUrl;
                                icon.setTag(url);
                            	Log.d(tag, "icon url:" + url);
                            	MainApplication.getImageLoader().get(url, new ImageLoaderListener() {                    			
                        			@Override
                        			public void onSuccess(ImageContainer container) {
                        				if(container.getUrl().equals((icon).getTag())){
                        					icon.setImageBitmap(container.getBitmap());
                        				}
                        			}
                        			
                        			@Override
                        			public void onError(ImageContainer container) {
                        				// TODO Auto-generated method stub
                        				
                        			}
                        		});  
//                                imageLoader.displayImage(Applications.PRO_IP + sNetable.getImg(), icon, options);
                                i3 = 0;
                                for (i2 = 0; i2 < sixList.size(); i2++) {
                                    netable = (Netable) sixList.get(i2);
                                    switch (i2) {
                                        case 0 /*0*/:
                                            if (!TextUtils.isEmpty(netable.getSecondType())) {
                                                type1_btn.setText(netable.getSecondType());
                                                length = netable.getSecondType().toString().length();
                                            } else {
                                                type1_btn.setText(netable.getFirstType());
                                                length = netable.getFirstType().toString().length();
                                            }
  
                                            if (length < 3) {
                                                length = 3;
                                            }
                                            type1_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, (float) length));
                                            type1_btn.setVisibility(View.VISIBLE);
                                            i3 += length;
                                            break;
                                        case 1 /*1*/:
                                            if (!TextUtils.isEmpty(netable.getSecondType())) {
                                                type2_btn.setText(netable.getSecondType());
                                                length = netable.getSecondType().toString().length();
                                            } else {
                                                type2_btn.setText(netable.getFirstType());
                                                length = netable.getFirstType().toString().length();
                                            }
                                            if (length < 3) {
                                                length = 3;
                                            }
                                            type2_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, (float) length));
                                            type2_btn.setVisibility(View.VISIBLE);
                                            i3 += length;
                                            break;
                                        case 2 /*2*/:
                                            if (!TextUtils.isEmpty(netable.getSecondType())) {
                                                type3_btn.setText(netable.getSecondType());
                                                length = netable.getSecondType().toString().length();
                                            } else {
                                                type3_btn.setText(netable.getFirstType());
                                                length = netable.getFirstType().toString().length();
                                            }
                                            if (length < 3) {
                                                length = 3;
                                            }
                                            type3_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, (float) length));
                                            type3_btn.setVisibility(View.VISIBLE);
                                            i3 += length;
                                            break;
                                        case 3 /*3*/:
                                            if (!TextUtils.isEmpty(netable.getSecondType())) {
                                                type4_btn.setText(netable.getSecondType());
                                                length = netable.getSecondType().toString().length();
                                            } else {
                                                type4_btn.setText(netable.getFirstType());
                                                length = netable.getFirstType().toString().length();
                                            }
                                            if (length < 3) {
                                                length = 3;
                                            }
                                            type4_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, (float) length));
                                            type4_btn.setVisibility(View.VISIBLE);
                                            i3 += length;
                                            break;
                                        case 4 /*4*/:
                                            if (!TextUtils.isEmpty(netable.getSecondType())) {
                                                type5_btn.setText(netable.getSecondType());
                                                length = netable.getSecondType().toString().length();
                                            } else {
                                                type5_btn.setText(netable.getFirstType());
                                                length = netable.getFirstType().toString().length();
                                            }
                                            if (length < 3) {
                                                length = 3;
                                            }
                                            type5_btn.setVisibility(View.VISIBLE);
                                            type5_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, (float) length));
                                            i3 += length;
                                            break;
                                        case 5 /*5*/:
                                            if (!TextUtils.isEmpty(netable.getSecondType())) {
                                                type6_btn.setText(netable.getSecondType());
                                                length = netable.getSecondType().toString().length();
                                            } else {
                                                type6_btn.setText(netable.getFirstType());
                                                length = netable.getFirstType().toString().length();
                                            }
                                            if (length < 3) {
                                                length = 3;
                                            }
                                            type6_btn.setVisibility(View.VISIBLE);
                                            type6_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, (float) length));
                                            i3 += length;
                                            break;
                                    }
                                    if (i3 > 0) {
                                        type_ll.setWeightSum((float) i3);
                                    }
                                }
                                if (Arrays.asList(new String[]{"合肥市", "亳州市", "六安市", "安庆市", "宣城市", "宿州市", "池州市", "淮北市", "淮南市", "滁州市", "芜湖市", "蚌埠市", "铜陵市", "阜阳市", "马鞍山市", "黄山市"}).indexOf(PreferfenceUtils.getCityPreferences(StoreDetailActivity.this)) != -1) {
                                    yhq_ll.setVisibility(View.GONE);
                                    line.setVisibility(View.GONE);
                                    line2.setVisibility(View.GONE);
                                } else {
                                    yhq_ll.setVisibility(View.GONE);
                                    line.setVisibility(View.GONE);
                                    line2.setVisibility(View.GONE);
                                }
                                saveTags = PreferfenceUtils.getSaveTags(StoreDetailActivity.this);
                                currentTimeMillis = System.currentTimeMillis();
                                j2 = ((((currentTimeMillis - saveTags) / 1000) / 60) / 60) / 24;;
//                                showSavePop();
                                PreferfenceUtils.setSaveTags(StoreDetailActivity.this, currentTimeMillis);
                                Log.d(tag, "ago:" + saveTags + "\nnow:" + currentTimeMillis + "\ntimes:" + j2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return;
                }
                mHandelr.post(new Runnable() {
                    public void run() {
                        Toast.makeText(StoreDetailActivity.this, sNetable.getReason(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }	
}
