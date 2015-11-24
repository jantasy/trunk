package  cn.yjt.oa.app.beans;

import java.util.List;


public class IndustryTagGroupInfo{
	private long id;
	private String name;
	private String createTime;
	private List<IndustryTagInfo> industryTags;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public List<IndustryTagInfo> getIndustryTags() {
		return industryTags;
	}
	public void setIndustryTags(List<IndustryTagInfo> industryTags) {
		this.industryTags = industryTags;
	}
}
