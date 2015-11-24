package cn.yjt.oa.app.attendance;

import java.util.List;

import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.utils.UserData;

public class BeaconServerLoader {

	public void load(){
		ApiHelper.getUserBeaconsInArea(new ResponseListener<List<BeaconInfo>>() {

			@Override
			public void onSuccess(List<BeaconInfo> payload) {
				UserData.setUserAreaBeaconInfos(payload);
			}
		});
	}
}
