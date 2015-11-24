package cn.yjt.oa.app.beans;

import io.luobo.widget.XNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class DeptDetailInfo implements XNode ,Parcelable{
	private long id; // 部门ID
	private String name; // 部门名称
	private List<DeptDetailInfo> children; // 子部门集合
	private List<DeptDetailUserInfo> members; // 部门成员集合
	private int orderIndex;
	
	public int getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	private List<XNode> xChildren;
	private long parentId;
	
	public DeptDetailInfo() {
	}
	@SuppressWarnings("unchecked")
	public DeptDetailInfo(Parcel in) {
		id = in.readLong();
		name = in.readString();
		children = in.readArrayList(DeptDetailInfo.class.getClassLoader());
		members = in.readArrayList(DeptDetailUserInfo.class.getClassLoader());
		parentId = in.readLong();
		orderIndex = in.readInt();
	}

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

	public List<DeptDetailInfo> getChildren() {
		return children;
	}

	public void setChildren(List<DeptDetailInfo> children) {
		this.children = children;
	}

	public List<DeptDetailUserInfo> getMembers() {
		return members;
	}

	public void setMembers(List<DeptDetailUserInfo> members) {
		this.members = members;
	}

	@Override
	public List<XNode> getXChildren() {
		if(xChildren == null){
			xChildren = new ArrayList<XNode>();
			if(members != null){
				xChildren.addAll(members);
			}
			if(children != null){
				xChildren.addAll(children);
			}
		}
		return xChildren;
	}

	@Override
	public String getXTitle() {
		return name;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeList(children);
		dest.writeList(members);
		dest.writeLong(parentId);
		dest.writeInt(orderIndex);
	}
	
	public static final Creator<DeptDetailInfo> CREATOR = new Creator<DeptDetailInfo>() {
		
		@Override
		public DeptDetailInfo[] newArray(int size) {
			return new DeptDetailInfo[size];
		}
		
		@Override
		public DeptDetailInfo createFromParcel(Parcel source) {
			return new DeptDetailInfo(source);
		}
	};

	@Override
	public String toString() {
		return "DeptDetailInfo [id=" + id + ", name=" + name + ", children="
				+ children + ", members=" + members + ", orderIndex="
				+ orderIndex + ", parentId=" + parentId + "]";
	} 
	
	
	//根据orderIndex排序
	public void sortMembers(){
		Collections.sort(members,new Comparator<DeptDetailUserInfo>() {

			@Override
			public int compare(DeptDetailUserInfo lhs, DeptDetailUserInfo rhs) {
				Integer a = (Integer) lhs.getOrderIndex();
                Integer b = (Integer) rhs.getOrderIndex(); 
				return a.compareTo(b);
			}
		});
	}
	

}
