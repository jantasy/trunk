package cn.yjt.oa.app.beacon;

public class EncryptedBeacon extends Beacon {

	private String originalUumm;
	
	public EncryptedBeacon(String uumm,String originalUumm) {
		super(uumm);
		this.originalUumm = originalUumm;
		
	}

	public String getOriginalUumm() {
		return originalUumm;
	}

	public void setOriginalUumm(String originalUumm) {
		this.originalUumm = originalUumm;
	}
	
}
