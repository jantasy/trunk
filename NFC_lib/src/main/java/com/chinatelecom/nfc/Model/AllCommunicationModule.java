package com.chinatelecom.nfc.Model;

public class AllCommunicationModule {
	public BluetoothModule _BluetoothModule;
	public WifiModule _WifiModule;
	public AllCommunicationModule(String bluetoothAddress,String wifiAddress,String ip)
	{
		_BluetoothModule = new BluetoothModule(bluetoothAddress);
		_WifiModule = new WifiModule(wifiAddress, ip);
	}
}