package com.example.wlanhelper;

import java.util.List;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

public class WifiConfigurator {

    private WifiManagerProviderInterface mWifiManagerProvider;
    
    public WifiConfigurator(WifiManagerProviderInterface wifiManagerProvider) {
        mWifiManagerProvider = wifiManagerProvider;
    }
    
    /**
     * Sets a new WLAN account and takes it into use.
     * 
     * @param wifiInfo An info object containing the wifi configuration details.
     * @throws WifiSetupException
     */
    public void useWifiInfo( WifiInfo wifiInfo ) throws WifiSetupException {

        ensureEnabledState();

        WifiConfiguration alreadyExistingWifiConfiguration = getExistingWifiConfiguration( wifiInfo.getSsid() );

        if (alreadyExistingWifiConfiguration == null) {
            Log.d("JARI WLAN", "The given WIFI network WAS NOT found in the list of already existing WIFI networks.");
            setupNewWifiConfiguration(wifiInfo.getSsid(), wifiInfo.getPreSharedKey());

        } else {
            Log.d("JARI WLAN", "The given WIFI network WAS found in the list of already existing WIFI networks.");

            // we are currently connected to the network that was given by user -> return
            if (alreadyExistingWifiConfiguration.status != WifiConfiguration.Status.CURRENT) {
                enableWifiConfiguration(alreadyExistingWifiConfiguration);
            }
        }

    }
    
    private void ensureEnabledState() {
        displayWifiState("ensureEnabledState() start");
        
        // If the WIFI feature is still being enabled, let's wait for that to finish in 1s steps, max 4s.
        final int retryCount = 4;
        for (int retry = 0; retry < retryCount; retry++) {
            if ( WifiManager.WIFI_STATE_ENABLING == mWifiManagerProvider.getWifiManager().getWifiState() ) {
                Log.d("JARI WLAN", "Still enabling wifi -> WAITING FOR 1 SEC");
                SystemClock.sleep(1000);
            } else {
                break;
            }
        }
        
        displayWifiState("ensureEnabledState() end");
    }
    
    public void displayWifiState(String callingPlace) {
        String stateStr;
        int state = mWifiManagerProvider.getWifiManager().getWifiState();
        
        if ( state == WifiManager.WIFI_STATE_DISABLING ) {
            stateStr = new String("DISABLING");
        } else if ( state == WifiManager.WIFI_STATE_DISABLED ) {
            stateStr = new String("DISABLED");
        } else if ( state == WifiManager.WIFI_STATE_ENABLING ) {
            stateStr = new String("ENABLING");
        } else if ( state == WifiManager.WIFI_STATE_ENABLED ) {
            stateStr = new String("ENABLED");
        } else {
            stateStr = new String("UNKNOWN");
        }
        Log.d("JARI WLAN", "WIFI state: " + stateStr + " (called from " + callingPlace + ")");
    }

    private void setupNewWifiConfiguration(String ssid, String preSharedKey) throws WifiSetupException {
        WifiConfiguration newConf = createWPAWifiConfiguration(ssid, preSharedKey);
        
        // Connect to and enable the newly-created WIFI configuration.
        int netId = mWifiManagerProvider.getWifiManager().addNetwork(newConf);
        if ( netId != -1 ) {
            Log.d("JARI WLAN", "A new WIFI network added successfully, netId: " + netId);
            
            newConf.networkId = netId;
            enableWifiConfiguration(newConf);
            
        } else {
            throw new WifiSetupException("Addition of a new WIFI network failed");
        }
    }
    
    private void enableWifiConfiguration(WifiConfiguration wifiConfiguration) throws WifiSetupException {
        Log.d("JARI WLAN", "enabling network id " + wifiConfiguration.networkId);
        boolean enableSuccess = mWifiManagerProvider.getWifiManager().enableNetwork(wifiConfiguration.networkId, true);
        if (!enableSuccess) { 
            throw new WifiSetupException("Enabling the already existing network failed");
        }
    }


    private WifiConfiguration getExistingWifiConfiguration(String ssid) {
        WifiConfiguration existingWifiConfiguration = null;
        
        List<WifiConfiguration> wifiList = mWifiManagerProvider.getWifiManager().getConfiguredNetworks();
        Log.d("JARI WLAN", "wifiList count: " + wifiList.size());       
        
        // Check if we already have the network configured.
        for ( int i = 0; i < wifiList.size(); ++i ) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) wifiList.get(i);
            
            String ssidInList =
                new String( wifiConfiguration.SSID.substring(wifiConfiguration.SSID.indexOf("\"")+1,
                                                             wifiConfiguration.SSID.lastIndexOf("\"")) );
            if(ssid.equals(ssidInList)) {
                Log.i("JARI WLAN", "***** Our network already found from the list *****");
                existingWifiConfiguration = wifiConfiguration;
            }
            
            logWifiConfigurationInfo(i, wifiConfiguration);
        }
        return existingWifiConfiguration;
    }

    private WifiConfiguration createWPAWifiConfiguration(String ssid, String preSharedKey) {
        WifiConfiguration newConf = new WifiConfiguration();        
        newConf.SSID = "\"" + ssid + "\"";
        newConf.preSharedKey = "\"" + preSharedKey + "\"";
        newConf.priority = TagReader.NEWCONFPRIORITY;
        newConf.status = WifiConfiguration.Status.DISABLED;
        newConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        newConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        newConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        newConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        newConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        newConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        newConf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        newConf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        newConf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return newConf;
    }

    private void logWifiConfigurationInfo(int i, WifiConfiguration wifiConfiguration) {
        Log.i("JARI WLAN", "WifiConfiguration # "+ (i+1) +" ************************** -->");
        Log.i("JARI WLAN", "*");
        Log.i("JARI WLAN", "networkId: " + wifiConfiguration.networkId);
        Log.i("JARI WLAN", "SSID: " + wifiConfiguration.SSID);
        Log.i("JARI WLAN", "hiddenSSID: " + wifiConfiguration.hiddenSSID);
        Log.i("JARI WLAN", "BSSID: " + wifiConfiguration.BSSID);
        Log.i("JARI WLAN", "preSharedKey: " + wifiConfiguration.preSharedKey);
        Log.i("JARI WLAN", "status: " + wifiConfiguration.status);
        Log.i("JARI WLAN", "*");
        Log.i("JARI WLAN", "WifiConfiguration # "+ (i+1) +" ************************** <--");
    }
    
}
