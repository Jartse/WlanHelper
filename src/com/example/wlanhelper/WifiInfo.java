package com.example.wlanhelper;

public class WifiInfo {
    private String mSsid;
    private String mPreSharedKey;

    public WifiInfo(String ssid, String preSharedKey) {
        if (ssid.isEmpty() || preSharedKey.isEmpty()) {
            throw new IllegalArgumentException("Invalid wifi info, either SSID or pre-shared key is empty (ssid: " +
                                               ssid + ", pre-shared key: " + preSharedKey + ").");
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
