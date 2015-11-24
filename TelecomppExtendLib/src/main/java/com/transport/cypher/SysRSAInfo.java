package com.transport.cypher;


/**
 * SysRSAInfo entity. @author MyEclipse Persistence Tools
 */

public class SysRSAInfo implements java.io.Serializable {

	// Fields
 
	private Integer key_index;
	private String rsa_pri_p;
	private String rsa_pri_q;
	private String rsa_pri_dp;
	private String rsa_pri_dq;
	private String rsa_pri_qinv;
	private String rsa_pri_exponent;
	private String rsa_key_modulus;
	private Integer rsa_key_type;

	// Constructors

	/** default constructor */
	public SysRSAInfo() {
	}

	/** minimal constructor */
	public SysRSAInfo(Integer key_index) {
		this.key_index = key_index;
	}

	/** full constructor */
	public SysRSAInfo(Integer key_index, String rsa_pri_p, String rsa_pri_q,
			String rsa_pri_dp, String rsa_pri_dq, String rsa_pri_qinv,
			String rsa_pri_exponent, String rsa_key_modulus,
			Integer rsa_key_type) {
		this.key_index = key_index;
		this.rsa_pri_p = rsa_pri_p;
		this.rsa_pri_q = rsa_pri_q;
		this.rsa_pri_dp = rsa_pri_dp;
		this.rsa_pri_dq = rsa_pri_dq;
		this.rsa_pri_qinv = rsa_pri_qinv;
		this.rsa_pri_exponent = rsa_pri_exponent;
		this.rsa_key_modulus = rsa_key_modulus;
		this.rsa_key_type = rsa_key_type;
	}

	// Property accessors

	public Integer getKey_index() {
		return this.key_index;
	}

	public void setKey_index(Integer key_index) {
		this.key_index = key_index;
	}

	public String getRsa_pri_p() {
		return this.rsa_pri_p;
	}

	public void setRsa_pri_p(String rsa_pri_p) {
		this.rsa_pri_p = rsa_pri_p;
	}

	public String getRsa_pri_q() {
		return this.rsa_pri_q;
	}

	public void setRsa_pri_q(String rsa_pri_q) {
		this.rsa_pri_q = rsa_pri_q;
	}

	public String getRsa_pri_dp() {
		return this.rsa_pri_dp;
	}

	public void setRsa_pri_dp(String rsa_pri_dp) {
		this.rsa_pri_dp = rsa_pri_dp;
	}

	public String getRsa_pri_dq() {
		return this.rsa_pri_dq;
	}

	public void setRsa_pri_dq(String rsa_pri_dq) {
		this.rsa_pri_dq = rsa_pri_dq;
	}

	public String getRsa_pri_qinv() {
		return this.rsa_pri_qinv;
	}

	public void setRsa_pri_qinv(String rsa_pri_qinv) {
		this.rsa_pri_qinv = rsa_pri_qinv;
	}

	public String getRsa_pri_exponent() {
		return this.rsa_pri_exponent;
	}

	public void setRsa_pri_exponent(String rsa_pri_exponent) {
		this.rsa_pri_exponent = rsa_pri_exponent;
	}

	public String getRsa_key_modulus() {
		return this.rsa_key_modulus;
	}

	public void setRsa_key_modulus(String rsa_key_modulus) {
		this.rsa_key_modulus = rsa_key_modulus;
	}

	public Integer getRsa_key_type() {
		return this.rsa_key_type;
	}

	public void setRsa_key_type(Integer rsa_key_type) {
		this.rsa_key_type = rsa_key_type;
	}

}