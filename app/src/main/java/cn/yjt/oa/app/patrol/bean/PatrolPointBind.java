package cn.yjt.oa.app.patrol.bean;

import java.io.Serializable;

public class PatrolPointBind extends IcanSearched implements Serializable{

    private long id;
    private long routeId;
    private long custId;
    private int signRange; //范围（单位米）
    private boolean isBind;
    private boolean isSelected = false;
    private String name;
    private String description;//描述
    private String positionDescription; //位置POI信息
    private String positionData; //坐标
    private String check;//校验类型

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustId() {
        return custId;
    }

    public void setCustId(long custId) {
        this.custId = custId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPositionDescription() {
        return positionDescription;
    }

    public void setPositionDescription(String positionDescription) {
        this.positionDescription = positionDescription;
    }

    public String getPositionData() {
        return positionData;
    }

    public void setPositionData(String positionData) {
        this.positionData = positionData;
    }

    public int getSignRange() {
        return signRange;
    }

    public void setSignRange(int signRange) {
        this.signRange = signRange;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean isBind) {
        this.isBind = isBind;
    }

    public boolean isSelected() {
        return isSelected;
    }


    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String getSearchString() {
        return name;
    }
}
