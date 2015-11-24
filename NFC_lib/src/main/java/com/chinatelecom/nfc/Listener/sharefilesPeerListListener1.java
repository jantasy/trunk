package com.chinatelecom.nfc.Listener;

import java.util.List;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class sharefilesPeerListListener1 implements PeerListListener {
	List<WifiP2pDevice> peers;
	public sharefilesPeerListListener1(List<WifiP2pDevice> peers) {
		// TODO Auto-generated constructor stub
		this.peers = peers;
	}
	    @Override
	    public void onPeersAvailable(WifiP2pDeviceList peerList) {

	        // Out with the old, in with the new.
	        peers.clear();   //清空list，刷新
	        peers.addAll(peerList.getDeviceList());

	        if (peers.size() == 0) {
	            Log.d("Warnning", "No devices found");
	            return;
	        }
	    }

}
