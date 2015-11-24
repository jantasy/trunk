package cn.yjt.oa.app.personalcenter;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.PhoneUtils;

import com.baidu.android.pushservice.PushManager;

/** 系统设置界面 */
public class SettingActivity extends TitleFragmentActivity implements OnClickListener, OnCheckedChangeListener {

    private final String TAG = "SettingActivity";

    /** 新消息提醒的CheckBox */
    private CheckBox mCbNotification;
    /** 摇一摇音效是否开启的CheckBox */
    private CheckBox mCbShakingSound;
    /** 来电弹屏beta的线性布局 */
    private LinearLayout mLlInComeCall;

    /** 判断手机卡是否是电信号码 */
    private boolean mIsTelecom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_actitiy_layout);
        initParams();
        initView();
        fillData();
        setListener();
        businessLogic();
    }

	/*-----onCreate中执行的方法START-----*/

    /** 初始化一些成员参数 */
    private void initParams() {
        mIsTelecom = PhoneUtils.isTelecom(AccountManager.getCurrent(getApplicationContext()).getPhone());
    }

    /** 初始化控件 */
    private void initView() {
        mCbNotification = (CheckBox) findViewById(R.id.setting_notification);
        mCbShakingSound = (CheckBox) findViewById(R.id.setting_shakingsound);
        mLlInComeCall = (LinearLayout) findViewById(R.id.incomecall_layout);
    }

    /** 向控件中填充数据 */
    private void fillData() {
        //新消息提醒和摇一摇音效设置初始值
        mCbNotification.setChecked(StorageUtils.getSystemSettings(this).getBoolean(Config.IS_OPEN_PUSH_MESSAGE, true));
        mCbShakingSound.setChecked(StorageUtils.getSystemSettings(this).getBoolean(Config.IS_OPEN_SHAKING_SOUND, true));
        //设置标题左边按钮
        getLeftbutton().setImageResource(R.drawable.navigation_back);
    }

    /** 设置监听 */
    private void setListener() {
        //新消息提醒和摇一摇音效设置选择改变监听
        mCbNotification.setOnCheckedChangeListener(this);
        mCbShakingSound.setOnCheckedChangeListener(this);
        //“来电弹屏beta、提醒提示音、修改密码、恢复默认设置、巡更提醒 ”设置点击监听
        mLlInComeCall.setOnClickListener(this);
        findViewById(R.id.backup_default_setting).setOnClickListener(this);
        findViewById(R.id.tone_setting).setOnClickListener(this);
        findViewById(R.id.change_password).setOnClickListener(this);
        findViewById(R.id.rl_patrol_remind).setOnClickListener(this);
    }

    /** 业务逻辑 */
    private void businessLogic() {
        //如果不行电信的号码，将来电弹屏beta给取消掉
        if (!mIsTelecom) {
            mLlInComeCall.setVisibility(View.GONE);
        }
    }

	/*-----onCreate中执行的方法END-----*/

    /** 确定恢复默认设置？ */
    private void resetConfirm() {
        Builder builder = AlertDialogBuilder.newBuilder(this);
        builder.setTitle("恢复默认设置").setMessage("是否确认恢复默认设置？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         /*记录操作 0310*/
                        OperaEventUtils.recordOperation(OperaEvent.OPERA_RESTORE_DEFAULTS);

                        reset();
                    }
                }).setNegativeButton("取消", null).show();
    }

    /** 恢复默认设置 */
    private void reset() {
        //清除系统设置中所有保存在sp中的值
        StorageUtils.resetSystemSettings(this);
        mCbNotification.setChecked(true);
    }

    @Override
    public void onLeftButtonClick() {
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.setting_notification:
                //新消息提醒状态改变后，将改变后的状态写入sp中
                StorageUtils.getSystemSettings(this).edit().putBoolean(Config.IS_OPEN_PUSH_MESSAGE, isChecked).commit();
                //同时根据状态执行相应的操作
                if (isChecked) {
                /*记录操作 0302*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_OPEN_NEWINFO_REMIND);
                    PushManager.resumeWork(this);
                } else {
                /*记录操作 0303*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_CLOSE_NEWINFO_REMIND);
                    PushManager.stopWork(this);
                }
                break;

            case R.id.setting_shakingsound:
                //摇一摇音效状态发生改变后，将改变的状态写入sp中
                StorageUtils.getSystemSettings(this).edit().putBoolean(Config.IS_OPEN_SHAKING_SOUND, isChecked).commit();

                if (isChecked) {
                    /*记录操作 0308*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_OPEN_SHAKE_SOUND);
                } else {
                    /*记录操作 0309*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_CLOSE_SHAKE_SOUND);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //点击恢复默认设置
            case R.id.backup_default_setting:
                resetConfirm();

                break;

            //点击提醒提示音
            case R.id.tone_setting:
                Intent intent = new Intent(this, ToneSetting.class);
                startActivity(intent);
                break;

            //点击修改密码
            case R.id.change_password:
                Intent intentChangePwd = new Intent(this, ChangePassword.class);
                startActivity(intentChangePwd);
                break;

            //点击来电弹屏beta
            case R.id.incomecall_layout:
                IncomingSetting.launch(this);
                break;

            //点击巡更提醒
            case R.id.rl_patrol_remind:
                LogUtils.d(TAG, "点击了巡更提醒");
                Intent patrolRemindIntent = new Intent(this, PatrolRemindActivity.class);
                startActivity(patrolRemindIntent);
                break;

            default:
                LogUtils.w(TAG, "点击了奇怪的地方");
                break;
        }
    }
}
