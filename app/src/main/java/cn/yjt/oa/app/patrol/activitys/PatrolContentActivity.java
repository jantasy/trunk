package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.PatrolTag;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.adapter.PatrolBaseAdapter;
import cn.yjt.oa.app.patrol.adapter.PatrolPointAdapter;
import cn.yjt.oa.app.patrol.adapter.PatrolRouteAdapter;
import cn.yjt.oa.app.patrol.adapter.PatrolTagListAdapter;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

/**
 * 巡检点管理界面
 * @author 熊岳岳
 * @since 20150731
 */
public class PatrolContentActivity extends TitleFragmentActivity implements View.OnClickListener, View.OnFocusChangeListener,TextWatcher {

	private final String TAG = "PatrolPointActivity";

	/**获取intent传递过来的值的key（intent.putExtra(TYPE_STRING, type);）*/
	private final static String TYPE_STRING = "jump_type";

	public final static int TYPE_POINT = 0;
	public final static int TYPE_ROUTE = 1;
	public final static int TYPE_TAGLIST = 2;

	/**跳转到其他页面的跳转类型*/
	private int mJumpType = 0;

	/** 自定义的下拉刷新和上拉加载的listview，用来展示巡检点信息 */
	private PullToRefreshListView mPrlvShowList;
	/** 适配巡检点listview的适配器 */
	private PatrolBaseAdapter mAdapter;


    /*搜索框中的控件*/
    private LinearLayout mLlSearch;
    private EditText mEtSearchInput;
    private Button mBtnSearch;
    private LinearLayout mLlSearchClear;

	/*-----生命周期方法START-----*/
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.layout_tag_list_show);
		initParams();
		initView();
		fillData();
		setListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPrlvShowList.setRefreshingState();
		mAdapter.onRefresh();
	}

	/*-----生命周期方法END-----*/

	/*-----onCreate中执行的方法START-----*/
	/** 向控件中填充数据 */
	private void initParams() {
		mJumpType = getIntent().getIntExtra(TYPE_STRING, -1);
		initParamByType();

	}

	/** 初始化控件 */
	private void initView() {
		mPrlvShowList = (PullToRefreshListView) findViewById(R.id.prlv_show_list);
        //搜索框的控件初始化
        if(mJumpType == TYPE_TAGLIST){
            mLlSearchClear = (LinearLayout) findViewById(R.id.contact_search_clear_img);
            mEtSearchInput = (EditText) findViewById(R.id.et_search_input);
            mBtnSearch = (Button) findViewById(R.id.btn_search);
            mLlSearch = (LinearLayout)findViewById(R.id.ll_search);
        }
	}

	/** 往控件中填充数据 */
	private void fillData() {
		mPrlvShowList.setAdapter(mAdapter);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_add_group);
        if(mJumpType == TYPE_TAGLIST){
            mLlSearch.setVisibility(View.VISIBLE);
        }
	}

	/** 设置监听 */
	private void setListener() {
		mAdapter.setListViewAndListener(mPrlvShowList);
        if(mJumpType == TYPE_TAGLIST) {
            mLlSearchClear.setOnClickListener(this);
            mEtSearchInput.addTextChangedListener(this);
            mEtSearchInput.setOnFocusChangeListener(this);
            mBtnSearch.setOnClickListener(this);
        }
	}

	/** 初始化适配器 */
	private void initParamByType() {
		switch (mJumpType) {

		case TYPE_POINT:
			mAdapter = new PatrolPointAdapter(this);
			setTitle("巡检点管理");
			break;

		case TYPE_TAGLIST:
			mAdapter = new PatrolTagListAdapter(this);
			setTitle("巡检标签管理");
			break;

		case TYPE_ROUTE:
			mAdapter = new PatrolRouteAdapter(this);
			setTitle("巡检路线管理");
			break;

		default:
			break;
		}
	}
	/*-----onCreate中执行的方法END-----*/

	/** 开启PatrolManageActivity界面 */
	public static void lanuthWithType(Context context, int type) {
		if (type != TYPE_POINT && type != TYPE_TAGLIST && type != TYPE_ROUTE) {
			Toast.makeText(MainApplication.getAppContext(), "跳转类型出错", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(context, PatrolContentActivity.class);
		intent.putExtra(TYPE_STRING, type);
		context.startActivity(intent);
	}

    /** 清除搜索框 */
    private void clearSearchInput() {
        mEtSearchInput.setText("");
        if(mAdapter instanceof PatrolTagListAdapter){
            mPrlvShowList.setRefreshingState();
            ((PatrolTagListAdapter)mAdapter).onRefreshWithFilter(mEtSearchInput.getText().toString());
            mPrlvShowList.requestFocus();
        }
    }

    /**过滤搜索*/
    private void filterSearch() {
        if(mAdapter instanceof PatrolTagListAdapter){
            mPrlvShowList.setRefreshingState();
            ((PatrolTagListAdapter)mAdapter).onRefreshWithFilter(mEtSearchInput.getText().toString());
            mPrlvShowList.requestFocus();
        }
    }

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		super.onRightButtonClick();
		
		switch (mJumpType) {
		case TYPE_POINT:
			PatrolPointActivity.launth(this);
			break;

		case TYPE_ROUTE:
			PatrolRouteActivity.launth(this);
			break;

		case TYPE_TAGLIST:
			PatrolCreateNFCTagActivity.lanuth(this);
			break;

		default:
			break;
		}
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
}
