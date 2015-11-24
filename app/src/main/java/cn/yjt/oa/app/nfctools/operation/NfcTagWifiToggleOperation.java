package cn.yjt.oa.app.nfctools.operation;

import android.content.Context;
import android.net.wifi.WifiManager;

public class NfcTagWifiToggleOperation extends NfcTagOperation {

	@Override
	public void excuteOperation() {
		toggleWifi(getContext());
	}
	
	private void toggleWifi(Context context){
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		manager.setWifiEnabled(!manager.isWifiEnabled());
	}

}
