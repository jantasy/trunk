package cn.yjt.oa.app.checkin.interfaces;

import android.os.Parcelable;

import java.util.Date;

import cn.yjt.oa.app.widget.TimeLineAdapter;

/**
 * Created by 熊岳岳 on 2015/9/30.
 */
public interface ICheckInType extends Parcelable,TimeLineAdapter.DateItem{

    public static final String SINGIN_TYPE_CHECKIN = "CHECK_IN";
    public static final String SINGIN_TYPE_CHECKOUT = "CHECK_OUT";
    public static final String SINGIN_TYPE_VISIT = "VISIT";
    public static final String SIGNIN_TYPE_NFC = "NFC";
    public static final String SIGNIN_TYPE_QR = "BAR_CODE";
    public static final String SIGNIN_TYPE_BEACON = "BEACON";

    public static final int RESULT_NORMAL = 1;

    /**返回类型*/
    public String getType();

    /**返回结果参数*/
    public int getResultCode();

    /**返回结果描述*/
    public String getResultDesc();

    /**获取日期信息*/
    public Date getDate();

    /**获取结果颜色*/
    public int getResultColor();

    /**获取详情颜色*/
    public int getDescColor();

    /**设置用户id*/
    public void setUserId(long userId);

    /**设置标签信息*/
    public void setActrualPOI(String poi);

    /**设置位置描述*/
    public void setPositionDesc(String desc);

    /**设置位置坐标*/
    public void setPositionData(String position);

    /**设置iccid信息*/
    public void setIccId(String iccid);
}
