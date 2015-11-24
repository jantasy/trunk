package cn.yjt.oa.app.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

public class CardCheckinInfo implements DateItem{
	
	public static final String SINGIN_TYPE_CHECKIN = "CHECK_IN";
	public static final String SINGIN_TYPE_CHECKOUT = "CHECK_OUT";
	public static final String SINGIN_TYPE_VISIT = "VISIT";
	public static final String SIGNIN_TYPE_NFC = "NFC";
	public static final String SIGNIN_TYPE_QR = "BAR_CODE";

	private String cardTime; //打卡时间， 格式：HHMMSS
	private String cardDate; //打卡日期，格式：YYYYMMDD
	private String machineName; //刷卡机具名称
	private String type; //VISIT:位置，NFC:nfc，CARD:打卡
	private String positionData;
	private String actrualPOI; //若positionDescription不表示POI描述信息时，用该字段表示POI描述信息，如NFC签到

	
	
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public String getCardTime() {
		return cardTime;
	}
	public void setCardTime(String cardTime) {
		this.cardTime = cardTime;
	}
	
	
	public String getActrualPOI() {
		return actrualPOI;
	}
	public void setActrualPOI(String actrualPOI) {
		this.actrualPOI = actrualPOI;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getCardDate() {
		return cardDate;
	}
	public void setCardDate(String cardDate) {
		this.cardDate = cardDate;
	}
	@Override
	public Date getDate() {
		return parseDate(cardDate);
	}
	
	
	public String getPositionData() {
		return positionData;
	}
	public void setPositionData(String positionData) {
		this.positionData = positionData;
	}
	public static Date parseDate(String cardTime){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			return dateFormat.parse(cardTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String toString() {
		return "CardCheckinInfo [cardTime=" + cardTime + ", cardDate="
				+ cardDate + ", machineName=" + machineName + ", type=" + type
				+ ", actrualPOI=" + actrualPOI+"]";
	}
	
	
	
}
