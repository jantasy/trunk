package cn.yjt.oa.app.meeting;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.MeetingSignInInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.FileRequest;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.http.RequestQueueHolder;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.meeting.adapter.MeetingSigninAdapter;
import cn.yjt.oa.app.builder.EmailDialogBuilder;
import cn.yjt.oa.app.builder.EmailDialogBuilder.OnButtonClickListener;
import cn.yjt.oa.app.meeting.http.MeetingApiHelper;
import cn.yjt.oa.app.utils.FileUtils;
import cn.yjt.oa.app.widget.listview.OnLoadMoreListner;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.NoCache;

/**
 * 考勤人员的统计界面
 *
 * @author 熊岳岳
 * @since 20150827
 */
public class MeetingStaffActivity extends TitleFragmentActivity implements OnRefreshListener,
        OnLoadMoreListner, OnClickListener, OnFocusChangeListener, Listener<File>, ErrorListener, OnCancelListener, TextWatcher {

    /** 展示数据的listview控件 */
    private PullToRefreshListView mPrlvShowdata;
    /** 加载数据时的等待的progressbar控件 */
    private ProgressBar mPbLoading;
    /** 下载excel表格的按钮 */
    private Button mBtnDownloadExcel;
    /** 发送excel表格到邮箱的按钮 */
    private Button mBtnEmailExcel;

    //搜索框中的控件
    private EditText mEtSearchInput;
    private Button mBtnSearch;
    private LinearLayout mLlSearchClear;

    /** 会议人员详情的适配器 */
    private MeetingSigninAdapter mAdapter;
    /** 适配listview的数据源 */
    private List<MeetingSignInInfo> mDataList;

    /** 分页查询起始页 */
    private int mStartIndex = 0;
    /** 分页查询最终页 */
    private final int MAX_COUNT = 15;
    /** 查询过滤字段 */
    private String mFilterString = " ";
    /** 当前会议的会议id */
    private long mMeetingId;

    //下载报表相关参数
    private FileRequest mFileRequest;
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    private ProgressDialog emailProgressDialog;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_meeting_singin);
        initParams();
        initView();
        fillDate();
        setListener();
    }

    @Override
    protected void onResume() {
        mPrlvShowdata.setRefreshingState();
        this.onRefresh();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.stop();
        mRequestQueue = null;
        super.onDestroy();
    }

    /*-----oncreate中执行的方法START-----*/
    private void initParams() {
        mMeetingId = getIntent().getLongExtra("meetingId", -1);
        mDataList = new ArrayList<MeetingSignInInfo>();
        mAdapter = new MeetingSigninAdapter(this, mDataList);
        mRequestQueue = new RequestQueue(new NoCache(), new BasicNetwork(RequestQueueHolder.getInstance()
                .getHttpStack()));
        mRequestQueue.start();
    }

    private void initView() {
        mBtnDownloadExcel = (Button) findViewById(R.id.btn_download_excel);
        mBtnEmailExcel = (Button) findViewById(R.id.btn_email_excel);
        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mPrlvShowdata = (PullToRefreshListView) findViewById(R.id.prlv_showdata);
        //搜索框的控件初始化
        mLlSearchClear = (LinearLayout) findViewById(R.id.contact_search_clear_img);
        mEtSearchInput = (EditText) findViewById(R.id.et_search_input);
        mBtnSearch = (Button) findViewById(R.id.btn_search);
    }

    private void fillDate() {
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        mPbLoading.setVisibility(View.GONE);
        mPrlvShowdata.setAdapter(mAdapter);
        mEtSearchInput.setHint("搜索出席人员");
    }

    private void setListener() {
        mAdapter.setListViewAndListener(mPrlvShowdata);
        mPrlvShowdata.setOnRefreshListener(this);
        mPrlvShowdata.setOnLoadMoreListner(this);
        mLlSearchClear.setOnClickListener(this);
        mBtnDownloadExcel.setOnClickListener(this);
        mBtnEmailExcel.setOnClickListener(this);
        mEtSearchInput.setOnFocusChangeListener(this);
        mEtSearchInput.addTextChangedListener(this);
        mBtnSearch.setOnClickListener(this);
    }

	/*-----oncreate中执行的方法END-----*/

    /** 启动该Activity */
    public static void launch(Context context, long meetingId) {
        Intent intent = new Intent(context, MeetingStaffActivity.class);
        intent.putExtra("meetingId", meetingId);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        mFilterString = mEtSearchInput.getText().toString();
        mStartIndex = 0;
        MeetingApiHelper.getMeetingSigninInfoList(new ResponseListener<ListSlice<MeetingSignInInfo>>() {
            @Override
            public void onSuccess(ListSlice<MeetingSignInInfo> payload) {
                mDataList = payload.getContent();
                mAdapter.setDataListsAndRefresh(mDataList);
                mStartIndex += MAX_COUNT;
                mPrlvShowdata.onRefreshComplete();
            }
        }, mFilterString, mStartIndex, MAX_COUNT, mMeetingId);
    }

    @Override
    public void onLoadMore() {
        MeetingApiHelper.getMeetingSigninInfoList(new ResponseListener<ListSlice<MeetingSignInInfo>>() {
            @Override
            public void onSuccess(ListSlice<MeetingSignInInfo> payload) {
                mDataList.addAll(payload.getContent());
                mAdapter.setDataListsAndRefresh(mDataList);
                mStartIndex += MAX_COUNT;
                mPrlvShowdata.onLoadMoreComplete();
            }
        }, mFilterString, mStartIndex, MAX_COUNT, mMeetingId);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_download_excel:
                downloadExcel();


                /*记录操作 1608*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_DOWNLOAD_MEETING_REPORT);
                break;

            case R.id.btn_email_excel:
                emailExcel();


                /*记录操作 1609*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_EMAIL_MEETING_REPORT);

                break;

            case R.id.btn_search:
                filterMeetingSigninInfoList();
                break;

            case R.id.contact_search_clear_img:
                clearSearchInput();
                break;
            default:
                break;
        }
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
    public void onCancel(DialogInterface dialog) {
        if (mProgressDialog == dialog) {
            mFileRequest.cancel();
        }
    }

    @Override
    public void onErrorResponse(VolleyError arg0) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mFileRequest = null;
        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(File file) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mFileRequest = null;
        Toast.makeText(getApplicationContext(), "报表已下载到：" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    /** 根据过滤文字，请求数据 */
    private void filterMeetingSigninInfoList() {
        mFilterString = mEtSearchInput.getText().toString();
        mStartIndex = 0;
        MeetingApiHelper.getMeetingSigninInfoList(new ProgressDialogResponseListener<ListSlice<MeetingSignInInfo>>(
                this,
                "正在搜索") {
            @Override
            public void onSuccess(ListSlice<MeetingSignInInfo> payload) {
                mDataList = payload.getContent();
                mAdapter.setDataListsAndRefresh(mDataList);
                mStartIndex += MAX_COUNT;
                mPrlvShowdata.requestFocus();
            }
        }, mFilterString, mStartIndex, MAX_COUNT, mMeetingId);
    }

    /** 发送excel表格到邮箱 */
    private void emailExcel() {
        EmailDialogBuilder builder = EmailDialogBuilder.getInstance(this);
        builder.setOnButtonClickListener(new OnButtonClickListener() {

            @Override
            public void positiveButtonClick(String text) {
                sendEmail(text);
            }

            @Override
            public void negativeButtonClick() {

            }
        });
        builder.buildInputEmailDialog();
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

    /** 发送邮件 */
    private void sendEmail(final String email) {
        if (!checkEmail(email)) {
            Toast.makeText(getApplicationContext(), "请填写正确的邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        MeetingApiHelper.emailSigninExcel(sendEmailListener, email, mMeetingId);
        emailProgressDialog = ProgressDialog.show(this, null, "正在请求...");
        emailProgressDialog.setCancelable(false);
    }

    /** 校验邮箱 */
    private boolean checkEmail(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.matches("^.+@.+\\..+$");
        }
        return false;
    }

    /** 下载excel表格 */
    private void downloadExcel() {
        try {
            String url = new AsyncRequest.Builder()
                    .setModule(String.format(AsyncRequest.MODULE_MEETING_EXPORT, String.valueOf(mMeetingId))).build()
                    .getRequest().getUrl();
            File file = new File(FileUtils.getUserFolder() + "/会议签到列表/");
            if (!file.exists()) {
                file.mkdirs();
            }
            mFileRequest = new FileRequest(url, MeetingStaffActivity.this, file, MeetingStaffActivity.this);
            mFileRequest.setRetryPolicy(new RetryPolicy() {

                @Override
                public void retry(VolleyError arg0) throws VolleyError {

                }

                @Override
                public int getCurrentTimeout() {
                    return 30000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 0;
                }
            });
            mRequestQueue.add(mFileRequest);
            mProgressDialog = ProgressDialog.show(this, null, "正在下载报表...");
            mProgressDialog.setOnCancelListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /** 清除搜索框 */
    private void clearSearchInput() {
        mEtSearchInput.setText("");
        mPrlvShowdata.setRefreshingState();
        onRefresh();
    }

    /** 发送邮件的监听 */
    io.luobo.common.http.Listener<Response<String>> sendEmailListener = new io.luobo.common.http.Listener<Response<String>>() {

        @Override
        public void onErrorResponse(InvocationError error) {
            if (emailProgressDialog != null) {
                emailProgressDialog.dismiss();
            }
            Toast.makeText(getApplicationContext(), "请求失败，请重试", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(Response<String> response) {
            if (emailProgressDialog != null) {
                emailProgressDialog.dismiss();
            }
            if (response.getCode() == 0) {
                Toast.makeText(getApplicationContext(), "报表已发送到您的邮箱", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), response.getDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    };
}
