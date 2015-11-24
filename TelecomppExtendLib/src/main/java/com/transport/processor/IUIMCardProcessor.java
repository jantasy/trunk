package com.transport.processor;

import java.util.Map;

import cn.com.ctbri.yijitong.edep.ApduResponse;
import cn.com.ctbri.yijitong.edep.ResponseForLoad;

import com.telecompp.util.SumaPostHandler;


public interface IUIMCardProcessor {

	//	向平台发送POST请求的工具
	SumaPostHandler HttpPostHandler = new SumaPostHandler();
	public int GET_REQUEST_XML_ERROR = 0x01;//拼接请求报文出错
	
	//	没有获取到MAC2
	public final String LOAD_GET_NO_MAC2 = "0";
	//	获取到MAC2
	public final String LOAD_GET_MAC2 = "1";
	// 只扣款不需要刷卡充值的类型(加入大白卡后  新加的)
	public final String LOAD_NO_NEED_WRITECARD = "2";
	
	/**
	 * 获取卡内余额
	 * @return
	 */
	public String getCardBalance();
	
	/**
	 * 获取卡片充值计数器
	 * @return
	 */
	public String getCardLoadCount();
	
	/**
	 * 1.初始化翼机通卡
	 * @return
	 */
	public abstract void initEjitongCard();
	
	/**
	 * 2.初始化圈存
	 * 包括一下几个步骤:
	 * a.获取电子钱包消费计数
	 * b.验证pin
	 * c.初始化圈存
	 * @return
	 */
	public abstract ResponseForLoad initLoad(String tradeMoney);
	
	/**
	 * 3.访问平台进行圈存申请
	 * @param resultload: 圈存初始化返回结果 edepAdapter.init_load
	 * @param tradeMoney: 交易金额
	 * @return
	 */
	public abstract Map<String, Object> loadApply(ResponseForLoad resultload, String tradeMoney);
	
	/**
	 * 4.进行写卡充值
	 * @param MAC2
	 * @return
	 */
	public abstract ApduResponse creditForLoad(String MAC2);
	
	/**
	 * 5.上传TAC
	 * @param applicationSN: 
	 * @param consumeCount
	 * @param loadCount
	 * @param TAC
	 * @param tranResultFlag
	 * @param tradeNum
	 * @return
	 */
	public abstract Boolean sendTAC(String applicationSN, String consumeCount, String loadCount,
                                    String TAC, String tranResultFlag, String tradeNum);
	
	public abstract void noticeYJT();
	
	public abstract boolean interactionBetweenPhoneAndCard(String tradeMoney); 
}
