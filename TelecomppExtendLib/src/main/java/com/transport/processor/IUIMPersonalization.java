package com.transport.processor;

import java.util.Map;

/**
 * UIM卡的个人化
 * @author wbb
 *
 */
public interface IUIMPersonalization {

	/**
	 * 用户认证接口
	 * @param msisdn: 手机号码
	 * @param iccid: 卡片序列号
	 * @param seId: 
	 * @param serviceType: 服务类型(1.应用下载, 2.应用删除, 3.应用锁定, 4.应用解锁)
	 * @return  相应内容, 1.认证成功(当服务类型是应用下载时，表示用户审核成功，并且个人化数据也已准备完成，当服务类型是非应用下载时，表示用户审核成功), 2.认证失败(表示用户审核失败), 3.受理失败(适用于翼机通SP-TSM平台侧判断出用户发起的申请数据格式不合法等场景), 5.用户认证失败, 此卡信息错误, 6.认证失败, 此卡已经失效
	 */
	public abstract boolean yjtUserAUC(String msisdn, String iccid, String seId, String serviceType);
	
	
	/**
	 * 个人化操作接口
	 * @param msisdn: 手机号码
	 * @param iccid: 卡片序列号
	 * @return ifResp和apdu组
	 */
	public abstract boolean yjtPOSPersonReq(String msisdn, String iccid);
	
	
	/**
	 * 卡片状态同步
	 * @param msisdn: 手机号
	 * @param iccid: 卡片序列号
	 * @param lcs: 取值说明 (0：删除, 3：INSTALLED, 7：SELECTABLE, 15：PERSONALIZED, 131：LOCKED)
	 * @return
	 */
	public abstract boolean yjtCardStatusSyn(String msisdn, String iccid, String lcs);
	
	
	/**
	 * 卡片响应结果通知
	 * @param msisdn: 手机号码
	 * @param iccid: 卡片序列号
	 * @param sequenceId: 个人化过程中的上一条消息流水号
	 * @param apduIndex: 执行的最后一条apdu索引号, 当result值为0x01时无效
	 * @param rApdu: 执行的最后一条apdu执行的响应数据, 当result值为1时无效
	 * @param result: 0执行成功表示电信TSM平台成功接收到卡片响应数据, 1系统执行失败
	 * @return
	 */
	public abstract Map<String, Object> yjtAPDUResultReq(String msisdn, String iccid, String sequenceId, String apduIndex, String rApdu, String result);
	
	
	/**
	 * 去个人化请求
	 * @param msisdn: 手机号
	 * @param iccid: 卡片序列号
	 * @return
	 */
	public abstract boolean yjtDelPersoReq(String msisdn, String iccid);

	
}
