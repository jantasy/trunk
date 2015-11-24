package cn.yjt.oa.app.patrol.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.yjt.oa.app.patrol.service.PatrolRemindService;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.ToastUtils;

/**
 * Created by 熊岳岳 on 2015/10/13.
 */
public class PatrolRemindReceiver extends BroadcastReceiver{

    public static final String BROADCAST_PATROL_REMIND = "cn.yjt.oa.app.patrol.ACTION_PATROL_REMIND";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("PatrolRemindManager", "进入广播");
//        ToastUtils.shortToastShow("进入广播");
        context.startService(new Intent(context, PatrolRemindService.class));
    }
}
