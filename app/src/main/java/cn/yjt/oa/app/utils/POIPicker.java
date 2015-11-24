package cn.yjt.oa.app.utils;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.location.BDLocationServer;
import cn.yjt.oa.app.location.BaseLocationServer;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class POIPicker extends Dialog implements OnGetGeoCoderResultListener,
		OnItemClickListener, OnLoadMoreListner, OnRefreshListener,
		 OnClickListener, OnGetPoiSearchResultListener {
	private Context context;
	private GeoCoder geoCoder;
	private LinearLayout poiSearch;
	private EditText poiSearchInput;
	private LinearLayout poiSearchClear;
	private PullToRefreshListView listView;
	private SigninListViewAdapter adapter;
	private List<PoiInfo> poiInfoList = new ArrayList<PoiInfo>();
	private int index = 1;
	private List<String> poiInfoUids;
	private int count = 0;
	private LatLng latLng;
	private PoiInfo poiOne;
	private boolean isReverseSuccess;
	private POIPickerListener listener;

	private PoiSearch mPoiSearch = null;
	private int load_Index = 0;
	private static String address;
	private String city="";

	private static final int theme;
	private View progress;

    private MyLocationListener mLocationListenr = new MyLocationListener();

	static {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			theme = android.R.style.Theme_Holo_Light_Dialog_NoActionBar;
		} else {
			theme = -1;
		}
	}

	public POIPicker(Context context, POIPickerListener listener) {
		super(context, theme);
		this.context = context;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		if (listener == null) {
			throw new NullPointerException("POIPickerListener must not be null");
		}
		this.listener = listener;

		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(this);

		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		setContentView(R.layout.dialog_poi_picker);
		poiSearch = (LinearLayout) findViewById(R.id.poi_search);
		poiSearch.setVisibility(View.GONE);
		poiSearchClear = (LinearLayout) findViewById(R.id.poi_search_clear_img);
		poiSearchClear.setOnClickListener(this);
		poiSearchInput = (EditText) findViewById(R.id.poi_search_input);
		poiSearchInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				address = arg0.toString().trim();
				if (!TextUtils.isEmpty(address)) {
					index=1;
					poiSearchClear.setVisibility(View.VISIBLE);
					mPoiSearch.searchInCity((new PoiCitySearchOption())
							.city(city).keyword(address).pageNum(load_Index));
				} else {
					load_Index = 0;
					poiSearchClear.setVisibility(View.GONE);
//					BaseLocationServer.startLocation(POIPicker.this);
                    BDLocationServer.getInstance().startLocation(mLocationListenr);
					geoCoder.setOnGetGeoCodeResultListener(POIPicker.this);
				}
			}
		});

		listView = (PullToRefreshListView) findViewById(R.id.poiResultListView);
		progress = findViewById(R.id.pb_updating);
		adapter = new SigninListViewAdapter(getContext(), poiInfoList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnLoadMoreListner(this);
		listView.setOnRefreshListener(this);
//		BaseLocationServer.startLocation(this);
        BDLocationServer.getInstance().startLocation(mLocationListenr);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
	}

	@Override
	public void onGetReverseGeoCodeResult(
			ReverseGeoCodeResult reverseGeoCodeResult) {
		progress.setVisibility(View.GONE);
		poiSearch.setVisibility(View.VISIBLE);
		try {
			city=reverseGeoCodeResult.getAddressDetail().city;
			if (isReverseSuccess) {
				if(index==1 && poiInfoList.size()>0){
					poiInfoList.clear();
				}
				if (poiInfoList.size() == 0) {
					if (reverseGeoCodeResult.getPoiList() != null) {
						poiInfoList.addAll(reverseGeoCodeResult.getPoiList());
						poiInfoList.add(0, poiOne);
					}
				} else {
					filtRepeat(reverseGeoCodeResult.getPoiList());
				}
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getContext(), "检索数据失败！", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class SigninListViewAdapter extends BaseAdapter {

		private Context context;
		private List<PoiInfo> poiInfoList;

		public SigninListViewAdapter(Context context, List<PoiInfo> poiInfoList) {
			this.context = context;
			this.poiInfoList = poiInfoList;
		}

		@Override
		public int getCount() {
			return poiInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return poiInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setPoiInfoList(List<PoiInfo> poiInfoList) {
			this.poiInfoList = poiInfoList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.poi_list_item,
						null);
			}

			TextView poiname = (TextView) convertView
					.findViewById(R.id.listview_item_poiname);
			TextView topLine = (TextView) convertView
					.findViewById(R.id.top_line);
			PoiInfo poiInfo = (PoiInfo) getItem(position);
			if(TextUtils.isEmpty(address)){
				if (position == 0) {
					topLine.setVisibility(View.VISIBLE);
					poiname.setText(poiInfo.name + ":\t" + poiInfo.address);
				} else {
					topLine.setVisibility(View.GONE);
					poiname.setText(poiInfo.name);
				}
			}else{
				if (position == 0 && "当前位置".equals(poiInfo.name)) {
					address=null;
					topLine.setVisibility(View.VISIBLE);
					poiname.setText(poiInfo.name + ":\t" + poiInfo.address);
				} else if(position == 0){
					topLine.setVisibility(View.VISIBLE);
					poiname.setText(poiInfo.name + "(" + poiInfo.address+")");
				}else{
					topLine.setVisibility(View.GONE);
					poiname.setText(poiInfo.name + "(" + poiInfo.address+")");
				}
			}
			
			return convertView;
		}

	}

	private void filtRepeat(List<PoiInfo> ResultPoiInfoList) {
		poiInfoUids = new ArrayList<String>();
		for (int i = 0; i < poiInfoList.size(); i++) {
			poiInfoUids.add(i, poiInfoList.get(i).uid);
		}
		for (int i = 0; i < ResultPoiInfoList.size(); i++) {
			if (!poiInfoUids.contains(ResultPoiInfoList.get(i).uid)) {
				poiInfoList.add(ResultPoiInfoList.get(i));
				count++;
			}
		}
		if (count < 10) {
			reverseGeoCode(poiInfoList.get(index++).location);
		} else {
			count = 0;
			listView.onLoadMoreComplete();
		}
	}

	private void reverseGeoCode(LatLng latLng) {
		ReverseGeoCodeOption geoCodeOption = new ReverseGeoCodeOption();
		geoCodeOption.location(latLng);
		isReverseSuccess = geoCoder.reverseGeoCode(geoCodeOption);
	}

	@Override
	public void onRefresh() {
		listView.onRefreshComplete();
	}

	@Override
	public void onLoadMore() {
		if (TextUtils.isEmpty(address)) {
			reverseGeoCode(poiInfoList.get(index++).location);
			if (poiInfoList.size() > 100) {
				listView.enableFooterView(false);
			}
		} else {
			load_Index++;
			mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city)
					.keyword(address).pageNum(load_Index));
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PoiInfo info = (PoiInfo) parent.getAdapter().getItem(position);
		listener.onPick(info);
		dismiss();
	}

	private void dismissProgress() {
		progress.post(new Runnable() {

			@Override
			public void run() {
				progress.setVisibility(View.GONE);
				poiSearch.setVisibility(View.VISIBLE);
			}
		});
	}


    private class MyLocationListener implements BDLocationServer.YjtLocationListener{

        @Override
        public void locating() {

        }

        @Override
        public void locationSuccess(BDLocation location) {
            latLng = LocationUtil.changePointGCJ02(new LatLng(location.getLatitude(),
                    location.getLongitude()));
            poiOne = new PoiInfo();
            poiOne.name = "当前位置";
            poiOne.address = location.getAddrStr();
            poiOne.location = new LatLng(latLng.latitude, latLng.longitude);
            reverseGeoCode(latLng);
        }

        @Override
        public void locationFailure(BDLocation bdLocation) {
              dismissProgress();
        }
    }

	public static interface POIPickerListener {
		public void onPick(PoiInfo info);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.poi_search_clear_img:
			address=null;
			poiSearchInput.setText("");
			break;

		default:
			break;
		}

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(context, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}

		if (result.getAllPoi() != null && result.getAllPoi().size() > 0) {
			if (load_Index > 0) {
				listView.onLoadMoreComplete();
				poiInfoList.addAll(result.getAllPoi());
			} else {
				poiInfoList.clear();
				poiInfoList = result.getAllPoi();
			}
			adapter.setPoiInfoList(poiInfoList);
			adapter.notifyDataSetChanged();

		}
	}

}
