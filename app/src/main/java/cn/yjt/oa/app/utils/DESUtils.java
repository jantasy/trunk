package cn.yjt.oa.app.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils  {

	//算法秘钥
	static final byte[] RAW_KEY_DATA = { 0x11, 0x22, 0x33, 0x44, 0x44, 0x33,
			0x22, 0x11 };



	/**
	 * encrypt
	 * 
	 * @param rawKeyData
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte rawKeyData[], String source)
			throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key, sr);
		byte data[] = source.getBytes();
		byte[] encryptedData = cipher.doFinal(data);
		return encryptedData;
	}

	/**
	 * decrypt
	 * 
	 * @param rawKeyData
	 * @param encryptedData
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(byte rawKeyData[], byte[] encryptedData)
			throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, key, sr);
		byte decryptedData[] = cipher.doFinal(encryptedData);
		return new String(decryptedData, "utf-8");
	}

	public static String byte2HexStr(byte[] b) {
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
		}
		return sb.toString().trim();
	}

	public static byte[] hexStr3Byte(String str) {
		if (str.length() % 2 != 0) {
			return new byte[0];
		}

		char[] c = str.toCharArray();

		byte[] b = new byte[c.length / 2];
		for (int i = 0; i < c.length; i += 2) {
			char c1 = c[i];
			char c2 = c[i + 1];
			int t = Integer.parseInt("" + c1 + c2, 16);
			b[i / 2] = (byte) t;
		}

		return b;
	}

	public static void main(String[] args) {
		try {
			byte[] encrypt = encrypt(RAW_KEY_DATA, "1234");
			String decrypt = decrypt(RAW_KEY_DATA, encrypt);
			System.out.println(decrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
