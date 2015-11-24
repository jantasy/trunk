package cn.yjt.oa.app.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.http.AsyncRequest;

public class ImagePagerAdapter extends PagerAdapter{
	private Context context;
	private static final int defaultImageResId = 0;
	private static final int errorImageResId = 0;
	private List<String> previews;
	public ImagePagerAdapter(Context context){
		this.context = context;
	}
	public void bindData(AppInfo appInfo){
		initPreviews(appInfo);
	}
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
		// TextView view = new TextView(context);
		// view.setText("Item "+position);
		// view.setGravity(Gravity.CENTER);
		// view.setBackgroundColor(Color.argb(255, position * 50, position * 10,
		// position * 50));
		// container.addView(view);
        ImageView imageView = new ImageView(context);
        LogUtils.i("ImagePagerAdapter", "---url--"+previews.get(position));
        if(previews.get(position) != null){
        	AsyncRequest.getInImageView(previews.get(position), imageView, defaultImageResId, errorImageResId);
        }
        //imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.image1));
        container.addView(imageView);
        return imageView;
    }
	
	public void initPreviews(AppInfo appInfo){
		previews = new ArrayList<String>();
		if(!TextUtils.isEmpty(appInfo.getPreview1())){
			previews.add(appInfo.getPreview1());
		}
		if(!TextUtils.isEmpty(appInfo.getPreview2())){
			previews.add(appInfo.getPreview2());
		}
		if(!TextUtils.isEmpty(appInfo.getPreview3())){
			previews.add(appInfo.getPreview3());
		}
		if(!TextUtils.isEmpty(appInfo.getPreview4())){
			previews.add(appInfo.getPreview4());
		}
		if(!TextUtils.isEmpty(appInfo.getPreview5())){
			previews.add(appInfo.getPreview5());
		}
	}

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return previews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}
