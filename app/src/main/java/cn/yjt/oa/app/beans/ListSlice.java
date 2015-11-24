package cn.yjt.oa.app.beans;

import java.io.Serializable;
import java.util.List;

public class ListSlice<T> implements Serializable{

	private long total;
	private List<T> content;

	public ListSlice(long total, List<T> content) {
		this.total = total;
		this.content = content;
	}

	public long getTotal() {
		return total;
	}

	public List<T> getContent() {
		return content;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	

}
