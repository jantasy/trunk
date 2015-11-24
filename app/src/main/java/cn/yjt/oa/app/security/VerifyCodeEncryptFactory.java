package cn.yjt.oa.app.security;

public class VerifyCodeEncryptFactory {

	public static VerifyCodeEncrypt createVerifyCodeEncrypt(String type){
		if("1".equals(type)){
			return new Base64Encrypt();
		}else if("2".equals(type))
		{
			return new DESEncrpypt();
		}else if("3".equals(type)){
			return new MD5Encriypt();
		}else{
			return new DefaultVerifyCodeEncrypt();
		}
	}
}
