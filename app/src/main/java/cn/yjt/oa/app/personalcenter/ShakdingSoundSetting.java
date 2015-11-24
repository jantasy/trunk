package cn.yjt.oa.app.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;

public class ShakdingSoundSetting extends BackTitleFragmentActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}
	
	@Override
	public void onClick(View v) {
		
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, ShakdingSoundSetting.class);
		context.startActivity(intent);
	}
}
