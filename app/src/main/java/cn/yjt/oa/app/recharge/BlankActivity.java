package cn.yjt.oa.app.recharge;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ViewUtil;

import com.telecompp.ContextUtil;
import com.telecompp.engine.PaymentEngine;

public class BlankActivity extends FragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {

			/*记录操作 1102*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_AUTO_RECHARGE);

			PaymentEngine.getInstance(this, null).pay(
					ContextUtil.getPhoneNum());
		}
	}
}
