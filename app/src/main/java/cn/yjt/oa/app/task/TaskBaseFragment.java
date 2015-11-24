package cn.yjt.oa.app.task;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.ClipboardUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.CopyPopupWindow;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.component.TimeLineFragment;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.widget.TimeLineView;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public abstract class TaskBaseFragment extends TimeLineFragment {
    private final String LOG_TAG = TaskBaseFragment.class.getSimpleName();

    public static final int REQUEST_CODE_OPEN_TASK_DETAIL = 1;
    public static final int REQUEST_CODE_PUBLISHING_NEW_TASK = 2;

    // 网络交互部分的UI
    private FrameLayout mLoadingStatus;
    private LinearLayout mLoadingAlert;
    private ProgressBar mLoadingProgress;

    private TimeLineView mTimeLineView;
    private PullToRefreshListView mTasksListView;

    private final String STATUS_FILTER = "UNCOMPLETED";

    private int mLocalTaskIndex = 0;
    private final int TASK_REQUEST_COUNT = 15;

    private Listener<Response<ListSlice<TaskInfo>>> mFirstPageLoadListner;

    private TasksListAdapter mTasksAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadingStatus = (FrameLayout) view.findViewById(R.id.all_tasks_request_status);
        mLoadingAlert = (LinearLayout) view.findViewById(R.id.task_request_network_alert);
        mLoadingProgress = (ProgressBar) view.findViewById(R.id.task_request_prgress);
        mTaskSearch = (EditText) view.findViewById(R.id.task_search);
        mTaskSearchClear = (ImageButton) view.findViewById(R.id.task_search_clear);

        mTaskSearchClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTaskSearch.setText("");
                mTaskSearchClear.setVisibility(View.GONE);
            }
        });

        mTaskSearch.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager manager = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    searchTask();
                    return true;
                }
                return false;
            }
        });

        mTaskSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence keywords, int start, int before, int count) {
                String filter = keywords.toString().trim();
                TaskListener listener = new TaskListener();
                listener.setTag(filter);
                if (cancelable != null) {
                    cancelable.cancel();
                }
                autoSearchTask(filter, listener);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mTaskSearchClear.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTaskSearchClear.setVisibility(View.VISIBLE);
            }
        });

        Button loadingRefresh = (Button) view.findViewById(R.id.task_request_refresh);
        loadingRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startLoadingFirstPage();
            }
        });

        mTimeLineView = (TimeLineView) view.findViewById(R.id.timelineview);
        mTimeLineView.setNodeDrawable(getActivity().getResources().getDrawable(
                R.drawable.task_list_timeline_node));

        mTasksListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        initTasksListView();
    }

    class TaskListener implements Listener<Response<ListSlice<TaskInfo>>> {
        private String tag = "";

        public void setTag(String tag) {
            this.tag = tag;
        }

        @Override
        public void onResponse(Response<ListSlice<TaskInfo>> response) {
            if (tag.equals(getTaskFilter())) {
                mTasksListView.setVisibility(View.VISIBLE);
                mLoadingStatus.setVisibility(View.GONE);
                if (response.getCode() == 0) {
                    ListSlice<TaskInfo> infos = response.getPayload();
                    if (infos != null) {
                        mLocalTaskIndex = (int) infos.getContent().size();
                        mTasksAdapter.clear();
                        mTasksAdapter.addTaskIntoAdapter(infos.getContent());
                        mTasksAdapter.notifyDataSetInvalidated();
                    }

                } else {
                    Toast.makeText(getActivity(),
                            response.getDescription(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onErrorResponse(InvocationError arg0) {
            if (mLocalTaskIndex > 0) {
                mLoadingStatus.setVisibility(View.INVISIBLE);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            R.string.task_list_refresh_failure,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                mLoadingProgress.setVisibility(View.INVISIBLE);
                mLoadingAlert.setVisibility(View.VISIBLE);
            }
        }

    }

    private void autoSearchTask(String keywords, Listener<Response<ListSlice<TaskInfo>>> listener) {
        startLoading(listener, 0, keywords);
        //progressDialog = ProgressDialog.show(getActivity(), null, "正在搜索任务...");
    }

    private void searchTask() {

         /*记录操作 1010*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_SCREEN_TASK);

        String keywords = mTaskSearch.getText().toString();
        autoSearchTask(keywords, mRefreshingListner);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFirstPageLoadListner = new Listener<Response<ListSlice<TaskInfo>>>() {

            @Override
            public void onResponse(Response<ListSlice<TaskInfo>> response) {
                mTasksListView.setVisibility(View.VISIBLE);
                mLoadingStatus.setVisibility(View.GONE);

                if (response.getCode() == 0) {
                    ListSlice<TaskInfo> infos = response.getPayload();
                    if (infos != null) {
                        mLocalTaskIndex = (int) infos.getContent().size();
                        mTasksAdapter.clear();
                        mTasksAdapter.addTaskIntoAdapter(infos.getContent());
                        mTasksAdapter.notifyDataSetInvalidated();
                    }

                } else {
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

    public void onTaskListDecreased(Intent data) {
        TaskInfo taskInfo = data.getParcelableExtra(TaskDetailActivity.TASK_INVALID_KEY);
        removeTargetTask(taskInfo.getId());
    }

    public void onTaskListRefreshing() {
        mLoadingStatus.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mLoadingAlert.setVisibility(View.INVISIBLE);

        startLoading(mRefreshingListner, 0, getTaskFilter());
    }

    protected abstract String getRequestType();

    private void initTasksListView() {
        mTasksAdapter = new TasksListAdapter(getActivity());
        setListViewAdapter(mTasksAdapter);

        mTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListAdapter adapter = mTasksListView.getAdapter();

                if (adapter.getItem(position) instanceof TaskInfo) {
                    TaskInfo info = (TaskInfo) adapter
                            .getItem(position);
                    uploadReadStatue(info);
                    Intent intent = new Intent(getActivity(),
                            TaskDetailActivity.class);
                    intent.putExtra(
                            TaskDetailActivity.TASK_INFO_DETAIL_KEY,
                            info);
                    getActivity().startActivityForResult(intent,
                            REQUEST_CODE_OPEN_TASK_DETAIL);
                }
            }
        });

        //长点击复制文字内容
        mTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                if(view.getTag() instanceof TasksListAdapter.ViewHolder){
                    final TasksListAdapter.ViewHolder holder = (TasksListAdapter.ViewHolder) view.getTag();
                    CopyPopupWindow copyWindow = new CopyPopupWindow(getActivity());
                    copyWindow.showAsDropDown(view,dp2px(0), dp2px(-60));
                    copyWindow.setOnCopyListener(new CopyPopupWindow.OnCopyClickListener() {
                        @Override
                        public void onCopyClick() {
                            ClipboardUtils.copyToClipboard(holder.taskContent.getText().toString());
                        }
                    });
                }
                return true;
            }
        });

        final Listener<Response<ListSlice<TaskInfo>>> refreshListner = new Listener<Response<ListSlice<TaskInfo>>>() {

            @Override
            public void onResponse(Response<ListSlice<TaskInfo>> response) {
                mTasksListView.onRefreshComplete();
                if (response.getCode() == 0) {
                    ListSlice<TaskInfo> infos = response.getPayload();
                    if (infos != null) {
                        mLocalTaskIndex = (int) infos.getContent().size();

                        mTasksAdapter.clear();
                        mTasksAdapter.addTaskIntoAdapter(infos.getContent());
                        mTasksAdapter.notifyDataSetInvalidated();
                    }

                    Log.e(LOG_TAG, "refreshListner---数据刷新成功");

                } else {
                    Toast.makeText(getActivity(),
                            response.getDescription(),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onErrorResponse(InvocationError error) {
                mTasksListView.onRefreshComplete();

                Toast.makeText(getActivity(), "数据刷新失败", Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "refreshListner---数据刷新失败");
            }

        };
        mTasksListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                startLoading(refreshListner, 0, getTaskFilter());
            }
        });

        final Listener<Response<ListSlice<TaskInfo>>> loadMoreListner = new Listener<Response<ListSlice<TaskInfo>>>() {

            @Override
            public void onResponse(Response<ListSlice<TaskInfo>> response) {
                mTasksListView.onLoadMoreComplete();
                if (response.getCode() == 0) {
                    ListSlice<TaskInfo> infos = response.getPayload();
                    if (infos != null) {
                        mLocalTaskIndex += (int) infos.getContent().size();
                        mTasksAdapter.addTaskIntoAdapter(infos.getContent());
                        mTasksAdapter.notifyDataSetChanged();
                    }

                    Log.e(LOG_TAG, "loadMoreListner---数据加载成功");

                } else {
                    Toast.makeText(getActivity(),
                            response.getDescription(),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onErrorResponse(InvocationError error) {
                mTasksListView.onLoadMoreComplete();

                Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_LONG)
                        .show();
                Log.e(LOG_TAG, "loadMoreListner---数据加载失败");
            }

        };
        mTasksListView.setOnLoadMoreListner(new OnLoadMoreListner() {

            @Override
            public void onLoadMore() {
                startLoading(loadMoreListner, mLocalTaskIndex, getTaskFilter());
            }
        });
    }

    public void uploadReadStatue(TaskInfo info) {
        Builder builder = new Builder();
        builder.setModule(String.format(AsyncRequest.MODULE_TASK_ISREAD, info.getId()));
        builder.setResponseType(new TypeToken<Response<String>>() {
        }.getType());
        builder.setResponseListener(new Listener<Response<String>>() {
            @Override
            public void onErrorResponse(InvocationError arg0) {

            }

            @Override
            public void onResponse(Response<String> arg0) {

            }
        });
        builder.build().put();
    }

    // 自动刷新任务列表
    private Listener<Response<ListSlice<TaskInfo>>> mRefreshingListner = new Listener<Response<ListSlice<TaskInfo>>>() {
        @Override
        public void onResponse(Response<ListSlice<TaskInfo>> response) {
//			if (progressDialog != null && progressDialog.isShowing()) {
//				progressDialog.dismiss();
//			}
            mTasksListView.setVisibility(View.VISIBLE);
            mLoadingStatus.setVisibility(View.GONE);
            if (response.getCode() == 0) {
                ListSlice<TaskInfo> infos = response.getPayload();
                if (infos != null) {
                    mLocalTaskIndex = (int) infos.getContent().size();
                    mTasksAdapter.clear();
                    mTasksAdapter.addTaskIntoAdapter(infos.getContent());
                    mTasksAdapter.notifyDataSetInvalidated();
                }

            } else {
                Toast.makeText(getActivity(),
                        response.getDescription(),
                        Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onErrorResponse(InvocationError arg0) {
            if (mLocalTaskIndex > 0) {
                mLoadingStatus.setVisibility(View.INVISIBLE);
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            R.string.task_list_refresh_failure,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                mLoadingProgress.setVisibility(View.INVISIBLE);
                mLoadingAlert.setVisibility(View.VISIBLE);
            }
        }

    };

    private EditText mTaskSearch;
    private ImageButton mTaskSearchClear;

    private ProgressDialog progressDialog;

    private String getTaskFilter() {
        return mTaskSearch.getText().toString().trim();
    }

    private void startLoadingFirstPage() {
        mTasksListView.setVisibility(View.INVISIBLE);

        mLoadingStatus.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mLoadingAlert.setVisibility(View.GONE);

        startLoading(mFirstPageLoadListner, 0, null);
    }

    private Cancelable cancelable;

    private void startLoading(Listener<Response<ListSlice<TaskInfo>>> listener,
                              int startTaskIndex, String keywords) {
        AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
        requestBuilder.setModule(AsyncRequest.MODULE_TASK);
        requestBuilder.addQueryParameter("statusFilter", STATUS_FILTER);
        requestBuilder.addQueryParameter("requestType", getRequestType());
        if (!TextUtils.isEmpty(keywords)) {
            requestBuilder.addQueryParameter("filter", keywords);
        }
        requestBuilder.addPageQueryParameter(startTaskIndex, TASK_REQUEST_COUNT);
        requestBuilder.setResponseType(new TypeToken<Response<ListSlice<TaskInfo>>>() {
        }.getType());
        requestBuilder.setResponseListener(listener);
        Cancelable currentCancelable = requestBuilder.build().get();
        cancelable = currentCancelable;
    }

    private void removeTargetTask(long taskId) {
        final int sectionCount = mTasksAdapter.getSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            final int itemCount = mTasksAdapter.getItemCountAtSection(i);
            for (int j = 0; j < itemCount; j++) {
                TaskInfo info = (TaskInfo) mTasksAdapter.getItem(i, j);
                if (info.getId() == taskId) {
                    mTasksAdapter.removeTaskFromAdapter(info);
                    mLocalTaskIndex--;
                    break;
                }
            }
        }
        mTasksAdapter.notifyDataSetInvalidated();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
