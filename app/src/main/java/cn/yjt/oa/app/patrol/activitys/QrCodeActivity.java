package cn.yjt.oa.app.patrol.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.BarCodeAttendanceInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.patrol.service.QrCodeService;
import cn.yjt.oa.app.patrol.service.QrCodeService.AttendanceBinder;
import cn.yjt.oa.app.patrol.service.QrCodeService.AttendanceListener;
import cn.yjt.oa.app.utils.CheckNetUtils;

/** 考勤的Activity */
@SuppressLint("SimpleDateFormat")
public class QrCodeActivity extends Activity implements OnClickListener {

    //四个状态参数
    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOCATION_FAILURE = 1;
    public static final int STATE_REQUEST_RESPONSE = 2;
    public static final int STATE_ERROR = 3;

    private ImageView signinImage;
    private TextView signinText;
    private TextView signinTime;
    private View progressBar;
    private View confirmButton;

    private ServiceConnection serviceConnection;
    private AttendanceBinder attendanceBinder;
    private BarCodeAttendanceInfo barCodeAttendanceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
        //			LaunchActivity.launch(this);
        //			finish();
        //		} else {
        prepareView();
        //		}
    }

    private void prepareView() {
        initView();

        //如果网络不可用的话，直接给出提示，然后结束该方法
        if (!CheckNetUtils.hasNetWork(this)) {
            signinText.setText(R.string.connect_network_fail);
            signinImage.setImageResource(R.drawable.signin_error_image);
            return;
        }

        //获取开启该activity时传递过来的状态参数，默认是STATE_NORMAL
        int state = getIntent().getIntExtra("state", STATE_NORMAL);

        //如果是普通状态
        if (state == STATE_NORMAL) {
            getInspectInfo();
            bindAttendanceService();
        } else {
        }
        switchState(state, getIntent().getStringExtra("description"));
    }


    private void switchState(int state, String description) {
        switch (state) {
            case STATE_ERROR:
                onSiginFailure("请求失败");
                break;
            case STATE_LOCATION_FAILURE:
                locationFail();
                break;
            case STATE_REQUEST_RESPONSE:
                onSiginFailure(description);
                break;

            default:
                break;
        }
    }

    private AttendanceListener locationSigninListener = new AttendanceListener() {

        @Override
        public void onRequesting() {
            signinText.setText("正在上传位置...");
        }

        @Override
        public void onLocationSuccess() {

        }

        @Override
        public void onLocationFailure() {
            locationFail();
        }

        @Override
        public void onLocating() {
            signinText.setText("正在定位...");
            progressBar.setVisibility(View.VISIBLE);
            signinImage.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError() {
            onSiginFailure("请求失败");
        }

        @Override
        public void onResponse(Response<BarCodeAttendanceInfo> response) {
            if (response.getCode() == 0) {
                BarCodeAttendanceInfo payload = response.getPayload();
                progressBar.setVisibility(View.GONE);
                signinImage.setVisibility(View.VISIBLE);
                setupColor(payload);
                if (payload.getResult() == 1) {
                    onSigninSuccess(payload);
                } else {
                    onSiginFailure(payload.getResultDesc() == null ? "您不在可签到范围内"
                            : payload.getResultDesc());
                }

            } else {
                onSiginFailure(response.getDescription());
            }
        }

    };

    private void setupColor(BarCodeAttendanceInfo payload) {
        if (payload.getResultColor() != 0) {
            signinText.setTextColor(payload.getResultColor());
        }
        if (payload.getDescColor() != 0) {
            signinTime.setTextColor(payload.getDescColor());
        }
    }

    private void onSigninSuccess(BarCodeAttendanceInfo payload) {
        signinText.setText("签到成功");
        signinTime.setText(payload.getResultDesc() == null ? String.format(
                "您在%s成功签到",
                new SimpleDateFormat("HH:mm:ss").format(payload.getDate()))
                : payload.getResultDesc());
        signinImage.setImageResource(R.drawable.signin_success_image);
    }

    private AttendanceListener otherSigninListener = new AttendanceListener() {

        @Override
        public void onRequesting() {
        }

        @Override
        public void onLocationSuccess() {
        }

        @Override
        public void onLocationFailure() {
            locationFail();
        }

        @Override
        public void onLocating() {
            progressBar.setVisibility(View.GONE);
            signinImage.setVisibility(View.VISIBLE);
            signinText.setText("签到信息已识别");
            signinTime.setText("签到计算中...");
            signinImage.setImageResource(R.drawable.signin_collected_image);
        }

        @Override
        public void onError() {
            onSiginFailure("请求失败");
        }

        @Override
        public void onResponse(Response<BarCodeAttendanceInfo> response) {
            if (response.getCode() == 0) {
                BarCodeAttendanceInfo payload = response.getPayload();
                if (payload.getResult() == 1) {
                    //					finish();
                    setupColor(payload);
                    onSigninSuccess(payload);
                } else {
                    onSiginFailure(payload.getResultDesc() == null ? "您不在可签到范围内"
                            : payload.getResultDesc());
                }
            } else {
                onSiginFailure(response.getDescription());
            }
        }
    };

    /** 绑定考勤服务 */
    private void bindAttendanceService() {
        Intent service = new Intent(this, QrCodeService.class);
        //将考勤信息传递过去
        service.putExtra("barCodeAttendanceInfo", barCodeAttendanceInfo);
        //回调的接口
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                attendanceBinder = (AttendanceBinder) service;
                attendanceBinder.onBind(otherSigninListener);
            }
        };
        //绑定服务
        bindService(service, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (attendanceBinder != null) {
            attendanceBinder.unBind(otherSigninListener);
        }
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }

    /**
     * 获取考勤信息
     *
     * @return
     */
    private BarCodeAttendanceInfo getInspectInfo() {
        BarCodeAttendanceInfo barCodeAttendanceInfo = getIntent().getParcelableExtra("barCodeAttendanceInfo");
        if (barCodeAttendanceInfo == null) {
            barCodeAttendanceInfo = new BarCodeAttendanceInfo();
        }
        this.barCodeAttendanceInfo = barCodeAttendanceInfo;
        return barCodeAttendanceInfo;
    }

    /**
     * 初始化控件，并为控件设置监听
     */
    private void initView() {
        setContentView(R.layout.activity_attendance);
        //初始化控件
        signinImage = (ImageView) findViewById(R.id.tv_check_in);
        signinText = (TextView) findViewById(R.id.tv_check_in_status);
        signinTime = (TextView) findViewById(R.id.tv_check_in_time);
        progressBar = findViewById(R.id.pb_updating);
        //为控件设置监听
        confirmButton = findViewById(R.id.btn_comfirm);
        confirmButton.setOnClickListener(this);
    }

    private void onLocationFailure() {
        runOnUiThread(new Runnable() {
            public void run() {
                progressBar.setVisibility(View.GONE);
                signinImage.setVisibility(View.VISIBLE);
                signinImage.setImageResource(R.drawable.signin_error_image);
                signinText.setText("定位失败");
                signinTime.setText("");
                confirmButton.setClickable(true);
            }
        });
    }

    private void locationFail() {
        onLocationFailure();
        return;
    }

    private void onSiginFailure(String msg) {
        signinText.setText("签到无效");
        signinTime.setText(msg);
        progressBar.setVisibility(View.GONE);
        signinImage.setVisibility(View.VISIBLE);
        signinImage.setImageResource(R.drawable.signin_error_image);
        confirmButton.setClickable(true);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, QrCodeActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void launchWithBarCodeAttendanceInfo(Context context,
                                                       BarCodeAttendanceInfo barCodeAttendanceInfo) {
        Intent intent = new Intent(context, QrCodeActivity.class);
        intent.putExtra("barCodeAttendanceInfo", barCodeAttendanceInfo);
        context.startActivity(intent);
    }

    public static void launchWithState(Context context, int state,
                                       String description) {
        Intent intent = new Intent(context, QrCodeActivity.class);
        intent.putExtra("state", state);
        intent.putExtra("description", description);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

}
