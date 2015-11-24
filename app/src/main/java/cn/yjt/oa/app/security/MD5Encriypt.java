package cn.yjt.oa.app.security;

import cn.yjt.oa.app.utils.MD5Utils;

public class MD5Encriypt implements VerifyCodeEncrypt {

	@Override
	public String encrypt(String verifyCode) {
		
		try {
			verifyCode = verifyCode+"yjt";
			return MD5Utils.encode(verifyCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
