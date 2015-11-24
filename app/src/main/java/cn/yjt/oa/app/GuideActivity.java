package cn.yjt.oa.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import cn.yjt.oa.app.component.UmengBaseActivity;
import cn.yjt.oa.app.imageloader.ResourceImageCache;
import cn.yjt.oa.app.imageloader.ResourceImageLoader;
import cn.yjt.oa.app.imageloader.ResourceImageLoader.ResourceImageLoaderListener;

public class GuideActivity extends UmengBaseActivity {

	private ViewPager viewPager;
	private GuideAdapter guideAdapter;
	private boolean isLastPager;
	private ResourceImageLoader imageLoader;
	private ResourceImageCache cache;
	private static final int[] RES_IDS = new int[] { R.drawable.guide1,
			R.drawable.guide2, R.drawable.guide3, R.drawable.guide4,
			R.drawable.guide5 };
	private static final int BG_COLOR = Color.rgb(241, 246, 251);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity_layout);
		cache = new ResourceImageCache();
		imageLoader = new ResourceImageLoader(getApplicationContext(), cache);

		viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		guideAdapter = new GuideAdapter(this);
		viewPager.setAdapter(guideAdapter);

		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (arg0 == RES_IDS.length - 1) {
					if (isLastPager) {
						setResult(RESULT_OK);
						finish();
					}
					isLastPager = true;
				} else {
					isLastPager = false;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void loadImage(final View view, int resId) {
		imageLoader.get(resId, new ResourceImageLoaderListener() {

			@Override
			public void onLoadSucess(Bitmap bitmap, int resId) {
				view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
			}

			@Override
			public void onLoadFailure(Throwable t, int resId) {

			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			setResult(RESULT_OK);
			finish();
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cache.clear();
	}

	class GuideAdapter extends PagerAdapter {

		private Context context;

		public GuideAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return RES_IDS.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = new View(context);
			view.setBackgroundResource(R.drawable.guide);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			loadImage(view, RES_IDS[position]);
			((ViewPager) container).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

}
