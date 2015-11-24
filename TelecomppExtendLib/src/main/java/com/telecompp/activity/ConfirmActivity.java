package com.telecompp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestpay.plugin.Plugin;
import com.telecom.pp.extend.R;
import com.telecompp.ContextUtil;
import com.telecompp.engine.BestPayEngine;
import com.telecompp.engine.PaymentEngine;
import com.telecompp.handler.MyExceptionHanlder;
import com.telecompp.ui.HelpDialog;
import com.telecompp.ui.MyDialog;
import com.telecompp.ui.MyRadioGroup;
import com.telecompp.ui.MyRadioGroup.OnCheckedChangeListener;
import com.telecompp.util.DialogUtil;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.ResponseExceptionInfo;
import com.telecompp.util.ResultConstant;
import com.telecompp.util.SharedHelp;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaConstants.IpCons;
import com.telecompp.util.SumaPostHandler;
import com.telecompp.util.TerminalDataManager;
import com.telecompp.xml.XmlMgr_BusTradeBase;
import com.transport.ConvertUtil;
import com.transport.RetryTask;
import com.transport.RetryTask.TaskAndCallback;
import com.transport.db.bean.ConfirmItem;
import com.transport.db.dao.PConfirmDao;
import com.transport.processor.ImpYJTCardProcessor;
import com.transport.service.UploadConfirmService;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 确认订单信息activity
 * 
 * @author poet
 * 
 */
public class ConfirmActivity extends Activity implements OnClickListener {

	private TextView tv_merchant_name;
//	private TextView tv_confirm_merchantId;
	private TextView tv_confirm_uimNum;
	private TextView tv_confirm_tradeNum;
	
	// 写卡成功
	private final String LOAD_SUCCESS = "loadSuccess";
	// 写卡失败
	private final String LOAD_FAILED = "loadFailed";
	
	// 卡片类型
	private TextView tv_confirm_cardType;
	
	private Dialog processDialog;
	//	碰碰交易流水号
	private String tradeNum = null;
	//	交易类型 "1" 表示客票充值
	private String tradeType = "";
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case HANDLE_MSG_GETORDERSTATUS_FAILD:
				if(pay != null) {
					//	错误提示
					pay.setResult(0x01, "获取订单状态失败");
				}
				break;
				
			case HANDLE_MSG_LOADAPPLY_COMPLETE:
				
				break;
				
			}
		};
	};
	private PaymentEngine pay = null;
	private final int HANDLE_MSG_GETORDERSTATUS_FAILD = 0;	//	查询订单状态失败
	private final int HANDLE_MSG_LOADAPPLY_COMPLETE = 1;	//	圈存请求结果返回
	
	// 快速充值
	private RadioButton checkbox_load_quickload;
	private RadioButton checkbox_load_othermoney;
	private MyRadioGroup myRadioGroup_load_quickload;
	private LinearLayout ll_load_othermoney;
	//	快速冲值输入的金额
	private String temp_myradiogroup_money = null;
	//	充值金额
	private String amount;
	//	其他充值输入的金额
	private EditText et_load_money;
	//	显示卡内余额的控件
	private TextView tv_confirm_balance;

	private Button btn_pay;
	
	
	//	========充值结果显示==========
	//	充值前金额
	private TextView tv_pre_balance;
	//	充值金额
	private TextView tv_load_amount;
	//	充值后余额
	private TextView tv_card_balance;
	//	卡号
	private TextView tv_card_num;
	//	充值结果 成功或者失败原因
	private TextView tv_result_load_purchase_result;
	private Button btn_card_confirm;
	private RelativeLayout rl_confirm_load;
	private LinearLayout ll_confirm_loadresult;
	private LinearLayout rl__confirm_balance;
	private RelativeLayout rl_confirm_success_img;
	private ImageView img_confirm_load_result;
	
	// 大白卡需要的控件
	private LinearLayout ll_confirm_kahao;
	private LinearLayout rl_result_pre_load_balance;
	private LinearLayout ll_confirm_post_balance;
	
	private LoggerHelper logger = new LoggerHelper(ConfirmActivity.class);
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHanlder(this));
		super.onCreate(savedInstanceState);
		MobclickAgent.setCatchUncaughtExceptions(true);
		// 初始化log
		intiView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		btn_pay.setBackgroundResource(R.drawable.list_item_last);
		btn_pay.setTextColor(getResources().getColor(R.color.white));
		btn_pay.setClickable(true);
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void intiView() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_confirm);
		tv_merchant_name = (TextView) findViewById(R.id.tv_merchant_name);
//		tv_confirm_merchantId = (TextView) findViewById(R.id.tv_confirm_merchantId);
		tv_confirm_uimNum = (TextView) findViewById(R.id.tv_confirm_uimNum);
		tv_confirm_tradeNum = (TextView) findViewById(R.id.tv_confirm_tradeNum);
		tv_confirm_cardType = (TextView) findViewById(R.id.tv_confirm_cardType);
		
		rl__confirm_balance = (LinearLayout)findViewById(R.id.rl__confirm_balance);
		
		//	商户名称
		tv_merchant_name.setText(TerminalDataManager.getPlug_yjtMerchantName());
		//	商户ID
//		tv_confirm_merchantId.setText(TerminalDataManager.getPlug_yjtMerchantId());
		btn_pay = (Button) findViewById(R.id.btn_pay);
		btn_pay.setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		
		tv_confirm_balance = (TextView) findViewById(R.id.tv_confirm_balance);
		
		if(TextUtils.isEmpty(TerminalDataManager.getPlug_yjtBalance())) {
			tv_confirm_cardType.setText("白卡");
			tv_confirm_balance.setText("");
			//	手机号
			tv_confirm_uimNum.setText(TerminalDataManager.getPlug_phoneNum());
			rl__confirm_balance.setVisibility(View.GONE);
		} else {
			rl__confirm_balance.setVisibility(View.VISIBLE);
			tv_confirm_cardType.setText("手机卡");
			//	UIM卡卡号
			tv_confirm_uimNum.setText(TerminalDataManager.getICCID());
			//	显示卡内余额
			tv_confirm_balance.setText(formatMoney("##0.00", TerminalDataManager.getPlug_yjtBalance()));
		}
		
		//	快速充值
		et_load_money = (EditText) findViewById(R.id.et_load_money);
		//	将光标设置到edittext的末尾
		et_load_money.setSelection(et_load_money.getText().length());
		checkbox_load_quickload = (RadioButton) findViewById(R.id.checkbox_load_quickload);
		checkbox_load_othermoney = (RadioButton) findViewById(R.id.checkbox_load_othermoney);
		myRadioGroup_load_quickload = (MyRadioGroup) findViewById(R.id.myRadioGroup_load_quickload);
		ll_load_othermoney = (LinearLayout) findViewById(R.id.ll_load_othermoney);
		
		myRadioGroup_load_quickload.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MyRadioGroup group,
					int checkedId) {
				temp_myradiogroup_money = ((RadioButton) ConfirmActivity.this
						.findViewById(checkedId)).getText().toString();
				temp_myradiogroup_money = temp_myradiogroup_money
						.substring(0,
								temp_myradiogroup_money.length() - 1);
			}
		});
		// 点击快速充值
		checkbox_load_quickload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 隐藏相应空间
							ll_load_othermoney.setVisibility(View.GONE);
							// 显示相应空间
							myRadioGroup_load_quickload
									.setVisibility(View.VISIBLE);
							checkbox_load_othermoney.setChecked(false);
						} else {
							// 隐藏相应空间
							ll_load_othermoney.setVisibility(View.VISIBLE);
							// 显示相应空间
							myRadioGroup_load_quickload
									.setVisibility(View.GONE);
							checkbox_load_othermoney.setChecked(true);
						}
					}
				});
		// 点击其他金额
		checkbox_load_othermoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// 隐藏相应控件
							ll_load_othermoney.setVisibility(View.VISIBLE);
							// 显示相应控件
							myRadioGroup_load_quickload
									.setVisibility(View.GONE);
							checkbox_load_quickload.setChecked(false);
						} else {
							// 隐藏相应控件
							ll_load_othermoney.setVisibility(View.GONE);
							// 显示相应控件
							myRadioGroup_load_quickload
									.setVisibility(View.VISIBLE);
							checkbox_load_quickload.setChecked(true);
						}
					}
				});
		// 初始化
		checkbox_load_quickload.setChecked(true);
		
		
		
		//	=======充值结果显示========
		tv_pre_balance = (TextView) findViewById(R.id.tv_pre_balance);
		tv_load_amount = (TextView) findViewById(R.id.tv_load_amount);
		tv_card_balance = (TextView) findViewById(R.id.tv_card_balance);
		tv_card_num = (TextView) findViewById(R.id.tv_card_num);
		tv_result_load_purchase_result = (TextView) findViewById(R.id.tv_result_load_purchase_result);
		btn_card_confirm = (Button) findViewById(R.id.btn_card_confirm);
		rl_confirm_load = (RelativeLayout) findViewById(R.id.rl_confirm_load);
		ll_confirm_loadresult = (LinearLayout) findViewById(R.id.ll_confirm_loadresult);
		rl_confirm_success_img = (RelativeLayout) findViewById(R.id.rl_confirm_success_img);
		img_confirm_load_result = (ImageView) findViewById(R.id.img_confirm_load_result);
		
		ll_confirm_kahao = (LinearLayout) findViewById(R.id.ll_confirm_kahao);
		rl_result_pre_load_balance = (LinearLayout) findViewById(R.id.rl_result_pre_load_balance);
		ll_confirm_post_balance = (LinearLayout) findViewById(R.id.ll_confirm_post_balance);
		
		pay = PaymentEngine.getInstance();
		
		btn_card_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pay.setResult(0x00, "支付成功");
				ConfirmActivity.this.finish();
			}
		});
		
		//	隐藏控件
		rl_confirm_load.setVisibility(View.VISIBLE);
		ll_confirm_loadresult.setVisibility(View.GONE);
	}

	private Dialog dialog;

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_pay){
			
			//	客票充值
			tradeType = "1";
			//	计算充值金额
			if(checkbox_load_quickload.isChecked()) {
				if(!TextUtils.isEmpty(temp_myradiogroup_money)) {
					amount = temp_myradiogroup_money;
				} else {
					Toast.makeText(this, "请选择金额", Toast.LENGTH_LONG).show();
					return;
				}
				
			} else {
				amount = et_load_money.getText().toString();
				//	格式化用户的充值金额
				amount = formatMoney("##0.00", amount);
                et_load_money.setText(amount);
			}
			
			//	将充值金额存放到TerminalDataManager中
			TerminalDataManager.setPlug_amount(amount);
			
			// 增加了商户类型后需要对类型做判断   1：企业内swp卡扣款充值，白卡用户提示不能充值，需要现金充值。 2：企业内swp卡扣款充值，白卡用户划账，到充值机直接充值。  3：企业内swp卡，白卡用户都划账，到充值机直接充值。
			if(!TerminalDataManager.getIsSwpCard()) {
				// 是大白卡
				if("1".equals(TerminalDataManager.getPlug_yjtMerchantRechargeType())) {
					// 提示用户大白卡不能充值
					messageDialog("该商户暂不支持白卡充值!");
					return;
				}
			}
			
			if(!SharedHelp.getIsHelpNeed(this)) {
				getYJTOrder(TerminalDataManager.getPlug_yjtMerchantId(), v);
			} else {				
				// 这里添加提示信息, 提示用户   为确保资金到帐，在订单支付成功后，别忘了点击“完成”按钮哟！
				final View v_btn = v;
				HelpDialog helpDialog = new HelpDialog(this, R.style.MyDialog,
						new HelpDialog.MyDialogListener() {
					
					@Override
					public void onPositiveClick(Dialog dialog, View view) {
						// 翼支付订单查询
						getYJTOrder(TerminalDataManager.getPlug_yjtMerchantId(), v_btn);
					}
					
					@Override
					public void onNegativeClick(Dialog dialog, View view) {
					}
				}, MyDialog.ButtonConfirm);
				helpDialog.MyDialogSetMsg("为确保资金写入本机账户, 在出现如下支付成功页面时,记得点击\"完成\"按钮");
				helpDialog.setCancelable(false);
				helpDialog.show();
				
			}
			
			
			
		}else if(v.getId() == R.id.btn_cancel){
			PaymentEngine pay = PaymentEngine.getInstance();
			if (pay != null) {
				pay.setResult(0x02, "取消支付");
			}
			finish();
		}
	}
	
	/**
	 * 2.翼支付下单, 3.返回翼支付下单结果
	 * @param yjtMerchantId 翼机通商户id
	 */
	private void getYJTOrder(final String yjtMerchantId, final View v) {
		new AsyncTask<Void, Void, Integer>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (dialog == null) {
					dialog = DialogUtil.getDialog(
							ConfirmActivity.this,
							DialogUtil.DIALOG_PROGRESS);
				}
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}

			@Override
			protected Integer doInBackground(Void... params) {
				
				// 2.翼支付下单  3.返回翼支付下单结果
				//	对接翼机通的翼支付下单接口  多了CSN 和 YJTMerchantId
				final Map<String, Object> order = BestPayEngine
						.getOrder(ContextUtil.CITYCODE, TerminalDataManager.getCSN(), 
								ConvertUtil.str2Cent(TerminalDataManager.getPlug_amount()),
										yjtMerchantId, tradeType, TerminalDataManager.getPlug_phoneNum());	
				if (order != null
						&& !TextUtils.isEmpty((String) order
								.get(Plugin.ORDERSEQ))) {
					//	记录碰碰交易流水号 tradeNum
					tradeNum = (String) order.get("TradeNum");
					//	急用订单号 orderseq 退款的时候需要提供
					TerminalDataManager.setPlug_orderseq((String) order.get("ORDERSEQ"));

					// 设置支付按钮不可点击
					final Button btnPay = (Button) v;

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// 设置按钮不可点击
							btnPay.setBackgroundResource(R.drawable.btn_register_checked);
							btnPay.setTextColor(getResources().getColor(R.color.black));
							btnPay.setClickable(false);
							
							//	4.调用翼支付收银台进行支付
							BestPayEngine.pay(ConfirmActivity.this,
									order);
						}
					});
					return 0;
				}
				return -1;
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if (dialog != null) {
					dialog.dismiss();
				}
				if (result != 0) {
					// 下单失败
					MyDialog mydialog = DialogUtil.getMyDialog(
							"提示", ResponseExceptionInfo.getErrorMsgFromHttpResponseCode(ResponseExceptionInfo.getHttpResponseCode()) + ResponseExceptionInfo.getHttpResponseCode(), ConfirmActivity.this,
							new MyDialog.MyDialogListener() {
								@Override
								public void onPositiveClick(
										Dialog dialog, View view) {
									onClick(findViewById(R.id.btn_pay));
								}
								@Override
								public void onNegativeClick(
										Dialog dialog, View view) {
									PaymentEngine pay = PaymentEngine.getInstance();
									if (pay != null) {
										pay.setResult(0x04, "翼支付下单失败");
									}
									finish();
								}
							}, MyDialog.ButtonBoth);
					mydialog.setNegaBtnMsg("取消支付");
					mydialog.setPosiBtnMsg("重试");
					mydialog.show();
				}
			}
		}.execute();
	}
	
	/**
	 * 提示信息dialog
	 */
	public void messageDialog(String message) {
		// 下单失败
		MyDialog mydialog = DialogUtil.getMyDialog(
				"提示", message, ConfirmActivity.this,
				new MyDialog.MyDialogListener() {
					@Override
					public void onPositiveClick(
							Dialog dialog, View view) {
						
					}
					@Override
					public void onNegativeClick(
							Dialog dialog, View view) {
					}
				}, MyDialog.ButtonConfirm);
		mydialog.setPosiBtnMsg("确定");
		mydialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		logger.printLogOnSDCard("===onActivityResult成功调用");
		try {
			logger.printLogOnSDCard("===onActivityResult成功调用, pay = PaymentEngine.getInstance();  执行前--pay==" + pay);
			pay = PaymentEngine.getInstance();
			logger.printLogOnSDCard("===onActivityResult成功调用, pay = PaymentEngine.getInstance();  执行后--pay==" + pay);
		} catch(Exception e) {
			e.printStackTrace();
			logger.printLogOnSDCard(e);
		} catch(Throwable thr) {
			logger.printLogOnSDCard(thr);
			thr.printStackTrace();
		}
		
		if (1000 == requestCode) {
			//翼支付支付结果返回
			if(resultCode == RESULT_OK){
				
				logger.printLogOnSDCard("===翼支付收银台返回成功===");
				
				//	5.查询订单支付结果 6.返回支付结果
				new AsyncTask<Void, Void, String>(){
					protected void onPreExecute() {
						
						logger.printLogOnSDCard("===开始异步任务===onPreExecute");
						if(processDialog == null){
							processDialog = DialogUtil.getDialog(ConfirmActivity.this, DialogUtil.DIALOG_PROGRESS);
						}
						processDialog.show();
					};
					@Override
					protected String doInBackground(Void... params) {
						
						try {
							logger.printLogOnSDCard("===开始异步任务===doInBackground开始");
							//	5.查询订单支付结果, 6.返回支付结果, 重复查询3次
							Map<String, Object> result = null;
							int i = 0;
							while(i < 3) {
								logger.printLogOnSDCard("===开始异步任务===获取订单支付结果前===");
								result = BestPayEngine.getOrderStatus(ContextUtil.CITYCODE, TerminalDataManager.getPlug_yjtMerchantId());
								logger.printLogOnSDCard("===开始异步任务===获取订单支付结果后==result==" + result);
								if(result != null) {
									break;
								} else {
									i++;
								}
							}
							
							ImpYJTCardProcessor yjtCardProcessor = new ImpYJTCardProcessor(ConfirmActivity.this, tradeNum);
							//正确返回数据
							if(result != null && "success".equals(result.get("OrderStatus"))){
								
								// 判断商户充值方式是否为3  或者为2  且是大白卡
								if(TerminalDataManager.YJT_MERCHANT_TYPE_3.equals(TerminalDataManager.getPlug_yjtMerchantRechargeType()) || 
										(TerminalDataManager.YJT_MERCHANT_TYPE_2.equals(TerminalDataManager.getPlug_yjtMerchantRechargeType()) && !TerminalDataManager.getIsSwpCard())) {
									
									logger.printLogOnSDCard("===开始异步任务===白卡处理逻辑==result==" + result);
									// 只是划账, 不充值
									// 这里发送的是成功确认报文
									String isSuccess = yjtCardProcessor.noLoadSuccessConfirm(TerminalDataManager.getPlug_amount());
									return isSuccess;
								}
								
								logger.printLogOnSDCard("===开始异步任务===swp卡处理逻辑==result==" + result);
								//	支付成功
								//	7.机卡交互
								boolean isSuccess = yjtCardProcessor.interactionBetweenPhoneAndCard(ConvertUtil.str2Cent(TerminalDataManager.getPlug_amount()) + "");
								if(isSuccess) {
									return LOAD_SUCCESS;
								} else {
									return LOAD_FAILED;
								}
//							return yjtCardProcessor.interactionBetweenPhoneAndCard(ConvertUtil.str2Cent(TerminalDataManager.getPlug_amount()) + "");
							}else{
								// 加入大白卡后需要进行判断
								if(TerminalDataManager.YJT_MERCHANT_TYPE_3.equals(TerminalDataManager.getPlug_yjtMerchantRechargeType()) || 
										(TerminalDataManager.YJT_MERCHANT_TYPE_2.equals(TerminalDataManager.getPlug_yjtMerchantRechargeType()) && !TerminalDataManager.getIsSwpCard())) {
									// 只是划账, 不充值
									// 这里发送的是成功确认报文
									String isSuccess = yjtCardProcessor.noLoadFailedConfirm(TerminalDataManager.getPlug_amount());
									return isSuccess;
								}
								
								// 支付失败,合理通知用户，可能未接收到通知
								mHandler.sendEmptyMessage(HANDLE_MSG_GETORDERSTATUS_FAILD);
								//	发送充值失败通知
								ImpYJTCardProcessor processor = new ImpYJTCardProcessor(ConfirmActivity.this, tradeNum);
								String xml = processor.getLoadNoticeXml(null, null, null, 
										"", "0x01", tradeNum, TerminalDataManager.getPlug_phoneNum(), null);
								SumaPostHandler HttpPostHandler = new SumaPostHandler();
								Map<String, Object> responseMap = HttpPostHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS,
										xml, SumaConstants.ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
								if(responseMap == null || !("success").equals(responseMap.get("OrderStatus"))) {
									//	重复发送3次, 如果仍然失败就记录到数据库中, 由service重复发送
									new RetryTask(3, true, new ConfirmPayNotice(xml, TerminalDataManager.getPlug_amount(), new Date(), SumaConstants.ERROR_TYPE_NO_PAYNOTICE, tradeNum)).start();
								}
								
								//	type 
								if(result != null && result.get("RespCode") != null && result.get("RespMsg") != null) {
									//	错误码
									ResponseExceptionInfo.setErrorCode("7");
									//	错误信息
									ResponseExceptionInfo.setErrorMsg((String)result.get("RespMsg"));
								} else {
									// session失效
									if("F003".equals(TerminalDataManager.getExceptionCode())) {
										ResponseExceptionInfo.setErrorCode("99");
										ResponseExceptionInfo.setErrorMsg("用户会话失效, 如果您正在做支付充值交易, 系统会自动冲正, 款项会自动退回账户, 请耐心等待退款通知短信");
									} else {									
										ResponseExceptionInfo.setErrorCode("6007");
										ResponseExceptionInfo.setErrorMsg("支付结果确认失败, 请保持网络通畅以尽快为您退款");
									}
								}
								return LOAD_FAILED;
							} 
						} catch(Exception e) {
							logger.printLogOnSDCard(e.getStackTrace().toString());
							return null;
						} catch (Throwable thr) {
							logger.printLogOnSDCard(thr.getStackTrace().toString());
							return null;
						}
					}
					
					@Override
					protected void onPostExecute(String result) {
						//
						if(processDialog!=null){
							processDialog.dismiss();
							processDialog = null;
						}
						
						if(result == null) {
							messageDialog("系统异常, 如果您正在做支付充值交易, 系统会自动冲正, 款项会自动退回账户, 请耐心等待");
						}
						
						// ===========正常的写卡流程==================
						if(LOAD_SUCCESS.equals(result)){
							//支付成功	通知入口应用充值成功结果 
							if (pay != null) {
								//	显示结果控件
								rl_confirm_load.setVisibility(View.GONE);
								ll_confirm_loadresult.setVisibility(View.VISIBLE);
								
								rl_result_pre_load_balance.setVisibility(View.VISIBLE);
								ll_confirm_kahao.setVisibility(View.VISIBLE);
								ll_confirm_post_balance.setVisibility(View.VISIBLE);
								//	设置数据
								tv_pre_balance.setText(formatMoney("##0.00", TerminalDataManager.getPlug_yjtBalance()));
								tv_load_amount.setText(TerminalDataManager.getPlug_amount());
								tv_card_balance.setText(formatMoney("##0.00", "" + (Float.parseFloat(TerminalDataManager.getPlug_yjtBalance()) + Float.parseFloat(TerminalDataManager.getPlug_amount()))));
								tv_card_num.setText(TerminalDataManager.getICCID());
								tv_confirm_tradeNum.setText(tradeNum);
//								成功的图片隐藏
								rl_confirm_success_img.setVisibility(View.VISIBLE);
								tv_result_load_purchase_result.setText("充值成功");
							}
						}else if(LOAD_FAILED.equals(result)){
							//	充值失败显示的界面
							if(pay != null) {
								//	显示结果控件
								rl_confirm_load.setVisibility(View.GONE);
								ll_confirm_loadresult.setVisibility(View.VISIBLE);
								
								rl_result_pre_load_balance.setVisibility(View.VISIBLE);
								ll_confirm_kahao.setVisibility(View.VISIBLE);
								ll_confirm_post_balance.setVisibility(View.VISIBLE);
								
								//	设置数据
								tv_pre_balance.setText(TerminalDataManager.getPlug_yjtBalance());
								tv_load_amount.setText(TerminalDataManager.getPlug_amount());
								DecimalFormat fnum = new DecimalFormat("##0.00");  
								tv_card_balance.setText(fnum.format(Float.parseFloat(TerminalDataManager.getPlug_yjtBalance())) + "");
								tv_card_num.setText(TerminalDataManager.getICCID());
								tv_confirm_tradeNum.setText(tradeNum);
								//	成功图片改为失败图片
								img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_failed));
								Integer errCode = 0x00;
								try {
									errCode = Integer.parseInt(ResponseExceptionInfo.getErrorCode());
								} catch (Exception e) {
									e.printStackTrace();
									logger.printLogOnSDCard("失败通知, errCode转换异常" + e.toString());
									pay.setResult(errCode, "未知错误");
								}
								tv_result_load_purchase_result.setText(errCode + ResponseExceptionInfo.getErrorMsg());
							}
							
							
							
							//	支付失败 通知入口应用充值失败
//							if (pay != null) { 
//								ConfirmActivity.this.finish();
////								pay.setResult(0x07, "支付失败");
//								Integer errCode = 0x00;
//								try {
//									errCode = Integer.parseInt(ResponseExceptionInfo.getErrorCode());
//									pay.setResult(errCode, ResponseExceptionInfo.getErrorMsg());
//								} catch (Exception e) {
//									e.printStackTrace();
//									WriteLog.writeLogOnSDCard("失败通知, errCode转换异常" + e.toString());
//									pay.setResult(errCode, "未知错误");
//								}
//								
//							}
						}
						// 加入大白卡后  不需要写卡的流程
						else {
							// 显示结果控件
							rl_confirm_load.setVisibility(View.GONE);
							ll_confirm_loadresult.setVisibility(View.VISIBLE);
							// 隐藏部分控件
							rl_result_pre_load_balance.setVisibility(View.GONE);
							ll_confirm_kahao.setVisibility(View.GONE);
							ll_confirm_post_balance.setVisibility(View.GONE);
							// 数据显示
							tv_confirm_tradeNum.setText(tradeNum);
							
							tv_load_amount.setText(TerminalDataManager.getPlug_amount());
							img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_failed));
							if(ResultConstant.SUCCESS_CONFIRM_SUCCESS.equals(result)) {
								img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_success));
								tv_result_load_purchase_result.setText("扣款成功, 请到充值机进行充值");
							} /*else if(ResultConstant.SUCCESS_CONFIRM_FAILED.equals(result)) {
								img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_failed));
								tv_result_load_purchase_result.setText("扣款成功, 由于网络原因退款请求失败, 请保证网络通畅, 我们会尽快帮您退款");
							}*/ else if(ResultConstant.REFUND_SUCCESS.equals(result) || ResultConstant.FAILED_CONFIRM_SUCCESS.equals(result)) {
								img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_failed));
								tv_result_load_purchase_result.setText("充值失败, 退款申请已发送");
							} else if(ResultConstant.REFUND_FAILED.equals(result) || ResultConstant.FAILED_CONFIRM_FAILED.equals(result)) {
								img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_failed));
								tv_result_load_purchase_result.setText("充值失败, 由于网络原因退款通知服务器失败, 请保证网络通畅, 我们会尽快帮您退款");
							} else if(ResultConstant.SUCCESS_CONFIRM_FAILED_F905.equals(result) || ResultConstant.SUCCESS_CONFIRM_FAILED.equals(result)) {
								img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_failed));
								tv_result_load_purchase_result.setText("您已完成了第一步的充值扣款，但由于网络原因，当前还未能及时将充值结果发送到充值机，请您保持网络畅通，待收到成功提示后，再到充值机充值。");
							}
						} 
						/*else if(NOWRITE_FAILED_SENDSUCCESS.equals(result) || NOWRITE_FAILED_SENDFAILED.equals(result)) {
							// 显示结果控件
							rl_confirm_load.setVisibility(View.GONE);
							ll_confirm_loadresult.setVisibility(View.VISIBLE);
							// 隐藏部分控件
							rl_result_pre_load_balance.setVisibility(View.GONE);
							ll_confirm_kahao.setVisibility(View.GONE);
							ll_confirm_post_balance.setVisibility(View.GONE);
							// 数据显示
							tv_confirm_tradeNum.setText(tradeNum);
							//	成功图片改为失败图片
							img_confirm_load_result.setImageDrawable(getResources().getDrawable(R.drawable.icon_failed));
							if(NOWRITE_SUCCESS_SENDSUCCESS.equals(result)) {
								tv_result_load_purchase_result.setText("扣款失败, 已发起退款");
							} else if(NOWRITE_SUCCESS_SENDFAILED.equals(result)) {
								tv_result_load_purchase_result.setText("扣款失败, 由于网络原因退款请求发送失败, 请保证网络通常, 我们会持续通知服务器");
							}
						}*/
					}
				}.execute();
			}else{
//				String mm = "";
//				if(data!=null){
//					mm = data.getStringExtra("result");
//					//支付失败
//				}
//				if (pay != null) {
//					pay.setResult(0x01, "支付失败0x"+resultCode);
//				}
			}
		}
	}
	
	/**
	 * 格式化金额
	 * @param type 格式化的模版  例如:##0.00
	 * @param money	金额字符串
	 * @return
	 */
	private String formatMoney(String type, String money) {
		DecimalFormat fnum = new DecimalFormat(type);
		return fnum.format(Float.parseFloat(money));
	}
	
	@Override   
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(pay == null) {				
				pay = PaymentEngine.getInstance();
			}
			pay.setResult(0x02, "取消充值");
			this.finish();
			return true;
		} 
		return  super.onKeyDown(keyCode, event);     
	} 
	
	public class ConfirmPayNotice implements TaskAndCallback{

		Map<String, Object> responseMap = null;
		String xml;
		String money;
		Date date;
		String type;
		String tradeNum;
		public ConfirmPayNotice(String xml,String money,Date date,String type){
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
		public ConfirmPayNotice(String xml, String money, Date date,
				String type, String tradeNum) {
			this(xml,money,date,type);
			this.tradeNum = tradeNum;
		}
		@Override
		public void task() {
			SumaPostHandler HttpPostHandler = new SumaPostHandler();
			responseMap = HttpPostHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS,
					xml, SumaConstants.ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
		}
		
		@Override
		public void success() {
			logger.printLogOnSDCard("异步任务发送成功   type = " + type);
		}
		
		@Override
		public boolean issuccess() {//TODO 是否成功需要和后台确认
			if(responseMap != null){
				responseMap = XmlMgr_BusTradeBase.parse((String) responseMap
						.get(SumaConstants.MAP_HTTP_RESPONSE_RESPONSE));
				if(!("0000".equals( responseMap.get("RespCode"))||"NTER".equals( responseMap.get("RespCode")))) {
					return false;//服务器端非正常处理结果
				}
				return true;
			}
			return false;
		}
		
		@Override
		public void failure() {
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
			Intent intent = new Intent(ConfirmActivity.this, UploadConfirmService.class);
			startService(intent);
		}
	
	}
}
