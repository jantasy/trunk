package cn.yjt.oa.app.beans;

public class AreaUser {

	/*
     * "areaId": 区域ID,
				"userId": 用户ID,
				"name": 用户姓名,
				"phone": 用户手机号
	 */

    private long areaId;
    private long userId;
    private String name;
    private String phone;
    private String avatar;
    private boolean isSelected = false;

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public static AreaUser fromContactInfo(ContactInfo contactInfo) {
        AreaUser areaUser = new AreaUser();
        areaUser.setName(contactInfo.getName());
        areaUser.setPhone(contactInfo.getPhone());
        areaUser.setUserId(contactInfo.getUserId());
        areaUser.setAvatar(contactInfo.getAvatar());
        return areaUser;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AreaUser) {
            AreaUser areaUser = (AreaUser) o;
            return areaUser.userId == this.userId;
        }
        return super.equals(o);
    }


}
