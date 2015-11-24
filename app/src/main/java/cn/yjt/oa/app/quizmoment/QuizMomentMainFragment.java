package cn.yjt.oa.app.quizmoment;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.yjt.oa.app.ImageBrowser;
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

public class QuizMomentMainFragment extends TimeLineFragment {
	private final String LOG_TAG = "精彩瞬间";
	// 网络交互部分的UI
	private FrameLayout mLoadingStatus;
	private LinearLayout mLoadingAlert;
	private ProgressBar mLoadingProgress;

	//
	private TimeLineView mTimeLineView;
	private PullToRefreshListView mMomentListView;
	private QuizMomentListAdapter mMmentsAdapter;

	private int mLocalMomentIndex = 0;
	private final int DOCUMENT_REQUEST_COUNT = 15;

	private Listener<Response<ListSlice<DocumentInfo>>> mFirstPageLoadListner;
	
	private ListSlice<DocumentInfo> infos;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.quiz_moment_fragment, container, false);
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

		mMomentListView = (PullToRefreshListView) view
				.findViewById(R.id.listview);
		initMomentsListView();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int positionInAdapter, long id) {
		Object item =  mMmentsAdapter.getItem(positionInAdapter);
		if(item instanceof DocumentInfo){
			DocumentInfo docInfo = (DocumentInfo) item;
			List<DocumentInfo>  docInfos = infos.getContent();
			String[] urls = new String[docInfos.size()];
			for (int i = 0; i < docInfos.size(); i++) {
				urls[i] = docInfos.get(i).getDownUrl();
			} 
			ImageBrowser.launch(getActivity(),urls, docInfos.indexOf(docInfo));				
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mFirstPageLoadListner = new Listener<Response<ListSlice<DocumentInfo>>>() {

			
			@Override
			public void onResponse(Response<ListSlice<DocumentInfo>> response) {
				mMomentListView.setVisibility(View.VISIBLE);
				mLoadingStatus.setVisibility(View.GONE);
				if(response.getCode()==0){
					infos = response.getPayload();
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
		mMmentsAdapter = new QuizMomentListAdapter(getActivity());
		setListViewAdapter(mMmentsAdapter);
		final Listener<Response<ListSlice<DocumentInfo>>> refreshListner = new Listener<Response<ListSlice<DocumentInfo>>>() {

			@Override
			public void onResponse(Response<ListSlice<DocumentInfo>> response) {
				mMomentListView.onRefreshComplete();
				if(response.getCode()==0){
					infos = response.getPayload();
					if (infos != null) {
						mLocalMomentIndex += infos.getContent().size();
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
				mMomentListView.onRefreshComplete();
				Toast.makeText(getActivity(), "数据刷新失败", Toast.LENGTH_LONG)
						.show();
				Log.e(LOG_TAG, "refreshListner---数据刷新失败");
			}
		};
		mMomentListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mLocalMomentIndex = 0;
				startLoading(refreshListner, mLocalMomentIndex);
			}
		});
		final Listener<Response<ListSlice<DocumentInfo>>> loadMoreListner = new Listener<Response<ListSlice<DocumentInfo>>>() {

			@Override
			public void onResponse(Response<ListSlice<DocumentInfo>> response) {
				mMomentListView.onLoadMoreComplete();
				if(response.getCode()==0){
					ListSlice<DocumentInfo> documentInfos = response.getPayload();
					if(documentInfos!=null &&infos!=null){
						infos.setTotal(documentInfos.getTotal());
						infos.getContent().addAll(documentInfos.getContent());
						mLocalMomentIndex += (int) documentInfos.getContent().size();
						mMmentsAdapter.addMoment2Adapter(documentInfos.getContent());
						mMmentsAdapter.notifyDataSetChanged();
					}else if (documentInfos != null && infos==null) {
						infos=documentInfos;
						mLocalMomentIndex += (int) documentInfos.getContent().size();
						mMmentsAdapter.addMoment2Adapter(documentInfos.getContent());
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
				mMomentListView.onLoadMoreComplete();

				Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_LONG)
						.show();
				Log.e(LOG_TAG, "loadMoreListner---数据加载失败");
			}

		};
		mMomentListView.setOnLoadMoreListner(new OnLoadMoreListner() {

			@Override
			public void onLoadMore() {
				startLoading(loadMoreListner, mLocalMomentIndex);
			}
		});
	}

	/**
	 * 访问网络获取精彩瞬间的数据
	 */
	public void startLoadingFirstPage() {
		mMomentListView.setVisibility(View.INVISIBLE);

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
		requestBuilder.setModuleItem(DocumentInfo.MOMENT+"/");
		requestBuilder.addPageQueryParameter(startTaskIndex,
				DOCUMENT_REQUEST_COUNT);
		requestBuilder
				.setResponseType(new TypeToken<Response<ListSlice<DocumentInfo>>>() {
				}.getType());
		requestBuilder.setResponseListener(listner);
		requestBuilder.build().get();
	}
	

}
