package cn.yjt.oa.app.lifecircle.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap.Config;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.utils.Constants;

public class SurroundAdapter extends BaseAdapter {
    private static final String tag = "SurroundAdapter";
    private long currTime;
//    protected ImageLoader imageLoader;
    private LayoutInflater mInflater;
    private ArrayList<Netable> mNetable = new ArrayList<Netable>();
//    DisplayImageOptions options;
    private String url;

    class ViewHolder {
        private TextView address;
        private TextView cost;
        private ImageView ic_1;
        private ImageView ic_2;
        private ImageView ic_3;
        private ImageView ic_4;
        private ImageView ic_5;
        private ImageView icon;
        private TextView locate;
        private TextView name;
        private ImageView new_iv;
        private ImageView sall_iv;
        private RatingBar shop_power;

        ViewHolder() {
        }
    }

    public SurroundAdapter(Context context, ArrayList<Netable> list) {                
        currTime = 0;
        mInflater = LayoutInflater.from(context);
        mNetable = list;            
        currTime = new Date().getTime();
    }

    public void setList(ArrayList<Netable> list) {
        this.mNetable = list;
    }
    
    public void addNewsItem(Netable netable) {
        mNetable.add(netable);
    }

    public int getCount() {
        return mNetable.size();
    }

    public Object getItem(int i) {
        return mNetable.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        Log.d(tag, "第几条" + i);
        if (view == null) {
            view = mInflater.inflate(R.layout.item_shop, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            viewHolder.shop_power = (RatingBar) view.findViewById(R.id.shop_power);
            viewHolder.ic_1 = (ImageView) view.findViewById(R.id.ic_1);
            viewHolder.ic_2 = (ImageView) view.findViewById(R.id.ic_2);
            viewHolder.ic_3 = (ImageView) view.findViewById(R.id.ic_3);
            viewHolder.ic_4 = (ImageView) view.findViewById(R.id.ic_4);
            viewHolder.ic_5 = (ImageView) view.findViewById(R.id.ic_5);
            viewHolder.new_iv = (ImageView) view.findViewById(R.id.new_iv);
            viewHolder.sall_iv = (ImageView) view.findViewById(R.id.sall_iv);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.cost = (TextView) view.findViewById(R.id.cost);
            viewHolder.address = (TextView) view.findViewById(R.id.address);
            viewHolder.locate = (TextView) view.findViewById(R.id.locate);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (mNetable != null && mNetable.size() > 0) {
            String imgUrl;
            viewHolder.name.setText(((Netable) mNetable.get(i)).getFullName());
            viewHolder.cost.setText(((Netable) mNetable.get(i)).getDiscount());
            viewHolder.address.setText(((Netable) mNetable.get(i)).getAddress());
            if (((Netable) mNetable.get(i)).getDistance().equals("0")) {
                viewHolder.locate.setVisibility(View.GONE);
            } else {
                viewHolder.locate.setVisibility(View.VISIBLE);
                if ("90000000".equals(((Netable) mNetable.get(i)).getDistance())) {
                    viewHolder.locate.setText("无坐标");
                } else {
                    double parseDouble = Double.parseDouble(((Netable) mNetable.get(i)).getDistance());
                    if (parseDouble < 1000.0d) {
                        viewHolder.locate.setText(((int) parseDouble) + "m");
                    } else {
                        viewHolder.locate.setText(new StringBuilder(String.valueOf(new DecimalFormat("###.0").format(parseDouble / 1000.0d))).append("km").toString());
                    }
                }
            }
            viewHolder.shop_power.setRating(((Netable) mNetable.get(i)).getScore());
            viewHolder.ic_1.setVisibility(View.GONE);
            viewHolder.ic_2.setVisibility(View.GONE);
            viewHolder.ic_3.setVisibility(View.GONE);
            viewHolder.ic_4.setVisibility(View.GONE);
            viewHolder.ic_5.setVisibility(View.GONE);
            for (int i2 = 0; i2 < ((Netable) mNetable.get(i)).getAttributes().size(); i2++) {
                imgUrl = ((Netable) ((Netable) mNetable.get(i)).getAttributes().get(i2)).getImgUrl();
                switch (i2) {
                    case 0 /*0*/:
                        if (!TextUtils.isEmpty(imgUrl)) {
                        	String url1 = "https://" + Constants.PRO_IP + "/" + imgUrl;
                        	viewHolder.ic_1.setTag(url1);
                        	Log.d(tag, "ic_1 icon url:" + url1);
                        	MainApplication.getImageLoader().get(url1, new ImageLoaderListener() {                    			
                    			@Override
                    			public void onSuccess(ImageContainer container) {
                    				if(container.getUrl().equals((viewHolder.ic_1).getTag())){
                    					viewHolder.ic_1.setImageBitmap(container.getBitmap());
                    				}
                    			}
                    			
                    			@Override
                    			public void onError(ImageContainer container) {
                    				// TODO Auto-generated method stub
                    				
                    			}
                    		});
                            viewHolder.ic_1.setVisibility(View.VISIBLE);                            
                        }
                        break;
                    case 1 /*1*/:
                        if (!TextUtils.isEmpty(imgUrl)) {
                        	String url2 = "https://" + Constants.PRO_IP + "/" + imgUrl;
                        	viewHolder.ic_2.setTag(url2);
                        	Log.d(tag, "ic_2 icon url:" + url2);
                        	MainApplication.getImageLoader().get(url2, new ImageLoaderListener() {                    			
                    			@Override
                    			public void onSuccess(ImageContainer container) {
                    				if(container.getUrl().equals((viewHolder.ic_2).getTag())){
                    					viewHolder.ic_2.setImageBitmap(container.getBitmap());
                    				}
                    			}
                    			
                    			@Override
                    			public void onError(ImageContainer container) {
                    				// TODO Auto-generated method stub
                    				
                    			}
                    		});
                            viewHolder.ic_2.setVisibility(View.VISIBLE);                            
                        }
                        break;
                    case 2 /*2*/:
                        if (!TextUtils.isEmpty(imgUrl)) {
                        	String url3 = "https://" + Constants.PRO_IP + "/" + imgUrl;
                        	viewHolder.ic_3.setTag(url3);
                        	Log.d(tag, "ic_3 icon url:" + url3);
                        	MainApplication.getImageLoader().get(url3, new ImageLoaderListener() {
                    			
                    			@Override
                    			public void onSuccess(ImageContainer container) {
                    				if(container.getUrl().equals((viewHolder.ic_3).getTag())){
                    					viewHolder.ic_3.setImageBitmap(container.getBitmap());
                    				}
                    			}
                    			
                    			@Override
                    			public void onError(ImageContainer container) {
                    				// TODO Auto-generated method stub
                    				
                    			}
                    		});
                            viewHolder.ic_3.setVisibility(View.VISIBLE);
                        	viewHolder.ic_1.setTag(Constants.PRO_IP + imgUrl);
                        	Log.d(tag, "ic_1 icon url:" + Constants.PRO_IP + imgUrl);
                        	MainApplication.getImageLoader().get((Constants.PRO_IP + imgUrl), new ImageLoaderListener() {                    			
                    			@Override
                    			public void onSuccess(ImageContainer container) {
                    				if(container.getUrl().equals((viewHolder.ic_1).getTag())){
                    					viewHolder.ic_1.setImageBitmap(container.getBitmap());
                    				}
                    			}
                    			
                    			@Override
                    			public void onError(ImageContainer container) {
                    				// TODO Auto-generated method stub
                    				
                    			}
                    		});
                            viewHolder.ic_1.setVisibility(View.VISIBLE);                            
                                                    
                        }
                        break;
                    case 3 /*3*/:
                        if (!TextUtils.isEmpty(imgUrl)) {
                        	String url4 = "https://" + Constants.PRO_IP + "/" + imgUrl;
                        	viewHolder.ic_4.setTag(url4);
                        	Log.d(tag, "ic_4 icon url:" + url4);
                        	MainApplication.getImageLoader().get(url4, new ImageLoaderListener() {                    			
                    			@Override
                    			public void onSuccess(ImageContainer container) {
                    				if(container.getUrl().equals((viewHolder.ic_4).getTag())){
                    					viewHolder.ic_4.setImageBitmap(container.getBitmap());
                    				}
                    			}
                    			
                    			@Override
                    			public void onError(ImageContainer container) {
                    				// TODO Auto-generated method stub
                    				
                    			}
                    		});
                            viewHolder.ic_4.setVisibility(View.VISIBLE);                            
                        }
                        break;
                    case 4 /*4*/:
                        if (!TextUtils.isEmpty(imgUrl)) {
                        	String url5 = "https://" + Constants.PRO_IP + "/" + imgUrl;
                        	viewHolder.ic_5.setTag(url5);
                        	Log.d(tag, "ic_5 icon url:" + url5);
                        	MainApplication.getImageLoader().get(url5, new ImageLoaderListener() {                    			
                    			@Override
                    			public void onSuccess(ImageContainer container) {
                    				if(container.getUrl().equals((viewHolder.ic_5).getTag())){
                    					viewHolder.ic_5.setImageBitmap(container.getBitmap());
                    				}
                    			}
                    			
                    			@Override
                    			public void onError(ImageContainer container) {
                    				// TODO Auto-generated method stub
                    				
                    			}
                    		});
                            viewHolder.ic_5.setVisibility(View.VISIBLE);                            
                        }
                        break;
                    default:
                        break;
                }
            }
            url = ((Netable) mNetable.get(i)).getImg();
            if (TextUtils.isEmpty(url)) {
                viewHolder.icon.setImageResource(R.drawable.item_defout);
            } else {
            	String urlIcon = "https://" + Constants.PRO_IP + "/" + url;
            	viewHolder.icon.setTag(urlIcon);
            	Log.d(tag, "icon icon url:" + urlIcon);
            	MainApplication.getImageLoader().get(urlIcon, new ImageLoaderListener() {                    			
        			@Override
        			public void onSuccess(ImageContainer container) {
        				if(container.getUrl().equals((viewHolder.icon).getTag())){
        					viewHolder.icon.setImageBitmap(container.getBitmap());
        				}
        			}
        			
        			@Override
        			public void onError(ImageContainer container) {
        				// TODO Auto-generated method stub
        				
        			}
        		});
//                imageLoader.displayImage(Applications.PRO_IP + url, viewHolder.icon, options);
            }
            try {
                long time;
                String enterTime = ((Netable) mNetable.get(i)).getEnterTime();
                imgUrl = ((Netable) mNetable.get(i)).getDiscountTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Log.d(tag, "------------新促:-----------" + enterTime + "," + imgUrl);
                if (TextUtils.isEmpty(enterTime)) {
                    viewHolder.new_iv.setVisibility(View.GONE);
                } else {
                    time = currTime - simpleDateFormat.parse(enterTime).getTime();
                    long j = (((time / 1000) / 60) / 60) / 24;
                    Log.d(tag, "------------新:-" + time);
                    if (j <= 7) {
                        viewHolder.new_iv.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.new_iv.setVisibility(View.GONE);
                    }
                }
                if (TextUtils.isEmpty(imgUrl)) {
                    viewHolder.sall_iv.setVisibility(View.GONE);
                } else {
                    long time2 = currTime - simpleDateFormat.parse(imgUrl).getTime();
                    time = (((time2 / 1000) / 60) / 60) / 24;
                    Log.d(tag, "------------促:-" + time2);
                    if (time <= 7) {
                        viewHolder.sall_iv.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.sall_iv.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        if (dataSetObserver != null) {
            super.unregisterDataSetObserver(dataSetObserver);
        }
    }
}
