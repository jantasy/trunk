package cn.yjt.oa.app.documentcenter;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.DocumentInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TimeLineFragment;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.widget.TimeLineView;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class DocumentCenterMainFragment extends TimeLineFragment {
	private final String LOG_TAG = "精彩瞬间";
	// 网络交互部分的UI
	private FrameLayout mLoadingStatus;
	private LinearLayout mLoadingAlert;
	private ProgressBar mLoadingProgress;

	//
	private TimeLineView mTimeLineView;
	private PullToRefreshListView mPullListView;
	private DocumentCenterListAdapter mMmentsAdapter;

	private int mLocalMomentIndex = 0;
	private final int DOCUMENT_REQUEST_COUNT = 15;

	private Listener<Response<ListSlice<DocumentInfo>>> mFirstPageLoadListner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.document_center_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// 初始化网络访问UI
		mLoadingStatus = (FrameLayout) view
				.findViewById(R.id.quiz_moment_request_status);
		mLoadingAlert = (LinearLayout) view
				.findViewById(R.id.task_request_network_alert);
		mLoadingProgress = (ProgressBar) view
				.findViewById(R.id.task_request_prgress);
		Button loadingRefresh = (Button) view
				.findViewById(R.id.task_request_refresh);
		loadingRefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startLoadingFirstPage();
			}
		});

		mTimeLineView = (TimeLineView) view.findViewById(R.id.timelineview);
		mTimeLineView.setNodeDrawable(getActivity().getResources().getDrawable(
				R.drawable.task_list_timeline_node));

		mPullListView = (PullToRefreshListView) view
				.findViewById(R.id.listview);
		initMomentsListView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mFirstPageLoadListner = new Listener<Response<ListSlice<DocumentInfo>>>() {

			@Override
			public void onResponse(Response<ListSlice<DocumentInfo>> response) {
				mPullListView.setVisibility(View.VISIBLE);
				mLoadingStatus.setVisibility(View.GONE);
				if(response.getCode()==0){
					ListSlice<DocumentInfo> infos = response.getPayload();
					if (infos != null) {
						mLocalMomentIndex = (int) infos.getContent().size();
						mMmentsAdapter.clear();
						mMmentsAdapter.addMoment2Adapter(infos.getContent());
						mMmentsAdapter.notifyDataSetInvalidated();
					}
				}else{
					Toast.makeText(getActivity(),
							response.getDescription(),
							Toast.LENGTH_LONG).show();
				}

				
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				mLoadingProgress.setVisibility(View.GONE);
				mLoadingAlert.setVisibility(View.VISIBLE);

				Log.e(LOG_TAG, "mFirstPageLoadListner---首次加载数据失败");
			}

		};
		startLoadingFirstPage();
	}

	/**
	 * 初始化listview内容
	 */
	private void initMomentsListView() {
		mMmentsAdapter = new DocumentCenterListAdapter(getActivity());
		setListViewAdapter(mMmentsAdapter);
		final Listener<Response<ListSlice<DocumentInfo>>> refreshListner = new Listener<Response<ListSlice<DocumentInfo>>>() {

			@Override
			public void onResponse(Response<ListSlice<DocumentInfo>> response) {
				mPullListView.onRefreshComplete();
				if(response.getCode()==0){
					ListSlice<DocumentInfo> infos = response.getPayload();
					if (infos != null) {
						mLocalMomentIndex = infos.getContent().size();
						mMmentsAdapter.clear();
						mMmentsAdapter.addMoment2Adapter(infos.getContent());
						mMmentsAdapter.notifyDataSetInvalidated();
					}
					Log.e(LOG_TAG, "数据刷新OK");
				}else{
					Toast.makeText(getActivity(),
							response.getDescription(),
							Toast.LENGTH_LONG).show();
				}
				
			}

			@Override
			public void onErrorResponse(InvocationError arg0) {
				mPullListView.onRefreshComplete();
				Toast.makeText(getActivity(), "数据刷新失败", Toast.LENGTH_LONG)
						.show();
				Log.e(LOG_TAG, "refreshListner---数据刷新失败");
			}
		};
		mPullListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				startLoading(refreshListner, 0);
			}
		});
		final Listener<Response<ListSlice<DocumentInfo>>> loadMoreListner = new Listener<Response<ListSlice<DocumentInfo>>>() {

			@Override
			public void onResponse(Response<ListSlice<DocumentInfo>> response) {
				mPullListView.onLoadMoreComplete();
				if(response.getCode()==0){
					ListSlice<DocumentInfo> infos = response.getPayload();
					if (infos != null) {
						mLocalMomentIndex += (int) infos.getContent().size();
						mMmentsAdapter.addMoment2Adapter(infos.getContent());
						mMmentsAdapter.notifyDataSetChanged();
					}
					
					Log.e(LOG_TAG, "loadMoreListner---数据加载成功");
					
				}else{
					Toast.makeText(getActivity(),
							response.getDescription(),
							Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void onErrorResponse(InvocationError error) {
				mPullListView.onLoadMoreComplete();

				Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_LONG)
						.show();
				Log.e(LOG_TAG, "loadMoreListner---数据加载失败");
			}

		};
		mPullListView.setOnLoadMoreListner(new OnLoadMoreListner() {

			@Override
			public void onLoadMore() {
				startLoading(loadMoreListner, mLocalMomentIndex);
			}
		});
	}

	/**
	 * 访问网络获取精彩瞬间的数据
	 */
	private void startLoadingFirstPage() {
		mPullListView.setVisibility(View.INVISIBLE);

		mLoadingStatus.setVisibility(View.VISIBLE);
		mLoadingProgress.setVisibility(View.VISIBLE);
		mLoadingAlert.setVisibility(View.GONE);

		startLoading(mFirstPageLoadListner, 0);
	}

	private void startLoading(
			Listener<Response<ListSlice<DocumentInfo>>> listner,
			int startTaskIndex) {
		AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
		requestBuilder.setModule(AsyncRequest.MODULE_DOCUMENT);
		requestBuilder.setModuleItem(DocumentInfo.DOCUMENT);
		requestBuilder.addPageQueryParameter(startTaskIndex,
				DOCUMENT_REQUEST_COUNT);
		requestBuilder
				.setResponseType(new TypeToken<Response<ListSlice<DocumentInfo>>>() {
				}.getType());
		requestBuilder.setResponseListener(listner);
		requestBuilder.build().get();
	}
}
