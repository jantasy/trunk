package com.chinatelecom.nfc.DB.Pojo;

import java.io.Serializable;

public class NameCard implements Serializable{
	
	private static final long serialVersionUID = 5684200915400642232L;
	private Integer id;
	private String n;//姓名
	private byte[] icon;//头像
	private String tel;//电话

	private String fa;//传真
	private String cn;//公司名称
	private String cadd;//公司网址
	private String cp;//公司电话
	private String s;//公司部门
	private String r;//公司职位
	
	private String add;//地址
	private String email;//邮箱
	private String d;//好友描述
	private short f;//显示项目
	private int sc;//快捷方式，1：有，0：无
	public NameCard(Integer id,String name,byte[] contactIcon,String telPhone, String fax
			,String companyName,String companyNetAddress,String companyPhone
			,String section,String rank,String address,String email, String description,short showflag,int shortcut){
		this.id = id;
		this.n = name;
		this.icon = contactIcon;
		this.tel = telPhone;
		this.fa = fax;
		this.cn = companyName;
		this.cadd = companyNetAddress;
		this.cp = companyPhone;
		this.s = section;
		this.r = rank;
		this.add = address;
		this.email = email;
		this.d = description;
		this.f = showflag;
		this.sc = shortcut;
	}
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	//姓名，头像，电话
	public String getName() {
		return n;
	}
	public void setName(String name) {
		this.n = name;
	}
	public byte[] getContactIcon() {
		return icon;
	}
	public void setContactIcon(byte[] contactIcon) {
		this.icon = contactIcon;
	}
	public String getTelPhone() {
		return tel;
	}
	public void setTelPhone(String telPhone) {
		this.tel = telPhone;
	}
	
	//公司相关
	public String getFax(){
		return fa;
	}
	public void setFax(String fax){
		this.fa = fax;
	}
	public String getCompanyName() {
		return cn;
	}
	public void setCompanyName(String companyName) {
		this.cn = companyName;
	}
	public String getCompanyTelPhone(){
		return cp;
	}
	public void setCompanyTelPhone(String companyPhone){
		this.cp = companyPhone;
	}
	public String getCompanyNetAddress(){
		return cadd;
	}
	public void setCompanyNetAddress(String companyNetAddress){
		this.cadd = companyNetAddress;
	}
	public String getSection(){
		return s;
	}
	public void setSection(String section){
		this.s = section;
	}
	public String getRank(){
		return r;
	}
	public void setRank(String rank){
		this.r = rank;
	}
	//其他
	public String getAddress() {
		return add;
	}
	public void setAddress(String address) {
		this.add = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDescription() {
		return d;
	}
	public void setDescription(String description) {
		this.d = description;
	}
	public short getShowFlag(){
		return f;
	}
	public void setShowFlag(short showFlag){
		this.f = showFlag;
	}
	public void setShortCut(int shortcut){
		this.sc = shortcut;
	}
	public int getShortCut(){
		return sc;
	}
	public boolean equals(NameCard tag){
		if(!tel.equals(tag.getTelPhone()))
			return false;
		if(!fa.equals(tag.getFax()))
			return false;
		if(!cn.equals(tag.getCompanyName()))
			return false;
		if(!cadd.equals(tag.getCompanyNetAddress()))
			return false;
		if(!cp.equals(tag.getCompanyTelPhone()))
			return false;
		if(!s.equals(tag.getSection()))
			return false;
		if(!r.equals(tag.getRank()))
			return false;
		if(!add.equals(tag.getAddress()))
			return false;
		if(!email.equals(tag.getEmail()))
			return false;
		if(!d.equals(tag.getDescription()))
			return false;
		return true;
	}
}
