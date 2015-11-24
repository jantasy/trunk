package com.chinatelecom.nfc.Debug;
/**
 * 上线后，所有变量设置为false;
 * @author ycliuc
 *
 */
public class MyDebug {
	public static boolean DEBUG = 					false;
	public static boolean DEBUG_NO_NFC_PHONE = 		false;
	public static boolean TEST_DATA = 				false;
	public static boolean LIFTCYCLE = 				false;
	public static boolean PULLTOREFRESH = 			false;
	
	public static void show(boolean b,String str){
		if( b) System.out.println(str);
	}
}