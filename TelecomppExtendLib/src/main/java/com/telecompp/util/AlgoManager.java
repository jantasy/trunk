package com.telecompp.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
 * 加密解密算法管理
 * 
 * @author xiongdazhuang 
 * 
 */
public class AlgoManager
{

	private final static byte[] DESIV =
	{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, };

	/**
	 * RSA加密、解密
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            数据
	 * @param mode
	 *            模式 true encrypt; false decrypt
	 * @return 解密或加密的结果
	 */
	public static String RSACrypto(String key, byte[] data, Boolean mode)
	{

		KeyFactory keyFactory = null;
		RSAPublicKey publicKey = null;
		Cipher cipher = null;
		int blockSize;

		byte[] pubKeyExp =
		{ (byte) 0x01, (byte) 0x00, (byte) 0x01 };
		String outputString = null;

		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(key, 16), new BigInteger(pubKeyExp));

		try
		{
			keyFactory = KeyFactory.getInstance("RSA");
			publicKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

			if (mode)
			{
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			}
			else
			{
				cipher.init(Cipher.DECRYPT_MODE, publicKey);
			}
			blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(data.length);
			int leavedSize = data.length%blockSize;
			int blocksSize = leavedSize!=0?data.length/blockSize+1:data.length/blockSize;
			byte[] raw = new byte[outputSize*blocksSize];
			int i = 0;
			while(data.length-i*blockSize>0){
				if(data.length-i*blockSize>blockSize){
					cipher.doFinal(data, i*blockSize, blockSize, raw,i*outputSize);
				}else{
					cipher.doFinal(data, i * blockSize, data.length - i * blockSize, raw, i * outputSize);
				}
				i++;
			}
			if (raw.length != 0)
			{
				outputString = ConversionTools.ByteArrayToString(raw, raw.length);
			}

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}

		return outputString;
	}

	/**
	 * 3DES加密/解密
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            数据
	 * @param encMode
	 *            模式 true:encrypt false:decrypt
	 * @param algoCBC
	 *            true:CBC false: ECB
	 * @return 结果
	 */
	public static String TDesCryption(String key, String data, boolean encMode, Boolean algoCBC)
	{
		String outData = null;
		byte[] outDataBuf;
		;
		byte[] inDataBuf;
		int length = 0;
		AlgorithmParameterSpec IV = null;
		int cryptMode;

		String algo;

		if (algoCBC)
		{
			algo = "DESede/CBC/NoPadding";
			IV = new IvParameterSpec(DESIV);
		}
		else
		{
			algo = "DESede/ECB/NoPadding";
		}

		if (encMode)
		{
			cryptMode = Cipher.ENCRYPT_MODE;
			// padding
			length = data.length() / 2;
			int paddingLen = 8 - length % 8;
			inDataBuf = new byte[length + paddingLen];
			System.arraycopy(ConversionTools.stringToByteArr(data), 0, inDataBuf, 0, length);
			inDataBuf[length] = (byte) 0x80;

		}
		else
		{
			cryptMode = Cipher.DECRYPT_MODE;
			inDataBuf = ConversionTools.stringToByteArr(data);
		}

		outDataBuf = new byte[inDataBuf.length];

		try
		{
			Cipher cipher = Cipher.getInstance(algo);
			SecretKey macTDESKey = new SecretKeySpec(ConversionTools.stringToByteArr(key), algo);

			if (algoCBC)
			{
				cipher.init(cryptMode, macTDESKey, IV);
			}
			else
			{
				cipher.init(cryptMode, macTDESKey);
			}

			length = cipher.doFinal(inDataBuf, 0, inDataBuf.length, outDataBuf);
			if (!encMode)
			{
				// remove padding
				for (int i = 1; i < 9; i++)
				{
					if ((outDataBuf[length - i] != (byte) 0x80) && (outDataBuf[length - i] != (byte) 0x00))
					{
						break;
					}
					if (outDataBuf[length - i] == (byte) 0x80)
					{
						length -= i;
						break;
					}
				}
			}
			if (length != 0)
			{
				outData = ConversionTools.ByteArrayToString(outDataBuf, length);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return outData;
	}

	/**
	 * 做SHA-1加密
	 * 
	 * @param s
	 *            数据
	 * @return
	 */
	public static String SHA1(String s)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 转HEX数
	 * 
	 * @param keyData
	 * @return
	 */
	public static String toHexString(byte[] keyData)
	{
		if (keyData == null)
		{
			return null;
		}
		int expectedStringLen = keyData.length * 2;
		StringBuilder sb = new StringBuilder(expectedStringLen);
		for (int i = 0; i < keyData.length; i++)
		{
			String hexStr = Integer.toString(keyData[i] & 0x00FF, 16);
			if (hexStr.length() == 1)
			{
				hexStr = "0" + hexStr;
			}
			sb.append(hexStr);
		}
		return sb.toString();
	}

	private static final String HMAC_SHA1 = "HmacSHA1";

	/**
	 * 生成签名数据
	 * 
	 * @param data
	 *            待加密的数据
	 * @param key
	 *            加密使用的key
	 * @return 生成MD5编码的字符串
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.NoSuchAlgorithmException
	 */
	public static String getSignature(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException
	{
		SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(data);
		return ConversionTools.ByteArrayToString(rawHmac, rawHmac.length);
	}
	/**
	 * @param s
	 *            待计算字符串
	 * @return 字符串MD5值
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
