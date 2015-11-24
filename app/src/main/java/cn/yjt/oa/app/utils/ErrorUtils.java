package cn.yjt.oa.app.utils;

import io.luobo.common.http.ErrorType;

public final class ErrorUtils {

	
	public static String getErrorDescription(ErrorType errorType){
		if(errorType == ErrorType.AUTH_FAILURE_ERROR){
			return "授权验证失败";
		}else if(errorType == ErrorType.BAD_REQUEST_ERROR){
			return "错误请求";
		}else if(errorType == ErrorType.INTERRUPTED_ERROR){
			return "请求已取消";
		}else if(errorType == ErrorType.NETWORK_ERROR){
			return "网络连接错误";
		}else if(errorType == ErrorType.NO_CONNECTION_ERROR){
			return "网络连接错误";
		}else if(errorType == ErrorType.PARSE_ERROR){
			return "数据解析错误";
		}else if(errorType == ErrorType.SERVER_ERROR){
			return "服务器响应错误";
		}else if(errorType == ErrorType.TIMEOUT_ERROR){
			return "请求超时";
		}else if(errorType == ErrorType.UNKNOWN_ERROR){
			return "请求失败";
		}
		return "请求失败";
	}
}
