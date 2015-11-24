package cn.yjt.oa.app.checkin.binder;

import android.os.Binder;

import cn.yjt.oa.app.checkin.interfaces.OnCheckInListener;

/**
 * Created by 熊岳岳 on 2015/9/30.
 */
public abstract class CheckInBinder extends Binder{
    public abstract void onBind(OnCheckInListener listener);
    public abstract void unBind();
}
