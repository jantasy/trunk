package com.telecompp.engine;

import java.util.Map;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.telecompp.ContextUtil;
import com.telecompp.PayPlugHelp;
import com.telecompp.activity.ConfirmActivity;
import com.telecompp.util.DialogUtil;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.ResponseExceptionInfo;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaPostHandler;
import com.telecompp.util.TerminalDataManager;
import com.transport.ConvertUtil;
import com.transport.processor.ImpYJTCardProcessor;

/**
 * 支付接口
 * 
 * @author poet
 * 
 */
public class PaymentEngine {

	private Dialog dialog;
	private Context context;
	private Activity activity;
	private PayListener payListener = null;
	private static PaymentEngine instance;
	
	private static LoggerHelper logger = new LoggerHelper(ConfirmActivity.class);

	public static PaymentEngine getInstance(Context context,
			PayListener payListener) {
		instance = new PaymentEngine(context, payListener);
		if (payListener == null) {
			instance.payListener = new MyPayListener((FragmentActivity) context);
		}
		return instance;
	}

	public static PaymentEngine getInstance() {
		return instance;
	}

	private PaymentEngine(Context context, PayListener payListener) {
		ContextUtil.setInstance(context);
		this.payListener = payListener;
		this.context = context;
		this.activity = (Activity)context;
	};

	/**
	 * 进行支付
	 * 
	 * @return
	 */
	public interface PayListener {
		public void onError(int errorCode, String msg);

		public void onProcess(int process, String msg);

		public void onSuccess(int resultCode, String msg);
	}

	/**
	 * 
	 * @param phoneNum
	 *            电话号码
	 */
	public void pay(final String phoneNum) {
		
		TerminalDataManager.setPlug_phoneNum(phoneNum);

		try {
			logger.printLogOnSDCard("开始初始化环境");
			startInitPayEnv(phoneNum);
			logger.printLogOnSDCard("初始化环境完成");
		} catch (Exception e) {
			
			if (payListener != null) {
				payListener.onError(1, "支付环境初始化失败" + ResponseExceptionInfo.getErrorMsg());
			}
			logger.printLogOnSDCard("支付环境初始化失败("
					+ ResponseExceptionInfo.getErrorMsg() + ")" + "\n"
				+ "http请求返回错误码:" + ResponseExceptionInfo.getHttpResponseCode() + e.toString());
			return;
		} catch(Throwable thr) {
			logger.printLogOnSDCard("支付环境初始化失败("
					+ thr.toString());
			return;
		}
	}

	/**
	 * 
	 * @param phoneNum
	 */
	private void startInitPayEnv(final String phoneNum) {
		new AsyncTask<String, Void, Integer>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// 正式代码
				dialog = DialogUtil.getDialog(
				context,
				DialogUtil.DIALOG_PROGRESS, "初始化环境...");
				dialog.show();
			}

			@Override
			protected Integer doInBackground(String... params) {
				
				try {
					// TODO 测试使用
//					params[0] = "17710110591";
					
					// 初始化网络环境
					int init_result = InitEngine.initEnv(params[0]);
					if (init_result != 0) {
						// 初始化失败
						return init_result;
					} else {
						
						// TODO 测试使用
//						TerminalDataManager.setPlug_phoneNum("17710110591");
						
						// 获取翼机通商户信息 (分开写)
						String yjtMerchantId = null;
						String yjtMerchantName = null;
						// 增加支持打败卡后 需要翼机通商户类别  暂时有 1  2  3  三种
						String yjtMerchantType = null;
						
						logger.printLogOnSDCard("开始获取商户信息");
						Map<String, Object> merchantInfo_map = YjtLoadRequestEngine
								.getYjtMerchantInfo(TerminalDataManager
										.getPlug_phoneNum());
						logger.printLogOnSDCard("获取商户信息结束" + merchantInfo_map);
						if (merchantInfo_map == null
								|| !"0000".equals(merchantInfo_map.get("RespCode"))) {
							
							// 添加一个错误提示F100表示客户端需要升级
							if("F100".equals(merchantInfo_map.get("RespCode"))) {
								// 错误码
								ResponseExceptionInfo
										.setErrorCode(SumaPostHandler.ERROR_GET_MERCHANTINFO_NEEDUPDATE);
								// 错误信息
								ResponseExceptionInfo
										.setErrorMsg(SumaPostHandler.ERROR_GET_MERCHANTINFO_NEEDUPDATE_MSG);
							} else if(merchantInfo_map != null) {
								// 获取商户信息出错, 提示用户
								// 错误码
								ResponseExceptionInfo
								.setErrorCode(SumaPostHandler.ERROR_GET_MERCHANTINFO_FAILD);
								// 错误信息
								ResponseExceptionInfo
								.setErrorMsg(SumaPostHandler.ERROR_GET_MERCHANTINFO_FAILD_MSG);
							}
							return 0x06;
						} else {
							yjtMerchantId = (String) merchantInfo_map
									.get("YJTMerchantId");
							yjtMerchantName = (String) merchantInfo_map
									.get("YJTMerchantName");
							
							// TODO 需要加上商户充值类型
							yjtMerchantType = (String)merchantInfo_map.get("YTJMerchantRechargeType");
							
							
							// 记录商户信息
							TerminalDataManager
									.setPlug_yjtMerchantId(yjtMerchantId);
							TerminalDataManager
									.setPlug_yjtMerchantName(yjtMerchantName);
							
							TerminalDataManager.setPlug_yjtMerchantRechargeType(yjtMerchantType);
//							TerminalDataManager.setPlug_yjtMerchantRechargeType("1");

							// 读取卡片余额
							ImpYJTCardProcessor yjtCardProcessor = new ImpYJTCardProcessor(
									context, null);  
							
							// 判断是否是swp卡  如果不是就不显示余额
							if(!yjtCardProcessor.isSwpCard() || SumaConstants.MERCHANT_RECHARGE_TYPE_3.equals(yjtMerchantType)) {	
								TerminalDataManager.setIsSwpCard(false);
								TerminalDataManager.setPlug_yjtBalance("");
							} else {
								TerminalDataManager.setIsSwpCard(true);
								// 存储卡内余额
								String balance = yjtCardProcessor.getCardBalance();
								// 存储卡内余额
								TerminalDataManager.setPlug_yjtBalance(balance);
							}
							
							
							// 读取补贴钱包的余额
//							ImpYJTCardEDProcessor yjtCardEDProcessor = new ImpYJTCardEDProcessor(context, null);
//							yjtCardEDProcessor.interactionBetweenPhoneAndCard("1");
//							yjtCardEDProcessor.getCardBalance();
							
							// iccid
//							TerminalDataManager
//									.setICCID(ContextUtil.getCardICCID());
							return 0;
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
					logger.printLogOnSDCard(e.toString());
					return -1;
				} catch(Throwable e) {
					e.printStackTrace();
					logger.printLogOnSDCard(e.toString());
					return -1;
				}
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				dialog.dismiss();
				if (result != 0) {
					if (payListener != null) {
						payListener.onError(result, ResponseExceptionInfo.getErrorMsg());
					}
					logger.printLogOnSDCard("支付环境初始化失败("
							+ ResponseExceptionInfo.getErrorMsg() + ")" + "\n"
						+ "http请求返回错误码:" + ResponseExceptionInfo.getHttpResponseCode());
//					activity.finish();
					return;
				} else {
					//
					TerminalDataManager.setPlug_isSecure(0);// 固定有密码
					TerminalDataManager.setPlug_phoneNum(phoneNum);

					Intent intent = new Intent();
					intent.setClass(ContextUtil.getInstance(),
							ConfirmActivity.class);
					// 仅仅进行翼支付扣款
					// 启动订单确认页面
					context.startActivity(intent);
					activity.finish();
				}
			}
		}.execute(phoneNum);
	}

	/**
	 * 
	 * @param merchantName
	 *            商户名称
	 * @param merchantNumber
	 *            商户编号
	 * @param merchantSerialNB
	 *            商户端订单
	 * @param paySerialNB
	 *            碰碰端支付订单
	 * @param amount
	 *            支付金额
	 * @param tradeType
	 *            交易类型
	 * @param signature
	 *            签名数据
	 * @param isSecure
	 *            是否 密码
	 * @param payAccount
	 *            支付账号
	 * @param phoneNum
	 *            支付账号
	 */
	public void pay(String merchantName, String merchantNumber,
			String merchantSerialNB, String paySerialNB, String amount,
			String tradeType, String signature, int isSecure,
			String payAccount, String phoneNum) {
		// 初始化网络环境
		int ires = InitEngine.initEnv(phoneNum);
		if (ires != 0) {
			if (payListener != null) {
				payListener.onError(ires, "支付环境初始化失败");
			}
			return;
		} else {
			//
			TerminalDataManager.setPlug_merchantName(merchantName);
			TerminalDataManager.setPlug_merchantNumber(merchantNumber);
			TerminalDataManager.setPlug_merchantSerialNB(merchantSerialNB);
			TerminalDataManager.setPlug_paySerialNB(paySerialNB);
			TerminalDataManager.setPlug_amount(amount);
			TerminalDataManager.setPlug_tradeType(tradeType);
			TerminalDataManager.setPlug_signature(signature);
			TerminalDataManager.setPlug_isSecure(0);// 固定有密码
			TerminalDataManager.setPlug_payAccount(payAccount);
			TerminalDataManager.setPlug_phoneNum(phoneNum);
			// 检验支付类型是否正确
			if (!isValidTradeType(tradeType)) {
				//
				if (payListener != null) {
					payListener.onError(0x02, "错误的交易类型:" + tradeType);
				}
				return;
			}
			// 检验签名是否正确
			PayPlugHelp PayPlugHelpHandler = new PayPlugHelp();
			String opdate = null;
			if ("95".equals(tradeType)) {
				opdate = ConvertUtil.amount2Format("0.00");
			} else if ("96".equals(tradeType)) {
				opdate = ConvertUtil.amount2Format(amount);
			}
			if (null == PayPlugHelpHandler.VerifySignature(
					ConvertUtil.string2GBK(merchantNumber + "||"
							+ merchantSerialNB + "||" + opdate + "||"
							+ paySerialNB), signature)) {
				// 验签失败
				if (payListener != null) {
					payListener.onError(0x02, "数据签名错误");
				}
				return;
			}
			//
			if (tradeType.equals("95")) {
				// 进行翼支付退款
				String result = BestPayEngine.refoundOrder("999999", amount,
						amount);
				int ir = -1;
				String strr;
				if (result == null) {
					ir = 0x01;
					strr = "未知错误";
				} else if ("0000".equals(result)) {
					ir = 0x00;
					strr = "退款成功";
				} else {
					ir = 0x01;
					strr = SumaPostHandler.getPostErr(result);
				}
				if (payListener != null) {
					if (ir != 0x00) {
						payListener.onError(ir, strr);
					} else {
						payListener.onSuccess(ir, strr);
					}
				}
			} else if ("96".equals(tradeType)) {
				Intent intent = new Intent();
				intent.setClass(ContextUtil.getInstance(),
						ConfirmActivity.class);
				// 仅仅进行翼支付扣款
				// 启动订单确认页面
				ContextUtil.getInstance().startActivity(intent);
			}
		}
	}

	private static boolean isValidTradeType(String tradeType) {
		if (TextUtils.isEmpty(tradeType))
			return false;
		else if (!("96".equals(tradeType) || "95".equals(tradeType))) {
			return false;
		} else {
			return true;
		}
	}

	public void setResult(int code, String msg) {
		if (payListener != null) {
			if (code != 0)
				payListener.onError(code, msg);
			else
				payListener.onSuccess(code, msg);
			payListener = null;
		}
	}
}
