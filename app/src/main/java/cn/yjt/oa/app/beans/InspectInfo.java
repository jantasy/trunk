package cn.yjt.oa.app.beans;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.util.Date;

import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.checkin.interfaces.ICheckInType;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

public class InspectInfo implements ICheckInType{

	private long id;
	private long userId;
	private String type; //签到类型，1：CHECK_IN：上班；2：CHECK_OUT：下班；3：VISIT：签到；4：CARD：打卡签到
	//5：NFC：NFC标签签到；	6：BAR_CODE：二维码扫描签到
	private String content;//附加信息
	private String positionDescription; //签到位置POI描述，NFC签到时是标签名称
	private String positionData; //签到位置信息
	private String inspectInTime;//签到时间
	private String attachment;//附件，用于照片描述，存放照片位置
	private String iccId;
	private String actrualData;//各类型签到特有数据，目前NFC签到和二维码签到存sn，位置签到不存
	private int inspectResult;//本次签到结果（客户端计算传入），0：签到（不在考勤围栏内），1：在考勤围栏内的签到，2：未计算
	private String inspectResultDesc;
	private String actrualPOI; //若positionDescription不表示POI描述信息时，用该字段表示POI描述信息，如NFC签到

	private int resultColor; //签到结果客户端显示颜色，如：0xffffff
	private int descColor; //签到描述信息客户端显示颜色，如：0xffffff

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

    @Override
    public int getResultCode() {
        return getInspectResult();
    }

    @Override
    public String getResultDesc() {
        return getInspectResultDesc();
    }

    public String getIccId() {
		return iccId;
	}

	public void setIccId(String iccId) {
		this.iccId = iccId;
	}

	/**
	 * one of {@link #SINGIN_TYPE_CHECKIN}, {@link #SINGIN_TYPE_CHECKOUT}, {@link #SINGIN_TYPE_VISIT}
	 */
	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getInspectInTime() {
		return inspectInTime;
	}

	public void setInspectInTime(String inspectInTime) {
		this.inspectInTime = inspectInTime;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getActrualData() {
		return actrualData;
	}

	public void setActrualData(String actrualData) {
		this.actrualData = actrualData;
	}

	public int getInspectResult() {
		return inspectResult;
	}

	public void setInspectResult(int inspectResult) {
		this.inspectResult = inspectResult;
	}

	public void setInspectResultDesc(String inspectResultDesc) {
		this.inspectResultDesc = inspectResultDesc;
	}

	public String getActrualPOI() {
		return actrualPOI;
	}

	public void setActrualPOI(String actrualPOI) {
		this.actrualPOI = actrualPOI;
	}

    @Override
    public void setPositionDesc(String desc) {
        setInspectResultDesc(desc);
    }

    public String getInspectResultDesc() {
		return inspectResultDesc;
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

	@SuppressLint("SimpleDateFormat")
	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		try {
			Date date = BusinessConstants.parseTime(inspectInTime);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtils.i("===e = " + e.toString());
		}
		return new Date(System.currentTimeMillis());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(userId);
		dest.writeString(type);
		dest.writeString(content);
		dest.writeString(positionDescription);
		dest.writeString(positionData);
		dest.writeString(inspectInTime);
		dest.writeString(attachment);
		dest.writeString(iccId);
		dest.writeString(actrualData);
		dest.writeInt(inspectResult);
		dest.writeString(inspectResultDesc);
		dest.writeString(actrualPOI);
		dest.writeInt(descColor);
		dest.writeInt(resultColor);
	}

	public InspectInfo() {
	}

	public InspectInfo(Parcel in) {
		id = in.readLong();
		userId = in.readLong();
		type = in.readString();
		content = in.readString();
		positionDescription = in.readString();
		positionData = in.readString();
		inspectInTime = in.readString();
		attachment = in.readString();
		iccId = in.readString();
		actrualData = in.readString();
		inspectResult = in.readInt();
		inspectResultDesc = in.readString();
		actrualPOI = in.readString();
		descColor = in.readInt();
		resultColor = in.readInt();
	}

	public static final Creator<InspectInfo> CREATOR = new Creator<InspectInfo>() {

		@Override
		public InspectInfo[] newArray(int size) {
			return new InspectInfo[size];
		}

		@Override
		public InspectInfo createFromParcel(Parcel source) {

			return new InspectInfo(source);
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
