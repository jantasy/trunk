package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.PatrolPoint;
import cn.yjt.oa.app.beans.PatrolRoute;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.adapter.PatrolPointSelectedAdapter;
import cn.yjt.oa.app.patrol.adapter.PatrolPointUnSelectedAdapter;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.OperaEventUtils;

/**
 * 巡检路线绑定巡检点的界面
 *
 * @author 熊岳岳
 * @since 20150914
 */

public class PatrolRouteBindPointActivity extends TitleFragmentActivity implements OnClickListener, TextWatcher,
        OnCheckedChangeListener {

    private final String TAG = "PatrolRouteBindPointActivity";

    private final static String PATROLROUTE_STRING = "patrolroute_string";
    /** 和路线绑定的巡检点的集合（界面显示用） */
    private List<PatrolPoint> selectedUsers;
    /** 和路线未绑定的巡检点的集合（界面显示用） */
    private List<PatrolPoint> unselectedUsers;
    /** 和路线绑定的巡检点的集合（提交服务器用） */
    private List<PatrolPoint> mRequestSelectedUsers;

    /** 和路线绑定的巡检点listview的适配器 */
    private PatrolPointSelectedAdapter mSeletedAdapter;
    /** 和路线未绑定的巡检点listveiw的适配器 */
    private PatrolPointUnSelectedAdapter mUnSeletedAdapter;

    private PatrolRoute mRouteInfo;

    //页面控件
    private EditText mEtSearchInput;
    private TextView mTvSearchCancel;
    private LinearLayout mLlSearchClear;
    private ListView selectedListView;
    private ListView unselectedListView;
    private CheckBox allSelectedCheckBox;
    private CheckBox allUnselectedCheckBox;
    private TextView mTvAdd;
    private TextView mTvRemove;


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_attendance_member_bind);
        initParams();
        initView();
        fillData();
        setListener();
        requestData();
    }


    private void initParams() {

        unselectedUsers = new ArrayList<PatrolPoint>();
        selectedUsers = new ArrayList<PatrolPoint>();
        //获取已关联的的巡检点集合，复制一份，用去请求网络用
        mRequestSelectedUsers = new ArrayList<PatrolPoint>();
        //初始化适配器
        mUnSeletedAdapter = new PatrolPointUnSelectedAdapter(this, unselectedUsers);
        mSeletedAdapter = new PatrolPointSelectedAdapter(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mRouteInfo = (PatrolRoute) bundle.getSerializable(PATROLROUTE_STRING);
        }
    }

    private void initView() {
        mEtSearchInput = (EditText) findViewById(R.id.address_search_input);
        mTvSearchCancel = (TextView) findViewById(R.id.search_cancel);
        mLlSearchClear = (LinearLayout) findViewById(R.id.contact_search_clear_img);
        selectedListView = (ListView) findViewById(R.id.selected_list);
        unselectedListView = (ListView) findViewById(R.id.unselected_list);
        allSelectedCheckBox = (CheckBox) findViewById(R.id.all_select);
        allUnselectedCheckBox = (CheckBox) findViewById(R.id.all_unselect);
        mTvAdd = (TextView) findViewById(R.id.add);
        mTvRemove = (TextView) findViewById(R.id.remove);
    }

    private void fillData() {
        mEtSearchInput.setHint("搜索巡检点");
        selectedListView.setAdapter(mSeletedAdapter);
        unselectedListView.setAdapter(mUnSeletedAdapter);
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        getRightButton().setImageResource(R.drawable.contact_list_save);
    }

    private void setListener() {
        mEtSearchInput.setOnClickListener(this);
        mEtSearchInput.addTextChangedListener(this);
        mTvSearchCancel.setOnClickListener(this);
        mLlSearchClear.setOnClickListener(this);
        allSelectedCheckBox.setOnCheckedChangeListener(this);
        allUnselectedCheckBox.setOnCheckedChangeListener(this);
        mTvAdd.setOnClickListener(this);
        mTvRemove.setOnClickListener(this);
    }


    private void requestData() {
        PatrolApiHelper.getPatrolPoint(new ProgressDialogResponseListener<List<PatrolPoint>>(this, "正在获取巡检点信息") {
            @Override
            public void onSuccess(List<PatrolPoint> payload) {
                unselectedUsers = payload;
                requestSelected();
            }


        }, AccountManager.getCurrent(this).getCustId());
    }

    public static void launthWithPatrolRoute(Context context, PatrolRoute info) {
        Intent intent = new Intent(context, PatrolRouteBindPointActivity.class);
        if (info != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PATROLROUTE_STRING, info);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    private void requestSelected() {
        if (mRouteInfo.getId() != 0) {
            PatrolApiHelper.getPatrolPointBindRoute(new ProgressDialogResponseListener<PatrolRoute>(this, "正在获取和该路径关联的巡检点信息") {
                @Override
                public void onSuccess(PatrolRoute payload) {
                    mRequestSelectedUsers.clear();
                    selectedUsers = payload.getPoints();
                    unselectedUsers.removeAll(selectedUsers);
                    mRequestSelectedUsers.addAll(selectedUsers);
                    mSeletedAdapter.setDataListsAndRefresh(selectedUsers);
                    mUnSeletedAdapter.setDataListsAndRefresh(unselectedUsers);

                }
            }, AccountManager.getCurrent(this).getCustId(), String.valueOf(mRouteInfo.getId()));
        }
    }

    /** 关联巡检点 */
    private void bindPoint() {
        List<PatrolPoint> tempInfo = new ArrayList<PatrolPoint>();
        //首先遍历适配器中的集合，如果有条目属于选中状态就将其添加到临时集合中
        if (mUnSeletedAdapter.mDatas != null) {
            for (PatrolPoint info : mUnSeletedAdapter.mDatas) {
                if (info.isSelected()) {
                    info.setSelected(false);
//                    info.setBind(true);
                    tempInfo.add(info);
                }
            }
        }
        //全选或全不选设为false
        allSelectedCheckBox.setChecked(false);
        allUnselectedCheckBox.setChecked(false);
        //选中或者未选中的两边集合执行相应的操作
        selectedUsers.addAll(tempInfo);
        unselectedUsers.removeAll(tempInfo);
        mRequestSelectedUsers.addAll(tempInfo);
        mUnSeletedAdapter.setDataListsAndRefresh(unselectedUsers);
        mSeletedAdapter.setDataListsAndRefresh(selectedUsers);
    }

    /** 巡检点解除关联 */
    private void unbindPoint() {
        List<PatrolPoint> tempInfo = new ArrayList<PatrolPoint>();
        for (PatrolPoint info : mSeletedAdapter.mDatas) {
            if (info.isSelected()) {
                info.setSelected(false);
//                info.setBind(false);
                tempInfo.add(info);
            }
        }
        allSelectedCheckBox.setChecked(false);
        allUnselectedCheckBox.setChecked(false);
        unselectedUsers.addAll(tempInfo);
        selectedUsers.removeAll(tempInfo);
        mRequestSelectedUsers.removeAll(tempInfo);
        mUnSeletedAdapter.setDataListsAndRefresh(unselectedUsers);
        mSeletedAdapter.setDataListsAndRefresh(selectedUsers);
    }

    /** 搜索 */
    private void search() {

        mEtSearchInput.requestFocus();
        mEtSearchInput.setCursorVisible(true);
        mTvSearchCancel.setVisibility(View.VISIBLE);
        matchSearchInput("");
    }


    /** 取消搜索显示默认数据 */
    private void cancelSearch() {
        mEtSearchInput.setText("");
        mEtSearchInput.clearFocus();
        mEtSearchInput.setCursorVisible(false);
        hideSoftInput();
        mTvSearchCancel.setVisibility(View.GONE);
        showDefaultList();
    }

    /** 清除搜索框 */
    private void clearSearchInput() {
        mEtSearchInput.setText("");
    }


    private void matchSearchInput(String input) {
        List<PatrolPoint> selectedMatched = findMatchAreaUserWithName(selectedUsers, input);
        List<PatrolPoint> unselectedMatched = findMatchAreaUserWithName(unselectedUsers, input);
        mSeletedAdapter.setDataListsAndRefresh(selectedMatched);
        mUnSeletedAdapter.setDataListsAndRefresh(unselectedMatched);
    }

    /** 显示默认的item */
    private void showDefaultList() {
        mSeletedAdapter.setDataListsAndRefresh(selectedUsers);
        mUnSeletedAdapter.setDataListsAndRefresh(unselectedUsers);
    }

    /** 根据搜索字符串，过滤集合中item */
    private List<PatrolPoint> findMatchAreaUserWithName(List<PatrolPoint> areaUsers, String name) {

        List<PatrolPoint> matchedAreaUsers = new ArrayList<PatrolPoint>();
        if (TextUtils.isEmpty(name)) {
            return matchedAreaUsers;
        }
        for (PatrolPoint areaUser : areaUsers) {
            if (areaUser.getName().contains(name) || isPinYinContains(areaUser.getName(), name)) {
                matchedAreaUsers.add(areaUser);
            }
        }
        return matchedAreaUsers;
    }

    /** 判断拼音是否匹配 */
    private boolean isPinYinContains(String sourceName, String matchText) {
        String pinYin = ContactlistUtils.getPinYin(matchText);
        String namePinYin = ContactlistUtils.getPinYin(sourceName);
        if (namePinYin.contains(pinYin)) {
            return true;
        }
        String target = "";
        for (int i = 0; i < sourceName.length(); i++) {
            String sub = sourceName.substring(i, i + 1);
            target += ContactlistUtils.getFirstLetter(sub);
        }
        if (target.contains(pinYin)) {
            return true;
        }
        return false;
    }

    /** 隐藏键盘 */
    protected void hideSoftInput() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive()) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                        0);
            }
        }
    }

    private List<PatrolPoint> orderPatrolPoint() {
        List<PatrolPoint> list = mSeletedAdapter.mDatas;
        if(!list.isEmpty()){
            for(int i=0;i<list.size();i++){
                list.get(i).setOrderId(i+1);
            }
        }
        return list;
    }
    /*-----重写父类或者接口中的方法START-----*/
    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onRightButtonClick() {

        if (mRouteInfo == null) {
            return;
        }

         /*记录操作 1718*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_BIND_PATROLPOINT);


        mRouteInfo.setPoints(mRequestSelectedUsers);

        mRequestSelectedUsers = orderPatrolPoint();

        PatrolApiHelper.bindPatrolPoint(new ProgressDialogResponseListener<Object>(this, "正在提交") {
            @Override
            public void onSuccess(Object payload) {
                Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, AccountManager.getCurrent(this).getCustId(),String.valueOf(mRouteInfo.getId()), mRequestSelectedUsers);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add:
                bindPoint();
                break;

            case R.id.remove:
                unbindPoint();
                break;

            case R.id.contact_search_clear_img:
                clearSearchInput();
                break;

            case R.id.search_cancel:
                cancelSearch();
                break;

            case R.id.address_search_input:
                search();
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
        selectedListView.onTextChanged(s, start, before, count);
        unselectedListView.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mLlSearchClear.setVisibility(View.VISIBLE);
        } else {
            mLlSearchClear.setVisibility(View.GONE);
        }
        matchSearchInput(s.toString());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.all_select:
                mSeletedAdapter.setAllCheck(isChecked);
                break;
            case R.id.all_unselect:
                mUnSeletedAdapter.setAllCheck(isChecked);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    /*-----重写父类或者接口中的方法END-----*/
}
