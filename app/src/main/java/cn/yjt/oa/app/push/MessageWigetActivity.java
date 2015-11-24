package cn.yjt.oa.app.push;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.TableMsgWidget;

public class MessageWigetActivity extends FragmentActivity implements
		OnClickListener {

	private List<PushMessageData> mpushMsgList = null;
	private MessageFragment fragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_wiget);
		getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

		/*记录操作 1201*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_WIDGET_REMIND);

		initView();
		getIntentDate();

		// 关掉桌面wigdet
		stop();

	}

	private void getIntentDate() {
		List<PushMessageData> listPushMessage = getIntent()
				.getParcelableArrayListExtra(PushMessageData.PUSH_MSG_TAG);
		for (PushMessageData pushMessageData : listPushMessage) {
			fragment.addMessage(pushMessageData);
		}

	}

	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		fragment = new MessageFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		receivedMessage(intent);
		stop();
	}

	private void stop() {
		// Auto-generated method stub
		Intent intent1 = new Intent(this, TableMsgWidget.class);
		this.stopService(intent1);
	}

	public void receivedMessage(Intent intent) {
		mpushMsgList = intent.getParcelableArrayListExtra(PushMessageData.PUSH_MSG_TAG);

		for (PushMessageData pushMessageData : mpushMsgList) {
			fragment.addMessage(pushMessageData);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();

			break;

		default:
			break;
		}
	}

}
