package cn.yjt.oa.app.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.utils.AppSettings;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class IncomingSetting extends BackTitleFragmentActivity implements
		OnClickListener {

	private Button incomingSure;
	private Button incomingCancle;
	private RadioGroup group;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.incoming_setting);

		group = (RadioGroup) findViewById(R.id.incoming_group);
		incomingSure = (Button) findViewById(R.id.incoming_sure);
		incomingCancle = (Button) findViewById(R.id.incoming_cancle);
		setupRadioGroup(group);
		findViewById(R.id.incoming_sure).setOnClickListener(this);
		findViewById(R.id.incoming_cancle).setOnClickListener(this);
		//group.setOnCheckedChangeListener(this);

	}

	private void setupRadioGroup(RadioGroup group) {
		int incomeAlertMode = AppSettings.getIncomeAlertMode();
		switch (incomeAlertMode) {
		case AppSettings.INCOME_MODE_CLOSE:
			group.check(R.id.incoming_close);
			break;
		case AppSettings.INCOME_MODE_SIMPLE:
			group.check(R.id.incoming_simple);
			break;
		case AppSettings.INCOME_MODE_COMPLEX:
			group.check(R.id.incoming_complex);
			break;
		default:
			break;
		}
	}


	public static void launch(Context context) {
		Intent intent = new Intent(context, IncomingSetting.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.incoming_sure:
			switch (group.getCheckedRadioButtonId()) {
			case R.id.incoming_close:
				AppSettings.setIncomeAlertMode(AppSettings.INCOME_MODE_CLOSE);

				 /*记录操作 0616*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_INCOMING_CLOSE);
				break;
			case R.id.incoming_simple:
				AppSettings.setIncomeAlertMode(AppSettings.INCOME_MODE_SIMPLE);

				 /*记录操作 0617*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_INCOMING_OPEN_BRIEF);
				break;
			case R.id.incoming_complex:
				AppSettings.setIncomeAlertMode(AppSettings.INCOME_MODE_COMPLEX);

				 /*记录操作 0618*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_CONTACTLIST_INCOMING_OPEN_GORGEOUS);
				break;
			default:
				break;
			}
			finish();
			break;
		case R.id.incoming_cancle:
			finish();
			break;
		default:
			break;
		}
		
	}
}
