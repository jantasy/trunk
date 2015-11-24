package cn.yjt.oa.app.sim;

public class SIMBalance {
	private float balance;
	private float edBalance;
	private int unit;

	
	
	public SIMBalance(float balance, float edBalance, int unit) {
		this.balance = balance;
		this.edBalance = edBalance;
		this.unit = unit;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public float getEdBalance() {
		return edBalance;
	}

	public void setEdBalance(float edBalance) {
		this.edBalance = edBalance;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

}
