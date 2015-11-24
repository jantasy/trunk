package cn.yjt.oa.app.beans;

/**
 * Created by 熊岳岳 on 2015/9/21.
 */
public class PatrolTag {

    /**
     * pointName : string
     * custId : 0
     * id : 0
     * createTime : string
     * sn : string
     * creatorName : string
     * name : string
     * creatorId : 0
     * pointId : 0
     * lineId : 0
     */

    private String pointName;
    private long custId;
    private long id;
    private String createTime;
    private String sn;
    private String creatorName;
    private String name;
    private long creatorId;
    private long pointId;
    private long lineId;


    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public long getCustId() {
        return custId;
    }

    public void setCustId(long custId) {
        this.custId = custId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public long getPointId() {
        return pointId;
    }

    public void setPointId(long pointId) {
        this.pointId = pointId;
    }

    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }
}
