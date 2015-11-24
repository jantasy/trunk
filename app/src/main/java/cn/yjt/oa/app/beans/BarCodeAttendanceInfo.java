package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import cn.yjt.oa.app.widget.TimeLineAdapter;

public class BarCodeAttendanceInfo implements TimeLineAdapter.DateItem, Parcelable {

    public static final String SINGIN_TYPE_CHECKIN = "CHECK_IN";
    public static final String SINGIN_TYPE_CHECKOUT = "CHECK_OUT";
    public static final String SINGIN_TYPE_VISIT = "VISIT";
    public static final String SIGNIN_TYPE_NFC = "NFC";
    public static final String SIGNIN_TYPE_QR = "BAR_CODE";
    public static final String SIGNIN_TYPE_BEACON = "BEACON";

    public static final int RESULT_NORMAL = 1;

	private String positionDescription; //签到位置POI描述，NFC签到时是标签名称
	private String positionData; //签到位置信息
	private String time; //签到时间
	private String iccId;
	
	private int result; //本次签到结果（客户端计算传入），0：签到（不在考勤围栏内），1：在考勤围栏内的签到，2：未计算
	private String resultDesc; //本次签到结果提示信息
	private String actrualData; //各类型签到特有数据，目前NFC签到存sn
	
	private int resultColor; //签到结果客户端显示颜色，如：0xffffff
	private int descColor; //签到描述信息客户端显示颜色，如：0xffffff
	
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIccId() {
		return iccId;
	}
	public void setIccId(String iccId) {
		this.iccId = iccId;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
	public String getActrualData() {
		return actrualData;
	}
	public void setActrualData(String actrualData) {
		this.actrualData = actrualData;
	}
	public int getResultColor() {
		return resultColor;
	}
	public void setResultColor(int resultColor) {
		this.resultColor = resultColor;
	}
	public int getDescColor() {
		return descColor;
	}
	public void setDescColor(int descColor) {
		this.descColor = descColor;
	}
	@Override
	public String toString() {
		return "BarCodeAttendanceInfo [positionDescription="
				+ positionDescription + ", positionData=" + positionData
				+ ", time=" + time + ", iccId=" + iccId + ", result=" + result
				+ ", resultDesc=" + resultDesc + ", actrualData=" + actrualData
				+ ", resultColor=" + resultColor + ", descColor=" + descColor
				+ "]";
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.positionDescription);
        dest.writeString(this.positionData);
        dest.writeString(this.time);
        dest.writeString(this.iccId);
        dest.writeInt(this.result);
        dest.writeString(this.resultDesc);
        dest.writeString(this.actrualData);
        dest.writeInt(this.resultColor);
        dest.writeInt(this.descColor);
    }

    public BarCodeAttendanceInfo() {
    }

    protected BarCodeAttendanceInfo(Parcel in) {
        this.positionDescription = in.readString();
        this.positionData = in.readString();
        this.time = in.readString();
        this.iccId = in.readString();
        this.result = in.readInt();
        this.resultDesc = in.readString();
        this.actrualData = in.readString();
        this.resultColor = in.readInt();
        this.descColor = in.readInt();
    }

    public static final Creator<BarCodeAttendanceInfo> CREATOR = new Creator<BarCodeAttendanceInfo>() {
        public BarCodeAttendanceInfo createFromParcel(Parcel source) {
            return new BarCodeAttendanceInfo(source);
        }

        public BarCodeAttendanceInfo[] newArray(int size) {
            return new BarCodeAttendanceInfo[size];
        }
    };

    @Override
    public Date getDate() {
        return null;
    }
}
