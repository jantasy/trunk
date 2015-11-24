package cn.yjt.oa.app.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ResourceImageLoader {

	public interface ResourceImageLoaderListener {
		public void onLoadSucess(Bitmap bitmap, int resId);

		public void onLoadFailure(Throwable t, int resId);
	}

	private Context context;
	private ResourceImageCache cache;

	public ResourceImageLoader(Context context, ResourceImageCache cache) {
		this.context = context;
		this.cache = cache;
	}
	
	public ResourceImageLoader(Context context) {
		this(context,null);
	}
	

	public void get(final int resId, final ResourceImageLoaderListener listener) {
		Bitmap bitmap = null;
		if(cache != null){
			bitmap = cache.get(resId);
		}
		
		if (bitmap != null) {
			listener.onLoadSucess(bitmap, resId);
		} else {
			new AsyncTask<Integer, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Integer... params) {
					try {
						Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(),
								resId);
						if(decodeResource != null && cache != null){
							cache.append(resId, decodeResource);
						}else{
							listener.onLoadFailure(new Exception("bitmap is null"), resId);
						}
						return decodeResource;
					} catch (Throwable e) {
						e.printStackTrace();
						listener.onLoadFailure(e, resId);
					}
					return null;
				}
				protected void onPostExecute(Bitmap bitmap) {
					if(bitmap != null){
						listener.onLoadSucess(bitmap, resId);
					}
				}
			}.execute(resId);
		}
	}

}
