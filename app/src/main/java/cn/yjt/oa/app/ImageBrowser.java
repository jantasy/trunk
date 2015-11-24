package cn.yjt.oa.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderProgressListener;
import cn.yjt.oa.app.utils.BitmapUtils;
import cn.yjt.oa.app.widget.ProgressView;
import cn.yjt.oa.app.widget.TouchImageView;

public class ImageBrowser extends TitleFragmentActivity implements
		OnClickListener {

	public static final String SCHEME_FILE = "file://";
	public static final String SCHEME_HTTP = "http://";
	static final String TAG = "ImageBrowser";

	private ViewPager viewPager;
	private String[] picUrls;
	private TextView numView;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	private SparseArray<LoaderListener> listeners = new SparseArray<LoaderListener>();

	public static void launch(Context context, String[] picUrls, int index) {
		Intent intent = new Intent(context, ImageBrowser.class);
		intent.putExtra("pic_url", picUrls);
		intent.putExtra("index", index);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_browser);

		picUrls = this.getIntent().getStringArrayExtra("pic_url");
		int index = getIntent().getIntExtra("index", 0);
		inflater = LayoutInflater.from(this);
		imageLoader = MainApplication.getImageLoader();
		initView();
		viewPager.setCurrentItem(index);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		viewPager.postDelayed(new Runnable() {

			@Override
			public void run() {
				setTitleBarVisibility(View.GONE);
			}
		}, 3000);

	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	private void initView() {
		if (picUrls == null) {
			return;
		}

		numView = (TextView) findViewById(R.id.pic_num);
		String numFormat = "1/" + picUrls.length;
		numView.setText(numFormat);

		viewPager = (ViewPager) findViewById(R.id.pic_browser);
		viewPager.setAdapter(new ImagePagerAdapter());
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int index) {
			String numFormat = "" + (index + 1) + "/" + picUrls.length;
			numView.setText(numFormat);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int position, float percent, int px) {

		}
	}

	private class ImagePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			if (picUrls != null) {
				return picUrls.length;
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View root = inflater.inflate(R.layout.imagebrower_page, container,
					false);
			final TouchImageView touchImageView = (TouchImageView) root
					.findViewById(R.id.touchImageView);
			touchImageView.setOnClickListener(ImageBrowser.this);
			String url = (String) picUrls[position];
			touchImageView.setTag(url);
			final ProgressView progressView = (ProgressView) root
					.findViewById(R.id.progressView);
			container.addView(root);
			if (!TextUtils.isEmpty(url)) {
				if (url.startsWith(SCHEME_HTTP)) {
					requestNetwork(position, touchImageView, progressView);
				} else if(url.startsWith(SCHEME_FILE)){
					String file = url.substring(SCHEME_FILE.length());
					BitmapUtils.setBitmapToImageView(file, touchImageView);
				}
			}
			return root;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	private void requestNetwork(int position,
			final TouchImageView touchImageView, final ProgressView progressView) {
		LoaderListener loaderListener = listeners.get(position);
		if (loaderListener == null) {
			loaderListener = new LoaderListener(touchImageView, progressView);
			listeners.put(position, loaderListener);
		} else {
			loaderListener.imageView = touchImageView;
			loaderListener.progressView = progressView;
		}
		imageLoader.get((String) picUrls[position], MainApplication.getDisplayMetrics().widthPixels,1,loaderListener,false);
	}

	private class LoaderListener implements ImageLoaderProgressListener {
		private ImageView imageView;
		private ProgressView progressView;

		public LoaderListener(ImageView imageView, ProgressView progressView) {
			super();
			this.imageView = imageView;
			this.progressView = progressView;
		}

		@Override
		public void onSuccess(final ImageContainer container) {
			if (imageView.getTag().equals(container.getUrl())) {
				progressView.post(new Runnable() {

					@Override
					public void run() {
						progressView.setVisibility(View.GONE);
						imageView.setImageBitmap(container.getBitmap());
					}
				});
			}
		}

		@Override
		public void onStart(ImageContainer container) {
			if (imageView.getTag().equals(container.getUrl())) {
				progressView.post(new Runnable() {
					
					@Override
					public void run() {
						progressView.setWaiting(false);
						progressView.setVisibility(View.VISIBLE);
					}
				});
			}
		}

		@Override
		public void onProgress(ImageContainer container) {
			if (imageView.getTag().equals(container.getUrl())) {
				progressView.setMax((int) container.getMax());
				progressView.setProgress((int) container.getProgress());
			}
		}

		@Override
		public void onError(ImageContainer container) {
			if (imageView.getTag().equals(container.getUrl())) {
				progressView.post(new Runnable() {
					@Override
					public void run() {
						progressView.setVisibility(View.GONE);
					}
				});
			}
		}

		@Override
		public void onWait(ImageContainer container) {
			if (imageView.getTag().equals(container.getUrl())) {
				progressView.post(new Runnable() {
					
					@Override
					public void run() {
						progressView.setWaiting(true);
						progressView.setVisibility(View.VISIBLE);
					}
				});
			}
		}

		@Override
		public void onStarted(final ImageContainer container) {
			if (imageView.getTag().equals(container.getUrl())) {
				progressView.post(new Runnable() {
					
					@Override
					public void run() {
						progressView.setWaiting(false);
						progressView.setVisibility(View.VISIBLE);
						progressView.setMax((int) container.getMax());
						progressView.setProgress((int) container.getProgress());
					}
				});
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (isTitleBarShow()) {
			setTitleBarVisibility(View.GONE);
		} else {
			setTitleBarVisibility(View.VISIBLE);
		}
	}

}
