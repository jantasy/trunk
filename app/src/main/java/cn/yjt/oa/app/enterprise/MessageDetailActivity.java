package cn.yjt.oa.app.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class MessageDetailActivity extends TitleFragmentActivity {
	
	private TextView messageContent;
	private TextView messageTitle;
	private MessageInfo messageInfo;
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.message_detail);
		initView();
		initData();
		
	}
	


	private void initView() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		messageTitle=(TextView) findViewById(R.id.message_title);
		messageContent=(TextView) findViewById(R.id.message_content);
	}
	
	private void initData() {
		messageInfo=getIntent().getParcelableExtra("messageInfo");
		messageTitle.setText(messageInfo.getTitle());
		messageContent.setText(messageInfo.getContent());
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	public static void launch(Context context,MessageInfo messageInfo){
		Intent intent = new Intent(context, MessageDetailActivity.class);
		intent.putExtra("messageInfo", messageInfo);
		context.startActivity(intent);
	}
	
	public static void launchWithMessageInfo(Fragment fragment,MessageInfo messageInfo,int requestCode){
		Intent intent = new Intent(fragment.getActivity(), MessageDetailActivity.class);
		intent.putExtra("messageInfo", messageInfo);
		fragment.startActivityForResult(intent, requestCode);
	}
	
}
