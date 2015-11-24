package cn.yjt.oa.app.nfctools.operation;


public class NfcTagWifiConnectOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		// 连接操作
		String ssid = getExtraData().toString().split("#")[0];
		String password = getExtraData().toString().split("#")[1];
		WifiHelper helper = new WifiHelper(getContext());
		try {
			helper.ConnectConfiguration2(ssid, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
