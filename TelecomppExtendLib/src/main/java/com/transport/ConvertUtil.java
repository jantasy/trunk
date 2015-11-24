package com.transport;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import com.telecompp.util.ConversionTools;

import android.text.Html;
import android.text.Spanned;

public class ConvertUtil
{

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
	 * 
	 * @param fen
	 * @return
	 */
	public static String cen2Money(int fen) {
		StringBuilder sb = new StringBuilder();
		int i = fen / 100;
		fen %= 100;
		sb.append("" + i + ".");
		i = fen / 10;
		fen %= 10;
		sb.append("" + i + "" + fen);
		return sb.toString();
	}
	public static void sys(String msg){
		System.out.println(msg+"");
	}
	private static String hexString = "0123456789ABCDEF";
	/** 
	 * 将16进制的字符串转化成byte
	 * @param cmd
	 * @return  
	 */
  public static byte[] hexstr2byte(String cmd)
  {
    cmd = cmd.replaceAll(" ", "");
    if(cmd.length()%2!=0){
    	cmd = "0"+cmd;
    }
    byte[] res = new byte[cmd.length() / 2];
    for (int i = 0; i < res.length; i++) {
      res[i] = ((byte)Integer.parseInt(cmd.substring(i * 2, i * 2 + 2), 16));
    }
    return res;
  }
  /**
   * 将byte数组转化成十六进制的字符串
   * @param bytes
   * @return
   */
  public static String byte2hex(byte[] bytes) {
    String res = "";
    for (int i = 0; i < bytes.length; i++) {
      res = res + shex(bytes[i]);
    }
    return res;
  }
  
  public static String byte2hex(byte[] bytes,int start,int len) {
	    String res = "";
	    for (int i = start; i < start+len; i++) {
	      res = res + shex(bytes[i]);
	    }
	    return res;
	  }
  /**
   * 将byte转化成十六进制取2位
   * @param b
   * @return
   */
  public static String shex(byte b) {
    String temp = Integer.toHexString(b);
    if (temp.length() == 2)
      return temp;
    if (temp.length() == 1)
      return "0" + temp;
    if (temp.length() > 2) {
      return temp.substring(temp.length() - 2, temp.length());
    }
    return null;
  }
  /**
   * 删除字符串中的空格
   * @param src
   * @return
   */
  public static String trimspace(String src) {
    String ret = "";
    for (int i = 0; i < src.length(); i++) {
      if (' ' != src.charAt(i)) {
        ret = ret + src.charAt(i);
      }
    }
    return ret;
  }
  public static String xHex(String lsData) {
    String xLsData = "";
    for (int i = 0; i < lsData.length(); i++) {
      xLsData = xLsData + Integer.toHexString(15 - Integer.parseInt(lsData.substring(i, i + 1), 16));
    }
    return xLsData;
  }

  public static String xHex(String s1, String s2) {
    if (s1.length() != s2.length()) {
      return null;
    }
    byte[] b1 = hexstr2byte(s1);
    byte[] b2 = hexstr2byte(s2);
    byte[] res = new byte[b1.length];
    for (int i = 0; i < b2.length; i++) {
      res[i] = ((byte)(b1[i] ^ b2[i]));
    }
    return byte2hex(res);
  }

  public static String shex(int val) {
    String temp = Long.toHexString(val);
    String ret = "";
    switch (temp.length()) {
    case 1:
      ret = "0" + temp;
      break;
    default:
      ret = temp.substring(temp.length() - 2, temp.length());
    }

    return ret;
  }
  /**
   * 
   * @param data
   * @return
   */
  public static String invertSequence(String data)
  {
    if (data.length() % 2 != 0) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < data.length() / 2; i++) {
      sb.append(data.substring(data.length() - 2 * i - 2, data.length() - 2 * i));
    }
    return sb.toString();
  }
  
  public static String complete00(String src, int toLen, String str, boolean isHead) {
	    StringBuffer ret = new StringBuffer(src);
	    int temp = toLen - src.length();
	    for (int i = 0; i < temp; i++) {
	      if (isHead)
	        ret.insert(0, str);
	      else {
	        ret.append(str);
	      }
	    }
	    return ret.toString();
	  }

	  public static String complete00(String src, int len) {
	    return complete00(src, len, "0", true);
	  }

	  /**
		 * 将字符串转化成证书数
		 * @param str 带转换的字符串
		 * @param radix 进制
		 * @param type 1低位在前0 高位在前
		 * @return
		 */
		public static int str2Int(String str,int radix,int type){
			if(type == 0){
				return Integer.parseInt(str,radix);
			}else{
				if(str.length()%2!=0){
					return -1;
				}else{
					StringBuilder sb = new StringBuilder();
					int len = str.length()/2;
					for(int i=len;i>0;i--){
						sb.append(str.subSequence((i-1)*2, i*2));
					}
					return Integer.parseInt(sb.toString(),radix);
				}
			}
		}
		public static int str2Cent(String money){
			String[] strs = money.split("\\.");
			int imoney = Integer.parseInt(strs[0])*100;
			if(strs.length==2){
				int cent = Integer.parseInt("1"+strs[1]);
				if(cent<100){
					cent=(cent -10)*10;
				}else{
					cent -= 100;
				}
				imoney += cent;
			}
			return imoney;
		}
		public static void reverse(byte[] buf){
			if(buf != null){
				int s = 0,e = buf.length-1;
				byte temp;
				for(;s<e;s++,e--){
					temp = buf[s];
					buf[s] = buf[e];
					buf[e] = temp;
				}
			}
		}
		
		/**
		 * 返回反码
		 * 
		 * @param code
		 * @return
		 */
		public static String getRCode(String code) {
			StringBuilder sb = new StringBuilder();
			if (code == null) {
				return null;
			}
			int length = code.length();
			for (int i = 0; i < length; i++) {
				sb.append(Integer.toHexString(15 - Integer.parseInt(
						code.substring(i, i+1), 16)));
			}
			return sb.toString();
		}
		
		public static Spanned getShowMoney(String money){
			String mm = "<font color=\"#FFA500\">"+money+"</font>";
			mm +="<font color=\"#000000\">元</font>";
			return Html.fromHtml(mm);
		}
		public static Spanned getResult(String money,String color){
			return Html.fromHtml("<font color=\""+color+"\">"+money+"</font>");
		}
		public static byte[] decode(String bytes) {
			if (bytes == null) {
				return null;
			}
			ByteArrayOutputStream baos;
			bytes = bytes.toUpperCase();
			if (bytes.length() % 2 != 0) {
				baos = new ByteArrayOutputStream((bytes.length() + 1) / 2);
			} else
				baos = new ByteArrayOutputStream(bytes.length() / 2);
			for (int i = 0; i < bytes.length(); i += 2) {
				//
				if (i == bytes.length() - 1) {
					baos.write(hexString.indexOf(bytes.toUpperCase().charAt(i)));
				}
				if (i != bytes.length() - 1)
					baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
							.indexOf(bytes.charAt(i + 1))));
			}
			return baos.toByteArray();
		}
		
		public static String dumpBytes(byte[] bytes) {
			int i;
			if(bytes==null){
				return "";
			}
			StringBuffer sb = new StringBuffer();
			for (i = 0; i < bytes.length; i++) {
				int n = bytes[i] >= 0 ? bytes[i] : 256 + bytes[i];
				String s = Integer.toHexString(n);
				if (s.length() < 2) {
					s = "0" + s;
				}
				if (s.length() > 2) {
					s = s.substring(s.length() - 2);
				}
				sb.append(s);
			}
			return sb.toString().toUpperCase();
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
