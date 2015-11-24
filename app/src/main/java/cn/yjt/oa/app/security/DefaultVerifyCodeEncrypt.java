package cn.yjt.oa.app.security;

public class DefaultVerifyCodeEncrypt implements VerifyCodeEncrypt {

	@Override
	public String encrypt(String verifyCode) {
		return verifyCode;
	}

}
