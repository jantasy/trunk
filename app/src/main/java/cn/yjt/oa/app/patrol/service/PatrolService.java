package cn.yjt.oa.app.patrol.service;

import cn.yjt.oa.app.beans.InspectInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.checkin.interfaces.ICheckInType;
import cn.yjt.oa.app.checkin.service.CheckInService;
import cn.yjt.oa.app.patrol.activitys.PatrolActivity;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import io.luobo.common.http.Listener;

/**巡更的后台服务*/
//public class PatrolService extends Service {

public class PatrolService extends CheckInService{

    @Override
    protected void requestCheckInInfo(ICheckInType info, Listener<Response<ICheckInType>> listener) {
        if(info instanceof InspectInfo){
            PatrolApiHelper.patrolCheckIn(listener,(InspectInfo)info);
        }
    }

    @Override
    protected void onErrorUnbind() {
        PatrolActivity.launchWithState(getApplicationContext(), PatrolActivity.STATE_ERROR,
								null);
    }

    @Override
    protected void onLocationFailureUnBind() {
        PatrolActivity.launchWithState(getApplicationContext(),
								PatrolActivity.STATE_LOCATION_FAILURE, null);
    }

    @Override
    protected void requestOtherCodeUnBind(Response<ICheckInType> response) {
        PatrolActivity.launchWithState(getApplicationContext(),
									PatrolActivity.STATE_REQUEST_RESPONSE, response.getDescription());
    }

    @Override
    protected void requestSuccessUnBind(ICheckInType info) {
        PatrolActivity.launchWithState(getApplicationContext(),
										PatrolActivity.STATE_REQUEST_RESPONSE, info.getResultDesc());
    }
}
