package cn.yjt.oa.app.beans;

import java.text.ParseException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

public class SigninInfo implements DateItem, Parcelable {

	public static final String SINGIN_TYPE_CHECKIN = "CHECK_IN";
	public static final String SINGIN_TYPE_CHECKOUT = "CHECK_OUT";
	public static final String SINGIN_TYPE_VISIT = "VISIT";
	public static final String SIGNIN_TYPE_NFC = "NFC";
	public static final String SIGNIN_TYPE_QR = "BAR_CODE";
	public static final String SIGNIN_TYPE_BEACON = "BEACON";

	public static final int RESULT_NORMAL = 1;

	private long id;
	private long userId;
	private String type; //签到类型，1：CHECK_IN：上班；2：CHECK_OUT：下班；3：VISIT：签到；4：CARD：打卡签到
	//5：NFC：NFC标签签到；	6：BAR_CODE：二维码扫描签到
	private String content;//附加信息
	private String positionDescription; //签到位置POI描述，NFC签到时是标签名称
	private String positionData; //签到位置信息
	private String signInTime;//签到时间
	private String attachment;//附件，用于照片描述，存放照片位置
	private String iccId;
	private String actrualData;
	private int signResult;//本次签到结果（客户端计算传入），0：签到（不在考勤围栏内），1：在考勤围栏内的签到，2：未计算
	private String signResultDesc;
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

	public String getSignInTime() {
		return signInTime;
	}

	public void setSignInTime(String signInTime) {
		this.signInTime = signInTime;
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

	public int getSignResult() {
		return signResult;
	}

	public void setSignResult(int signResult) {
		this.signResult = signResult;
	}

	public void setSignResultDesc(String signResultDesc) {
		this.signResultDesc = signResultDesc;
	}

	public String getActrualPOI() {
		return actrualPOI;
	}

	public void setActrualPOI(String actrualPOI) {
		this.actrualPOI = actrualPOI;
	}

	public String getSignResultDesc() {
		return signResultDesc;
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
			Date date = BusinessConstants.parseTime(signInTime);
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
		dest.writeString(signInTime);
		dest.writeString(attachment);
		dest.writeString(iccId);
		dest.writeString(actrualData);
		dest.writeInt(signResult);
		dest.writeString(signResultDesc);
		dest.writeString(actrualPOI);
		dest.writeInt(descColor);
		dest.writeInt(resultColor);
	}

	public SigninInfo() {
	}

	public SigninInfo(Parcel in) {
		id = in.readLong();
		userId = in.readLong();
		type = in.readString();
		content = in.readString();
		positionDescription = in.readString();
		positionData = in.readString();
		signInTime = in.readString();
		attachment = in.readString();
		iccId = in.readString();
		actrualData = in.readString();
		signResult = in.readInt();
		signResultDesc = in.readString();
		actrualPOI = in.readString();
		descColor = in.readInt();
		resultColor = in.readInt();
	}

	public static final Creator<SigninInfo> CREATOR = new Creator<SigninInfo>() {

		@Override
		public SigninInfo[] newArray(int size) {
			return new SigninInfo[size];
		}

		@Override
		public SigninInfo createFromParcel(Parcel source) {

			return new SigninInfo(source);
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
