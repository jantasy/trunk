package cn.yjt.oa.app.beans;

/**
 * Created by 熊岳岳 on 2015/9/25.
 */
public class PatrolAttendanceInfo {

    // 巡检日期
    private String dutyDate;
    // 巡检状态说明
    private int status;
    // 巡检的线路
    private long lineId;
    // 巡检的线路名称

    public String getInSTime() {
        return inSTime;
    }

    public void setInSTime(String inSTime) {
        this.inSTime = inSTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    private String lineName;
    // 巡检的用户Id
    private long userId;
    // 巡检的计划时间
    private String inSTime;
    private String outTime;

    // 已经完成的点的数量
    private int finishPoints;
    // 未完成的点
    private int allPoints;

    public String getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(String dutyDate) {
        this.dutyDate = dutyDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getFinishPoints() {
        return finishPoints;
    }

    public void setFinishPoints(int finishPoints) {
        this.finishPoints = finishPoints;
    }

    public int getAllPoints() {
        return allPoints;
    }

    public void setAllPoints(int allPoints) {
        this.allPoints = allPoints;
    }
}
