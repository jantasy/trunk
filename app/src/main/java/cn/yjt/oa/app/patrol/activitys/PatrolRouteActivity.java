package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.PatrolRoute;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.OperaEventUtils;

/**
 * 巡检路线界面
 *
 * @author 熊岳岳
 * @since 20150908
 */
public class PatrolRouteActivity extends TitleFragmentActivity implements OnClickListener {

    private final static String PATROLROUTE_STRING = "patrol_route";

    private EditText mEtRouteName;
    private EditText mEtRouteDecription;
    private RelativeLayout mRlManagerPoint;
    private RelativeLayout mRlManagerPerson;
    private TextView mEtPatrolStartTime;
    private TextView mEtPatrolStopTime;

    private PatrolRoute mRouteInfo;

    /*-----生命周期方法START-----*/
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_patrol_route);

        initParams();
        initView();
        fillData();
        setListener();
    }

    /*-----生命周期方法END-----*/

    /*-----onCreate中执行的方法START-----*/
    private void initParams() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mRouteInfo = (PatrolRoute) bundle.getSerializable(PATROLROUTE_STRING);
        }
    }

    private void initView() {
        mEtRouteName = (EditText) findViewById(R.id.et_route_name);
        mEtRouteDecription = (EditText) findViewById(R.id.et_route_decription);
        mRlManagerPoint = (RelativeLayout) findViewById(R.id.rl_manager_point);
        mRlManagerPerson = (RelativeLayout) findViewById(R.id.rl_manager_person);
        mEtPatrolStartTime = (TextView) findViewById(R.id.et_patrol_starttime);
        mEtPatrolStopTime = (TextView) findViewById(R.id.et_patrol_stoptime);
    }

    private void fillData() {
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        getRightButton().setImageResource(R.drawable.contact_list_save);
        if (mRouteInfo != null) {
            mEtRouteName.setText(mRouteInfo.getName());
            mEtRouteDecription.setText(mRouteInfo.getDescription());
        }
    }

    private void setListener() {
        mRlManagerPoint.setOnClickListener(this);
        mRlManagerPerson.setOnClickListener(this);
        mEtPatrolStartTime.setOnClickListener(this);
        mEtPatrolStopTime.setOnClickListener(this);
    }
    /*-----onCreate中执行的方法END-----*/

    /** 启动当前界面 */
    public static void launth(Context context) {
        launthWithPatrolRoute(context, null);
    }

    /** 启动该界面并传递一个Patrol对象 */
    public static void launthWithPatrolRoute(Context context, PatrolRoute info) {

        Intent intent = new Intent(context, PatrolRouteActivity.class);
        if (info != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PATROLROUTE_STRING, info);
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**绑定巡检点*/
    private void toBindPoint() {
        if(mRouteInfo ==null || mRouteInfo.getId()==0){
            if(TextUtils.isEmpty(mEtRouteName.getText().toString())){
                Toast.makeText(this,"请先填写名称并提交",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this,"请先提交",Toast.LENGTH_SHORT).show();
            return;
        }
        PatrolRouteBindPointActivity.launthWithPatrolRoute(this,mRouteInfo);
    }

    /**绑定巡检人员*/
    private void toBindPerson() {
        if(mRouteInfo ==null || mRouteInfo.getId()==0){
            if(TextUtils.isEmpty(mEtRouteName.getText().toString())){
                Toast.makeText(this,"请先填写名称并提交",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this,"请先提交",Toast.LENGTH_SHORT).show();
            return;
        }
        PatrolRouteBindPersonActivity.launthWithPatrolRoute(this,mRouteInfo);
    }

    /*-----重写父类或者接口中的方法START-----*/
    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onRightButtonClick() {
        //检测巡检路线名称是否填写
        if(TextUtils.isEmpty(mEtRouteName.getText())){
            Toast.makeText(this,"请填写巡检路线名称",Toast.LENGTH_SHORT).show();
            return ;
        }
        //根据mRouteInfo是否存在判断是更新路线还是新建路线
        String message = "正在更新巡检路线";
        if(mRouteInfo==null){
           mRouteInfo = new PatrolRoute();
            message = "正在创建巡检路线";

             /*记录操作 1716*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_CREATE_PATROLROUTE);
        }else{
             /*记录操作 1717*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_MODIFY_PATROLROUTE);
        }
        mRouteInfo.setName(mEtRouteName.getText().toString());
        mRouteInfo.setDescription(mEtRouteDecription.getText().toString());

        //提交巡检路线
        PatrolApiHelper.addOrUpdatePatrolRoute(new ProgressDialogResponseListener<PatrolRoute>(this,message) {
            @Override
            public void onSuccess(PatrolRoute payload) {
                //提交成功后，刷新页面的控件，并土司提示
                mRouteInfo = payload;
                mEtRouteName.setText(mRouteInfo.getName());
                mEtRouteDecription.setText(mRouteInfo.getDescription());
                Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
            }
        }, AccountManager.getCurrent(this).getCustId(),mRouteInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //点击管理巡检人员
            case R.id.rl_manager_person:
                toBindPerson();
                break;

                //点击管理巡检点
            case R.id.rl_manager_point:
                toBindPoint();
                break;

            //点到了奇怪的地方-.-!
            default:
                break;
        }
    }

    /*-----重写父类或者接口中的方法STOP-----*/
}
