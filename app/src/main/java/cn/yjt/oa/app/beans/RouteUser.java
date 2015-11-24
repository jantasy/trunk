package cn.yjt.oa.app.beans;

public class RouteUser {

	/*
     * "areaId": 区域ID,
				"userId": 用户ID,
				"name": 用户姓名,
				"phone": 用户手机号
	 */

    private long lineId;
    private long userId;
    private String name;
    private String phone;
    private String avatar;
    private boolean isSelected = false;


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

    public static RouteUser fromContactInfo(ContactInfo contactInfo) {
        RouteUser areaUser = new RouteUser();
        areaUser.setName(contactInfo.getName());
        areaUser.setPhone(contactInfo.getPhone());
        areaUser.setUserId(contactInfo.getUserId());
        areaUser.setAvatar(contactInfo.getAvatar());
        return areaUser;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RouteUser) {
            RouteUser areaUser = (RouteUser) o;
            return areaUser.userId == this.userId;
        }
        return super.equals(o);
    }


    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }
}
