package com.telecompp.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 生成随机数
 *  
 * @author xiongdazhuang
 * 
 */
public class RandomMaker
{
	/**
	 * 获取随机数byte
	 * 
	 * @param length
	 *            长度
	 * @return 随机数byte
	 */
	public static byte[] getRandomByte(int length)
	{
		// TODO test
		SecureRandom random = null;
		try
		{
			random = SecureRandom.getInstance("SHA1PRNG");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		if (random == null) {
            return null;
        }
		byte bytes[] = new byte[length];
		random.nextBytes(bytes);
		return bytes;

		// byte[] array = new byte[length];
		//
		// for (int i = 0; i < length; i++)
		// {
		// array[i] = (byte) (Math.random() * 256);
		// }
		// return array;
	}
}
