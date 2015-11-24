package cn.yjt.oa.app.beans;

public class Response<T> {
	
	public static final int RESPONSE_OK = 0;
	public static final int RESPONSE_NOT_YOUR_ICCID = 16770014; //用户登录时，校验iccid，不是当前用户iccid，且不是当前用户所在企业其他用户iccid
    public static final int RESPONSE_OTHER_ICCID = 16770015; //用户登录时，校验iccid，使用的是当前用户所在企业其他用户的iccid
    public static final int RESPONSE_WEAK_PASSWORD = 16770026;//用户登录时，弱密码
    
	
	private int code;
	private String description;
	private T payload;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public T getPayload() {
		return payload;
	}
	public void setPayload(T payload) {
		this.payload = payload;
	}
}
