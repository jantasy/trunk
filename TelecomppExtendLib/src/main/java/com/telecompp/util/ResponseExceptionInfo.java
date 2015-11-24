package com.telecompp.util;

public class ResponseExceptionInfo {

	private static String errorCode;
	private static String errorMsg;
	
	private static String httpResponseCode;
	
	public static String getHttpResponseCode() {
		return httpResponseCode;
	}
	public static void setHttpResponseCode(String httpResponseCode) {
		ResponseExceptionInfo.httpResponseCode = httpResponseCode;
	}
	public static String getErrorCode() {
		return errorCode;
	}
	public static void setErrorCode(String errorCode) {
		ResponseExceptionInfo.errorCode = errorCode;
	}
	public static String getErrorMsg() {
		return errorMsg;
	}
	public static void setErrorMsg(String errorMsg) {
		ResponseExceptionInfo.errorMsg = errorMsg;
	}
	
	
	//	F200	找不到该商户
	public static String getErrorMsgFromHttpResponseCode(String httpResponseCode) {
		if("F200".equals(httpResponseCode)) {
			return "商户找不到!";
		} else if("F255".equals(httpResponseCode)) {
			return "该功能需要营业厅受理，请拨打10000号或咨询客户经理开通(F255)!";
		} else if("F991".equals(httpResponseCode)) {
			return "该功能需要营业厅受理，请拨打10000号或咨询客户经理开通(F991)!";
		} else if("500".equals(httpResponseCode)) {
			return "httpResponseCode=500, 网络连接异常";
		} else if("F300".equals(httpResponseCode)) {
			return "该功能需要营业厅受理，请拨打10000号或咨询客户经理开通(F300)!";
		}
		return "网络错误";
	}
}
