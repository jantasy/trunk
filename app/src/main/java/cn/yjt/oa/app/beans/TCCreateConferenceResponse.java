package cn.yjt.oa.app.beans;

public class TCCreateConferenceResponse extends TCResponse {
	private String msg;
	private String conference_id;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getConference_id() {
		return conference_id;
	}
	public void setConference_id(String conference_id) {
		this.conference_id = conference_id;
	}
	@Override
	public String toString() {
		return "TCCreateConferenceResponse [msg=" + msg + ", conference_id="
				+ conference_id + "]";
	}
}
