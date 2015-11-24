package cn.yjt.oa.app.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.adapter.ImagePagerAdapter;
import cn.yjt.oa.app.app.controller.OnButtonRefreshListener;
import cn.yjt.oa.app.app.widget.ExtendableTextView;
import cn.yjt.oa.app.app.widget.ExtendableTextView.OnSizeChangeListener;
import cn.yjt.oa.app.app.widget.PagerContainer;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.http.AsyncRequest;

import com.viewpagerindicator.CirclePageIndicator;

public class AppDetailsHolder extends AppItemHolder implements
		OnClickListener {
	private static final int CONVERT_TO_MB = 1024 * 1024;
	
	public AppDetailsHolder(Context context, AppInfo appInfo) {
		super(context, appInfo);
	}

	// 应用下载按钮
	private Button btnDownload;
	// 应用介绍展开按钮
	private Button btnUnfold;
	// 下载进度
	private ProgressBar appDownloadProgress;
	// 应用预览图容器
	private PagerContainer pagerContainer;
	// 应用预览图
	private ViewPager viewPager;
	// 应用预览图圆点指示器
	private CirclePageIndicator indicator;
	// 应用预览图适配器
	private ImagePagerAdapter adapter;
	// 应用详情图标
	private ImageView appThumnails;
	// 应用名称
	private TextView appName;
	// 应用下载量
	private TextView appDownCount;
	// 版本号
	private TextView appVersion;
	// 应用介绍
	private ExtendableTextView appDescription;

	public void initView(View view) {
		appThumnails = (ImageView) view.findViewById(R.id.app_details_thumbnails);
		if(appInfo.getIcon()!=null){
			AsyncRequest.getInImageView(appInfo.getIcon(), appThumnails, 0, 0);
		}
		appName = (TextView) view.findViewById(R.id.app_details_title);
		appName.setText(appInfo.getName());
		appDownCount = (TextView) view.findViewById(R.id.app_details_size_source);
		appDownCount.setText(appInfo.getDownCount() + "人下载 | "+ appInfo.getSize() / CONVERT_TO_MB + "MB");
		appVersion = (TextView) view.findViewById(R.id.app_details_version);
		appVersion.setText("版本号：" + appInfo.getVersionCode());
		appDescription = (ExtendableTextView) view.findViewById(R.id.app_details_description);
		appDescription.setText(appInfo.getDescription().trim());
		appDescription.setOnSizeChangedListener(new OnSizeChangeListener() {

			@Override
			public void onLineCountChanged(int lineCount) {
				if (lineCount > 4) {
					btnUnfold.setVisibility(View.VISIBLE);
				}
			}
		});

		pagerContainer = (PagerContainer) view.findViewById(R.id.app_pager_container);

		adapter = new ImagePagerAdapter(context);
		adapter.bindData(appInfo);
		viewPager = pagerContainer.getViewPager();
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(adapter.getCount());
		viewPager.setPageMargin(15);
		viewPager.setClipChildren(false);

		indicator = (CirclePageIndicator) view.findViewById(R.id.app_details_indicator);
		indicator.setViewPager(viewPager);

		btnUnfold = (Button) view.findViewById(R.id.app_details_unfold);
		btnUnfold.setOnClickListener(this);
		
		btnDownload = (Button) view.findViewById(R.id.app_details_download);
		
		btnDownload.setText(appItem.getStatusText());
		btnDownload.setOnClickListener(this);
		appDownloadProgress = (ProgressBar) view.findViewById(R.id.app_details_download_progress);
		appItem.setOnButtonRefreshListener(new OnButtonRefreshListener() {
			
			@Override
			public void onRefresh() {
				btnDownload.post(new Runnable() {
					
					@Override
					public void run() {
						btnDownload.setText(appItem.getStatusText());
					}
				});
				appDownloadProgress.setVisibility(View.VISIBLE);
				appDownloadProgress.setProgress(appItem.getProgress());
				appDownloadProgress.setMax(appItem.getTotal());
			}
		});
		appItem.registerListener();
	}

	@Override
	public View createView() {
		View view = inflater.inflate(R.layout.activity_fragment_app_details,parent, false);
		initView(view);
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId()!=R.id.app_details_unfold){
			appItem.doAction();
		}else{
			appDescription.setMaxLines(Integer.MAX_VALUE);
			btnUnfold.setVisibility(View.GONE);
		}
	}

}
