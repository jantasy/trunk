package cn.yjt.oa.app.checkin.interfaces;

import cn.yjt.oa.app.beans.Response;

/**
 * Created by 熊岳岳 on 2015/9/30.
 */
public interface OnCheckInListener {

    public void onLocating();

    public void onLocationFailure();

    public void onRequesting();

    public void onResponse(Response<ICheckInType> response);

    public void onError();
}
