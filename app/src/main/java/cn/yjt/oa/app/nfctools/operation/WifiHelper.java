package cn.yjt.oa.app.nfctools.operation;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiHelper {
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;

	// 构造器
	public WifiHelper(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	// 打开WIFI
	public void OpenWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	public boolean isWifiEnabled() {
		// return mWifiManager.isWifiEnabled();
		mWifiInfo = mWifiManager.getConnectionInfo();
		return mWifiInfo != null;
	}

	public boolean isWifiOpened() {
		switch (mWifiManager.getWifiState()) {
		// switch判断状态
		case WifiManager.WIFI_STATE_DISABLED:
		case WifiManager.WIFI_STATE_DISABLING:
		case WifiManager.WIFI_STATE_ENABLING:
		case WifiManager.WIFI_STATE_UNKNOWN:
			return false;
		case WifiManager.WIFI_STATE_ENABLED:
			return true;
		}
		return false;
	}

	public String getWifiState() {
		switch (mWifiManager.getWifiState()) {
		// switch判断状态
		case WifiManager.WIFI_STATE_DISABLED:
			return "WIFI_STATE_DISABLED";
		case WifiManager.WIFI_STATE_DISABLING:
			return "WIFI_STATE_DISABLING";
		case WifiManager.WIFI_STATE_ENABLING:
			return "WIFI_STATE_ENABLING";
		case WifiManager.WIFI_STATE_UNKNOWN:
			return "WIFI_STATE_UNKNOWN";
		case WifiManager.WIFI_STATE_ENABLED:
			return "WIFI_STATE_ENABLED";
		}
		return "";
	}

	// 关闭WIFI
	public void CloseWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 锁定WifiLock
	public void AcquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void ReleaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void CreateWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> GetConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void ConnectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index > mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	public void ConnectConfiguration(String ssID) throws Exception {
		boolean opened = false;

		for (int i = 0; i < mWifiConfiguration.size(); i++) {

			String originalSSID = mWifiConfiguration.get(i).SSID.substring(1,
					mWifiConfiguration.get(i).SSID.length() - 1);
			if (ssID.equals(originalSSID)) {
				// 连接配置好的指定ID的网络
				mWifiManager.enableNetwork(mWifiConfiguration.get(i).networkId,
						true);
				opened = true;
			}
		}
		
		if (opened == false) {
			throw new Exception("连接指定WIFI失败！");
		}
	}
	
	
	public void ConnectConfiguration2(String ssID, String password) throws Exception {
		boolean opened = false;

		for (int i = 0; i < mWifiConfiguration.size(); i++) {
			String originalSSID = mWifiConfiguration.get(i).SSID.substring(1,
					mWifiConfiguration.get(i).SSID.length() - 1);
			if (ssID.equals(originalSSID)) {
				// 连接配置好的指定ID的网络
				mWifiManager.enableNetwork(mWifiConfiguration.get(i).networkId,
						true);
				opened = true;
			}
		}
		
		do {
			WifiConfiguration tempWC;
			tempWC = IsExsits(ssID);

			if (tempWC != null ) {
				mWifiManager.removeNetwork(tempWC.networkId);
			}

		} while (IsExsits(ssID) != null);

		
		WifiConfiguration config;
		if (true) {
			config = new WifiConfiguration();  
			config.allowedAuthAlgorithms.clear();  
			config.allowedGroupCiphers.clear();  
			config.allowedKeyManagement.clear();  
			config.allowedPairwiseCiphers.clear();  
			config.allowedProtocols.clear();  
			config.SSID = "\"" + ssID + "\"";
			WifiConfiguration tempConfig = this.IsExsits(ssID);  
			if (tempConfig != null) {  
				mWifiManager.removeNetwork(tempConfig.networkId);  
			}  
			config.wepKeys[0] = "";  
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
			config.wepTxKeyIndex = 0;  
			int id = mWifiManager.addNetwork(config);
			// 连接配置好的指定ID的网络
			Boolean b = mWifiManager.enableNetwork(id, true);
			opened = (id == -1) ? false : true;
		}
		if (true) {
			config = new WifiConfiguration();
			config.hiddenSSID = true;  
			config.wepKeys[0] = "\"" + password + "\"";  
			config.allowedAuthAlgorithms  
			.set(WifiConfiguration.AuthAlgorithm.SHARED);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
			config.allowedGroupCiphers  
			.set(WifiConfiguration.GroupCipher.WEP104);  
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 
			config.SSID = "\"" + ssID + "\"";
			config.wepTxKeyIndex = 0;  
			int id = mWifiManager.addNetwork(config);
			// 连接配置好的指定ID的网络
			Boolean b = mWifiManager.enableNetwork(id, true);
			opened = (id == -1) ? false : true;
		}
		if (true) {
			config = new WifiConfiguration();
			config.preSharedKey = "\"" + password + "\"";  
			config.hiddenSSID = true;  
			config.allowedAuthAlgorithms  
			.set(WifiConfiguration.AuthAlgorithm.OPEN);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
			config.allowedPairwiseCiphers  
			.set(WifiConfiguration.PairwiseCipher.TKIP);  
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.SSID = "\"" + ssID + "\"";
			config.status = WifiConfiguration.Status.ENABLED;  
			int id = mWifiManager.addNetwork(config);
			// 连接配置好的指定ID的网络
			Boolean b = mWifiManager.enableNetwork(id, true);
			opened = (id == -1) ? false : true;
		}
		
		if (opened == false) {
			throw new Exception("连接指定WIFI失败！");
		}
	}
	private WifiConfiguration IsExsits(String SSID) {  
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();  
        for (WifiConfiguration existingConfig : existingConfigs) {  
            if (existingConfig.SSID.equals("\"" + SSID + "\"") /*&& existingConfig.preSharedKey.equals("\"" + password + "\"")*/) {  
                return existingConfig;  
            }  
        }  
        return null;  
    }  
	
	
	

	public void StartScan() {
		mWifiManager.startScan();
		// 得到扫描结果
		mWifiList = mWifiManager.getScanResults();
		// 得到配置好的网络连接
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	// 得到网络列表
	public List<ScanResult> GetWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuilder LookUpScan() {
		StringBuilder stringBuilder = new StringBuilder();
		// for (int i = 0; i < mWifiList.size(); i++)
		// {
		// stringBuilder.append("Index_"+new Integer(i + 1).toString() + ":");
		// //将ScanResult信息转换成一个字符串包
		// //其中把包括：BSSID、SSID、capabilities、frequency、level
		// stringBuilder.append((mWifiList.get(i)).toString());
		// stringBuilder.append("/nxxxxxxxxxxxxxxxxxxxx");
		// }
		for (int i = 0; i < mWifiConfiguration.size(); i++) {
			stringBuilder
					.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mWifiConfiguration.get(i)).SSID);
			stringBuilder.append("/nxxxxxxxxxxxxxxxxxxxx");
		}
		stringBuilder.append(mWifiInfo.getSSID());
		return stringBuilder;
	}

	// 得到MAC地址
	public String GetMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String GetBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public String GetSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
	}

	// 得到IP地址
	public int GetIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int GetNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到WifiInfo的所有信息包
	public String GetWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public void AddNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		mWifiManager.enableNetwork(wcgID, true);
	}

	// 断开指定ID的网络
	public void DisconnectWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
}