package cn.yjt.oa.app.patrol.activitys;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.patrol.utils.PatrolRemindData;
import cn.yjt.oa.app.utils.ToastUtils;

/**
 * 巡检提醒设置的界面
 *
 * Created by 熊岳岳 on 2015/10/12.
 */
public class PatrolRemindActivity extends TitleFragmentActivity implements View.OnClickListener {

    //界面控件
    private ImageView mIvNoRemind;
    private ImageView mIvTypeOne;
    private ImageView mIvTypeTwo;
    private ImageView mIvTypeThree;
    private ImageView mIvUserDefinede;
    private CardView mCvTimePicker;
    private TimePicker mTpUserDefinded;

    /**时间选择器是否正处于隐藏的动画状态*/
    private boolean isTimePickerHiding = false;
    /**成员变量，当前的提醒类型*/
    private int mCurrentType;
    /**成员变量，当前的提醒时间*/
    private String mCurrentTime;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_patrol_reminded);
        initParams();
        initView();
        fillData();
        setListener();
    }

    private void initParams() {
        mCurrentType = PatrolRemindData.getRemindType();
        mCurrentTime = PatrolRemindData.getRemindTime();
    }

    private void initView() {
        mIvNoRemind = (ImageView) findViewById(R.id.iv_no_remind);
        mIvTypeOne = (ImageView) findViewById(R.id.iv_type_one);
        mIvTypeTwo = (ImageView) findViewById(R.id.iv_type_two);
        mIvTypeThree = (ImageView) findViewById(R.id.iv_nine);
        mIvUserDefinede = (ImageView) findViewById(R.id.iv_user_definded);
        mCvTimePicker = (CardView) findViewById(R.id.cv_time_picker);
        mTpUserDefinded = (TimePicker) findViewById(R.id.tp_user_definded);

    }

    private void fillData() {
        getLeftbutton().setImageResource(R.drawable.navigation_back);
        getRightButton().setImageResource(R.drawable.contact_list_save);
        initTimePicker();
        setViewVisible();
    }

    private void setListener() {
        findViewById(R.id.rl_no_remind).setOnClickListener(this);
        findViewById(R.id.rl_seven).setOnClickListener(this);
        findViewById(R.id.rl_eight).setOnClickListener(this);
        findViewById(R.id.iv_type_three).setOnClickListener(this);
        findViewById(R.id.rl_user_definded).setOnClickListener(this);
    }

    /** 启动该界面 */
    public static void launch(Context context) {
        Intent intent = new Intent(context, PatrolRemindActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**初始化时间选择器参数*/
    private void initTimePicker() {
        Date date = null;
        Calendar calendar = Calendar.getInstance();

        //初始化时间选择其中的时间，如果sp中保存的时间为空，默认设为10:00
        try {
            if(!TextUtils.isEmpty(mCurrentTime)){
                date = new SimpleDateFormat("HH:mm").parse(mCurrentTime);
            }else{
                date = new SimpleDateFormat("HH:mm").parse("10:00");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            calendar.setTime(date);
        }
        //设置时间选择器的相关参数
        mTpUserDefinded.setIs24HourView(true);
        mTpUserDefinded.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        mTpUserDefinded.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    /**设置控件的显示*/
    private void setViewVisible() {
        switch(mCurrentType){

            case PatrolRemindData.TYPE_USER_DEFINDED:
                selectUserDefinded();
                break;

            case PatrolRemindData.TYPE_THREE:
                selectNine();
                break;

            case PatrolRemindData.TYPE_TWO:
                selectEight();
                break;

            case PatrolRemindData.TYPE_ONE:
                selectSeven();
                break;

            case PatrolRemindData.TYPE_NO_REMIND:
                selectNoReMind();
                break;
        }
    }


    /** 设置所有的"√"和时间选择器被隐藏 */
    private void setAllSelectGone() {
        mIvNoRemind.setVisibility(View.GONE);
        mIvTypeOne.setVisibility(View.GONE);
        mIvTypeTwo.setVisibility(View.GONE);
        mIvTypeThree.setVisibility(View.GONE);
        mIvUserDefinede.setVisibility(View.GONE);
    }

    /** 选中 自定义 */
    private void selectUserDefinded() {
        if (!userDefinedeIsSelected()) {
            setAllSelectGone();
            mIvUserDefinede.setVisibility(View.VISIBLE);
            setTimePickerVisible();
        }
    }

    /** 选中 09:00 */
    private void selectNine() {
        if (!nineIsSelected()) {
            setAllSelectGone();
            setTimePickerGone();
            mIvTypeThree.setVisibility(View.VISIBLE);
        }
    }

    /** 选中 08:00 */
    private void selectEight() {
        if (!eightIsSelected()) {
            setAllSelectGone();
            setTimePickerGone();
            mIvTypeTwo.setVisibility(View.VISIBLE);
        }
    }

    /** 选中 07:00 */
    private void selectSeven() {
        if (!sevenIsSelected()) {
            setAllSelectGone();
            setTimePickerGone();
            mIvTypeOne.setVisibility(View.VISIBLE);
        }
    }

    /** 选中 不提醒 */
    private void selectNoReMind() {
        if (!noRemindIsSelected()) {
            setAllSelectGone();
            setTimePickerGone();
            mIvNoRemind.setVisibility(View.VISIBLE);
        }
    }

    /** 显示时间选择器 */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setTimePickerVisible() {
        mCvTimePicker.setVisibility(View.VISIBLE);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
            final ObjectAnimator anim = ObjectAnimator.ofFloat(mCvTimePicker," ",0.0f,1.0f).setDuration(500);
            anim.start();
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = (float) animation.getAnimatedValue();
                    mCvTimePicker.setAlpha(val);
                    mCvTimePicker.setScaleX(val);
                    mCvTimePicker.setScaleY(val);
                }
            });
        }
    }

    /** 隐藏时间选择器 */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setTimePickerGone() {

        //如果时间选择器已经是隐藏的状态，不做任何操作
        if(!isTimePickerVisible()){
            return;
        }

        //如果当前sdk兼容属性动画,给予时间选择器所在的CardView一个隐藏的渐变动画
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
            final ObjectAnimator anim = ObjectAnimator.ofFloat(mCvTimePicker," ",1.0f,0.0f).setDuration(500);
            anim.start();
            isTimePickerHiding = true;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = (float) animation.getAnimatedValue();
                    mCvTimePicker.setAlpha(val);
                    mCvTimePicker.setScaleX(val);
                    mCvTimePicker.setScaleY(val);
                }
            });
            //动画完成之后真正隐藏CardView。
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mCvTimePicker.setVisibility(View.GONE);
                    isTimePickerHiding = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else{
            //如果sdk版本不兼容属性动画，直接关闭CardView
            mCvTimePicker.setVisibility(View.GONE);
        }
    }

    /** 判断自定义是否被选中 */
    private boolean userDefinedeIsSelected() {
        return mIvUserDefinede.getVisibility() == View.VISIBLE;
    }

    /** 判断09:00是否被选中 */
    private boolean nineIsSelected() {
        return mIvTypeThree.getVisibility() == View.VISIBLE;
    }

    /** 判断08:00是否被选中 */
    private boolean eightIsSelected() {
        return mIvTypeTwo.getVisibility() == View.VISIBLE;
    }

    /** 判断07:00是否被选中 */
    private boolean sevenIsSelected() {
        return mIvTypeOne.getVisibility() == View.VISIBLE;
    }

    /** 判断不提醒是否被选中 */
    private boolean noRemindIsSelected() {
        return mIvNoRemind.getVisibility() == View.VISIBLE;
    }

    /**时间选择器是否处于显示状态*/
    private boolean isTimePickerVisible() {
        return mCvTimePicker.getVisibility() == View.VISIBLE;
    }

    /** 获取 时间选择器中的时间*/
    private String getTimePickerString() {
        //保存在sp中的提醒时间的格式为 "HH:mm"
        Calendar calendar = Calendar.getInstance();
        calendar.set(0,0,0,mTpUserDefinded.getCurrentHour(),mTpUserDefinded.getCurrentMinute(),0);
        return DateUtils.sTimeFormat.format(calendar.getTime());
    }

    /**记录当前界面信息(提醒类型和提醒时间)*/
    private void updateCurrentInfo() {
        if(noRemindIsSelected()){
            mCurrentType = PatrolRemindData.TYPE_NO_REMIND;
            mCurrentTime = "";
        }else if(sevenIsSelected()){
            mCurrentType = PatrolRemindData.TYPE_ONE;
            mCurrentTime = "07:00";
        }else if(eightIsSelected()){
            mCurrentType = PatrolRemindData.TYPE_TWO;
            mCurrentTime = "08:00";
        }else if(nineIsSelected()){
            mCurrentType = PatrolRemindData.TYPE_THREE;
            mCurrentTime = "09:00";
        }else if(userDefinedeIsSelected()){
            mCurrentType = PatrolRemindData.TYPE_USER_DEFINDED;
            mCurrentTime = getTimePickerString();
        }

    }

    /**界面信息（提醒时间和提醒类型）是否修改*/
    private boolean isModify() {
        //如果提醒类型未改变，且类型不是自定义，说明未修改
        if(mCurrentType==PatrolRemindData.getRemindType()
                && !(mCurrentType==PatrolRemindData.TYPE_USER_DEFINDED)){
            return false;
        }

        //如果提醒类型为未修改，仍未自定义，自定义时间也未修改，说明未修改
        if(mCurrentType==PatrolRemindData.getRemindType()
                &&mCurrentType==PatrolRemindData.TYPE_USER_DEFINDED
                &&mCurrentTime.equals(PatrolRemindData.getRemindTime())){
            return false;
        }

        //其余情况均为修改
        return true;
    }

    /**将巡检提醒信息保存到本地*/
    private void saveToLocal() {
        PatrolRemindData.setRemindType(mCurrentType);
        PatrolRemindData.setRemindTime(mCurrentTime);
        ToastUtils.shortToastShow("保存成功");
    }


    @Override
    public void onLeftButtonClick() {
        //点击退出界面按钮时，先判断界面的巡检提醒信息是否有过修改
        //若有修改，提醒用户保存；若无修改，直接关闭界面
        updateCurrentInfo();
        if(isModify()){
            AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(this);
            builder.setTitle("是否保存修改").setMessage("您的巡检提醒信息有修改，是否保存").setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveToLocal();
                    PatrolRemindActivity.super.onBackPressed();
                }
            }).setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PatrolRemindActivity.super.onBackPressed();
                }
            }).show();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public void onRightButtonClick() {
        super.onRightButtonClick();
        //点击保存按钮直接保存巡检提醒信息，然后关闭当前界面
        updateCurrentInfo();
        saveToLocal();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(isTimePickerHiding){
            return ;
        }
        switch (v.getId()) {

            case R.id.rl_no_remind:
                selectNoReMind();
                break;

            case R.id.rl_seven:
                selectSeven();
                break;

            case R.id.rl_eight:
                selectEight();
                break;

            case R.id.iv_type_three:
                selectNine();
                break;

            case R.id.rl_user_definded:
                selectUserDefinded();
                break;

            default:
                break;
        }
    }
}
