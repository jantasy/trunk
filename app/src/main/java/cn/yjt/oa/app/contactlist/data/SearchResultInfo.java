package cn.yjt.oa.app.contactlist.data;

import cn.yjt.oa.app.beans.CommonContactInfo;

public class SearchResultInfo {
	public ContactlistGroupInfo cGroupInfo;
	public ContactlistContactInfo cContactInfo;
	public CommonContactInfo commonContactInfo;
	public int currPage;
	public int infoPos;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + infoPos;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchResultInfo other = (SearchResultInfo) obj;
		if (infoPos != other.infoPos)
			return false;
		return true;
	}

}
