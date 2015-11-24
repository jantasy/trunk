package cn.yjt.oa.app.builder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import cn.yjt.oa.app.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * 日期时间对话框构造器
 * <p>
 * 可以创建时间选择器和日期选择器对话框，以及二者的整合
 * </p>
 * Created by 熊岳岳 on 2015/8/20.
 */
public class DateTimePickDialogBuilder implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    private static DateTimePickDialogBuilder mBuilder;

    private final int TYPE_SIGNLE = 0;
    private final int TYPE_DOUBLE = 1;
    private int mBuilderType = TYPE_SIGNLE;

    /** 上下文对象 */
    private Activity mActivity;
    /** 消息对话框 */
    private AlertDialog mDialog;
    /** 日期选择器控件 */
    private DatePicker mDatePicker;
    /** 时间选择器控件 */
    private TimePicker mTimePicker;

    /** 时间选择器是否是24小时制的，默认为true */
    private boolean is24HourView = true;

    /** 弹出对话框时默认的显示的日期 */
    private Date initDateTime;
    /** 选择器选中的日期 */
    private Date mDateTime;

    public void setInitDateTime(Date initDateTime) {
        this.initDateTime = initDateTime;
    }

    /** 对话框按钮点击监听 */
    private OnButtonClickListener mListener;


    /** 对话框按钮点击监听 */
    public interface OnButtonClickListener {

        /** 确定按钮点击事件 */
        public void positiveButtonClick(Date dataTime);

        /** 取消按钮点击事件 */
        public void negativeButtonClick();
    }

    private DateTimePickDialogBuilder(Activity activity, Date initDateTime) {
        this.mActivity = activity;
        this.initDateTime = initDateTime;
    }

    /** 获取一个DateTimePickDialogBuilder对象 */
    public static DateTimePickDialogBuilder getIntances(Activity activity, Date initDateTime) {
        if (mBuilder == null) {
            synchronized (DateTimePickDialogBuilder.class) {
                mBuilder = new DateTimePickDialogBuilder(activity, initDateTime);
            }
        }else{
            mBuilder.setActivity(activity);
            mBuilder.setInitDateTime(initDateTime);
        }
        return mBuilder;
    }

    /** 弹出一个日期对话框 */
    public AlertDialog buildDatePickerDialog() {
        this.mBuilderType = TYPE_SIGNLE;
        View timeView = View.inflate(mActivity, R.layout.datepicker_item, null);
        mDatePicker = (DatePicker) timeView.findViewById(R.id.datepicker);
        initDatePicker();
        //创建对话框
        mDialog = new AlertDialog.Builder(mActivity)
                .setView(timeView)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.positiveButtonClick(mDateTime);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.negativeButtonClick();
                        }
                    }
                })
                .show();
        onDateChanged(null, 0, 0, 0);
        return mDialog;
    }

    /** 弹出一个时间对话框 */
    public AlertDialog buildTimePickerDialog() {
        this.mBuilderType = TYPE_SIGNLE;
        View timeView = View.inflate(mActivity, R.layout.timepicker_item, null);
        mTimePicker = (TimePicker) timeView.findViewById(R.id.timepicker);
        initTimePicker();
        //创建对话框
        mDialog = new AlertDialog.Builder(mActivity)
                .setView(timeView)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.positiveButtonClick(mDateTime);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.negativeButtonClick();
                        }
                    }
                })
                .show();
        onTimeChanged(null, 0, 0);

        return mDialog;
    }

    /** 弹出一个日期和时间合并的对话框 */
    public AlertDialog buildDateTimePickerDialog() {
        this.mBuilderType = TYPE_DOUBLE;
        View timeView = View.inflate(mActivity, R.layout.datetimepicker_item, null);
        mDatePicker = (DatePicker) timeView.findViewById(R.id.datepicker);
        mTimePicker = (TimePicker) timeView.findViewById(R.id.timepicker);
        initPicker();
        //创建对话框
        mDialog = new AlertDialog.Builder(mActivity)
                .setView(timeView)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.positiveButtonClick(mDateTime);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.negativeButtonClick();
                        }
                    }
                }).show();
        onDateChanged(null, 0, 0, 0);
        return mDialog;
    }

    /** 给选择器填充默认数据并设置监听 */
    private void initPicker() {
        Calendar calendar = Calendar.getInstance();
        if (initDateTime != null) {
            calendar.setTime(initDateTime);
        }
        //初始化日期选择器并设置监听
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
        //设置时间选择器的相关参数
        mTimePicker.setIs24HourView(is24HourView);
        mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        mTimePicker.setOnTimeChangedListener(this);
    }

    /** 初始化DatePicker */
    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (initDateTime != null) {
            calendar.setTime(initDateTime);
        }
        //初始化日期选择器并设置监听
        mDatePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
    }

    /** 初始化TimePicker */
    private void initTimePicker() {
        Calendar calendar = Calendar.getInstance();
        if (initDateTime != null) {
            calendar.setTime(initDateTime);
        }
        //设置时间选择器的相关参数
        mTimePicker.setIs24HourView(is24HourView);
        mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        mTimePicker.setOnTimeChangedListener(this);
    }

    /**设置activity*/
    private void setActivity(Activity activity){
        mActivity = activity;
    }
    /*-----set、get方法START-----*/
    public void setIs24HourView(boolean is24HourView) {
        this.is24HourView = is24HourView;
    }

    public void setOnButtonClickListener(OnButtonClickListener mListener) {
        this.mListener = mListener;
    }

	/*-----set、get方法END-----*/

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        switch (mBuilderType) {

            case TYPE_SIGNLE:
                Calendar calendar = Calendar.getInstance();
                calendar.set(0, 0, 0,
                        mTimePicker.getCurrentHour(),
                        mTimePicker.getCurrentMinute());
                mDateTime = calendar.getTime();
                break;

            case TYPE_DOUBLE:
                onDateChanged(null,0,0,0);
                break;

            default:
                break;

        }

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        switch (mBuilderType) {

            case TYPE_SIGNLE:
                Calendar calendarOne = Calendar.getInstance();
                calendarOne.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),
                        0, 0);
                mDateTime = calendarOne.getTime();
                break;

            case TYPE_DOUBLE:
                Calendar calendarTwo = Calendar.getInstance();
                calendarTwo.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),
                        mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
                mDateTime = calendarTwo.getTime();
                break;

            default:
                break;

        }
    }


}
