package cn.yjt.oa.app.nfctools.operation;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class NfcTagWifiOpenOperation extends NfcTagOperation {
	
	public static final byte[] DATA_SEPARATOR = { -0x01, 0x1F, 0x1E };
	public static final String SECURITY_NONE = "NONE";
	public static final String SECURITY_WEP = "WEP";
	public static final String SECURITY_WPA = "WAP/WPA2 PSK";
	

	@Override
	public void excuteOperation() {
		WifiManager manager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
		if(!manager.isWifiEnabled()){
			manager.setWifiEnabled(true);
			IntentFilter filter = new IntentFilter();
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			getContext().registerReceiver(wifiBroadcastReceiver, filter );
		}else{
			connectWifi();
		}
		
	}
	
	private  BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0);
			if(wifiState == WifiManager.WIFI_STATE_ENABLED){
				connectWifi();
			}
		}
	};
	
	private void connectWifi(){
		
		List<byte[]> bytes = new ArrayList<byte[]>();
		byte[] data = getExtraData();
		if(data == null){
			return;
		}
		int len = 0;
		byte[] b = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			// TagName and Operations are splited by a line feed.
			if (data[i] == DATA_SEPARATOR[0] && data.length > i + 3
					&& data[i + 1] == DATA_SEPARATOR[1]
					&& data[i + 2] == DATA_SEPARATOR[2]) {
				byte[] dst = new byte[len];
				System.arraycopy(b, 0, dst, 0, len);
				bytes.add(dst);
				len = 0;
				i += 2;
			} else {
				b[len++] = data[i];
				if(i == data.length -1){
					byte[] dst = new byte[len];
					System.arraycopy(b, 0, dst, 0, len);
					bytes.add(dst);
				}
			}
		}
		
		String ssid = new String(bytes.get(0));
		String password = new String(bytes.get(1));
		String security = new String(bytes.get(2));
		connect(ssid, password, security);
	}
	
	private void connect(String ssid,String password,String security){
		
		String networkSSID = ssid;
		String networkPass = password;

		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
		
		//Then, for WEP network you need to do this:
		if(SECURITY_WEP.equals(security)){
			
		conf.wepKeys[0] = "\"" + networkPass + "\""; 
		conf.wepTxKeyIndex = 0;
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); 
		}else
		//For WPA network you need to add passphrase like this:
		if(SECURITY_WPA.equals(security)){
			

		conf.preSharedKey = "\""+ networkPass +"\"";
		}else
			//For Open network you need to do this:
		if(SECURITY_NONE.equals(security)){
			

		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		}
		//Then, you need to add it to Android wifi manager settings:

		WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE); 
		wifiManager.addNetwork(conf);
		//And finally, you might need to enable it, so Android conntects to it:

		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		for( WifiConfiguration i : list ) {
		    if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
		         wifiManager.disconnect();
		         boolean enableNetwork = wifiManager.enableNetwork(i.networkId, true);
		         System.out.println("enableNetwork:"+enableNetwork);
		         boolean reconnect = wifiManager.reconnect();               
		         System.out.println("reconnect:"+reconnect);

		         break;
		    }           
		 }
	}
	
   public static byte[] generateExtraData(String ssid, String password,
			String security) {
		if (ssid != null && password != null && security != null) {
			byte[] ssidData = ssid.getBytes();
			byte[] passwordData = password.getBytes();
			byte[] securityData = security.getBytes();
			
			byte[] data = new byte[ssidData.length + passwordData.length + securityData.length + DATA_SEPARATOR.length * 2];
			System.arraycopy(ssidData, 0, data, 0, ssidData.length);
			System.arraycopy(DATA_SEPARATOR, 0, data, ssidData.length, DATA_SEPARATOR.length);
			System.arraycopy(passwordData, 0, data, ssidData.length + DATA_SEPARATOR.length, passwordData.length);
			System.arraycopy(DATA_SEPARATOR, 0, data,ssidData.length + DATA_SEPARATOR.length +passwordData.length, DATA_SEPARATOR.length);
			System.arraycopy(securityData, 0, data, ssidData.length + passwordData.length +2*DATA_SEPARATOR.length, securityData.length);
			return data;
		}
		return null;
	}
	
}
