package cn.yjt.oa.app.nfctools.operation;

import java.util.ArrayList;
import java.util.List;

public class NfcTagOperatorGroup {
	
	private String groupName;
	private boolean isHide;
	private List<NfcTagOperator> nfcTagOperators = new ArrayList<NfcTagOperator>();
	
	public String getGroupName(){
		return groupName;
	}
	
	void setGroupName(String groupName){
		this.groupName = groupName;
	}
	
	public List<NfcTagOperator> getNfcTagOperators(){
		return nfcTagOperators;
	}
	
	void addNfcTagOperator(NfcTagOperator nfcTagOperator){
		nfcTagOperators.add(nfcTagOperator);
	}

	public boolean isHide() {
		return isHide;
	}

	void setHide(boolean isHide) {
		this.isHide = isHide;
	}

	
	
}
