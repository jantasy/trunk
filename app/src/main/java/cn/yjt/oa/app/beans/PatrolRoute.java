package cn.yjt.oa.app.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 熊岳岳 on 2015/9/17.
 */
public class PatrolRoute implements Serializable{

    /**
     * id : 0
     * custId : 0
     * name : string
     * description : string
     * createTime : string
     * updateTime : string
     */

    private int id;
    private long custId;
    private String name;
    private String description;
    private String createTime;
    private String updateTime;
    private List<PatrolPoint> points;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public long getCustId() {
        return custId;
    }

    public void setCustId(long custId) {
        this.custId = custId;
    }

    public List<PatrolPoint> getPoints() {
        return points;
    }

    public void setPoints(List<PatrolPoint> points) {
        this.points = points;
    }
}
