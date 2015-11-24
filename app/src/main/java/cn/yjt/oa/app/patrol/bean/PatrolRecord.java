package cn.yjt.oa.app.patrol.bean;

/**
 * Created by xiong on 2015/9/23.
 */
public class PatrolRecord {
    private int id;
    private String name;
    private String planStartTime;
    private int compPointCount;
    private int uncompPointCount;
    private String status;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public int getCompPointCount() {
        return compPointCount;
    }

    public void setCompPointCount(int compPointCount) {
        this.compPointCount = compPointCount;
    }

    public int getUncompPointCount() {
        return uncompPointCount;
    }

    public void setUncompPointCount(int uncompPointCount) {
        this.uncompPointCount = uncompPointCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
