package cn.yjt.oa.app.consume;

import android.os.Bundle;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.component.TitleFragmentActivity;

import com.telecompp.engine.PaymentEngine;

public class RechargeActivity extends TitleFragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		try {
			PaymentEngine.getInstance(this, null).pay(
					AccountManager.getCurrent(getApplicationContext()).getPhone());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
