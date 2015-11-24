package cn.yjt.oa.app.patrol.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;

/**
 * 巡检管理页面
 *
 * @author 熊岳岳
 * @since 20150731
 */
public class PatrolManageActivity extends TitleFragmentActivity implements OnClickListener {

    private final String TAG = "PatrolManageActivity";

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_patrol_manage);

        fillData();
        setListenter();
    }

	/*-----onCreate中执行的方法START-----*/

    /** 向控件中填充数据 */
    private void fillData() {
        getLeftbutton().setImageResource(R.drawable.navigation_back);
    }

    /** 设置监听 */
    private void setListenter() {
        findViewById(R.id.rl_patrol_point).setOnClickListener(this);
        findViewById(R.id.rl_patrol_route).setOnClickListener(this);
        findViewById(R.id.rl_patrol_nfc_tag).setOnClickListener(this);

        findViewById(R.id.rl_patrol_time).setOnClickListener(this);
    }

	/*-----onCreate中执行的方法END-----*/

    /** 开启PatrolManageActivity界面 */
    public static void launch(Context context) {
        Intent intent = new Intent(context, PatrolManageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //点击了管理巡检点
            case R.id.rl_patrol_point:
                PatrolContentActivity.lanuthWithType(this, PatrolContentActivity.TYPE_POINT);
                LogUtils.d(TAG, "点击了管理巡检点");

                  /*记录操作 1708*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_PATORLPOINT_LISTS);

                break;

            //点击了管理巡检路线
            case R.id.rl_patrol_route:
                PatrolContentActivity.lanuthWithType(this, PatrolContentActivity.TYPE_ROUTE);
                LogUtils.d(TAG, "点击了管理巡检路线");

                  /*记录操作 1709*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_WTACH_PATROLROUTE_LISTS);

                break;

            //点击了管理本企业NFC巡检标签
            case R.id.rl_patrol_nfc_tag:
                PatrolContentActivity.lanuthWithType(this, PatrolContentActivity.TYPE_TAGLIST);
                LogUtils.d(TAG, "点击了管理本企业NFC巡检标签");

                  /*记录操作 1710*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_PATROLTAG_LISTS);

                break;

            //点击巡检时间设置
            case R.id.rl_patrol_time:
                PatrolTimeActivity.launch(this);
                LogUtils.d(TAG, "点击了巡检时间");

                  /*记录操作 1711*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_PATROLTIME);

                break;

        }
    }


}
