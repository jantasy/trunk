package cn.yjt.oa.app.security;

public class Base64Encrypt implements VerifyCodeEncrypt {

	@Override
	public String encrypt(String verifyCode) {
		return DataEncoder.encode(verifyCode);
	}

}
