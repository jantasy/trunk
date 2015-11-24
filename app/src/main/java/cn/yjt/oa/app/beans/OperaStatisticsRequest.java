package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import cn.yjt.oa.app.utils.AccountUtils;

/**
 * Created by 熊岳岳 on 2015/10/9.
 */


public class OperaStatisticsRequest implements Parcelable {

    private long userId;

    private String custId;

    private String operaEventNo;

    private long time;

    private String iccid;

    private String imei;

    private String channelName;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public OperaStatisticsRequest(OperaStatistics info){
        this.setCustId(info.getCustId());
        this.setUserId(info.getUserId());
        this.setTime(info.getTime());
        this.setOperaEventNo(info.getOperaEventNo());
        this.setIccid(info.getIccid());
        this.setImei(info.getImei());
        this.setChannelName(info.getChannelName());
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getOperaEventNo() {
        return operaEventNo;
    }

    public void setOperaEventNo(String operaEventNo) {
        this.operaEventNo = operaEventNo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.custId);
        dest.writeString(this.operaEventNo);
        dest.writeLong(this.time);
        dest.writeString(this.iccid);
        dest.writeString(this.imei);
        dest.writeString(this.channelName);
    }

    protected OperaStatisticsRequest(Parcel in) {
        this.userId = in.readLong();
        this.custId = in.readString();
        this.operaEventNo = in.readString();
        this.time = in.readLong();
        this.iccid = in.readString();
        this.imei = in.readString();
        this.channelName = in.readString();
    }

    public static final Parcelable.Creator<OperaStatisticsRequest> CREATOR = new Parcelable.Creator<OperaStatisticsRequest>() {
        public OperaStatisticsRequest createFromParcel(Parcel source) {
            return new OperaStatisticsRequest(source);
        }

        public OperaStatisticsRequest[] newArray(int size) {
            return new OperaStatisticsRequest[size];
        }
    };
}
