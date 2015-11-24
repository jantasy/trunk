package cn.yjt.oa.app.beans;

import java.io.Serializable;

/**
 * Created by 熊岳岳 on 2015/9/16.
 */
public class PatrolPoint implements Serializable{

    public final static int CHECK_TRUE = 1;
    public final static int CHECK_FALSE = 0;

    /**
     * id : 0
     * custId : 0
     * name : string
     * description :string
     * positionDescription : string
     * positionData : string
     * createTime : string
     * updateTime : string
     * checkTag : 0
     * checkPosition : 0
     * signRange : 0
     */

    private int id;
    private long custId;
    private String name;
    private String description;
    private String positionDescription;
    private String positionData;
    private String createTime;
    private String updateTime;
    private int checkTag;
    private int checkPosition;
    private int signRange;

    private int orderId;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    private boolean isSelected;
    public void setId(int id) {
        this.id = id;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public void setPositionData(String positionData) {
        this.positionData = positionData;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setCheckTag(int checkTag) {
        this.checkTag = checkTag;
    }

    public void setCheckPosition(int checkPosition) {
        this.checkPosition = checkPosition;
    }

    public void setSignRange(int signRange) {
        this.signRange = signRange;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public String getPositionData() {
        return positionData;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public int getCheckTag() {
        return checkTag;
    }

    public int getCheckPosition() {
        return checkPosition;
    }

    public int getSignRange() {
        return signRange;
    }

    public long getCustId() {
        return custId;
    }

    public void setCustId(long custId) {
        this.custId = custId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PatrolPoint) {
            PatrolPoint info = (PatrolPoint) o;
            return info.getId() == this.getId();
        }
        return super.equals(o);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
