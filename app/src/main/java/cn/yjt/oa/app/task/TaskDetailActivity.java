package cn.yjt.oa.app.task;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.ClipboardUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.CopyPopupWindow;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.ReplyInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.TaskCloseModel;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.beans.UserSimpleInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageContainer;
import cn.yjt.oa.app.imageloader.ImageLoader.ImageLoaderListener;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class TaskDetailActivity extends TitleFragmentActivity implements
        OnClickListener, OnCheckedChangeListener {
    private final int MENU_ITEM_ID_CLOSE_TASK = 3;
    private final int MENU_ITEM_ID_CLOSED_TASK = 2;
    private final int MENU_ITEM_ID_DELETE_TASK = 4;

    public static final String TASK_INFO_DETAIL_KEY = "task_info_detail";

    public static final String TASK_LIST_DECREASED_KEY = "task_list_decreased";
    public static final String TASK_INVALID_KEY = "task_invalid";

    public static final String TASK_NEW_REPLY_SENT = "new_reply_sent";

    public static final int COLOR_MARK_GREEN_TO_SERVER = 0x00FF00;
    public static final int COLOR_MARK_YELLOW_TO_SERVER = 0xFFFF00;
    public static final int COLOR_MARK_RED_TO_SERVER = 0xFF0000;

    public static final int COLOR_MARK_GREEN = 0x83CC28;
    public static final int COLOR_MARK_YELLOW = 0xFEAE21;
    public static final int COLOR_MARK_RED = 0xD12A0A;

    private Resources mResources;

    private ImageView mFromUserIcon;
    private TextView mFromUserName;
    private TextView mTaskCreationTime;

    private PullToRefreshListView mReplyListView;
    private TaskReplyListAdapter mReplyListAdapter;

    private int mCheckedColor = COLOR_MARK_GREEN_TO_SERVER;
    private int mCheckedId;
    private EditText mNewReplyContent;
    private boolean mReplyContentEditable = false;

    private List<ReplyInfo> mReplyInfoList = new ArrayList<ReplyInfo>();
    private int mStartIndex = 0;
    private final int REQUEST_COUNT = 15;

    private ProgressDialog mReplyingDialog;
    private Listener<Response<ReplyInfo>> mReplyingTaskListner;

    private ProgressDialog mClosingTaskDialog;
    private Listener<Response<Object>> mClosingTaskListner;

    private ProgressDialog mDeletingTaskDialog;
    private Listener<Response<Object>> mDeletingTaskListner;

    private TaskInfo mCurrentTaskInfo;
    private boolean mNewReplyAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         /*记录操作 1011*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_TASK_DETAIL);


        setContentView(R.layout.task_detail_layout);
        mResources = getResources();

        mFromUserIcon = (ImageView) findViewById(R.id.from_user_icon);
        mFromUserName = (TextView) findViewById(R.id.from_user_name);
        mTaskCreationTime = (TextView) findViewById(R.id.task_creation_time);

        RadioGroup taskMark = (RadioGroup) findViewById(R.id.task_color_mark);
        taskMark.check(R.id.task_mark_progress);
        taskMark.setOnCheckedChangeListener(this);

        mNewReplyContent = (EditText) findViewById(R.id.new_task_reply_content);
        mNewReplyContent.setHint(R.string.task_reply_color_mark_normal_hint);
        mNewReplyContent.setOnClickListener(this);
        ImageView newReplySend = (ImageView) findViewById(R.id.task_reply_send);
        newReplySend.setOnClickListener(this);

        Intent intent = getIntent();
        mCurrentTaskInfo = intent.getParcelableExtra(TASK_INFO_DETAIL_KEY);
        dealTaskInfo(mCurrentTaskInfo);

        initTitleBar();
    }

    private void initTitleBar() {
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        UserSimpleInfo userInfo = AccountManager.getCurrentSimpleUser(this);
        if (userInfo.getId() == mCurrentTaskInfo.getFromUser().getId()) {
            getRightButton().setImageResource(R.drawable.navigation_menu);
        }
    }

    public void onLeftButtonClick() {
        if (mNewReplyAdded) {
            Intent intent = new Intent();
            intent.putExtra(TASK_NEW_REPLY_SENT, true);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!isMenuOpened())
            onLeftButtonClick();

        super.onBackPressed();
    }

    private Listener<Response<ListSlice<ReplyInfo>>> mFirstPageLoadListner = new Listener<Response<ListSlice<ReplyInfo>>>() {
        @Override
        public void onResponse(Response<ListSlice<ReplyInfo>> response) {
            mReplyListView.onRefreshComplete();

            if (response.getCode() == 0) {
                ListSlice<ReplyInfo> infos = response.getPayload();
                if (infos != null) {
                    mStartIndex = (int) infos.getContent().size();
                    mReplyInfoList.clear();
                    mReplyInfoList.addAll(infos.getContent());
                    mReplyListAdapter.notifyDataSetInvalidated();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        response.getDescription(),
                        Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onErrorResponse(InvocationError arg0) {
            mReplyListView.onRefreshComplete();

            Toast.makeText(getApplicationContext(),
                    R.string.task_reply_list_refresh_failure,
                    Toast.LENGTH_SHORT).show();
        }

    };
    private MediaPlayer player;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AccountManager.getCurrentSimpleUser(this).getId() == mCurrentTaskInfo
                .getFromUser().getId()) {
            if (mCurrentTaskInfo.getStatus() == -1) {
                menu.add(0, MENU_ITEM_ID_CLOSED_TASK, 1,
                        mResources.getString(R.string.task_owner_closed_task));
            } else {
                menu.add(0, MENU_ITEM_ID_CLOSE_TASK, 1,
                        mResources.getString(R.string.task_owner_closing_task));
            }
            menu.add(0, MENU_ITEM_ID_DELETE_TASK, 2,
                    mResources.getString(R.string.task_owner_deleting_task));
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_ID_CLOSE_TASK:
                closeCurrentTask();
                break;
            case MENU_ITEM_ID_CLOSED_TASK:
                break;
            case MENU_ITEM_ID_DELETE_TASK:
                deleteCurrentTask();
                break;
            default:
                break;
        }

        return true;
    }

    private void initReplyListView(TaskInfo info) {
        mReplyListView = (PullToRefreshListView) findViewById(R.id.task_reply_list);
        mReplyListAdapter = new TaskReplyListAdapter(this, mReplyInfoList, info);
        mReplyListView.setAdapter(mReplyListAdapter);

        final Listener<Response<ListSlice<ReplyInfo>>> refreshListner = new Listener<Response<ListSlice<ReplyInfo>>>() {

            @Override
            public void onResponse(Response<ListSlice<ReplyInfo>> response) {
                mReplyListView.onRefreshComplete();
                if (response.getCode() == 0) {
                    ListSlice<ReplyInfo> infos = response.getPayload();
                    if (infos != null) {
                        mStartIndex = (int) infos.getContent().size();
                        mReplyInfoList.clear();
                        mReplyInfoList.addAll(infos.getContent());

                        mReplyListView.requestFocusFromTouch();
                        mReplyListView.setSelection(0);
                        mReplyListAdapter.notifyDataSetInvalidated();
                        mReplyListView.requestFocusFromTouch();
                        mReplyListView.setSelection(0);
                        mReplyListView.scrollTo(0, 0);
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            response.getDescription(),
                            Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onErrorResponse(InvocationError error) {
                mReplyListView.onRefreshComplete();
            }

        };
        mReplyListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                startLoading(refreshListner, 0);
            }
        });

        final Listener<Response<ListSlice<ReplyInfo>>> loadMoreListner = new Listener<Response<ListSlice<ReplyInfo>>>() {

            @Override
            public void onResponse(Response<ListSlice<ReplyInfo>> response) {
                mReplyListView.onLoadMoreComplete();
                if (response.getCode() == 0) {
                    ListSlice<ReplyInfo> infos = response.getPayload();
                    if (infos != null) {
                        mStartIndex += (int) infos.getContent().size();
                        mReplyInfoList.addAll(infos.getContent());
                        mReplyListAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            response.getDescription(),
                            Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onErrorResponse(InvocationError error) {
                mReplyListView.onLoadMoreComplete();
            }

        };
        mReplyListView.setOnLoadMoreListner(new OnLoadMoreListner() {

            @Override
            public void onLoadMore() {
                startLoading(loadMoreListner, mStartIndex);
            }
        });

        //长按添加文字复制功能
        mReplyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                if (TaskReplyListAdapter.VIEW_TYPE_TASK_REPLY == mReplyListAdapter.getItemViewType(position==0?position:position-1)) {
                    CopyPopupWindow copyWindow = new CopyPopupWindow(TaskDetailActivity.this);
                    copyWindow.showAsDropDown(view, dp2px(0), dp2px(-20));
                    copyWindow.setOnCopyListener(new CopyPopupWindow.OnCopyClickListener() {
                        @Override
                        public void onCopyClick() {
                            TextView tvReply = (TextView) view.findViewById(R.id.reply_content);
                            ClipboardUtils.copyToClipboard(tvReply.getText().toString());
                        }
                    });
                }else if(TaskReplyListAdapter.VIEW_TYPE_TASK_CONTENT == mReplyListAdapter.getItemViewType(position==0?position:position-1)){
                    CopyPopupWindow copyWindow = new CopyPopupWindow(TaskDetailActivity.this);
                    copyWindow.showAsDropDown(view, dp2px(0), dp2px(-20));
                    copyWindow.setOnCopyListener(new CopyPopupWindow.OnCopyClickListener() {
                        @Override
                        public void onCopyClick() {
                            TextView tvContent = (TextView) view.findViewById(R.id.task_content);
                            ClipboardUtils.copyToClipboard(tvContent.getText().toString());
                        }
                    });
                }
                return true;
            }
        });
    }

    private void dealTaskInfo(TaskInfo info) {
        if (info == null)
            return;

//		ImageURLConsulter.getInstance(this).consultImageUrl(
//				info.getFromUser().getId(), iconView);
        String icon = info.getIcon();
        mFromUserIcon.setImageResource(R.drawable.contactlist_contact_icon_default);
        MainApplication.getHeadImageLoader().get(icon, new ImageLoaderListener() {

            @Override
            public void onSuccess(ImageContainer container) {
                mFromUserIcon.setImageBitmap(container.getBitmap());
            }

            @Override
            public void onError(ImageContainer container) {
                mFromUserIcon.setImageResource(R.drawable.contactlist_contact_icon_default);
            }
        });

        UserSimpleInfo fromUser = info.getFromUser();
        mFromUserName.setText(fromUser.getName());

        Date taskDate = null;
        if (!TextUtils.isEmpty(info.getCreateTime())) {
            try {
                taskDate = BusinessConstants.parseTime(info.getCreateTime());
            } catch (ParseException e) {
                taskDate = null;
            }
        }

        TaskDateCompareUtils.setComparedDateForView(this, taskDate,
                mTaskCreationTime);

        // Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
        // Html.fromHtml(info.getContent()));
        // Spannable processedText =
        // URLSpanNoUnderline.removeUnderlines(spannedText);

        initReplyListView(info);

        initReplyListLoading();
    }

    private void initReplyListLoading() {
        startLoadingFirstPage();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mCurrentTaskInfo = intent.getParcelableExtra(TASK_INFO_DETAIL_KEY);
        dealTaskInfo(mCurrentTaskInfo);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.task_request_refresh:
                startLoadingFirstPage();
                break;
            case R.id.task_reply_send:
                if (mCurrentTaskInfo.getStatus() != -1) {
                    sendTaskReply();
                    mReplyListView.requestFocusFromTouch();
                    mReplyListView.setSelection(0);
                } else {
                    Toast.makeText(this, "任务已关闭，不能回复", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.new_task_reply_content:
                if (!mReplyContentEditable) {
                    mNewReplyContent.setCursorVisible(true);
                    mReplyContentEditable = true;

                    String result = mNewReplyContent.getText().toString();
                    mNewReplyContent.setSelection(result.length());
                }
                break;
            default:
                break;
        }

    }

    private void startLoadingFirstPage() {
        mReplyListView.setRefreshingState();

        startLoading(mFirstPageLoadListner, 0);
    }

    // 回复成功后，刷新回复列表
    private Listener<Response<ListSlice<ReplyInfo>>> mRefreshingListner = new Listener<Response<ListSlice<ReplyInfo>>>() {
        @Override
        public void onResponse(Response<ListSlice<ReplyInfo>> response) {
            mReplyListView.onRefreshComplete();
            if (response.getCode() == 0) {

                ListSlice<ReplyInfo> infos = response.getPayload();
                if (infos != null) {
                    mStartIndex = (int) infos.getContent().size();
                    mReplyInfoList.clear();
                    mReplyInfoList.addAll(infos.getContent());
                    mReplyListAdapter.notifyDataSetInvalidated();

                }

            } else {
                Toast.makeText(getApplicationContext(),
                        response.getDescription(),
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onErrorResponse(InvocationError arg0) {
            mReplyListView.setRefreshingState();

            Toast.makeText(getApplicationContext(),
                    R.string.task_reply_list_refresh_failure,
                    Toast.LENGTH_SHORT).show();
        }

    };
    private Cancelable cancelable;

    private void startRefreshingAfterReply() {
        mReplyListView.setRefreshingState();

        startLoading(mRefreshingListner, 0);
    }

    private void startLoading(Listener<Response<ListSlice<ReplyInfo>>> listner,
                              int startIndex) {
        AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
        requestBuilder.setModule(AsyncRequest.MODULE_TASK);
        requestBuilder.setModuleItem(mCurrentTaskInfo.getId() + "/replies");
        requestBuilder.addPageQueryParameter(startIndex, REQUEST_COUNT);
        requestBuilder
                .setResponseType(new TypeToken<Response<ListSlice<ReplyInfo>>>() {
                }.getType());
        requestBuilder.setResponseListener(listner);
        cancelable = requestBuilder.build().get();

    }

    @Override
    protected void onDestroy() {
        cancelable.cancel();
        mReplyListAdapter.release();
        super.onDestroy();
    }

    private void sendTaskReply() {
        /*记录操作 1015*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_REPLY_TASK);

        closeSoftInput();

        ReplyInfo replyInfo = new ReplyInfo();
        replyInfo.setTaskId(mCurrentTaskInfo.getId());
        replyInfo.setMark(mCheckedColor);
        replyInfo.setFromUser(AccountManager.getCurrentSimpleUser(this));
        String content = mNewReplyContent.getText().toString().trim();
        if (content.length() <= 0) {
            content = mNewReplyContent.getHint().toString();
        }
        replyInfo.setContent(content);
        if (mReplyingDialog == null) {
            mReplyingDialog = new ProgressDialog(this);
            mReplyingDialog.setMessage(mResources.getString(R.string.task_replying_progress_dialog_message));
            mReplyingDialog.setCanceledOnTouchOutside(false);
            mReplyingDialog.setCancelable(false);
        }
        mReplyingDialog.show();

        if (mReplyingTaskListner == null) {
            mReplyingTaskListner = new Listener<Response<ReplyInfo>>() {

                @Override
                public void onErrorResponse(InvocationError arg0) {
                    Toast.makeText(getApplicationContext(),
                            R.string.task_replying_result_failure,
                            Toast.LENGTH_LONG).show();
                    mReplyingDialog.dismiss();
                }

                @Override
                public void onResponse(Response<ReplyInfo> arg0) {
                    mReplyingDialog.dismiss();
                    if (arg0.getCode() == 0) {
                        Toast.makeText(getApplicationContext(),
                                R.string.task_replying_result_success,
                                Toast.LENGTH_LONG).show();

                        if (!mNewReplyAdded) {
                            mNewReplyAdded = true;
                        }
                        mNewReplyContent.getText().clear();
                        startRefreshingAfterReply();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                arg0.getDescription(),
                                Toast.LENGTH_LONG).show();
                    }

                }

            };
        }

        AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
        requestBuilder.setModule(AsyncRequest.MODULE_TASK);
        requestBuilder.setModuleItem(mCurrentTaskInfo.getId() + "/replies");
        requestBuilder.setRequestBody(replyInfo);
        requestBuilder.setResponseType(new TypeToken<Response<ReplyInfo>>() {
        }.getType());
        requestBuilder.setResponseListener(mReplyingTaskListner);
        requestBuilder.build().post();
    }

    /**
     * 关闭、完成任务
     */
    private void closeCurrentTask() {
        /*记录操作 1013*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_CLOSE_TASK);

        closeSoftInput();

        if (mClosingTaskDialog == null) {
            mClosingTaskDialog = new ProgressDialog(this);
            mClosingTaskDialog.setMessage(mResources
                    .getString(R.string.task_closing_progress_dialog_message));
            mClosingTaskDialog.setCanceledOnTouchOutside(false);
            mClosingTaskDialog.setCancelable(false);
        }
        mClosingTaskDialog.show();

        if (mClosingTaskListner == null) {
            mClosingTaskListner = new Listener<Response<Object>>() {

                @Override
                public void onErrorResponse(InvocationError arg0) {
                    Toast.makeText(getApplicationContext(),
                            R.string.task_closing_result_failure,
                            Toast.LENGTH_LONG).show();
                    mClosingTaskDialog.dismiss();
                }

                @Override
                public void onResponse(Response<Object> arg0) {
                    mClosingTaskDialog.dismiss();
                    if (arg0.getCode() == 0) {

                        Toast.makeText(getApplicationContext(),
                                R.string.task_closing_result_success,
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent();
                        intent.putExtra(TASK_LIST_DECREASED_KEY, true);
                        intent.putExtra(TASK_INVALID_KEY, mCurrentTaskInfo);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                arg0.getDescription(),
                                Toast.LENGTH_LONG).show();
                    }
                }

            };
        }

        AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
        requestBuilder.setModule(AsyncRequest.MODULE_TASK);
        requestBuilder.setModuleItem(mCurrentTaskInfo.getId() + "");
        requestBuilder.setRequestBody(new TaskCloseModel("COMPLETE"));
        requestBuilder.setResponseType(new TypeToken<Response<Object>>() {
        }.getType());
        requestBuilder.setResponseListener(mClosingTaskListner);
        requestBuilder.build().put();
    }

    /**
     * 删除、完成任务
     */
    private void deleteCurrentTask() {
         /*记录操作 1014*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_DELETE_TASK);

        closeSoftInput();

        if (mDeletingTaskDialog == null) {
            mDeletingTaskDialog = new ProgressDialog(this);
            mDeletingTaskDialog.setMessage(mResources
                    .getString(R.string.task_deleting_progress_dialog_message));
            mDeletingTaskDialog.setCanceledOnTouchOutside(false);
            mDeletingTaskDialog.setCancelable(false);
        }
        mDeletingTaskDialog.show();

        if (mDeletingTaskListner == null) {
            mDeletingTaskListner = new Listener<Response<Object>>() {

                @Override
                public void onErrorResponse(InvocationError arg0) {
                    Toast.makeText(getApplicationContext(),
                            R.string.task_deleting_result_failure,
                            Toast.LENGTH_LONG).show();
                    mDeletingTaskDialog.dismiss();
                }

                @Override
                public void onResponse(Response<Object> arg0) {
                    mDeletingTaskDialog.dismiss();
                    if (arg0.getCode() == 0) {
                        Toast.makeText(getApplicationContext(),
                                R.string.task_deleting_result_success,
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent();
                        intent.putExtra(TASK_LIST_DECREASED_KEY, true);
                        intent.putExtra(TASK_INVALID_KEY, mCurrentTaskInfo);
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                arg0.getDescription(),
                                Toast.LENGTH_LONG).show();
                    }
                }

            };
        }

        AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
        requestBuilder.setModule(AsyncRequest.MODULE_TASK);
        requestBuilder.setModuleItem(mCurrentTaskInfo.getId() + "");
        requestBuilder.setResponseType(new TypeToken<Response<Object>>() {
        }.getType());
        requestBuilder.setResponseListener(mDeletingTaskListner);
        requestBuilder.build().delete();
    }

    private void closeSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNewReplyContent.getWindowToken(), 0);
    }

    public static void openTaskDetail(Context context, TaskInfo info) {
        if (info == null)
            return;
        Intent intent = new Intent(context, TaskDetailActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(TaskDetailActivity.TASK_INFO_DETAIL_KEY, info);
        context.startActivity(intent);
    }

    public static void launchForResult(Activity context, TaskInfo info,
                                       int requestCode) {
        if (info != null) {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra(TaskDetailActivity.TASK_INFO_DETAIL_KEY, info);
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void launchForResult(Fragment fragment, TaskInfo info,
                                       int requestCode) {
        if (info != null) {
            Intent intent = new Intent(fragment.getActivity(), TaskDetailActivity.class);
            intent.putExtra(TaskDetailActivity.TASK_INFO_DETAIL_KEY, info);
            fragment.startActivityForResult(intent, requestCode);
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String hint;
        switch (mCheckedId) {
            case R.id.task_mark_progress:
                hint = mResources
                        .getString(R.string.task_reply_color_mark_normal_hint);
                break;
            case R.id.task_mark_hint:
                hint = mResources
                        .getString(R.string.task_reply_color_mark_delay_hint);
                break;
            case R.id.task_mark_alarming:
                hint = mResources
                        .getString(R.string.task_reply_color_mark_alarming_hint);
                break;
            default:
                hint = mResources
                        .getString(R.string.task_reply_color_mark_normal_hint);
                break;
        }

        String currentString = mNewReplyContent.getText().toString();

        switch (checkedId) {
            case R.id.task_mark_progress:
                mCheckedColor = COLOR_MARK_GREEN_TO_SERVER;

                if (hint == null || hint.equals(currentString)
                        || currentString == null || currentString.length() < 1) {
                    mNewReplyContent
                            .setHint(R.string.task_reply_color_mark_normal_hint);
                    mNewReplyContent.setCursorVisible(false);
                    mReplyContentEditable = false;
                }
                break;
            case R.id.task_mark_hint:
                mCheckedColor = COLOR_MARK_YELLOW_TO_SERVER;

                if (hint == null || hint.equals(currentString)
                        || currentString == null || currentString.length() < 1) {
                    mNewReplyContent
                            .setHint(R.string.task_reply_color_mark_delay_hint);
                    mNewReplyContent.setCursorVisible(false);
                    mReplyContentEditable = false;
                }
                break;
            case R.id.task_mark_alarming:
                mCheckedColor = COLOR_MARK_RED_TO_SERVER;

                if (hint == null || hint.equals(currentString)
                        || currentString == null || currentString.length() < 1) {
                    mNewReplyContent
                            .setHint(R.string.task_reply_color_mark_alarming_hint);
                    mNewReplyContent.setCursorVisible(false);
                    mReplyContentEditable = false;
                }
                break;
        }

        mCheckedId = checkedId;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
