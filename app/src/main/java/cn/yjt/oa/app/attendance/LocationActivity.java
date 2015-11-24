package cn.yjt.oa.app.attendance;

import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.signin.AttendanceActivity;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ViewUtil;
import android.os.Bundle;

public class LocationActivity extends BackTitleFragmentActivity {

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
		} else {
			/*记录操作 0801*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_ATTENDANCE_LOCATION);
			AttendanceActivity.launchWithSigninInfo(this,null);
		}
		finish();
	}
}
