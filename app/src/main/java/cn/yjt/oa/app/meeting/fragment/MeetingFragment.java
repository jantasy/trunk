package cn.yjt.oa.app.meeting.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.base.adapter.YjtBaseAdapter;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.meeting.MeetingActivity;
import cn.yjt.oa.app.meeting.adapter.MeetingAdapter;
import cn.yjt.oa.app.meeting.http.MeetingApiHelper;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

/**
 * 展示会议列表的Fragment
 * <pre>
 * 包含一个搜索框和一个展示会议条目的listView,用来展示会议信息,同时会议 信息分为’我参加的会议‘和’我发起的会议‘
 * </pre>
 * @author 熊岳岳
 * @since 2050827
 */
public class MeetingFragment extends Fragment implements OnRefreshListener, OnLoadMoreListner,
		OnClickListener, OnFocusChangeListener,TextWatcher {

	private final String TAG = "MeetingFragment";

	/**作为当前操作属于下拉刷新的标识符*/
	private final int REQUEST_REFRESH = 0;
	/**作为当前操作属于上拉加载的标识符*/
	private final int REQUEST_LOADMORE = 1;

	/**listview的相关适配器*/
	private YjtBaseAdapter<MeetingInfo> mAdapter;
	/**当前界面的数据源*/
	private List<MeetingInfo> mDataList;

	/**分页查询起始页*/
	private int mStartIndex = 0;
	/**分页查询最终页*/
	private final int MAX_COUNT = 15;
	/**查询过滤字段*/
	private String mFilterString = " ";

	/**判断该Fragment的标签*/
	private int mTag = -1;

	/*展示数据的控件*/
	private PullToRefreshListView mPrlvShowdata;
	private ProgressBar mPbLoading;
	/*搜索框中的控件*/
	private EditText mEtSearchInput;
	private Button mBtnSearch;
    private LinearLayout mLlSearchClear;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_list_item, container, false);
		initParam();
		initView(view);
		fillData();
		setListener();
		return view;
	}

	@Override
	public void onStart() {
		mPrlvShowdata.setRefreshingState();
		this.onRefresh();
		super.onStart();
	}

	/*-----onCreateView中的方法START-----*/
	private void initParam() {
		//获取表示当前页面类型的标签
		mTag = getArguments().getInt("tag");
		mDataList = new ArrayList<MeetingInfo>();
		if(getActivity()!=null){
			mAdapter = new MeetingAdapter(getActivity(), mDataList);
		}
	}

	private void initView(View view) {
		mPbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
		mPrlvShowdata = (PullToRefreshListView) view.findViewById(R.id.prlv_showdata);
		//搜索框的控件初始化
        mLlSearchClear = (LinearLayout) view.findViewById(R.id.contact_search_clear_img);
        mEtSearchInput = (EditText) view.findViewById(R.id.et_search_input);
        mBtnSearch = (Button) view.findViewById(R.id.btn_search);
	}

	private void fillData() {
		mPbLoading.setVisibility(View.GONE);
		mPrlvShowdata.setAdapter(mAdapter);
		mEtSearchInput.setHint("搜索会议");
	}

	private void setListener() {
		mAdapter.setListViewAndListener(mPrlvShowdata);
		mPrlvShowdata.setOnRefreshListener(this);
		mPrlvShowdata.setOnLoadMoreListner(this);
        mLlSearchClear.setOnClickListener(this);
        mEtSearchInput.addTextChangedListener(this);
		mEtSearchInput.setOnFocusChangeListener(this);
		mBtnSearch.setOnClickListener(this);
	}

	/*-----oncreate中的方法END-----*/


	/**根据请求数据的类型，请求数据*/
	private void requestDataByType(int requestType) {
		switch (mTag) {

		case MeetingActivity.FRAGMENT_JOIN:
			requestJoinData(requestType);
			break;

		case MeetingActivity.FRAGMENT_PUBLIC:
			requestPublicData(requestType);
			break;

		default:
			LogUtils.e(TAG, "页面类型未知,无法获取数据");
			break;
		}
	}

	/**根据搜索框的字符串进行过滤请求*/
	private void filterMeetingInfo() {
		switch (mTag) {

		case MeetingActivity.FRAGMENT_JOIN:
			filterJoinMeetingInfo();

		case MeetingActivity.FRAGMENT_PUBLIC:
			filterPublicMeetingInfo();
			break;

		default:
			LogUtils.e(TAG, "页面类型未知,无法获取数据");
			break;
		}
	}

	/**向服务器发出请求，请求获取对应的'我参与的会议'信息*/
	private void requestJoinData(final int requestType) {
		MeetingApiHelper.getJoinMeeting(new ResponseListener<ListSlice<MeetingInfo>>() {
			@Override
			public void onSuccess(ListSlice<MeetingInfo> payload) {
				if (requestType == REQUEST_REFRESH) {
					refreshSuccess(payload);
				} else if (requestType == REQUEST_LOADMORE) {
					loadMoreSuccess(payload);
				}
			}
		}, mFilterString, mStartIndex, MAX_COUNT);
	}

	/**向服务器发出请求，请求获取对应的'我发起的会议'信息*/
	private void requestPublicData(final int requestType) {
		MeetingApiHelper.getPublicMeeting(new ResponseListener<ListSlice<MeetingInfo>>() {
			@Override
			public void onSuccess(ListSlice<MeetingInfo> payload) {
				requestSuccess(requestType, payload);
			}
		}, mFilterString, mStartIndex, MAX_COUNT);
	}

	/**向服务器发出请求，根据过滤文字，请求获取对应的'我参与的会议'信息*/
	private void filterJoinMeetingInfo() {
		mStartIndex = 0;
		mFilterString = mEtSearchInput.getText().toString();
		MeetingApiHelper.getJoinMeeting(new ProgressDialogResponseListener<ListSlice<MeetingInfo>>(getActivity(), "正在搜索") {
			@Override
			public void onSuccess(ListSlice<MeetingInfo> payload) {
				filterSuccess(payload);
			}
		}, mFilterString, mStartIndex, MAX_COUNT);
	}

	/**向服务器发出请求，根据过滤文字，请求获取对应的'我发起的会议'信息*/
	private void filterPublicMeetingInfo() {
		mStartIndex = 0;
		mFilterString = mEtSearchInput.getText().toString();
		MeetingApiHelper.getPublicMeeting(new ProgressDialogResponseListener<ListSlice<MeetingInfo>>(getActivity(), "正在搜索") {
			@Override
			public void onSuccess(ListSlice<MeetingInfo> payload) {
				filterSuccess(payload);
			}
		}, mFilterString, mStartIndex, MAX_COUNT);
	}

	/**请求数据成功*/
	private void requestSuccess(final int requestType, ListSlice<MeetingInfo> payload) {
		switch (requestType) {

		case REQUEST_REFRESH:
			refreshSuccess(payload);
			break;

		case REQUEST_LOADMORE:
			loadMoreSuccess(payload);
			break;

		default:
			LogUtils.e(TAG, "请求成功,请求的类型未知，无法进行相印的处理");
			break;
		}
	}

	/** 刷新成功 */
	private void refreshSuccess(ListSlice<MeetingInfo> payload) {
		if (mAdapter != null && mAdapter instanceof MeetingAdapter) {
			//刷新成功需要改变分页查询的初始值，并给适配器设置数据源
			mStartIndex += MAX_COUNT;
			mDataList = payload.getContent();
			((MeetingAdapter) mAdapter).setDataListsAndRefresh(mDataList);
			mPrlvShowdata.onRefreshComplete();
			mPrlvShowdata.onLoadMoreComplete();
		}
	}

	/**加载更多成功*/
	private void loadMoreSuccess(ListSlice<MeetingInfo> payload) {
		if (mAdapter != null && mAdapter instanceof MeetingAdapter) {
			mStartIndex += MAX_COUNT;
			mDataList.addAll(payload.getContent());
			((MeetingAdapter) mAdapter).setDataListsAndRefresh(mDataList);
			mPrlvShowdata.onRefreshComplete();
			mPrlvShowdata.onLoadMoreComplete();
		}
	}

	/** 过滤请求成功 */
	private void filterSuccess(ListSlice<MeetingInfo> payload) {
		if (mAdapter != null && mAdapter instanceof MeetingAdapter) {
			mDataList = payload.getContent();
			mStartIndex += MAX_COUNT;
			((MeetingAdapter) mAdapter).setDataListsAndRefresh(mDataList);
			//过滤成功之后置空搜索框
//			mEtSearchInput.setText("");
			mPrlvShowdata.requestFocus();
		}
	}

    /** 清除搜索框 */
    private void clearSearchInput() {
        mEtSearchInput.setText("");
        mPrlvShowdata.setRefreshingState();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        //下拉刷新时首先获取搜索框中的字符作为查询过滤条件，并且将分页查询的起始值设为0
        mFilterString = mEtSearchInput.getText().toString();
        mStartIndex = 0;
        //根据当前页Fragment的类型，从而获取不同的数据
        requestDataByType(REQUEST_REFRESH);
    }

    @Override
    public void onLoadMore() {
        requestDataByType(REQUEST_LOADMORE);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mBtnSearch.setVisibility(View.VISIBLE);
        } else {
            mBtnSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                filterMeetingInfo();
                break;
            case R.id.contact_search_clear_img:
                clearSearchInput();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mLlSearchClear.setVisibility(View.VISIBLE);
        } else {
            mLlSearchClear.setVisibility(View.GONE);
        }
    }
}
