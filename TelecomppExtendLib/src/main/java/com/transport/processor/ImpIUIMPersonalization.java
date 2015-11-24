package com.transport.processor;

import java.util.Map;

import android.content.Context;
import cn.com.ctbri.yijitong.edep.ApduResponse;
import cn.com.ctbri.yijitong.edep.Method;

import com.telecompp.engine.YjtLoadRequestEngine;
import com.telecompp.util.TerminalDataManager;

public class ImpIUIMPersonalization extends BaseYJTCard implements IUIMPersonalization {
	
	/**
	 * 个人化过程中的上一条消息流水号
	 */
	private String sequenceId;
	
	/**
	 * 最后一条apdu指令的索引号
	 */
	private String apduIndex;
	
	/**
	 * 执行的最后一条apdu
	 */
	private String rApdu;

	public ImpIUIMPersonalization(String tradeNum, Context context) {
		super(tradeNum, context);
	}

	@Override
	public boolean yjtUserAUC(String msisdn, String iccid, String seId,
			String serviceType) {
		Map<String, Object> map_userAUC = YjtLoadRequestEngine.yjtUserIdentification(msisdn, iccid, seId, serviceType);
		if(map_userAUC != null) {
			// 1表示认证成功
			if("1".equals(map_userAUC.get("result"))) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean yjtPOSPersonReq(String msisdn, String iccid) {
		
		Map<String, Object> map_result = YjtLoadRequestEngine.yjtPOSPersonReq(msisdn, iccid);
		if(map_result != null) {
			// 表示认证成功
			// TODO 这里需要解析出apdu指令组
			String[] apdus = map_result.get("APDUS").toString().split("|");
			
			// TODO 写卡
			for (int i = 0; i < apdus.length; i++) {
				ApduResponse apduResp = edepAdapter.sendapdu(apdus[i]);
				byte[] result = apduResp.getBytes();
				short result_short = apduResp.getSw12();
				byte sw1 = apduResp.getSw1();
				byte sw2 = apduResp.getSw2();
				
				String str_result = Method.byte2HexString(sw1) + Method.byte2HexString(sw2);
				if("9000".equals(str_result)) {
					// TODO apdu执行成功, 
					// 这里需要记录sequenceId, apduIndex, rapdu
					if(i == apdus.length - 1) {
						rApdu = apdus[i];
						apduIndex = "" + i;
						// 这里需要获取sequenceId
						sequenceId = "";
					}
					
				} else {
					return false;
				}
				
			}
			
			// 写卡成功
			return true;
		}
		
		return false;
	}

	@Override
	public boolean yjtCardStatusSyn(String msisdn, String iccid, String lcs) {
		Map<String, Object> map_result = YjtLoadRequestEngine.yjtCardStatusSyn(msisdn, iccid, lcs);
		if(map_result != null) {
			// 表示认证成功
			if("true".equals(map_result.get("status"))) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Map<String, Object> yjtAPDUResultReq(String msisdn,
			String iccid, String sequenceId, String apduIndex, String rApdu,
			String result) {
		return YjtLoadRequestEngine.yjtAPDUResultReq(msisdn, iccid, sequenceId, apduIndex, rApdu, result);
	}

	@Override
	public boolean yjtDelPersoReq(String msisdn, String iccid) {
		Map<String, Object> map_result = YjtLoadRequestEngine.yjtDelPersoReq(msisdn, iccid);
		if(map_result != null) {
			// 表示认证成功
			// TODO 这里需要解析出apdu指令组
			String[] apdus = map_result.get("APDUS").toString().split("|");
			
			// TODO 写卡
			for (int i = 0; i < apdus.length; i++) {
				ApduResponse apduResp = edepAdapter.sendapdu(apdus[i]);
				byte[] result = apduResp.getBytes();
				short result_short = apduResp.getSw12();
				byte sw1 = apduResp.getSw1();
				byte sw2 = apduResp.getSw2();
				
				String str_result = Method.byte2HexString(sw1) + Method.byte2HexString(sw2);
				if("9000".equals(str_result)) {
					// TODO apdu执行成功, 
					
				} else {
					return false;
				}
				
			}
			
			// 写卡成功
			return true;
		}
		
		return false;
	}
	
	
	public void startPersonal() {
		
		// 手机号, iccid, seId,, serviceType
		String msisdn = TerminalDataManager.getPlug_phoneNum();
		String iccid = TerminalDataManager.getICCID();
		String seId = "";
		String serviceType = "";  // 1应用下载, 2应用删除
		// 个人化流程
		
		// 1.用户认证
		if(yjtUserAUC(msisdn, iccid, seId, serviceType)) {
			// 2.卡片状态同步
			String lcs = "3";
			if(yjtCardStatusSyn(msisdn, iccid, lcs)) {
				// 3.个人化操作   TODO 这里需要判断个人化操作什么时候完成
				if(yjtPOSPersonReq(msisdn, iccid)) {
					// 由于有多组apdu指令需要执行, 所以使用循环
					while(true) {
						// 4.卡片响应结果通知  成功报文  result为0标识成功
						Map<String, Object> map_apduResultReq = yjtAPDUResultReq(msisdn, iccid, sequenceId, apduIndex, rApdu, "0");
						if(map_apduResultReq != null && map_apduResultReq.get("APDUS") != null) {
							// 表示认证成功
							// TODO 这里需要解析出apdu指令组
							String[] apdus = map_apduResultReq.get("APDUS").toString().split("|");
							
							// 本条的消息流水
							sequenceId = map_apduResultReq.get("Sequence_Id").toString();
							
							// TODO 写卡
							for (int i = 0; i < apdus.length; i++) {
								ApduResponse apduResp = edepAdapter.sendapdu(apdus[i]);
								byte[] result = apduResp.getBytes();
								short result_short = apduResp.getSw12();
								byte sw1 = apduResp.getSw1();
								byte sw2 = apduResp.getSw2();
								
								String str_result = Method.byte2HexString(sw1) + Method.byte2HexString(sw2);
								if("9000".equals(str_result)) {
									// TODO apdu执行成功, 
									// apdu执行成功后会返回apduindex
									
									
									// 这里需要记录sequenceId, apduIndex, rapdu
									if(i == apdus.length - 1) {
										rApdu = apdus[i];
										apduIndex = "" + i;
										
									}
									
								} else {
									break;
								}
								
							}
						} else {
							// 跳出循环
							break;
						}
					}
				} else {
					yjtAPDUResultReq(msisdn, iccid, sequenceId, apduIndex, rApdu, "1");
				}
			}
		}
		
		
		
	}

}
