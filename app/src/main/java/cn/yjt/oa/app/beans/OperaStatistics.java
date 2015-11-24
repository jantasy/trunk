package cn.yjt.oa.app.beans;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import cn.yjt.oa.app.utils.AccountUtils;

/**
 * Created by 熊岳岳 on 2015/10/9.
 */

@Table(name="OperaStatistics")
public class OperaStatistics extends Model{

    @Column(name="userId")
    private long userId;

    @Column(name="custId")
    private String custId;

    @Column(name="operaEventNo")
    private String operaEventNo;

    @Column(name = "time")
    private long time;

    @Column(name = "iccid")
    private String iccid;

    @Column(name = "imei")
    private String imei;

    @Column(name = "channelName")
    private String channelName;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
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

    public OperaStatistics() {
        super();
    }

    public OperaStatistics(String operaEventNo){
        super();
        setUserId(AccountUtils.getCurrentUserId());
        setCustId(AccountUtils.getCurrentCustId());
        setIccid(AccountUtils.getCurrentIccId());
        setImei(AccountUtils.getCurrentImei());
        setOperaEventNo(operaEventNo);
        setChannelName(AccountUtils.getChannelName());
        setTime(AccountUtils.getCurrentTime());
    }

    public OperaStatistics(String operaEventNo,long userId,String custId){
        super();
        setUserId(userId);
        setCustId(custId);
        setIccid(AccountUtils.getCurrentIccId());
        setImei(AccountUtils.getCurrentImei());
        setOperaEventNo(operaEventNo);
        setTime(AccountUtils.getCurrentTime());
        setChannelName(AccountUtils.getChannelName());
    }
}
