package cn.yjt.oa.app.office.cypher;
/**
 * 系统  综合办公开发平台-核心业务系统
 * 项目  ioop-util 
 * 创建时间  2014-3-3 上午11:17:12 
 * Copyright (c) 2014, 中国电信甘肃万维公司 All rights reserved.
 * 中国电信甘肃万维公司 专有/保密源代码,未经许可禁止任何人通过任何* 渠道使用、修改源代码.
 */
/** 
 * Project Name:ioop-util 
 * File Name:ThreeDES.java 
 * Package Name:com.gsww.ioop.util.outMail 
 * Date:2014-3-3上午11:17:12 
 * Copyright (c) 2014, 中国电信甘肃万维公司 All rights reserved.
 * 
 */  

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * 类名: ThreeDES <br/> 
 * 功能: ThreeDES算法 <br/> 
 * 创建日期: 2014-3-3 上午11:17:12 <br/> 
 *
 * @author 
 * @version V3.0
 * @since Jdk 1.6
 * @see       
 *
 */

public class ThreeDES {

private static final String Algorithm = "DESede"; // DES,DESede,Blowfish
    
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
       try {
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptMode(byte[] keybyte, byte[] src) {      
    try {
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b) {
        String hs="";
        String stmp="";
        for (int i=0; i < b.length; i++) {
            stmp=(Integer.toHexString(b[i] & 0XFF));
            if (stmp.length() == 1)
            	hs += ("0" + stmp);
            else 
            	hs += stmp;
        }
        return hs.toUpperCase();
    }
    
    public static String byte2Hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}
	
	public static byte[] hex2Byte(String str) {
		if (str == null)
			return null;
		str = str.trim();
		int len = str.length();
		if (len == 0 || len % 2 == 1)
			return null;
		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}
	}
    
    public static String bytesToHexString(byte[] b){
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < b.length; i++) {
    		sb.append(Integer.toHexString(b[i]));
		}
    	return sb.toString().toUpperCase();
    }
    
    public static void main(String args[]){
    	//1.生成用户信息
    	String userInfo = "18966668888|990002717980997|20130831093021";
    	//2.生成加密串
    	byte [] encode = ThreeDES.encryptMode("zonghebangongwangluoban3".getBytes(),userInfo.getBytes());
    	String encryptedStr  = ThreeDES.byte2hex(encode);
    	//3.生成token
    	String token = "18966668888|990002717980997|20130831093021"+"|"+encryptedStr ;
  	    System.out.println(token);
    }
}
