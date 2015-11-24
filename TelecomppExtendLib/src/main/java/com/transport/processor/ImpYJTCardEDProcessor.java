
package com.transport.processor;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import cn.com.ctbri.yijitong.edep.ApduResponse;
import cn.com.ctbri.yijitong.edep.EjitongCard;
import cn.com.ctbri.yijitong.edep.Method;
import cn.com.ctbri.yijitong.edep.ResponseForLoad;

import com.telecompp.ContextUtil;
import com.telecompp.engine.YjtLoadRequestEngine;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.ResponseExceptionInfo;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.TerminalDataManager;
import com.telecompp.util.WriteLog;
import com.telecompp.xml.XmlHandler;
import com.telecompp.xml.XmlMgr_BusTradeBase;
import com.transport.RetryTask;
import com.transport.RetryTask.TaskAndCallback;
import com.transport.db.bean.ConfirmItem;
import com.transport.db.bean.IsLoadSuccessItem;
import com.transport.db.dao.PConfirmDao;
import com.transport.db.dao.PIsLoadSuccessDao;
import com.transport.service.UploadConfirmService;

public class ImpYJTCardEDProcessor implements IUIMCardProcessor, SumaConstants{
	
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
	
	private static LoggerHelper logger = new LoggerHelper(ImpYJTCardEDProcessor.class);
	
	public ImpYJTCardEDProcessor(Context context, String tradeNum) {
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
		}
	}

	@Override
	public ResponseForLoad initLoad(String tradeMoney) {
		ResponseForLoad resultload = null;
		try {
			//	获取翼机通卡片序列号   圈存请求的时候使用
			applicationSN = edepAdapter.GetApplicationofSN();
			//	获取电子存折消费计数
			String APDU = "8050"+"0101"+"0B"+"01"+"00000000"+"000000000000"+"0F";
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
				logger.printLogOnSDCard("初始化圈存失败, 金额转化成int异常:" + e.toString());
			}
			//	4.初始化圈存 对应的参数 type:ED 01, EP 02; secrtID: 密钥索引号; cash:交易金额; terminalID:终端机编号  (0 + 电话号码 )
			resultload = edepAdapter.init_load((byte)0x01, (byte)0x01, _tradeMoney, Method.hexStringToByte("0" + TerminalDataManager.getPlug_phoneNum()));
		} catch (Exception e) {
			//	设置错误信息
			ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_INITLOAD_EXCEPTION);
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_INITLOAD_EXCEPTION_MSG);
			e.printStackTrace();
			logger.printLogOnSDCard("初始化圈存失败:" + e.toString());
		}
		return resultload;
	}
	
	
	/**
	 * 消费初始化
	 * @param balance
	 * @return
	 */
	public ApduResponse initConsume(String balance) {
		ApduResponse apduResponse = null;
		try {
			//	获取翼机通卡片序列号   圈存请求的时候使用
			applicationSN = edepAdapter.GetApplicationofSN();
			//	获取电子存折消费计数
			String APDU = "8050"+"0101"+"0B"+"01"+"00000000"+"000000000000"+"0F";
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
				_tradeMoney = Integer.parseInt(balance);
			} catch (Exception e) {
				_tradeMoney = 0;
				e.printStackTrace();
				logger.printLogOnSDCard("初始化圈存失败, 金额转化成int异常:" + e.toString());
			}
			
			
			// 1.初始化消费
			// 申请11个字节的缓冲区
			ByteBuffer buff = ByteBuffer.allocate(11);
			// 消费金额
		    byte[] dealcash = Method.intToByteArray(_tradeMoney);
		    buff.put((byte)0x01)	// 密钥索引号
		      .put(dealcash)	// 消费金额
		      .put(Method.hexStringToByte("0" + TerminalDataManager.getPlug_phoneNum())); // 终端机编号
			// 初始化消费的apdu
			String apdu = "80" + "50" + "01" + "01" + "0B" + "01" + Method.bytesToHexString(buff.array()) + "0F";
			apduResponse = edepAdapter.sendapdu(apdu);
			
		} catch (Exception e) {
			//	设置错误信息
			ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_INITLOAD_EXCEPTION);
			ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_INITLOAD_EXCEPTION_MSG);
			e.printStackTrace();
			logger.printLogOnSDCard("初始化圈存失败:" + e.getStackTrace().toString());
		}
		return apduResponse;
	}
	

	@Override
	public Map<String, Object> loadApply(ResponseForLoad resultload,
			String tradeMoney) {
		//	初始化圈存返回数据	ED或EP余额、联机交易序号、密钥版本号、算法标识、伪随机数、MAC1。可通过ResponseForLoad对象get得到。
		//	ED或EP余额余额
		byte[] balance = resultload.Getbalance();
		//	电子存折充值计数
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
		
		// 这里需要判断是否需要进行冲零请求
		boolean is0ApplyCorrect = load0Apply(balance_str, consumeCount, loadCount, currentDateTime_mac2, random_str);
		if(!is0ApplyCorrect) {
			return null;
		}
		
		// 补贴钱包的圈存申请
		Map<String, Object> map_loadApplyResult = YjtLoadRequestEngine.loadApply("2", "01", 
				applicationSN, "02", balance_str, _tradeMoney, consumeCount, 
				loadCount, currentDateTime_mac2, 
				random_str, mac1_str, tradeNum, TerminalDataManager.getPlug_phoneNum(), XML_MSGID_YJT_ED_LOAD);
		return map_loadApplyResult;
	}

	@Override
	public ApduResponse creditForLoad(String MAC2) {
		//	根据申请Mac2的时候currentDateTime_mac2计算日期和时间
		String currDateStr = currentDateTime_mac2.substring(0, 8);
		String currTimeStr = currentDateTime_mac2.substring(8, currentDateTime_mac2.length());
		//	5.执行圈存指令  进行写卡充值  	参数: 交易日期, 交易时间, 平台返回的MAC2
		/*ByteBuffer bytebuff = ByteBuffer.allocate(11);
		bytebuff.put(Method.str2bytes(currDateStr))
		.put(Method.str2bytes(currTimeStr))
		.put(Method.str2bytes(MAC2));*/
		
		// 圈存指令
//		String apdu = "80" + "52" + "00" + "00" + "0B" + Method.bytesToHexString(bytebuff.array()) + "04";
		
		
		
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
			logger.printLogOnSDCard("获取电子钱包计数失败:" + e.toString());
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
//			balance = "" + edepAdapter.GetBalance();
			
			System.out.println("========================getEdBalance==" + edepAdapter.GetEdBalance());
			
			// verify 校验个人识别码的正确性
			byte[] pin = new byte[]{0x00, 0x00, 0x00};
			String str_pin = Method.bytesToHexString(pin);
			System.out.println("=======str_pin==" + str_pin + "---");
			String apdu_verify = "00200000" + "03" + str_pin;
			ApduResponse _response = edepAdapter.sendapdu(apdu_verify);
			byte[] _result = _response.getBytes();
			short _result_short = _response.getSw12();
			byte _sw1 = _response.getSw1();
			byte _sw2 = _response.getSw2();
			System.out.println("============_result==" + _result + "--=_result_short==" + _result_short + "--=_sw1==" + Method.byte2HexString(_sw1) + "--=_sw2==" + Method.byte2HexString(_sw2) + "--===");
			
			
			String apdu = "80" + "5C" + "00" + "01" + "04";
			ApduResponse response = edepAdapter.sendapdu(apdu);
			byte[] result = response.getBytes();
			short result_short = response.getSw12();
			byte sw1 = response.getSw1();
			byte sw2 = response.getSw2();
			
			
//			System.out.println("=========result==" + Method.byteArrayToInt(result, result.length) + "---");
			System.out.println("============result==" + Method.bytes2Int(result) + "--=result_short==" + result_short + "--=sw1==" + Method.byte2HexString(sw1) + "--=sw1==" + Method.byte2HexString(sw2) + "--===");
			
		} finally {
			if(edepAdapter != null) {
				edepAdapter.release();
			}
		}
		return balance;
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
				//	1.初始化翼机通卡
				initEjitongCard();
				
				//	2.初始化圈存
				ResponseForLoad responseForLoad = initLoad(tradeMoney);
				//	3.访问平台进行圈存申请
				Map<String, Object> map_loadApplyResult = loadApply(responseForLoad, tradeMoney);
				
				//	4.判断圈存结果是否正确
				//	判断圈存请求是否正常
				if(map_loadApplyResult == null || !"0000".equals(map_loadApplyResult.get("RespCode"))) {	//	4个字符  发送失败通知
					Log.i(log_activity, "==wb======获取MAC2失败");
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
						apduResp = creditForLoad(MAC2);
					} catch (Exception e) {
						e.printStackTrace();
						//	写卡出现异常需要向服务器发送失败通知
						String xml = getLoadNoticeXml(applicationSN, consumeCount, loadCount, 
								"", "0x01", tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str);
						new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
						//	设置错误信息
						ResponseExceptionInfo.setErrorCode(SumaConstants.ERROR_GET_MAC2_SUCCED_LOAD_FAILD);
						ResponseExceptionInfo.setErrorMsg(SumaConstants.ERROR_GET_MAC2_SUCCED_LOAD_FAILD_MSG);
						//	写log
						logger.printLogOnSDCard(e.getStackTrace().toString());
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
						
						//	上送TAC
						Map<String, Object> map_loadResultConfirm = YjtLoadRequestEngine.loadResultConfirm(applicationSN, consumeCount, loadCount, tac_str, 
								"0x99", //	交易结果标志(0x01 表示异常交易记录, 0x99表示正常记录)
								tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str, XML_MSGID_YJT_ED_LOADCONFIRM);
						
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
								tradeNum, TerminalDataManager.getPlug_phoneNum(), balance_str, XML_MSGID_YJT_ED_LOADCONFIRM);
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
	 * 判断是否需要进行冲零
	 */
	private boolean load0Apply(String balance, String consumeCount, String loadCount, String tradeDate, String seudoRandomNumber) {
		// 冲零请求
		// 查看补贴的有效期
		String apdu_0apply = "00" + "B0" + "99" + "2D" + "04";
		ApduResponse rsp = edepAdapter.sendapdu(apdu_0apply);
		
		// 三个参数 分别是sfi, 偏移量 和 读取的字节数
//		edepAdapter.ReadBinary(Method.str2byte("25"), Method.str2byte("45"), Method.str2byte("04"));
	
		byte[] _result = rsp.getBytes();
		short _result_short = rsp.getSw12();
		byte _sw1 = rsp.getSw1();
		byte _sw2 = rsp.getSw2();
		System.out.println("==111==========_result==" + Method.bytesToHexString(_result) + "--=_result_short==" + Method.bytesToHexString(Method.short2Bytes(_result_short)) + "--=_sw1==" + Method.byte2HexString(_sw1) + "--=_sw2==" + Method.byte2HexString(_sw2) + "--===");
		String result = Method.byte2HexString(_sw1) + Method.byte2HexString(_sw2);
		if("9000".equals(result)) {
			// 读取补贴有效期成功
			String validityDuration = Method.bytesToHexString(_result);
			
			// 转化成时间, 判断是否需要进行冲零
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
			try {
				Date date_validity = simpleDateFormat.parse(validityDuration);
				Date date_current = new Date();
				// TODO 判断补贴是否过期  判断条件需要改动
//				if(date_validity.before(date_current)) {
				if(true) {
					// 需要进行冲零
					// 冲零请求
					Map<String, Object> map_0apply = YjtLoadRequestEngine.subsidyApply("01", applicationSN, balance, "0", consumeCount, loadCount, tradeDate, seudoRandomNumber, "", "", "", XML_MSGID_YJT_ED_0LOAD);
					
					if(map_0apply != null) {
						if(map_0apply.get("MAC1") != null && !"".equals(map_0apply.get("MAC1").toString())) {
							String mac1 = map_0apply.get("MAC1").toString();
							// 执行冲零命令, 将余额全部消费
							
							// 初始化消费指令
							ApduResponse apduResponse = initConsume(balance);
							
							// 获取终端交易序号
							byte[] consume_response = apduResponse.getAllBytes();
							// 电子存折余额
							byte[] _balance = new byte[4];
							for (int i = 0; i < _balance.length; i++) {
								_balance[i] = consume_response[i];
							}
							// 脱机交易序号
							byte[] transactionNum = new byte[2];
							for (int i = 0; i < _balance.length; i++) {
								transactionNum[i] = consume_response[i + 4];
							}
							
							// 交易日期
							byte[] transactionDate = Method.str2bytes(tradeDate.substring(0, 8));
							// 交易时间
							byte[] transactionTime = Method.str2bytes(tradeDate.substring(8, tradeDate.length()));
							
							// 执行消费指令
							// 申请11个字节的缓冲区
							ByteBuffer buff = ByteBuffer.allocate(15);
						    buff.put(transactionNum)	// 终端交易序号
						      .put(transactionDate)	// 交易日期4个字节
						      .put(transactionTime)	// 交易时间3个字节
						      .put(Method.str2bytes(mac1)); // MAC1
							
						    // apdu字符串
							String apdu = "80" + "54" + "01" + "00" + "0F" + Method.bytesToHexString(buff.array()) + "08";
							
							ApduResponse rsp_0apply = edepAdapter.sendapdu(apdu);
							byte sw1_0apply = rsp_0apply.getSw1();
							byte sw2_0apply = rsp_0apply.getSw2();
							String result_0apply = Method.byte2HexString(sw1_0apply) + Method.byte2HexString(sw2_0apply);
							if("9000".equals(result_0apply)) {
								return true;
							} else {
								return false;
							}
							
						} else {
							// 提示用户没有获取到mac1
							Toast.makeText(context, "没有获取到MAC1", Toast.LENGTH_LONG).show();
							return false;
						}
					} else {
						// 没有收到
						Toast.makeText(context, "网络异常!", Toast.LENGTH_LONG).show();
						return false;
					}
//					YjtLoadRequestEngine.subsidy0Apply(cardType, validityDuration, purseType, balance, tradeMoney, validityDuration, validityDuration, tradeDate, seudoRandomNumber, terminalNo);
					
				} else {
					// 不需要进行冲零
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
			
		} else {
			// 读取补贴有效期失败
			Toast.makeText(context, "没有获取到补贴的有效日期!", Toast.LENGTH_LONG).show();
			return false;
		}
		
		
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
				return XML_MSGID_YJT_ED_LOADCONFIRM;
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
			logger.printLogOnSDCard("==wb======ConfirmTask==task===type=" + type);
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
}
