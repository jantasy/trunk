package cn.yjt.oa.app.nfctools.operation;

import cn.yjt.oa.app.beans.InspectInfo;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.patrol.activitys.PatrolActivity;

public class NfcTagPatrolOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		InspectInfo inspectInfo = new InspectInfo();
        inspectInfo.setType(SigninInfo.SIGNIN_TYPE_NFC);
        inspectInfo.setActrualData(getSn());
        inspectInfo.setPositionDescription(getTagName());
		PatrolActivity.launchWithInspectInfo(getContext(), inspectInfo);
	}

}
