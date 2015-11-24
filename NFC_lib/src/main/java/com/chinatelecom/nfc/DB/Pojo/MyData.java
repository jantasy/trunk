package com.chinatelecom.nfc.DB.Pojo;

import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

import com.chinatelecom.nfc.Const.Const;

public class MyData implements Parcelable {

	public Integer id;
	public String n;
	/**
	 * 环境类型，
	 * <ul>
	 * <li>1 还原手机模式</li>
	 * <li>2 会议室工作管理</li>
	 * <li>3 工作日程安排</li>
	 * <li>4 NFC抽奖</li>
	 * <li>5 广告</li>
	 * <li>6 自定义环境</li>
	 * 
	 * <li>7 电子名片</li>
	 * <li>8 字符标签</li>
	 * <li>9 网址标签</li>
	 * </ul>
	 */
	public Integer dt;
	/**
	 * 环境类型表ID
	 */
	public Integer tId;

	public Long time;
	public String uuid;
	/**
	 * <ul>
	 * <li>1 从NFC标签中读取数据，默认</li>
	 * <li>2 制作NFC标签</li>
	 * </ul>
	 */
	public Integer row;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param dataType
	 *            判断类型，查询相应的表
	 * @param tableID
	 *            对应表中的数据ID
	 * @param createTime
	 *            long
	 * @param readOrwrite
	 *            插入值：MyDataTable.WRITETAG|MyDataTable.READFROMNFC
	 */
	public MyData(Integer id, String name, Integer dataType, Integer tableID,
			Long createTime, Integer readOrwrite) {
		super();
		this.id = id;
		this.n = name;
		this.dt = dataType;
		this.tId = tableID;
		this.time = createTime;
		this.row = readOrwrite;
		uuid = UUID.randomUUID().toString();
	}

	public MyData(Integer id, String name, Integer dataType, Integer tableID,
			Long createTime, Integer readOrwrite, String uuid) {
		super();
		this.id = id;
		this.n = name;
		this.dt = dataType;
		this.tId = tableID;
		this.time = createTime;
		this.row = readOrwrite;
		this.uuid = uuid;
	}

	// ashley 0923 删除无用资源
	// private Ad ad;
	// private Lottery lot;
	private Meetting mee;
	private MyMode myM;
	// ashley 0923 删除无用资源
	// private Coupon cou;
	// private ParkTicket par;

	// ashley 0923 删除无用资源
	// private MovieTicket mov;

	private String text;
	private String web;
	private NameCard nam;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public NameCard getNameCard() {
		return nam;
	}

	public void setNameCard(NameCard nameCard) {
		this.nam = nameCard;
	}

	// ashley 0923 删除无用资源
	// public Ad getAd() {
	// return ad;
	// }
	// public void setAd(Ad ad) {
	// this.ad = ad;
	// }
	// public Lottery getLottery() {
	// return lot;
	// }
	// public void setLottery(Lottery lottery) {
	// this.lot = lottery;
	// }
	public Meetting getMeeting() {
		return mee;
	}

	public void setMeeting(Meetting meeting) {
		this.mee = meeting;
	}

	public MyMode getMyMode() {
		return myM;
	}

	public void setMyMode(MyMode myMode) {
		this.myM = myMode;
	}

	@Override
	public String toString() {
		return "MyData [id=" + id + ", n=" + n + ", dt=" + dt + ", tId=" + tId
				+ ", time=" + time + ", uuid=" + uuid + ", row=" + row
				+ ", mee=" + mee + ", myM=" + myM + ", text=" + text + ", web="
				+ web + ", nam=" + nam + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(arg1);
	}

	public static final Creator<MyData> CREATOR = new Creator<MyData>() {
		public MyData createFromParcel(Parcel in) {
			return null;
		}

		public MyData[] newArray(int size) {
			return null;
		}
	};
}
