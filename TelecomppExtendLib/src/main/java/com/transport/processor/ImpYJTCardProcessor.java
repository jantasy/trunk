
package com.transport.processor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.simalliance.openmobileapi.Channel;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.util.Log;
import cn.com.ctbri.yijitong.edep.ApduResponse;
import cn.com.ctbri.yijitong.edep.ChannelOpenmobileApi;
import cn.com.ctbri.yijitong.edep.EjitongCard;
import cn.com.ctbri.yijitong.edep.Method;
import cn.com.ctbri.yijitong.edep.ResponseForLoad;

import com.telecompp.ContextUtil;
import com.telecompp.engine.YjtLoadRequestEngine;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.ResponseExceptionInfo;
import com.telecompp.util.ResultConstant;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.TerminalDataManager;
import com.telecompp.xml.XmlHandler;
import com.telecompp.xml.XmlMgr_BusTradeBase;
import com.transport.RetryTask;
import com.transport.RetryTask.TaskAndCallback;
import com.transport.db.bean.ConfirmItem;
import com.transport.db.bean.IsLoadSuccessItem;
import com.transport.db.dao.PConfirmDao;
import com.transport.db.dao.PIsLoadSuccessDao;
import com.transport.service.UploadConfirmService;

public class ImpYJTCardProcessor implements IUIMCardProcessor, SumaConstants{
	
	//	翼机通卡第三方jar包
	private EjitongCard edepAdapter;
	//	当前上下文对象
	private Context context;
	//	卡应用序列号
	private String applicationSN = "";
	//	消费计数器
	private String consumeCount = "";
	//	充值计数器
	private String loadCount = "";
	//	碰碰交易流水号
	private String tradeNum = "";
	//	交易前金额
	private String balance_str = "";
	//	系统当前时间
	private String currentDateTime_mac2 = "";
	
	//	log
	private String log_activity = "ImpYJTCardProcessor";
	
	private IsLoadSuccessItem item;
	
	private static LoggerHelper logger = new LoggerHelper(ImpYJTCardProcessor.class);
	
	public ImpYJTCardProcessor(Context context, String tradeNum) {
		this.context = context;
		this.tradeNum = tradeNum;
	}

	@Override
	public void initEjitongCard() {
		try {
			//	1.每次机卡交互, 必须先获得翼机通卡
			edepAdapter = EjitongCard.getDefaultCard();
			//	2.初始化机卡通道         aid:应用标识符 D1560000401000300000000200000000
			edepAdapter.init("D1560000401000300000000200000000", context);
			
		} catch (Exception e) {
			//	设置错误信息
			ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_INITYJTCARD_EXCEPTION);
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_INITYJTCARD_EXCEPTION_MSG);			
			e.printStackTrace();
			logger.printLogOnSDCard("机卡通道初始化失败:" + e.toString());
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		} catch(Throwable thr) {
			//	设置错误信息
			ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_INITYJTCARD_EXCEPTION);
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_INITYJTCARD_EXCEPTION_MSG);			
			thr.printStackTrace();
			logger.printLogOnSDCard("机卡通道初始化失败:" + thr.toString());
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		}
	}

	@Override
	public ResponseForLoad initLoad(String tradeMoney) {
		ResponseForLoad resultload = null;
		try {			
			//	获取翼机通卡片序列号   圈存请求的时候使用
			applicationSN = edepAdapter.GetApplicationofSN();
			//	获取电子钱包消费计数
			String APDU = "8050"+"0102"+"0B"+"01"+"00000000"+"000000000000"+"0F";
			ApduResponse rsp = edepAdapter.sendapdu(APDU);
			
			if (rsp.isOkey()) {
				String resp = Method.bytesToHexString(rsp.getBytes());
				consumeCount = resp.substring(8,12); 				
			}
			
			//	3.验证pin	
			edepAdapter.veifyPin("000000");
			//	将金额转化成int型
			int _tradeMoney = 0;
			try {
				_tradeMoney = Integer.parseInt(tradeMoney);
			} catch (Exception e) {
				_tradeMoney = 0;
				e.printStackTrace();
				logger.printLogOnSDCard("初始化圈存失败, 金额转化成int异常:" + e.getStackTrace().toString());
			}  catch(Throwable thr) {
				thr.printStackTrace();
				_tradeMoney = 0;
				logger.printLogOnSDCard("初始化圈存失败, 金额转化成int异常:" + thr.getStackTrace().toString());
			}
			//	4.初始化圈存 对应的参数 type:ED 01, EP 02; secrtID: 密钥索引号; cash:交易金额; terminalID:终端机编号  (0 + 电话号码 )
			resultload = edepAdapter.init_load((byte)0x02, (byte)0x01, _tradeMoney, Method.hexStringToByte("0" + TerminalDataManager.getPlug_phoneNum()));
		} catch (Exception e) {
			ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_INITLOAD_EXCEPTION);
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_INITLOAD_EXCEPTION_MSG);			
			e.printStackTrace();
			logger.printLogOnSDCard("初始化圈存失败:" + e.getStackTrace().toString());
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		} catch(Throwable thr) {
			ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_INITLOAD_EXCEPTION);
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_INITLOAD_EXCEPTION_MSG);			
			thr.printStackTrace();
			logger.printLogOnSDCard("初始化圈存失败:" + thr.getStackTrace().toString());
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		}
		return resultload;
	}

	@Override
	public Map<String, Object> loadApply(ResponseForLoad resultload,
			String tradeMoney) {
		//	初始化圈存返回数据	ED或EP余额、联机交易序号、密钥版本号、算法标识、伪随机数、MAC1。可通过ResponseForLoad对象get得到。
		//	ED或EP余额余额
		byte[] balance = resultload.Getbalance();
		//	电子钱包充值计数
		byte[] tradeSN = resultload.GettransactionSN();
		//	伪随机数
		byte[] radom = resultload.GetpseudoRandomnumber();
		//	获取mac1
		byte[] mac1  = resultload.GetMAC1();
		
		//	需要将 电子钱包充值计数, 伪随机数, mac1都转化成Hex String
		String random_str = Method.bytesToHexString(radom);
		String mac1_str = Method.bytesToHexString(mac1);
		loadCount = Method.bytesToHexString(tradeSN);
		//	将交易前金额转化成string
		balance_str= Method.bytesToHexString(balance);
		//	交易金额
		String _tradeMoney = moneyToHexString(tradeMoney);
		
		//	获取当前时间
		currentDateTime_mac2 = getCurrentDateOrTime("yyyyMMddHHmmss");
		Map<String, Object> map_loadApplyResult = YjtLoadRequestEngine.loadApply("1", "01", 
				applicationSN, "01", balance_str, _tradeMoney, consumeCount, 
				loadCount, currentDateTime_mac2, 
				random_str, mac1_str, tradeNum, TerminalDataManager.getPlug_phoneNum(), XML_MSGID_YJT_LOAD);
		return map_loadApplyResult;
	}

	@Override
	public ApduResponse creditForLoad(String MAC2) {
		//	根据申请Mac2的时候currentDateTime_mac2计算日期和时间
		String currDateStr = currentDateTime_mac2.substring(0, 8);
		String currTimeStr = currentDateTime_mac2.substring(8, currentDateTime_mac2.length());
		//	5.执行圈存指令  进行写卡充值  	参数: 交易日期, 交易时间, 平台返回的MAC2
		return edepAdapter.CreditForLoad(Method.str2bytes(currDateStr), Method.str2bytes(currTimeStr), Method.str2bytes(MAC2));
	}

	@Override
	public Boolean sendTAC(String applicationSN, String consumeCount,
			String loadCount, String TAC, String tranResultFlag, String tradeNum) {
		return null;
	}

	@Override
	public void noticeYJT() {
		
	}
	
	

	/**
	 * 将钱数 转化成十六进制字符串
	 * @param tradeMoney
	 * @return
	 */
	private String moneyToHexString(String tradeMoney) {
		String money = Integer.toHexString(Integer.parseInt(tradeMoney));
		int i = money.length();
		StringBuffer sb = new StringBuffer();
		for (int j = 8 - i; j > 0; j--) {
			sb.append("0");
		}
		sb.append(money);
		return sb.toString();
	}
	
	
	/**
	 * 根据pattern返回当前的日期或者时间
	 * @param pattern 日期:"yyyyMMdd"  时间:"HHmmss"
	 * @return
	 */
	private String getCurrentDateOrTime(String pattern) {
		String date = "";
		SimpleDateFormat sdFormat = new SimpleDateFormat(pattern);
		Calendar calendar = Calendar.getInstance();
		date = sdFormat.format(calendar.getTime());
		return date;
	}
	
	
	/**
	 * 获取上次交易的TAC
	 * @return
	 */
	public String getPreTAC() {
		//	1.初始化翼机通卡
		initEjitongCard();
		String tac = "";
		//	进行一次0元充值  获取联机交易序号
		ResponseForLoad resp = initLoad("0");
		String transactionNum = Method.bytesToHexString(resp.GettransactionSN());
		//	拼装apdu指令 02 : EP电子钱包
		String apdu = "80" + "5A" + "00" + "02" + "02" + transactionNum + "08";
		ApduResponse response = edepAdapter.sendapdu(apdu);
		String respCode = Method.byte2HexString(response.getSw1()) + Method.byte2HexString(response.getSw2());
		if("9000".equals(respCode)) {
			//	获取TAC
			byte[] all_byte = response.getAllBytes();
			tac = getTACFromBytes(all_byte, 4, 4);
		}
		return tac;
	}
	
	
//	public void reUploadTac() {
//		try {
//			String tac = getPreTAC();
//			String xml = getLoadNoticeXml(applicationSN, consumeCount, loadCount, 
//					tac, "0x99", "000000140000", TerminalDataManager.getPlug_phoneNum(), balance_str);
//			Map<String, Object> responseMap = HttpPostHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS,
//					xml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
//			responseMap = XmlMgr_BusTradeBase.parse((String) responseMap
//					.get(MAP_HTTP_RESPONSE_RESPONSE));
//			if(!"0000".equals( responseMap.get("RespCode")) ||"NTER".equals( responseMap.get("RespCode"))) {
//				System.out.println("==wb=====TAC报文发送失败");
//			} else {
//				System.out.println("==wb=====TAC报文发送成功");				
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(edepAdapter != null) {
//				edepAdapter.release();
//			}
//		}
//	}
	
	private String getTACFromBytes(byte[] bytes, int start, int length) {
		String tac = "";
		for (int i = start; i < start + length; i++) {
			tac = tac + Method.byte2HexString(bytes[i]);
		}
		return tac;
	}
	
	@Override
	public String getCardLoadCount() {
		byte[] tradeSN = null;
		try {			
			//	1.初始化翼机通卡
			initEjitongCard();
			//	2.初始化圈存
			ResponseForLoad responseForLoad = initLoad("0");
			//	电子钱包充值计数
			tradeSN = responseForLoad.GettransactionSN();
		} catch(Exception e) {
			logger.printLogOnSDCard("获取电子钱包计数失败:" + e.getStackTrace().toString());
		} catch(Throwable thr) {
			thr.printStackTrace();
			logger.printLogOnSDCard("获取电子钱包计数失败:" + thr.getStackTrace().toString());
		} finally {
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		}
		return Method.bytesToHexString(tradeSN);
	};
	
	/**
	 * 获取卡片余额
	 * @return
	 */
	@Override
	public String getCardBalance() {
		String balance = null;
		try {			
			//	1.初始化翼机通卡
			initEjitongCard();
			balance = "" + edepAdapter.GetBalance();
		} catch(Exception e) {
			logger.printLogOnSDCard("获取卡余额exception" + e.toString());
			e.printStackTrace();
			balance = "";
		} catch(Throwable e) {
			logger.printLogOnSDCard("获取卡余额throwable" + e.toString());
			e.printStackTrace();
			balance = "";
		}
		finally {
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		}
		return balance;
	}
	
	/**
	 * 判断用户是否是给swp卡充值
	 * @return
	 */
	public boolean isSwpCard() {
		logger.printLogOnSDCard("判断是否是swp卡");
		
		// 先判断手机是否支持nfc
		NfcAdapter adpater = NfcAdapter.getDefaultAdapter(context);
		if(adpater == null) {	// 如果不支持nfc直接返回false只支持白卡充值
			return false;
		}
		
		ChannelOpenmobileApi m_channel = new ChannelOpenmobileApi();
		Channel channel = null;
		try {
			// 选择应用
			channel = m_channel.init("D1560000401000300000000200000000", context);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.printLogOnSDCard("判断是否是swp卡发生exception" + e.toString());
		} catch(Throwable thr) {
			thr.printStackTrace();
			logger.printLogOnSDCard("判断是否是swp卡发生exception" + thr.toString());
		} finally {
			if(m_channel != null) {				
				m_channel.release();
			}
			if(channel == null) {
				// 说明是大白卡
				return false;
			}
		}
		// 是swp卡
		return true;
	}

	
	public String getCardBalanceHex() {
		String balance = null;
		try {			
			//	1.初始化翼机通卡
			initEjitongCard();
			ResponseForLoad responseForLoad = initLoad("0");
			byte[] bytes_balance = responseForLoad.Getbalance();
			balance = Method.bytesToHexString(bytes_balance);
		} finally {
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		}
		return balance;
	}
	
	/**
	 * 判断是否有未上传的交易记录
	 * 有则上传
	 * @return true 表示没有需要上传的数据
	 * 		   false 表示有需要上传的数据
	 */
	private boolean hasNoErrorTradeRecord() {
		int i = 0;
		//	申请MAC2之前需要判断 数据库中 是否有  type为1的记录, type=1表示MAC2获取成功, 但是写卡失败, 如果有则必须上传成功才能继续充值
		PConfirmDao dao = new PConfirmDao();
		int nums_needConfirm = dao.subMitConfirm("1");
		while (nums_needConfirm > 0) {
			//	如果有MAC2获取成功 但最终结果没有通知到服务器的记录 就继续上传
			nums_needConfirm = dao.subMitConfirm("1");
			i++;
			if(i >= 30) {
				return false;
			}
		}
		i = 0;
		//	判断是否有极特殊情况(获取到MAC2  但是没有来得及发送 确认通知  程序就退出了, 这样不知道是否写卡成功)
		PIsLoadSuccessDao extremeDao = new PIsLoadSuccessDao();
		boolean flag = extremeDao.subExtremeRecord(context);
		while(!flag) {
			flag = extremeDao.subExtremeRecord(context);
			i++;
			if(i >= 30) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean interactionBetweenPhoneAndCard(String tradeMoney) {
	
		boolean bool_errorTradeRecord = hasNoErrorTradeRecord();
		if(bool_errorTradeRecord) {
			try {
				logger.printLogOnSDCard("机卡通道初始开始");
				//	1.初始化翼机通卡
				initEjitongCard();
				logger.printLogOnSDCard("机卡通道初始完毕   interactionbetweenphoneandcard" + edepAdapter);
				logger.printLogOnSDCard("圈存初始化开始");
				//	2.初始化圈存
				ResponseForLoad responseForLoad = initLoad(tradeMoney);
				logger.printLogOnSDCard("圈存初始完毕   interactionbetweenphoneandcard" + edepAdapter);
				logger.printLogOnSDCard("圈存申请开始");
				//	3.访问平台进行圈存申请
				Map<String, Object> map_loadApplyResult = loadApply(responseForLoad, tradeMoney);
				logger.printLogOnSDCard("圈存申请完毕   interactionbetweenphoneandcard" + edepAdapter);
				
				//	4.判断圈存结果是否正确
				//	判断圈存请求是否正常
				if(map_loadApplyResult == null || !"0000".equals(map_loadApplyResult.get("RespCode"))) {	//	4个字符  发送失败通知
					Log.i(log_activity, "==wb======获取MAC2失败");
					logger.printLogOnSDCard("圈存申请完毕, 获取MAC2失败");
					//	未得到服务器端的圈存申请结果通知, 或者 圈存申请结果通知有误 则发送充值失败通知 (要确保服务器收到通知, 重试3次, 仍不行则存储到数据库)
					//	得到xml报文	(0x01 表示异常交易记录, 0x99表示正常记录)
					String xml = getLoadNoticeXml(applicationSN, consumeCount, loadCount, 
							"", "0x01", tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str);
					//	type 
					new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_NO_MAC2, tradeNum)).start();
					//	设置错误信息
					ResponseExceptionInfo.setErrorMsg(ERROR_GET_MAC2_FAILD_MSG);
					ResponseExceptionInfo.setErrorCode(ERROR_GET_MAC2_FAILD);
					return false;
				} else {
					logger.printLogOnSDCard("圈存申请完毕, 获取MAC2成功");
					//	5.如果圈存请求正常  及顺利拿到平台返回的mAC2  则进行写卡充值
					//	从返回结果中拿到mac2
					String MAC2 = (String) map_loadApplyResult.get("YJTRechargeMAC2");
					//	获取MAC2成功 写卡之前 先将数据写入数据库, 防止程序异常退出导致不能进行充值(充值前余额, 充值计数器等数据存入数据库)
					//	需要存放的数据 applicationSN 交易序列号, consumeCount 消费计数器, loadCount 充值计数器, TAC, tradeNum 交易流水号, phone电话号码, balance_str交易前金额, type
					item = getIsLoadSuccessItem(applicationSN, balance_str, consumeCount, loadCount, TerminalDataManager.getPlug_phoneNum(), "", tradeNum, "");
					addIsLoadSuccessItem(item);
					
					//	进行写卡充值
					ApduResponse apduResp = null;
					try {
						logger.printLogOnSDCard("写卡前");
						apduResp = creditForLoad(MAC2);
						logger.printLogOnSDCard("写卡后");
					} catch (Exception e) {
						e.printStackTrace();
						logger.printLogOnSDCard("写卡出现异常==" + e.toString());
						//	写卡出现异常需要向服务器发送失败通知
						String xml = getLoadNoticeXml(applicationSN, consumeCount, loadCount, 
								"", "0x01", tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str);
						new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
						//	设置错误信息
						ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_GET_MAC2_SUCCED_LOAD_FAILD);
						ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_GET_MAC2_SUCCED_LOAD_FAILD_MSG);
						return false;
					}
					//	写卡结束 不论是否写卡成功都将addIsLoadSUccessItem方法中添加的那条记录删除
					delIsLoadSuccessItem(item);
					
					//	判断写卡是否成功  ???
					String response = Method.byte2HexString(apduResp.getSw1()) + Method.byte2HexString(apduResp.getSw2());
					if("9000".equals(response)) {
						//	成功
						//	如果写卡成功则上送充值TAC
						byte[] tac = apduResp.getBytes();
						String tac_str = Method.bytesToHexString(tac);
						
						logger.printLogOnSDCard("上送tag前");
						//	上送TAC
						Map<String, Object> map_loadResultConfirm = YjtLoadRequestEngine.loadResultConfirm(applicationSN, consumeCount, loadCount, tac_str, 
								"0x99", //	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
								tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str, XML_MSGID_YJT_LOADCONFIRM);
						logger.printLogOnSDCard("上送tag后");
						
						//	对充值结果确认请求进行判断
						if(map_loadResultConfirm == null || !"0000".equals(map_loadResultConfirm.get("RespCode"))) {
							//	确认失败  此处客户端要确保平台收到了TAC, 如遇到网络问题则将数据存储在客户端, 定时上送 
							//	获取xml报文
							String xml = getLoadNoticeXml(applicationSN, consumeCount, loadCount, 
									tac_str, "0x99", tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str);
							new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
							
							//	写log
							logger.printLogOnSDCard("TAC上送失败" + map_loadResultConfirm.toString());
						} 
						logger.printLogOnSDCard("TAC上送成功" + map_loadResultConfirm.toString());
						//	返回用户确认成功
						return true;
					} else {
						//	设置错误信息
						ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_GET_MAC2_SUCCED_LOAD_FAILD);
						ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_GET_MAC2_SUCCED_LOAD_FAILD_MSG);
						//	写卡失败
						//	上传失败报文:
						Map<String, Object> map_loadResultConfirm = YjtLoadRequestEngine.loadResultConfirm(applicationSN, consumeCount, loadCount, "", 
								"0x01", //	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
								tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str, XML_MSGID_YJT_LOADCONFIRM);
						if(map_loadResultConfirm == null || !"0000".equals(map_loadResultConfirm.get("RespCode"))) {
							//	确认失败  此处客户端要确保平台收到了TAC, 如遇到网络问题则将数据存储在客户端, 定时上送 
							//	获取xml报文
							String xml = getLoadNoticeXml(applicationSN, consumeCount, loadCount, 
									"", "0x01", tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str);
							new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
							//	写log
							logger.printLogOnSDCard("TAC无获取充值失败报文上送失败  写卡的response=" + response + map_loadResultConfirm.toString());
						}
						logger.printLogOnSDCard("TAC无获取充值失败报文TAC上送成功 写卡的response=" + response + map_loadResultConfirm.toString());
						return false;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
				logger.printLogOnSDCard(e.getStackTrace().toString());
				return false;
			} catch(Throwable thr) {
				thr.printStackTrace();
				logger.printLogOnSDCard(thr.getStackTrace().toString());
				return false;
			} finally {
				//	释放资源
				if(edepAdapter != null) {
					edepAdapter.release();
				}
			}
		}
		return false;
	}
	
	
	/**
	 * @param applicationSN
	 * @param balance
	 * @param consumeCount
	 * @param loadCount
	 * @param phone
	 * @param tac	
	 * @param tradeNum
	 * @param tranResultFlag	是否成功
	 * @return
	 */
	private IsLoadSuccessItem getIsLoadSuccessItem(String applicationSN, String balance, String consumeCount, String loadCount,
			String phone, String tac, String tradeNum, String tranResultFlag) {
		IsLoadSuccessItem _item = new IsLoadSuccessItem();
		_item.setApplicationSN(applicationSN);
		_item.setBalance(balance);
		_item.setConsumeCount(consumeCount);
		_item.setLoadCount(loadCount);
		_item.setPhone(phone);
		_item.setTac(tac);
		_item.setTradeNum(tradeNum);
		_item.setTranResultFlag(tranResultFlag);
		_item.setMd5();
		return _item;
	}
	
	private int delIsLoadSuccessItem(IsLoadSuccessItem item) {
		Log.i(log_activity, "==wb======addIsLoadSuccessItem==将获取到MAC2的信息从数据库删除=" + item);
		int retVal = new PIsLoadSuccessDao().delIsLoadSuccessItem(item);
		Log.i(log_activity, "==wb======addIsLoadSuccessItem==将获取到MAC2的信息删除完毕=" + item);
		return retVal;
	}
	
	private void addIsLoadSuccessItem(IsLoadSuccessItem item) {
		
		Log.i(log_activity, "==wb======addIsLoadSuccessItem==将获取到MAC2的信息写入数据库备份=" + item);
		new PIsLoadSuccessDao().addIsLoadSuccessItem(item);
		Log.i(log_activity, "==wb======addIsLoadSuccessItem==将获取到MAC2的信息写入数据库备份完毕=" + item);
	}
	
	/**
	 * 判断是否有极特殊情况(获取到MAC2  但是没有来得及发送 确认通知  程序就退出了, 这样不知道是否写卡成功)
	 * @param tradeMoney
	 */
	/*private void subExtremeRecord(String tradeMoney) {
		PIsLoadSuccessDao isDao = new PIsLoadSuccessDao();
		List<IsLoadSuccessItem> list_isLoadSuccessItem = isDao.queryUnConfirmed();
		if(list_isLoadSuccessItem.size() > 0) {
			//	如果有极端情况, 这种情况只可能有一个
			IsLoadSuccessItem item = list_isLoadSuccessItem.get(0);
			String temp_balance = item.getBalance();
			String temp_loadCount = item.getLoadCount();
			//	与从卡片中读取的充值计数器, 和 卡内余额 进行比较  
			if(temp_balance.equals(balance_str) && temp_loadCount.equals(loadCount)) {
				//	如果相同则表示充值失败 , 需要发送失败通知
				String xml = getLoadNoticeXml(item.getApplicationSN(), item.getConsumeCount(), item.getLoadCount(), 
						item.getTac(), "0x01", item.getTradeNum(), item.getPhone(), item.getBalance());
				new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
				//	写log
				WriteLog.writeLogOnSDCard("上送获取到MAC2  但是没有来得及发送 确认通知  程序就退出后出现的问题---充值失败");
			} else {
				//	充值成功, 发送成功确认通知
				//	首先获取TAC
				String temp_tac = getPreTAC();
				//	拼接成功报文
				String xml = getLoadNoticeXml(item.getApplicationSN(), item.getConsumeCount(), item.getLoadCount(), 
						temp_tac, "0x99", item.getTradeNum(), item.getPhone(), item.getBalance());
				new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
				WriteLog.writeLogOnSDCard("上送获取到MAC2  但是没有来得及发送 确认通知  程序就退出后出现的问题---充值成功");
			}
		}
	}*/
	
	/**
	 * 获取充值结果通知xml字符串  , 用于异常处理时向服务器发送的xml报文
	 * 成功 0x99
	 * 失败0x01
	 * @param applicationSN	应用序列号
	 * @param consumeCount	电子钱包消费序号
	 * @param loadCount	电子钱包充值序号
	 * @param TAC	
	 * @param tranResultFlag	交易结果标识 0x01表示异常交易记录, 0x99表示正常记录
	 * @param tradeNum	交易流水号
	 * @param phoneNum	电话号码
	 * @param transBeginAmount	交易前金额
	 * @return
	 */
	public String getLoadNoticeXml(final String applicationSN, final String consumeCount, final String loadCount, 
			final String TAC, final String tranResultFlag, final String tradeNum, final String phoneNum, final String transBeginAmount) {
		return new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				//	手机的ICCID
				objMgr.addText("CARD_SERIAL", applicationSN);
				objMgr.addText("CONSUME_TIMES", consumeCount);
				objMgr.addText("RECHARGE_TIMES", loadCount);
				//	服务器端写死  "0"
//				objMgr.addText("TERMINAL_NO", terminalNo);
				objMgr.addText("TAC", TAC);
				objMgr.addText("TRAN_RESULT_FLAG", tranResultFlag);
				objMgr.addText("TradeNum", tradeNum);
				objMgr.addText("YJTPhone", phoneNum);
				objMgr.addText("TRANS_BEGIN_AMOUNT", transBeginAmount);
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_LOADCONFIRM;
			}
		}.get();
	}
	
	public class  ConfirmTask implements TaskAndCallback {
		Map<String, Object> responseMap = null;
		String xml;
		String money;
		Date date;
		String type;
		String tradeNum;
		public ConfirmTask(String xml,String money,Date date,String type){
			this.xml = xml;
			this.money = money;
			this.date = date;
			this.type = type;
		}
		/**
		 * @param xml	发送的xml字符串
		 * @param money	金额
		 * @param date	时间
		 * @param type	类型	0表示获取MAC2失败	1写卡失败	2表示写卡成功
		 * @param tradeNum	交易流水号
		 */
		public ConfirmTask(String xml, String money, Date date,
				String type, String tradeNum) {
			this(xml,money,date,type);
			this.tradeNum = tradeNum;
		}
		@Override
		public void task() {
			responseMap = HttpPostHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS,
					xml, ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
			
			Log.i(log_activity, "==wb======ConfirmTask==task===type=" + type);
		}
		
		@Override
		public void success() {
			logger.printLogOnSDCard("异步任务发送成功   type = " + type);
			Log.i(log_activity, "==wb======ConfirmTask==task===type=" + type + "==发送成功==");
		}
		
		@Override
		public boolean issuccess() {//TODO 是否成功需要和后台确认
			if(responseMap != null){
				responseMap = XmlMgr_BusTradeBase.parse((String) responseMap
						.get(MAP_HTTP_RESPONSE_RESPONSE));
				if(!("0000".equals( responseMap.get("RespCode"))||"NTER".equals( responseMap.get("RespCode")))) {
					return false;//服务器端非正常处理结果
				}
				return true;
			}
			return false;
		}
		
		@Override
		public void failure() {
			Log.i(log_activity, "==wb======ConfirmTask==task===type=" + type + "==发送失败==");
			logger.printLogOnSDCard("异步任务发送失败  type = " + type);
			
			//存储确认记录
			ConfirmItem item = new ConfirmItem();
			item.setCity(ContextUtil.CITYCODE);
			item.setType(this.type);
			item.setStat("0");
			item.setTrade_money(money);
			item.setTrade_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
			item.setXml(xml);
			item.setTradeNum(tradeNum);
			new PConfirmDao().add(item);//添加交易失败记录
			
			//	启动service定时读取数据库上传失败的记录
			Log.i("ImpYJTCardProcessor", "==wb===startService UploadConfirmService.class====");
			Intent intent = new Intent(context, UploadConfirmService.class);
			context.startService(intent);
		}
	}
	
	
	public String noLoadSuccessConfirm(String tradeMoney) {
		//	发送确认报文
		Map<String, Object> map_loadResultConfirm = YjtLoadRequestEngine.noLoadConfirm("0x99", //	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
				tradeNum, TerminalDataManager.getPlug_phoneNum());
		
		//	对充值结果确认请求进行判断  如果收到的确认报文是不成功, 则说服务器发送确认报文也失败了, 则发送失败确认报文
		if(map_loadResultConfirm != null && !"0000".equals(map_loadResultConfirm.get("RespCode"))) {
			logger.printLogOnSDCard("只划款, 不充值, 确认报文上送失败, 发送失败报文");
			
			if("F905".equals(map_loadResultConfirm.get("RespCode"))) {
				// F905 服务器后台会持续通知, 客户端不用做处理
				return ResultConstant.SUCCESS_CONFIRM_FAILED_F905;
			} else if("F904".equals(map_loadResultConfirm.get("RespCode"))) {
				// F904 客户端发起失败确认
				// 失败, 发送失败报文, 退款获取xml报文
				Map<String, Object> map_refund = YjtLoadRequestEngine.noLoadConfirm("0x01", //	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
						tradeNum, TerminalDataManager.getPlug_phoneNum());
				if(map_refund != null) {
					logger.printLogOnSDCard("只划款, 不充值, 发送失败报文成功");
					// 说明服务器收到失败确认
					return ResultConstant.REFUND_SUCCESS;
				} else {
					logger.printLogOnSDCard("只划款, 不充值, 发送失败报文失败");
					String xml = getNoLoadNoticeXml("0x01", tradeNum, TerminalDataManager.getPlug_phoneNum(), false);
					new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_NO_NEED_WRITECARD, tradeNum)).start();
					return ResultConstant.REFUND_FAILED;
				}
			}
		} else if(map_loadResultConfirm == null) {
			// 没有响应, 则继续发送成功报文	 获取xml报文
			String xml = getNoLoadNoticeXml("0x99", tradeNum, TerminalDataManager.getPlug_phoneNum(), true);
			new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_NO_NEED_WRITECARD, tradeNum)).start();
			
			//	写log
			logger.printLogOnSDCard("只划款, 不充值, 确认报文上送失败");
			return ResultConstant.SUCCESS_CONFIRM_FAILED;
		}
		logger.printLogOnSDCard("只划款, 不充值, 确认报文上送成功");
		return ResultConstant.SUCCESS_CONFIRM_SUCCESS;
	}
	
	
	/**
	 * 失败确认
	 * @param tradeMoney
	 * @return
	 */
	public String noLoadFailedConfirm(String tradeMoney) {
		//	发送确认报文
		Map<String, Object> map_loadResultConfirm = YjtLoadRequestEngine.noLoadConfirm("0x01", //	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
				tradeNum, TerminalDataManager.getPlug_phoneNum());
		
		//	失败确认服务器已收到, 不管responseCode是否是0000都不用在发请求
		if(map_loadResultConfirm != null) {
			logger.printLogOnSDCard("只划款, 不充值, 失败确认报文上送成功");
			return ResultConstant.FAILED_CONFIRM_SUCCESS;
		} else {
			// 没有响应, 则继续发送失败确认报文  获取xml报文
			String xml = getNoLoadNoticeXml("0x01", tradeNum, TerminalDataManager.getPlug_phoneNum(), false);
			new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_NO_NEED_WRITECARD, tradeNum)).start();
			
			//	写log
			logger.printLogOnSDCard("只划款, 不充值, 失败确认报文上送失败");
			return ResultConstant.FAILED_CONFIRM_FAILED;
		}
	}
	
	
	/**
	 * 获取充值结果通知xml字符串  , 用于异常处理时向服务器发送的xml报文
	 * 成功 0x99
	 * 失败0x01
	 * @param applicationSN	应用序列号
	 * @param consumeCount	电子钱包消费序号
	 * @param loadCount	电子钱包充值序号
	 * @param TAC	
	 * @param tranResultFlag	交易结果标识 0x01表示异常交易记录, 0x99表示正常记录
	 * @param tradeNum	交易流水号
	 * @param phoneNum	电话号码
	 * @param transBeginAmount	交易前金额
	 * @return
	 */
	public String getNoLoadNoticeXml(final String tranResultFlag, final String tradeNum, final String phoneNum, final boolean isNeedSMS) {
		return new XmlMgr_BusTradeBase(ContextUtil.CITYCODE) {
			@Override
			protected void initContent(XmlHandler objMgr) {
				//	手机的ICCID
				objMgr.addText("TRAN_RESULT_FLAG", tranResultFlag);
				objMgr.addText("TradeNum", tradeNum);
				objMgr.addText("YJTPhone", phoneNum);
				if(isNeedSMS) {					
					// 这个字段用于通知服务器接到通知后, 给用户发送短信通知, 用户可以到充值机充值
					objMgr.addText("ExpInform", "1");
				}
			}
			@Override
			protected String getMsgId() {
				return XML_MSGID_YJT_NOLOADCONFIRM;
			}
		}.get();
	}
}
