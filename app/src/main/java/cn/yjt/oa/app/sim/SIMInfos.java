package cn.yjt.oa.app.sim;

import java.util.List;

public class SIMInfos {

	private static final float SIM_READ_ERROR_RESULT = -1.00f;
	private float balance;
	private float edBalance;
	private int unit;
	private List<SIMRecord> simRecords;

	public float getBalance() {
		return balance;
	}

	public String getBalanceString(){
		float balDisplay = balance;
		if(SIM_READ_ERROR_RESULT == balDisplay) {
//			balDisplay = 9788.65f;
			return "--.--";
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
		return df.format(balDisplay);
	}
	
	public float getEdBalance() {
		return edBalance;
	}
	
	public String getEdBalanceString(){
		float balDisplay = edBalance;
		if(SIM_READ_ERROR_RESULT == balDisplay) {
//			balDisplay = 22f;
			return "0";
		}
		return UnitGuesser.guessBalance(balDisplay);
	}

	public List<SIMRecord> getSimRecords() {
		return simRecords;
	}

	public int getUnit() {
		return unit;
	}

	public SIMInfos(List<SIMRecord> simRecords, int unit, float balance,
			float edBalance) {
		this.balance = balance;
		this.edBalance = edBalance;
		this.simRecords = simRecords;
		this.unit = unit;
	}

	public String getUnitString() {
		System.out.println("unit:"+unit);
		if (unit == Constant.UNIT_YUAN_DEFAULT) {
			return "元";
		} else if (unit == Constant.UNIT_TIMES_DEFAULT) {
			return "次";
		} else {
			return UnitGuesser.guessUnit(edBalance);
		}
	}
	

}
