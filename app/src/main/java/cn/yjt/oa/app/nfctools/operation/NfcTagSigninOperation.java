package cn.yjt.oa.app.nfctools.operation;

import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.signin.AttendanceActivity;

public class NfcTagSigninOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		SigninInfo signinInfo = new SigninInfo();
		signinInfo.setType(SigninInfo.SIGNIN_TYPE_NFC);
		signinInfo.setActrualData(getSn());
		signinInfo.setPositionDescription(getTagName());
		AttendanceActivity.launchWithSigninInfo(getContext(), signinInfo);
	}

}
