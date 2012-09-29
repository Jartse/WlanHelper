package com.example.wlanhelper;

public class WifiInfo {
	String mSsid = "";
	String mPreSharedKey = "";
	
	public WifiInfo( String ssid, String preSharedKey ) {
	    if (ssid.isEmpty() || preSharedKey.isEmpty()) {
	        throw new IllegalArgumentException("Unable to create WifiInfo, ssid: " + ssid + ", presharedKey: " + preSharedKey);
	    }
		this.mSsid = ssid;
		this.mPreSharedKey = preSharedKey;
	}
	
	public String getSsid() {
		return mSsid;
	}
	
	public String getPreSharedKey() {
		return mPreSharedKey;
	}
	
}
