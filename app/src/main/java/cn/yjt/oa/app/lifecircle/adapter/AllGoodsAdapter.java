package cn.yjt.oa.app.lifecircle.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap.Config;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.utils.Constants;

public class AllGoodsAdapter extends BaseAdapter {
	private static final String tag = "AllGoodsAdapter";
    private Context context;
//    protected ImageLoader imageLoader;
    LayoutInflater inflater;
    ArrayList<Netable> list_more;
//    DisplayImageOptions options;

    public AllGoodsAdapter(Context context, ArrayList<Netable> arrayList) {
//        imageLoader = ImageLoader.getInstance();
        context = context;
        list_more = arrayList;
        inflater = (LayoutInflater) context.getSystemService("layout_inflater");
//        options = new Builder().showImageForEmptyUri((int) R.drawable.item_defout).showImageOnFail((int) R.drawable.item_defout).cacheInMemory().cacheOnDisc().bitmapConfig(Config.RGB_565).delayBeforeLoading(500).build();
    }

    public int getCount() {
        return list_more.size();
    }

    public Object getItem(int i) {
        return list_more.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = inflater.inflate(R.layout.item_allgoods, null);
        inflate.setId(i);
        TextView textView = (TextView) inflate.findViewById(R.id.goodname_tv);
        TextView textView2 = (TextView) inflate.findViewById(R.id.goodpricetv);
        TextView textView3 = (TextView) inflate.findViewById(R.id.priceold_tv);
        final ImageView imageView = (ImageView) inflate.findViewById(R.id.left);
        textView3.getPaint().setFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
        textView3.getPaint().setAntiAlias(true);
        textView3.setFocusable(false);
        Netable netable = (Netable) getItem(i);
        if (netable != null) {
            textView.setText(netable.getName());
            textView2.setText("￥" + (netable.getPrice() / 100.0f));
            textView3.setText("原价￥" + (netable.getMoney() / 100.0f));
            final ImageView img = (ImageView) inflate.findViewById(R.id.attrapp_iv);
            String imgUrl = netable.getImageUrl();
            imageView.setTag(Constants.PRO_IP + imgUrl);
        	Log.d(tag, "img icon url:" + Constants.PRO_IP + imgUrl);
        	MainApplication.getImageLoader().get((Constants.PRO_IP + imgUrl), new ImageLoaderListener() {                    			
    			@Override
    			public void onSuccess(ImageContainer container) {
    				if(container.getUrl().equals((imageView).getTag())){
    					imageView.setImageBitmap(container.getBitmap());
    				}
    			}
    			
    			@Override
    			public void onError(ImageContainer container) {
    				// TODO Auto-generated method stub
    				
    			}
    		});              
//            imageLoader.displayImage(Applications.PRO_IP + netable.getImageUrl(), imageView, options);
        }
        return inflate;
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        if (dataSetObserver != null) {
            super.unregisterDataSetObserver(dataSetObserver);
        }
    }
}
