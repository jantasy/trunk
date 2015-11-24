package cn.yjt.oa.app.beans;

public class LoginInfo {
    private String loginName;
    private String password;
    private String verifyCode;
    private String iccid;
    private String imei;
    private String recommendPhone;

    public String getIccd() {
        return iccid;
    }

    public void setIccd(String iccd) {
        this.iccid = iccd;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        System.out.println("setVerifyCode:" + verifyCode);
        this.verifyCode = verifyCode;
    }

    public String getRecommendPhone() {
        return recommendPhone;
    }

    public void setRecommendPhone(String recommendPhone) {
        this.recommendPhone = recommendPhone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}
