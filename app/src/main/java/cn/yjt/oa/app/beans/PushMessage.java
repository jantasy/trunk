package cn.yjt.oa.app.beans;

import io.luobo.common.http.volley.GsonConverter;

import java.lang.reflect.Type;

public class PushMessage {

	private String cmd;
	private String payload;
	private Object payloadObject;
	private String custId;
	
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String payload(){
		return payload;
	}
	
	public Object getPayload(GsonConverter converter, Type type) {
		if (payloadObject == null && payload != null) {
			payloadObject = converter.convertToObject(payload, type);
		}
		
		return payloadObject;
	}
}
