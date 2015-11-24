package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.patrol.adapter.PatrolTagListAdapter;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.NFCTagInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.nfctools.CreateSigninTagActivity;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class AttendanceTagListActivity extends TitleFragmentActivity implements OnRefreshListener, OnClickListener, View.OnFocusChangeListener, TextWatcher {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TagListAdapter adapter;
    private PullToRefreshListView listView;
    private ProgressDialog progressDialog;

    /*搜索框中的控件*/
    private LinearLayout mLlSearch;
    private EditText mEtSearchInput;
    private Button mBtnSearch;
    private LinearLayout mLlSearchClear;


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_attendance_tag_list);
        initSearchView();


        listView = (PullToRefreshListView) findViewById(R.id.tag_listview);
        listView.setOnRefreshListener(this);
        listView.enableFooterView(false);
//		findViewById(R.id.tag_add).setOnClickListener(this);
        adapter = new TagListAdapter();

        listView.setAdapter(adapter);

        listView.setRefreshingState();
        getRightButton().setImageResource(R.drawable.contact_add_group);
        getLeftbutton().setImageResource(R.drawable.navigation_back);


    }

    private void initSearchView() {
        mLlSearchClear = (LinearLayout) findViewById(R.id.contact_search_clear_img);
        mEtSearchInput = (EditText) findViewById(R.id.et_search_input);
        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mLlSearch = (LinearLayout)findViewById(R.id.ll_search);

        mLlSearchClear.setOnClickListener(this);
        mEtSearchInput.addTextChangedListener(this);
        mEtSearchInput.setOnFocusChangeListener(this);
        mBtnSearch.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    private void requestTagList(String filter) {
        AsyncRequest.Builder builder = new AsyncRequest.Builder();
        builder.setModule(String.format(AsyncRequest.MODULE_NFCTAGS, AccountManager.getCurrent(getApplicationContext()).getCustId())).setResponseType(new TypeToken<Response<ListSlice<NFCTagInfo>>>() {
        }.getType()).addQueryParameter("filter",filter)
                .setResponseListener(new Listener<Response<ListSlice<NFCTagInfo>>>() {

                    @Override
                    public void onErrorResponse(InvocationError arg0) {
                        listView.onRefreshComplete();
                    }

                    @Override
                    public void onResponse(Response<ListSlice<NFCTagInfo>> response) {
                        listView.onRefreshComplete();
                        if (response.getCode() == 0) {
                            List<NFCTagInfo> payload = response.getPayload().getContent();
                            if (payload != null) {
                                adapter.setData(payload);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), response.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        builder.build().get();

    }

    private void deleteTag(final NFCTagInfo info) {
        AsyncRequest.Builder builder = new AsyncRequest.Builder();
        builder.setModule(String.format(AsyncRequest.MODULE_NFDTAGS_DELETE, AccountManager.getCurrent(getApplicationContext()).getCustId())).setModuleItem(String.valueOf(info.getId())).setResponseType(new TypeToken<Response<List<NFCTagInfo>>>() {
        }.getType())
                .setResponseListener(new Listener<Response<List<NFCTagInfo>>>() {

                    @Override
                    public void onErrorResponse(InvocationError arg0) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(Response<List<NFCTagInfo>> response) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        if (response.getCode() == 0) {
                            adapter.remove(info);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), response.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        builder.build().delete();
        progressDialog = ProgressDialog.show(this, null, "正在删除...");
    }


    private class TagListAdapter extends BaseAdapter {
        private List<NFCTagInfo> tagInfos = Collections.emptyList();

        public void setData(List<NFCTagInfo> data) {
            if (data != null) {
                tagInfos = data;
            }
        }

        public void remove(NFCTagInfo info) {
            if (tagInfos.contains(info)) {
                tagInfos.remove(info);
            }
        }

        @Override
        public int getCount() {
            return tagInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return tagInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AttendanceTagListActivity.this).inflate(R.layout.attendance_tag_list_item, parent, false);
            }
            TextView tagName = (TextView) convertView.findViewById(R.id.tag_name);
            TextView tagSn = (TextView) convertView.findViewById(R.id.tag_sn);
            TextView createTime = (TextView) convertView.findViewById(R.id.tag_create_time);
            TextView areaName = (TextView) convertView.findViewById(R.id.tag_area);

            final NFCTagInfo info = (NFCTagInfo) getItem(position);
            tagSn.setText("SN：" + info.getSn());
            tagName.setText(info.getTagName());
            try {
                createTime.setText(DATE_FORMAT.format(BusinessConstants.parseTime(info.getCreateTime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            areaName.setText("区域：" + info.getAreaName());
            convertView.findViewById(R.id.tag_delete).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    deleteConfirm(info);
                }
            });
            ;
            return convertView;
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void deleteConfirm(final NFCTagInfo info) {
        AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(this);
        builder.setTitle("删除考勤标签").setMessage("确认删除此考勤标签吗？").setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTag(info);
            }
        }).setNegativeButton("取消", null).show();
    }


//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.tag_add:
//			CreateSigninTagActivity.launch(this);
//			break;
//
//		default:
//			break;
//		}
//	}

    @Override
    public void onRightButtonClick() {
        CreateSigninTagActivity.launch(this);
        super.onRightButtonClick();
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, AttendanceTagListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        clearSearchInput();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                filterSearch();
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

    /** 过滤搜索 */
    private void filterSearch() {

        listView.setRefreshingState();
        requestTagList(mEtSearchInput.getText().toString());
        listView.requestFocus();

    }

    /** 清除搜索框 */
    private void clearSearchInput() {
        mEtSearchInput.setText("");
        listView.setRefreshingState();
        requestTagList(mEtSearchInput.getText().toString());
        listView.requestFocus();

    }
}
