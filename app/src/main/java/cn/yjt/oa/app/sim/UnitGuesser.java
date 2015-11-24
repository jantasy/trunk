package cn.yjt.oa.app.sim;

import java.text.DecimalFormat;

public class UnitGuesser {
	
	final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
	
	public static String guessUnit(float edbalance) {
		if (edbalance == 0) {
			return "次";
		} else if (edbalance > 0.03) {
			return "元";
		} else if (edbalance >= 0.01 && edbalance <= 0.03) {
			return "次";
		}else{
			return "";
		}
	}
	
	public static String guessBalance(float edbalance){
		if (edbalance == 0) {
			return "0";
		} else if (edbalance > 0.03) {
			return DECIMAL_FORMAT.format(edbalance/100.0)+"";
		} else if (edbalance >= 0.01 && edbalance <= 0.03) {
			return DECIMAL_FORMAT.format(edbalance)+"";
		}else{
			return DECIMAL_FORMAT.format(edbalance)+"";
		}
	}
}
