package cn.yjt.oa.app.personalcenter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class ToneSetting extends TitleFragmentActivity implements OnClickListener, OnCheckedChangeListener {

    private RadioGroup toneGroup;

    private RadioButton muteBtn, soundVibrationBtn, soundBtn, vibrationBtn;

    private Button btnSure, btnCancle;

    private int sceneMode;


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        sceneMode = StorageUtils.getSystemSettings(this).getInt(Config.ALERT_TONE, Config.RINGER_MODE_NORMAL);
        setContentView(R.layout.tone_setting);
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        setTitle("提醒提示音");
        initview();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    private void initview() {
        toneGroup = (RadioGroup) findViewById(R.id.tone_group);
        muteBtn = (RadioButton) findViewById(R.id.mute);
        soundVibrationBtn = (RadioButton) findViewById(R.id.sound_vibration);
        soundBtn = (RadioButton) findViewById(R.id.sound);
        vibrationBtn = (RadioButton) findViewById(R.id.vibration);
        if (sceneMode == Config.RINGER_MODE_NORMAL) {
            soundBtn.setChecked(true);
        } else if (sceneMode == Config.RINGER_MODE_VIBRATE) {
            vibrationBtn.setChecked(true);
        } else if (sceneMode == Config.RINGER_NORMAL_VIBRATE) {
            soundVibrationBtn.setChecked(true);
        } else {
            muteBtn.setChecked(true);
        }

        btnSure = (Button) findViewById(R.id.tone_setting_sure);
        btnCancle = (Button) findViewById(R.id.tone_setting_cancle);

        btnSure.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        toneGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onLeftButtonClick() {
        super.onLeftButtonClick();
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tone_setting_sure:
                StorageUtils.getSystemSettings(this).edit().putInt(Config.ALERT_TONE, sceneMode).commit();
                this.finish();
                break;
            case R.id.tone_setting_cancle:
                this.finish();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
        switch (arg1) {
            case R.id.sound_vibration:
                sceneMode = Config.RINGER_NORMAL_VIBRATE;

            /*记录操作 0305*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_CHANGE_SOUNDANDSHOCK);
                break;
            case R.id.sound:
                sceneMode = Config.RINGER_MODE_NORMAL;

			/*记录操作 0306*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_CHANGE_SOUND);
                break;
            case R.id.vibration:
                sceneMode = Config.RINGER_MODE_VIBRATE;

			/*记录操作 0307*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_CHANGE_SHOCK);
                break;
            case R.id.mute:
                sceneMode = Config.RINGER_MODE_SILENT;

			/*记录操作 0304*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_CHANGE_SOUND_OFF);
                break;

            default:
                break;
        }

    }

}
