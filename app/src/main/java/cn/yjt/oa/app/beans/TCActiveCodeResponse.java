package cn.yjt.oa.app.beans;

public class TCActiveCodeResponse extends TCResponse {
	private String msg;
	private String ecp_token;
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getEcp_token() {
		return ecp_token;
	}

	public void setEcp_token(String ecp_token) {
		this.ecp_token = ecp_token;
	}

	@Override
	public String toString() {
		return "TCActiveCode [msg=" + msg + ", ecp_token=" + ecp_token + "]";
	}
	
}
