package com.telecompp;

import com.telecompp.util.ConversionTools;
import com.telecompp.util.RSAUtil;
import com.telecompp.util.SumaConstants;

public class PayPlugHelp implements SumaConstants
{
	/**
	 * 商户签名验签
	 * "商户编号||商户订单号||支付金额||支付订单编号" 取SHA1值后在用碰碰平台证书进行签名
	 * 
	 * @param indataString
	 * @param signature 
	 * @return
	 */
	//84882ee5b2dea430e5f6d4d5ddafd048d8c63909146e58621b399b2eb987dd173dba2ff284bb206fd8861e5a5624afbff939412a11b19735b9aba5344df832e2df1eb92f3c84eff9938596df15aa32f34584d96dd79e6cdaf4c3fea40381eebcbaa8613d8b96e61a73b773bf6f898579eb093e0ef0b40c8fa99b0b4128e1fbe3
//	String RSA_MODULU ="84882ee5b2dea430e5f6d4d5ddafd048d8c63909146e58621b399b2eb987dd173dba2ff284bb206fd8861e5a5624afbff939412a11b19735b9aba5344df832e2df1eb92f3c84eff9938596df15aa32f34584d96dd79e6cdaf4c3fea40381eebcbaa8613d8b96e61a73b773bf6f898579eb093e0ef0b40c8fa99b0b4128e1fbe3"; 
	String RSA_MODULU ="84a9e1f891fe0bcdc65395d8697dfe34eb21fda94a622c6e5022e31e113d6af7d82c28ec44b7d0b7c33817bd3a2a070f0fe31c60de7f11b8d7578043a49f22f9a09aaba2733764403b27d94bda7a9327e5fa706dcfa5516797c23f7e24bd6e851923466d614839b4be0c25b2ecc0672f57f863ffee223111f324a8803670bca9"; 
			//"a68c8e0fa1d1efbdb9a38f9b547e0056c8166fcc588b89164d1167eab6d4ce7d04b3e739d1fef8ba2c0f8c9e742ce2e3c513d7cb7932eba15a30b9853653e0e91d22c12e3f5ed2e6fcdb800c5d8124e2f0573e7ff2fc080d83d7c885893ab0d0ccb97159131097dd8be017abc086e0cdbbcf30f11443aabaa26cb43ae24002dd";
	String pub_exponet = "010001";

	public String VerifySignature(String indataString, String signature)
	{
		Boolean verifyResult = RSAUtil.rsa_pubkey_verify(ConversionTools.stringToByteArr(indataString),
				ConversionTools.stringToByteArr(signature), "SHA1WITHRSA", RSA_MODULU, pub_exponet);
		if (verifyResult == true)
		{
			return "00";
		}
		else
		{
			return null;
		}
	}
}
