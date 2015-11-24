package com.telecompp.util;

/**
 * package cipher define contants
 * 
 * @author wanglinfang
 *  
 */
public class CipherContants
{

	public static final byte[] IV =
	{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	public static final byte[] Padding =
	{ (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	// ===============================RSA contants============================
	public static final byte[] byte_pub_exponent =
	{ 0x01, 0x00, 0x01 };// 公钥指数，默认65535
	public static final String RSA_Name_Public_Modulus = "RSA_Pub_Modulus";
	public static final String RSA_Name_Public_Exponent = "RSA_Pub_Exponent";
	public static final String RSA_Name_Private_Modulus = "RSA_Pri_Modulus";
	public static final String RSA_Name_Private_Exponent = "RSA_Pri_Exponent";
	public static final String RSA_Name_Private_P = "RSA_Pri_P";
	public static final String RSA_Name_Private_Q = "RSA_Pri_Q";
	public static final String RSA_Name_Private_DP = "RSA_Pri_DP";
	public static final String RSA_Name_Private_DQ = "RSA_Pri_DQ";
	public static final String RSA_Name_Private_QINV = "RSA_Pri_QINV";
	// ============RSA KeySpec init key type===============
	public static final int RSA_KeyType_PubKey = 1;
	public static final int RSA_KeyType_PriKey_Std = 2;
	public static final int RSA_KeyType_PriKey_Crt = 3;
	// =======alg========================================
	public static final String ALG_SHA1WithRSA = "SHA1WithRSA";
	public static final String ALG_MD2withRSA = "MD2withRSA";
	public static final String ALG_MD5withRSA = "MD5withRSA";
	public static final String ALG_SHA256withRSA = "SHA256withRSA";
	public static final String ALG_SHA384withRSA = "SHA384withRSA";
	public static final String ALG_SHA512withRSA = "SHA512withRSA";
	public static final String ALG_DES_CBC_NoPadding = "DES/CBC/NoPadding";
	public static final String ALG_DES_ECB_NoPadding = "DES/ECB/NoPadding";
	public static final String ALG_DESede_ECB_NoPadding = "DESede/ECB/NoPadding";
	public static final String ALG_DESede_CBC_NoPadding = "DESede/CBC/NoPadding";
	// =====message_digest_encrypt
	public static final String ALG_SHA1 = "SHA-1";
	public static final String ALG_SHA256 = "SHA-256";
	public static final String ALG_SHA384 = "SHA-384";
	public static final String ALG_SHA512 = "SHA-512";
	public static final String ALG_MD5 = "MD5";
	// ======message_digest_encrypt_hmac
	public static final String ALG_HmacSHA1 = "HmacSHA1";
	public static final String ALG_HmacSHA256 = "HmacSHA256";
	public static final String ALG_HmacSHA384 = "HmacSHA384";
}
