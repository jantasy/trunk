package cn.yjt.oa.app.patrol.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.yjt.oa.app.beans.InspectInfo;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.checkin.CheckInActivity;
import cn.yjt.oa.app.checkin.interfaces.ICheckInType;
import cn.yjt.oa.app.patrol.service.PatrolService;

/**
 * @author 熊岳岳
 */
public class PatrolActivity extends CheckInActivity {

	public static void launch(Context context) {
		Intent intent = new Intent(context, PatrolActivity.class);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

	public static void launchWithInspectInfo(Context context,
			InspectInfo InspectInfo) {
		Intent intent = new Intent(context, PatrolActivity.class);
		intent.putExtra("InspectInfo", InspectInfo);
		context.startActivity(intent);
	}

	public static void launchWithState(Context context, int state,
			String description) {
		Intent intent = new Intent(context, PatrolActivity.class);
		intent.putExtra("state", state);
		intent.putExtra("description", description);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.startActivity(intent);
	}

    @Override
    public int getState() {
        return getIntent().getIntExtra("state",STATE_NORMAL);
    }

    @Override
    public String getKeyWord() {
        return "巡检";
    }

    @Override
    protected String getDescription() {
        return getIntent().getStringExtra("description");
    }

    @Override
    protected ICheckInType getCheckInfo() {
        InspectInfo info =getIntent().getParcelableExtra("InspectInfo");
        if(info == null){
            info = new InspectInfo();
            info.setType(SigninInfo.SINGIN_TYPE_VISIT);
        }
        return info;
    }

    @Override
    protected Intent getServiceIntent() {
        return new Intent(this, PatrolService.class);
    }


}
