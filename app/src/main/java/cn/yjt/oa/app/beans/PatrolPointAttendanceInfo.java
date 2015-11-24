package cn.yjt.oa.app.beans;

/**
 * Created by 熊岳岳 on 2015/9/25.
 */
public class PatrolPointAttendanceInfo {


    /**
     * pointId : 0
     * pointName : string
     * status : 0
     * inTime : string
     */

    private int pointId;
    private String pointName;
    private int status;
    private String inTime;

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public int getPointId() {
        return pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public int getStatus() {
        return status;
    }

    public String getInTime() {
        return inTime;
    }
}
