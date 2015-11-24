package com.telecompp.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 数据编码、格式转换
 *  
 * @author xiongdazhuang
 * 
 */
public final class ConversionTools
{
	private final static char[] HEX =
	{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 工具函数：GBK coding to character String , eg. 0x31b6d432 to "1对2"
	 * 
	 * @param cArray
	 *            HEX value
	 * @return 含有汉字的字符串
	 */
	public static String GBKtoSring(String gbkEncoding)
	{
		String sResult = "";
		String sTmp = "";
		char c1, c2;
		int index = 0;

		// String to Chinese , eg. "31b6d432" to "1对2"
		index = 0;
		while (index < gbkEncoding.length())
		{
			if (gbkEncoding.charAt(index) >= '0' && gbkEncoding.charAt(index) <= '9')
			{
				// ASCII , eg. "31" to '1', "41" to 'A'
				c1 = (char) ((gbkEncoding.charAt(index) - '0') * 16);
				c2 = gbkEncoding.charAt(index + 1);
				if (c2 >= '0' && c2 <= '9')
				{
					c2 -= '0';
				}
				else if (c2 >= 'A' && c2 <= 'F')
				{
					c2 -= 'A' - 10;
				}
				else if (c2 >= 'a' && c2 <= 'f')
				{
					c2 -= 'a' - 10;
				}
				c1 += c2;
				sResult += c1;
				index += 2;
			}
			else
			{
				// GBK , eg. "b6d4" to "对"
				sTmp = gbkEncoding.substring(index, index + 4);
				sResult += toGbkChar(sTmp);
				index += 4;
			}
		}
		return sResult;
	}

	/**
	 * 工具函数：单个汉字，GBK code to Chinese character ,eg. "b6d4" to "对"
	 * 
	 * @param gbkEncoding
	 * @return corresponding Chinese
	 */
	public static char toGbkChar(String gbkEncoding)
	{
		if (gbkEncoding == null || !gbkEncoding.matches("[0-9a-fA-F]{4}"))
		{
			throw new IllegalArgumentException("GBK encoding data error!");
		}
		int num = Integer.parseInt(gbkEncoding, 16);
		if (num < 0 || num > 0xffff)
		{
			throw new IllegalArgumentException("GBK encoding data error!");
		}
		byte[] bys = new byte[2];
		bys[0] = (byte) (num >> 8 & 0xff);
		bys[1] = (byte) (num & 0xff);

		if (bys == null || bys.length != 2)
		{
			throw new IllegalArgumentException("GBK encoding byte length must be 2");
		}
		String str = null;
		try
		{
			str = new String(bys, "gbk");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException("GBK encoding convert error!");
		}
		if (str == null)
		{
			throw new IllegalArgumentException("GBK encoding convert error!");
		}
		return str.charAt(0);
	}

	/**
	 * 工具函数，将两个字符转换为一个字节，如‘A’‘B’-> 0xAB
	 * 
	 * @param oldData
	 *            待转换数据
	 * @param inLen
	 *            待转换数据长度，即字符个数
	 * @param newData
	 *            转换后的数据
	 * @param outLen
	 *            转换后数据长度，即字节个数
	 * @return true 转换成功； false 转换失败，有非HEX字符
	 */

	public static boolean StringToBCD(char[] oldData, int inLen, char[] newData, int outLen[])
	{

		char[] buff = new char[inLen];
		char j, k;
		int i;

		Arrays.fill(buff, (char) 0xFF);
		Arrays.fill(newData, (char) 0xFF);
		outLen[0] = (inLen + inLen % 2) / 2;

		// 对字符的BCD码转换
		for (i = 0; i < inLen; i++)
		{
			if (oldData[i] >= '0' && oldData[i] <= '9')
			{
				buff[i] = (char) (oldData[i] - 0x30);
			}
			else if (oldData[i] >= 'a' && oldData[i] <= 'f')
			{
				buff[i] = (char) (oldData[i] - 0x57);
			}
			else if (oldData[i] >= 'A' && oldData[i] <= 'F')
			{
				buff[i] = (char) (oldData[i] - 0x37);
			}
			else
			{
				return false;
			}
		}

		// 两个字符合成一个字节
		for (i = 0; i < (outLen[0] - 1); i++)
		{
			j = buff[2 * i];
			k = buff[2 * i + 1];
			newData[i] = (char) ((char) j << 4 & (char) 0xF0);
			newData[i] |= (char) (k & (char) 0x0F);
		}
		j = buff[2 * i];
		newData[i] = (char) ((char) j << 4 & (char) 0xF0);

		// 对字符长度奇偶的处理
		if ((inLen % 2) != 0)
		{
			newData[i] |= (char) 0x0F;
		}
		else
		{
			k = buff[2 * i + 1];
			newData[i] |= (char) (k & (char) 0x0F);
		}
		return true;
	}

	/**
	 * 
	 * @param oldData
	 *            待转换数据
	 * @param inLen
	 *            待转换数据长度
	 * @param newData
	 *            生成的转换数据
	 * @param offset
	 *            偏移
	 * @return 结果 0：成功 非0：失败
	 */
	public static int StringToBCD(String oldData, int inLen, byte[] newData, int offset)
	{
		char[] outData = new char[(inLen + inLen % 2) / 2];
		int[] len = new int[1];
		boolean result;

		result = StringToBCD(oldData.toCharArray(), oldData.length(), outData, len);

		if (result)
		{
			for (int i = 0; i < len[0]; i++)
			{
				newData[offset + i] = (byte) outData[i];
			}
			return len[0];
		}
		else
		{
			return 0;
		}

	}

	/**
	 * int型数据转byte
	 * 
	 * @param a
	 *            输入参数
	 * @return byte数据
	 */
	public static byte[] toBytes(int a)
	{
		return new byte[]
		{ (byte) (0x000000ff & (a >>> 24)), (byte) (0x000000ff & (a >>> 16)), (byte) (0x000000ff & (a >>> 8)),
				(byte) (0x000000ff & (a)) };
	}

	/**
	 * 数据转Int
	 * 
	 * @param b
	 *            byte数据
	 * @param s
	 *            起始点
	 * @param n
	 *            偏移
	 * @return 输出的Int数据
	 */
	public static int toInt(byte[] b, int s, int n)
	{
		int ret = 0;

		final int e = s + n;
		for (int i = s; i < e; ++i)
		{
			ret <<= 8;
			ret |= b[i] & 0xFF;
		}
		return ret;
	}

	/**
	 * 
	 * @param b
	 * @return
	 */
	public static int toInt(byte... b)
	{
		int ret = 0;
		for (final byte a : b)
		{
			ret <<= 8;

			ret |= a & 0xFF;
		}
		return ret;
	}

	/**
	 * 
	 * @param d
	 *            输入数据
	 * @param s
	 *            起始
	 * @param n
	 *            偏移
	 * @return 返回
	 */
	public static String toHexString(byte[] d, int s, int n)
	{
		final char[] ret = new char[n * 2];
		final int e = s + n;

		int x = 0;
		for (int i = s; i < e; ++i)
		{
			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}
		return new String(ret);
	}

	public static String toHexStringR(byte[] d, int s, int n)
	{
		final char[] ret = new char[n * 2];

		int x = 0;
		for (int i = s + n - 1; i >= s; --i)
		{
			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}
		return new String(ret);
	}

	/**
	 * 
	 * @param txt
	 * @param radix
	 * @param def
	 * @return
	 */
	public static int parseInt(String txt, int radix, int def)
	{
		int ret;
		try
		{
			ret = Integer.valueOf(txt, radix);
		}
		catch (Exception e)
		{
			ret = def;
		}

		return ret;
	}

	/**
	 * byte数据 转 String
	 * 
	 * @param indata
	 * @param len_indata
	 * @return
	 */
	public static String ByteArrayToString(byte[] indata, int len_indata)
	{

		int g_RespLen = len_indata;
		byte[] g_Response = indata;

		int m = 0;
		String g_InfoString = "";

		while (m < g_RespLen)
		{
			if ((g_Response[m] & 0xF0) == 0x00)
			{
				g_InfoString += '0' + Integer.toHexString((short) (0x00FF & g_Response[m]));
			}
			else
			{
				g_InfoString += Integer.toHexString((short) (0x00FF & g_Response[m]));
			}
			m++;
		}

		return g_InfoString.toUpperCase();
	}

	/**
	 * Byte 转String
	 * 
	 * @param indata
	 * @param offset
	 * @param len_indata
	 * @return
	 */
	public static String ByteArrayToString(byte[] indata, int offset, int len_indata)
	{

		int g_RespLen = len_indata;
		byte[] g_Response = indata;

		int m = offset;
		String g_InfoString = "";

		while (m < (offset + g_RespLen))
		{
			if ((g_Response[m] & 0xF0) == 0x00)
			{
				g_InfoString += '0' + Integer.toHexString((short) (0x00FF & g_Response[m]));
			}
			else
			{
				g_InfoString += Integer.toHexString((short) (0x00FF & g_Response[m]));
			}
			m++;
		}

		return g_InfoString;
	}

	/**
	 * String 转 byte
	 * 
	 * @param iStr
	 * @return
	 */
	public static byte[] stringToByteArr(String iStr)
	{
		iStr = iStr.replaceAll(" ", "");
//		iStr = iStr.replaceAll(System.getProperty("line.separator"), "");
		iStr = iStr.replaceAll("\n", "");
		iStr = iStr.replaceAll("\t", "");
		if (iStr.length() == 0)
		{
			return null;
		}
		if (iStr.length() % 2 != 0)
		{
			iStr += "F";
		}
		else
		{

		}
		byte[] outArr = new byte[iStr.length() / 2];
		byte b = 0;
		String hex = "";

		for (int i = 0; i < iStr.length(); i += 2)
		{
			hex = iStr.substring(i, i + 2);
			try
			{
				if (hex.equalsIgnoreCase(null) || hex.equalsIgnoreCase(""))
				{
					break;
				}
				b = (byte) ((int) Integer.parseInt(hex, 16) & 0xFF);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				break;
			}

			outArr[i / 2] = b;
		}
		return outArr;
	}

	/**
	 * 获取short类型
	 * 
	 * @param src
	 * @param srcoffset
	 * @return
	 */
	public static short getShort(byte[] src, short srcoffset)
	{
		short result = 0;

		result = (short) (((src[srcoffset + 0] << 8) | src[srcoffset + 1] & 0xff));

		return result;
	}

	/**
	 * 数据转换 eg 000000000001 -> 0.01
	 * 
	 * @param newBalanceString
	 * @return
	 */
	public static String format2Amount(String newBalanceString)
	{
		newBalanceString = newBalanceString.substring(0, 10) + "." + newBalanceString.substring(10, 12);

		int i = 0;
		String newBalanceStringFinal = "";

		for (i = 0; i < 10; i++)
		{
			if (!newBalanceString.substring(i, i + 1).equals("0"))
			{
				newBalanceStringFinal = newBalanceString.substring(i, 13);
				break;
			}
		}

		if (i == 10)
		{
			newBalanceStringFinal = "0." + newBalanceString.substring(11, 13);
		}
		String balance = newBalanceStringFinal;
		return balance;
	}

	/**
	 * 数据转换 eg 0.01 -> 000000000001
	 * 
	 * @param transAmount
	 * @return
	 */
	public static String amount2Format(String transAmount)
	{
		int i = 0;

		while (i < transAmount.length())
		{
			if (transAmount.charAt(i) == '.')
			{
				if ((transAmount.length() - i - 1) == 2)
				{
					break;
				}
				else if ((transAmount.length() - i - 1) == 1)
				{
					transAmount += '0';
					break;
				}
				else if ((transAmount.length() - i - 1) == 0)
				{
					transAmount += "00";
					break;
				}
			}
			i++;
		}
		if (i == transAmount.length())
		{
			// amount = transAmount + '.' + "00";
			// no decimal point
			transAmount += "00";
			while (transAmount.length() < 12)
			{
				transAmount = '0' + transAmount;
			}
		}
		else
		{
			// amount = transAmount;
			// get rid of decimal point
			transAmount = transAmount.substring(0, i) + transAmount.substring(i + 1, transAmount.length());
			while (transAmount.length() < 12)
			{
				transAmount = '0' + transAmount;
			}
		}

		return transAmount;
	}

	/**
	 * 金额格式化
	 * 
	 * @param transAmount
	 *            输入的String类型金额
	 * @return 输出的格式化金额
	 */
	public static String amountToEwalletFormat(String transAmount)
	{
		int i = 0;

		if (transAmount.contains("."))
		{
			while (i < transAmount.length())
			{
				if (transAmount.charAt(i) == '.')
				{
					if ((transAmount.length() - i - 1) == 2)
					{
						break;
					}
					else if ((transAmount.length() - i - 1) == 1)
					{
						transAmount += '0';
						break;
					}
					else if ((transAmount.length() - i - 1) == 0)
					{
						transAmount += "00";
						break;
					}
				}
				i++;
			}
		}
		else
		{
			transAmount += "00";
		}
		transAmount = transAmount.replace(".", "");
		int amountInt = Integer.parseInt(transAmount, 10);
		transAmount = Integer.toString(amountInt, 16);

		while (transAmount.length() < 8)
		{
			transAmount = '0' + transAmount;
		}

		return transAmount;
	}

	/**
	 * 格式化数据
	 * 
	 * @param newBalanceString
	 *            输入的金额
	 * @return 输出的格式化金额
	 */
	public static String formatHex2Amount(String newBalanceString)
	{
		int amount = Integer.valueOf(newBalanceString, 16);
		SumaCalculate sumaCalculateHandler = new SumaCalculate();

		return sumaCalculateHandler.calc((double) amount, 100, "/");
	}

	/**
	 * 交易类型文字说明
	 * 
	 * @param indexString
	 *            输入的交易类型编码
	 * @return 输出的文字
	 */
	public static String transTypeCode2String(String indexString)
	{
		if (indexString.equals("00"))
		{
			return "无效记录";
		}
		else if (indexString.equals("01"))
		{
			return "圈存";
		}
		else if (indexString.equals("02"))
		{
			return "圈存";
		}
		else if (indexString.equals("03"))
		{
			return "圈存";
		}
		else if (indexString.equals("04"))
		{
			return "取款";
		}
		else if (indexString.equals("05"))
		{
			return "脱机消费";
		}
		else if (indexString.equals("06"))
		{
			return "脱机消费";
		}
		else if (indexString.equals("07"))
		{
			return "修改透支限额";
		}
		else if (indexString.equals("08"))
		{
			return "信用消费";
		}
		else if (indexString.equals("09"))
		{
			return "联机消费";
		}
		else if (indexString.equals("10"))
		{
			return "联机消费";
		}
		else
		{
			return null;
		}
	}

	/**
	 * GBK 2 String
	 * 
	 * @param indata
	 * @return
	 */
	public String GBK2String(String indata)
	{
		if (indata == null)
		{
			return null;
		}

		String finalDataString = null;

		byte[] indataByte = ConversionTools.stringToByteArr(indata);
		try
		{
			finalDataString = new String(indataByte, "GBK");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}

		return finalDataString;
	}

	/**
	 * String 2 GBK
	 * 
	 * @param indata
	 * @return
	 */
	public static String string2GBK(String indata)
	{
		if (indata == null)
		{
			return null;
		}

		byte[] GBKDataBytes;

		try
		{
			GBKDataBytes = indata.getBytes("GBK");
		}
		catch (UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
			return null;
		}

		String GBKDataString = ConversionTools.ByteArrayToString(GBKDataBytes, GBKDataBytes.length);
		return GBKDataString;
	}

	
	
}
