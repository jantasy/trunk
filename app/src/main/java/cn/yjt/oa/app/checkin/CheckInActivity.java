package cn.yjt.oa.app.checkin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.checkin.binder.CheckInBinder;
import cn.yjt.oa.app.checkin.interfaces.ICheckInType;
import cn.yjt.oa.app.checkin.interfaces.OnCheckInListener;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.utils.CheckNetUtils;

/**
 * 签到的基类抽象类
 * <pre>
 * 用来处理签到的基类，封装了签到时候的一些界面显示逻辑，
 * 根据具体情况，创建不同的子类来完成考勤、巡检、会议之类的类似的签到功能
 * <pre/>
 * Created by 熊岳岳 on 2015/9/30.
 */
public abstract class CheckInActivity extends Activity implements View.OnClickListener {


    /** 绑定服务事，存入intent中“签到信息”时用到的键值 */
    public static final String CHECKIN_INFO = "check_in_info";

    /*
      状态参数
      这里的状态指的是界面的状态，包括
        “识别签到信息状态（normal），定位失败（location failure），
        获取请求的响应值（request response），错误状态（error）”
       这四种状态，每种状态对应不同的界面展示
     */
    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOCATION_FAILURE = 1;
    public static final int STATE_REQUEST_RESPONSE = 2;
    public static final int STATE_ERROR = 3;
    /** 当前界面的状态标识符 */
    private int mState = STATE_NORMAL;

    /** 界面展示的关键字，例:"考勤标签已识别"，“巡检标签已识别” 这里的考勤和巡检指的就是关键字 */
    private String mKeyWord;

    /*当前界面的控件*/
    private ImageView mIvCheckIn;
    private TextView mTvCheckInStatus;
    private TextView mTvCheckInTime;
    private ProgressBar mPbUpdating;
    private Button mBtnComfirm;


    /** 当前页面的签到信息 */
    private ICheckInType mInfo;
    /** 当前状态描述文字 */
    private String mDecription;

    private ServiceConnection mServiceConnection;
    private CheckInBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_attendance);
        initParams();
        initView();
        setListener();
        serviceLogic();
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unBind();
        }
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }

    /*------------------oncreate中的四个方法START--------------------*/
    private void initParams() {
        mKeyWord = getKeyWord();
        mState = getState();
        mInfo = getCheckInfo();
        mDecription = getDescription();
    }

    private void initView() {
        mTvCheckInStatus = (TextView) findViewById(R.id.tv_check_in_status);
        mTvCheckInTime = (TextView) findViewById(R.id.tv_check_in_time);
        mPbUpdating = (ProgressBar) findViewById(R.id.pb_updating);
        mIvCheckIn = (ImageView) findViewById(R.id.tv_check_in);
        mBtnComfirm = (Button) findViewById(R.id.btn_comfirm);

    }

    private void setListener() {
        mBtnComfirm.setOnClickListener(this);
    }

    private void serviceLogic() {
        //如果网络不可用的话，直接给出提示，然后结束该方法
        if (!CheckNetUtils.hasNetWork(this)) {
            mTvCheckInStatus.setText(R.string.connect_network_fail);
            mTvCheckInTime.setText("");
            mIvCheckIn.setImageResource(R.drawable.signin_error_image);
            return;
        }
        serviceByState();
    }
 /*------------------oncreate中的四个方法END--------------------*/

    /**获取界面展示关键字*/
    public String getKeyWord() {
        return "签到";
    }

    /** 获取当前页面的状态 */
    public int getState() {
        return STATE_NORMAL;
    }

    /** 根据状态进行不同的业务逻辑操作 */
    private void serviceByState() {
        switch (mState) {

            case STATE_NORMAL:
                setNormalState();
                break;

            case STATE_ERROR:
                checkFailure("请求失败");
                break;

            case STATE_LOCATION_FAILURE:
                locationFailure();
                break;

            case STATE_REQUEST_RESPONSE:
                checkFailure(mDecription);
                break;

            default:
                break;
        }
    }

    /**设置普通状态
     * <pre>
     * 处于识别状态下(普通状态下)，设置界面控件的显示内容，
     * 同时绑定服务，向后台发起请求，提交签到数据
     * <pre/>
     */
    private void setNormalState() {
        /*定位签到不显示“标签已识别”*/
        if (mInfo.getType() == mInfo.SINGIN_TYPE_VISIT) {
            mTvCheckInTime.setText("");
        }
        bindService();
    }

    /**签到失败*/
    private void checkFailure(String msg) {
        mTvCheckInStatus.setText("签到无效");
        mTvCheckInTime.setText(msg);
        mPbUpdating.setVisibility(View.GONE);
        mIvCheckIn.setVisibility(View.VISIBLE);
        mIvCheckIn.setImageResource(R.drawable.signin_error_image);
        mBtnComfirm.setClickable(true);
    }

    /**定位失败*/
    private void locationFailure() {
        mTvCheckInStatus.setText("定位失败");
        mTvCheckInTime.setText("");
        mPbUpdating.setVisibility(View.GONE);
        mIvCheckIn.setVisibility(View.VISIBLE);
        mIvCheckIn.setImageResource(R.drawable.signin_error_image);
        mBtnComfirm.setClickable(true);
    }


    /**设置界面某些TextView的字体颜色*/
    private void setStatusAndTimeColor(ICheckInType payload) {
        if (payload.getResultColor() != 0) {
            mTvCheckInStatus.setTextColor(payload.getResultColor());
        }
        if (payload.getDescColor() != 0) {
            mTvCheckInTime.setTextColor(payload.getDescColor());
        }
    }

    /** 签到成功 */
    private void onCheckInSuccess(ICheckInType payload) {
        mTvCheckInStatus.setText(mKeyWord + "成功");
        mTvCheckInTime.setText(payload.getResultDesc() == null
                ? String.format("您在%s成功" + mKeyWord, DateUtils.sTimeSecondFormat.format(payload.getDate()))
                : payload.getResultDesc());
        mIvCheckIn.setImageResource(R.drawable.signin_success_image);
    }

    /** 绑定服务 */
    private void bindService() {
        Intent intent = getServiceIntent();
        //将签到信息传递过去
        intent.putExtra(CHECKIN_INFO, mInfo);
        //回调的接口
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = (CheckInBinder) service;
                mBinder.onBind(mInfo.getType().equals(mInfo.SINGIN_TYPE_VISIT)
                        ? mlocationListener
                        : mTagListener);
            }
        };
        //绑定服务
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }



    /**定位签到的回调接口*/
    private OnCheckInListener mlocationListener = new OnCheckInListener() {

        @Override
        public void onRequesting() {
            mTvCheckInStatus.setText("正在上传位置...");
        }

        @Override
        public void onLocationFailure() {
            locationFailure();
        }

        @Override
        public void onLocating() {
            mTvCheckInStatus.setText("正在定位...");
            mPbUpdating.setVisibility(View.VISIBLE);
            mIvCheckIn.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError() {
            checkFailure("请求失败");
        }

        @Override
        public void onResponse(Response<ICheckInType> response) {
            if (response.getCode() == 0) {
                /*定位签到成功后界面的变化操作*/
                ICheckInType info = response.getPayload();
                mPbUpdating.setVisibility(View.GONE);
                mIvCheckIn.setVisibility(View.VISIBLE);
                setStatusAndTimeColor(info);
                if (info.getResultCode() == 1) {
                    onCheckInSuccess(info);
                } else {
                    checkFailure(info.getResultDesc() == null
                            ? "您不在可" + mKeyWord + "范围内"
                            : info.getResultDesc());
                }
            } else {
                checkFailure(response.getDescription());
            }
        }

    };

    private OnCheckInListener mTagListener = new OnCheckInListener() {

        @Override
        public void onRequesting() {

        }

        @Override
        public void onLocationFailure() {
            locationFailure();
        }

        @Override
        public void onLocating() {
            mPbUpdating.setVisibility(View.GONE);
            mIvCheckIn.setVisibility(View.VISIBLE);
            mTvCheckInStatus.setText(mKeyWord + "信息已识别");
            mTvCheckInTime.setText(mKeyWord + "计算中...");
            mIvCheckIn.setImageResource(R.drawable.signin_collected_image);
        }

        @Override
        public void onError() {
            checkFailure("请求失败");
        }

        @Override
        public void onResponse(Response<ICheckInType> response) {
            if (response.getCode() == 0) {
                /*其他方式签到成功界面的展示*/
                ICheckInType info = response.getPayload();
                if (info.getResultCode() == 1) {
                    setStatusAndTimeColor(info);
                    onCheckInSuccess(info);
                } else {
                    checkFailure(info.getResultDesc() == null
                            ? "您不在可" + mKeyWord + "范围内"
                            : info.getResultDesc());
                }
            } else {
                checkFailure(response.getDescription());
            }
        }
    };

    @Override
    public void onClick(View v) {
        finish();
    }

    /*-----------------------------抽象方法-------------------------*/

    /** 获取开启服务的intent */
    protected abstract Intent getServiceIntent();

    /** 获取当前页面的描述信息 */
    protected abstract String getDescription();

    /** 获取当前页面的签到信息 */
    protected abstract ICheckInType getCheckInfo();


}
