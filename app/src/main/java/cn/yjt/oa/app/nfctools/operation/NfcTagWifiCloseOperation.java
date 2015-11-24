package cn.yjt.oa.app.nfctools.operation;

import android.content.Context;
import android.net.wifi.WifiManager;

public class NfcTagWifiCloseOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		closeWifi(getContext());
	}
	
	private void closeWifi(Context context){
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		manager.setWifiEnabled(false);
	}

}
