package com.example.wlanhelper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements MessageDisplayer, WifiManagerProvider {
    private NfcAdapter mNfcAdapter;
    private TextView mMessageField;
    private WifiManager mWifiManager;
    private TagReader mReader;
    private TagWriter mWriter;
    private WifiConfigurator mWifiConfigurator;
    private boolean mInWriteMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager == null) {
            Log.d("JARI WLAN", "onCreate(), Could not get access to WifiManager -> closing the application...");
            Toast.makeText(this, "Could not get access to WifiManager -> closing the application...",
            Toast.LENGTH_SHORT).show();
            finish();
        }

        mReader = new TagReader(this);
        mWriter = new TagWriter(this);
        mWifiConfigurator = new WifiConfigurator(this);

        // grab our NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // TextView that we'll use to output messages to screen
        mMessageField = (TextView) findViewById(R.id.text_view);

        if (!mWifiManager.isWifiEnabled()) {
            mWifiConfigurator.displayWifiState("onCreate()");
            boolean setSuccess = mWifiManager.setWifiEnabled(true);
            Log.d("JARI WLAN", "onCreate(), Wifi network was not enabled -> setting it on, setSuccess: " + setSuccess);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("JARI WLAN", "onPause entered, disabling foreground dispatch");
        disableWriteMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("JARI WLAN", "onResume entered");

        // The WIFI feature was already set true in onCreate(),
        // but it might be possible that user has left our app to background and
        // closed the WIFI feature in the meanwhile, so just to making sure here...
        if (!mWifiManager.isWifiEnabled()) {
            boolean setSuccess = mWifiManager.setWifiEnabled(true);
            Log.d("JARI WLAN", "onResume(), Wifi network was not enabled -> setting it on, setSuccess: " + setSuccess);
        }

        // Read NFC message from the intent
        Intent intent = getIntent();
        
        if (intent.getType() != null
                && intent.getType().equals(Constants.WLAN_HELPER_MIMETYPE)
                && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Log.d("JARI WLAN", "onResume(), Correct intent found -> setting up the Wifi network from tag");
            try {
                WifiInfo wifiInfo = mReader.parseWifiInfoFromTag(intent);
                // Create a new WIFI configuration based on these pieces of info, and take it into use.
                mWifiConfigurator.useWifiInfo(wifiInfo);

            } catch (ReadException e) {
                displayMessage("Failed to read a wifi configuration from tag: " + e.getMessage());
            } catch (Exception e) {
                displayMessage("Failed to set up a new wifi configuration from tag: " + e.getMessage());
            }
        }
    }

    /**
     * Called when our blank tag is scanned executing the PendingIntent
     */
    @Override
    public void onNewIntent(Intent intent) {
        Log.d("JARI WLAN", "onNewIntent(), mInWriteMode: " + mInWriteMode);

        if (mInWriteMode) {
            mInWriteMode = false;

            // write to newly scanned tag
            try {
                mWriter.writeTag(intent, getWritableWifiInfo());
            } catch (Exception e) {
                displayMessage("Failed to write into tag: " + e.getMessage());
            }
        }
    }

    public void writeButtonClicked(View v) {
        if (v.getId() == R.id.write_tag_button) {
            displayMessage("Touch and hold tag against phone to write.");
            enableWriteMode();
        }
    }

    private WifiInfo getWritableWifiInfo() {
        String ssid = getTextFieldContent(R.id.ssid);
        String preSharedKey = getTextFieldContent(R.id.pre_shared_key);
        return new WifiInfo(ssid, preSharedKey);
    }

    private String getTextFieldContent(int fieldId) {
        return ((EditText) findViewById(fieldId)).getText().toString();
    }

    /**
     * Force this Activity to get our specific NFC events first
     */
    private void enableWriteMode() {
        mInWriteMode = true;

        // set up a PendingIntent to open our app when a NFC tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };

        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableWriteMode() {
        Log.d("JARI WLAN", "disableWriteMode(), calling disableForegroundDispatch()");
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void displayMessage(String message) {
        mMessageField.setText(message);
        Log.d("JARI WLAN", "displayMessage: " + message);
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

}