package cn.yjt.oa.app.lifecircle.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap.Config;
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

public class SearchFavoriteAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Netable> list_more;

    public SearchFavoriteAdapter(Context context, ArrayList<Netable> arrayList) {
        mContext = context;
        list_more = arrayList;
        inflater = (LayoutInflater) mContext.getSystemService("layout_inflater");
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
        View inflate = inflater.inflate(R.layout.item_searchfavorite, null);
        inflate.setId(i);
        TextView textView = (TextView) inflate.findViewById(R.id.name);
        TextView textView2 = (TextView) inflate.findViewById(R.id.msg);
        final ImageView imageView = (ImageView) inflate.findViewById(R.id.left);
        Netable netable = (Netable) getItem(i);
        if (netable != null) {
            textView.setText(netable.getShortName());
            textView2.setText(netable.getAddress());
            String url = "https://" + Constants.PRO_IP + "/" + netable.getImg();
            imageView.setTag(url);        	
        	MainApplication.getImageLoader().get(url, new ImageLoaderListener() {    			
    			@Override
    			public void onSuccess(ImageContainer container) {
    				if(container.getUrl().equals( imageView.getTag())){
    					imageView.setImageBitmap(container.getBitmap());
    				}
    			}
    			
    			@Override
    			public void onError(ImageContainer container) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
        }
        return inflate;
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        if (dataSetObserver != null) {
            super.unregisterDataSetObserver(dataSetObserver);
        }
    }
}
