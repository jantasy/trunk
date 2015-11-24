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
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.PatrolRoute;
import cn.yjt.oa.app.beans.RouteUser;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.adapter.PatrolPersonBindAdapter;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.OperaEventUtils;


/**
 * 巡检路线绑定人员activity
 *
 * @author 熊岳岳
 * @since 20150914
 */

public class PatrolRouteBindPersonActivity extends TitleFragmentActivity implements OnClickListener, TextWatcher,
        OnCheckedChangeListener {

    private final String TAG = "PatrolRouteBindPointActivity";

    private final static String PATROLROUTE_STRING = "patrolroute_string";
    /** 和路线绑定的巡检员的集合（界面显示用） */
    public List<RouteUser> mSelectedList;
    /** 和路线未绑定的巡检员的集合（界面显示用） */
    public List<RouteUser> mUnSelectedList;
    /** 和路线绑定的巡检员的集合（请求数据用） */
    private List<RouteUser> mRequestList;

    /** 和路线绑定的巡检点listview的适配器 */
    public PatrolPersonBindAdapter mSelectedAdapter;
    /** 和路线未绑定的巡检点listveiw的适配器 */
    public PatrolPersonBindAdapter mUnSelectedAdapter;

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

        initParams();
        initView();
        fillData();
        setListener();
    }

    private void initParams() {
        setContentView(R.layout.activity_attendance_member_bind);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mRouteInfo = (PatrolRoute) bundle.getSerializable(PATROLROUTE_STRING);
        }

        mSelectedList = new ArrayList<>();
        mUnSelectedList = new ArrayList<>();
        mRequestList = new ArrayList<>();
        mSelectedAdapter = getSelectedAdapter();
        mUnSelectedAdapter = getUnSelectedAdapter();
        mSelectedAdapter.setDataListsAndRefresh(mSelectedList);
        mUnSelectedAdapter.setDataListsAndRefresh(mUnSelectedList);
        setUnSelectedList();

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
        mEtSearchInput.setHint("搜索巡检员");
        selectedListView.setAdapter(mSelectedAdapter);
        unselectedListView.setAdapter(mUnSelectedAdapter);
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

    /** 关联巡检点 */
    private void bind() {
        bindData();
        allSelectedCheckBox.setChecked(false);
        allUnselectedCheckBox.setChecked(false);
    }


    /** 巡检点解除关联 */
    private void unbind() {
        UnbindData();
        allSelectedCheckBox.setChecked(false);
        allUnselectedCheckBox.setChecked(false);
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
        List<RouteUser> selectedMatched = findMatchRouteUserWithName(mSelectedList, input);
        List<RouteUser> unselectedMatched = findMatchRouteUserWithName(mUnSelectedList, input);
        mSelectedAdapter.setDataListsAndRefresh(selectedMatched);
        mUnSelectedAdapter.setDataListsAndRefresh(unselectedMatched);
    }

    /** 显示默认的item */
    private void showDefaultList() {
        mSelectedAdapter.setDataListsAndRefresh(mSelectedList);
        mUnSelectedAdapter.setDataListsAndRefresh(mUnSelectedList);
    }

    /** 根据搜索字符串，过滤集合中item */
    private List<RouteUser> findMatchRouteUserWithName(List<RouteUser> RouteUsers, String name) {

        List<RouteUser> matchedRouteUsers = new ArrayList<RouteUser>();
        if (TextUtils.isEmpty(name)) {
            return matchedRouteUsers;
        }
        for (RouteUser RouteUser : RouteUsers) {
            if (RouteUser.getName().contains(name) || isPinYinContains(RouteUser.getName(), name)) {
                matchedRouteUsers.add(RouteUser);
            }
        }
        return matchedRouteUsers;
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

    public static void launthWithPatrolRoute(Context context, PatrolRoute info) {
        Intent intent = new Intent(context, PatrolRouteBindPersonActivity.class);
        if (info != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PATROLROUTE_STRING, info);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /*-----重写父类或者接口中的方法START-----*/
    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onRightButtonClick() {
         /*记录操作 1719*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_BIND_PATROLPERSON);

        PatrolApiHelper.bindPatrolPerson(new ProgressDialogResponseListener<Object>(this,"正在提交关联巡检员") {
            @Override
            public void onSuccess(Object payload) {
                Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        },AccountManager.getCurrent(getApplicationContext()).getCustId(),String.valueOf(mRouteInfo.getId()),mRequestList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add:
                bind();
                break;

            case R.id.remove:
                unbind();
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
                setAllForSelect(isChecked);
                break;
            case R.id.all_unselect:
                setAllForUnSelect(isChecked);
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

    /** 获取已关联的数据list */
    public void setSelectedList() {
        PatrolApiHelper.getPatrolPersonBindRoute(new ProgressDialogResponseListener<ListSlice<RouteUser>>(this,"正在获取关联联系人") {
            @Override
            public void onSuccess(ListSlice<RouteUser> payload) {

//                List<ContactInfo> localContacts = ContactManager.getContactManager(getApplicationContext())
//                        .loadAllLocalContacts(true);
                mSelectedList = payload.getContent();
//                setAvatarForRouteUsers(localContacts,mSelectedList);
//                mUnSelectedList = getUnSelectedList(localContacts);
                mUnSelectedList.removeAll(mSelectedList);
                mSelectedAdapter.setDataListsAndRefresh(mSelectedList);
                mUnSelectedAdapter.setDataListsAndRefresh(mUnSelectedList);
                setReqeustLists();
            }
        }, AccountManager.getCurrent(getApplicationContext()).getCustId(),String.valueOf(mRouteInfo.getId()));
    }

    private void setUnSelectedList() {
        PatrolApiHelper.getPatrolPerson(new ProgressDialogResponseListener<ListSlice<RouteUser>>(this, "正在获取关联联系人") {
            @Override
            public void onSuccess(ListSlice<RouteUser> payload) {
                if (payload.getContent().size() == 0) {
                    Toast.makeText(getApplicationContext(), "您尚未设置巡检员，请到后台设置巡检员", Toast.LENGTH_SHORT).show();
                } else {
                    mUnSelectedList = payload.getContent();
                    setSelectedList();
                }
            }
        }, AccountManager.getCurrent(getApplicationContext()).getCustId());
    }

    /** 获取未关联的数据list */
    public List<RouteUser> getUnSelectedList(List<ContactInfo> localContacts) {
        List<RouteUser> unselectedRouteUser = convertToRouteUser(localContacts);
        unselectedRouteUser.removeAll(mSelectedList);
        return unselectedRouteUser;
    }

    /** 设置已关联的巡检人员的集合，用于提交服务器 */
    private void setReqeustLists() {
        mRequestList = new ArrayList<>();
        mRequestList.addAll(mSelectedList);
    }

    /** 获取未关联的listview的适配器 */
    protected PatrolPersonBindAdapter getUnSelectedAdapter() {
        return new PatrolPersonBindAdapter(this);
    }

    /** 获取已关联listview的适配器 */
    protected PatrolPersonBindAdapter getSelectedAdapter() {
        return new PatrolPersonBindAdapter(this);
    }

    /** 点击关联时设置的绑定操作 */
    protected void bindData() {

        List<RouteUser> tempInfo = new ArrayList<RouteUser>();
        //首先遍历适配器中的集合，如果有条目属于选中状态就将其添加到临时集合中
        if (mUnSelectedAdapter.mDatas != null) {
            for (RouteUser info : mUnSelectedAdapter.mDatas) {
                if (info.isSelected()) {
                    info.setSelected(false);
                    tempInfo.add(info);
                }
            }
        }
        //选中或者未选中的两边集合执行相应的操作
        mSelectedList.addAll(tempInfo);
        mUnSelectedList.removeAll(tempInfo);
        mRequestList.addAll(tempInfo);
        mUnSelectedAdapter.setDataListsAndRefresh(mUnSelectedList);
        mSelectedAdapter.setDataListsAndRefresh(mSelectedList);

    }

    /** 点击取消关联时设置的解绑操作 */
    protected void UnbindData() {
        List<RouteUser> tempInfo = new ArrayList<RouteUser>();
        //首先遍历适配器中的集合，如果有条目属于选中状态就将其添加到临时集合中
        if (mSelectedAdapter.mDatas != null) {
            for (RouteUser info : mSelectedAdapter.mDatas) {
                if (info.isSelected()) {
                    info.setSelected(false);
                    tempInfo.add(info);
                }
            }
        }
        //选中或者未选中的两边集合执行相应的操作
        mUnSelectedList.addAll(tempInfo);
        mSelectedList.removeAll(tempInfo);
        mRequestList.removeAll(tempInfo);
        mUnSelectedAdapter.setDataListsAndRefresh(mUnSelectedList);
        mSelectedAdapter.setDataListsAndRefresh(mSelectedList);
    }

    /** 点击未关联的全选 */
    protected void setAllForUnSelect(boolean isCheck) {
        mUnSelectedAdapter.setAllCheck(isCheck);
    }

    /** 点击关联的全选 */
    protected void setAllForSelect(boolean isCheck) {
        mSelectedAdapter.setAllCheck(isCheck);
    }

    private List<RouteUser> convertToRouteUser(List<ContactInfo> localContacts) {
        List<RouteUser> routeUsers = new ArrayList<RouteUser>();
        for (ContactInfo contactInfo : localContacts) {
            routeUsers.add(RouteUser.fromContactInfo(contactInfo));
        }
        return routeUsers;
    }

    private void setAvatarForRouteUsers(List<ContactInfo> localContacts, List<RouteUser> routeUsers) {
        for (RouteUser routeUser : routeUsers) {
            setAvatarForRouteUser(localContacts, routeUser);
        }
    }

    private void setAvatarForRouteUser(List<ContactInfo> localContacts, RouteUser routeUser) {
        for (ContactInfo contactInfo : localContacts) {
            if (contactInfo.getUserId() == routeUser.getUserId()) {
                routeUser.setAvatar(contactInfo.getAvatar());
                return;
            }
        }
    }
}
