package com.chinatelecom.nfc.Model;

public class WifiModule extends CommunicationModule{
	public String Address;
	public String Ip;
	public WifiModule(String Address,String Ip)
	{
		this.Address = Address;
		this.Ip = Ip;
	}
	
}
