package com.example.wlanhelper;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

import com.example.wlanhelper.WlanHelperExceptions.NoNdefMessagesExtraInTagException;
import com.example.wlanhelper.WlanHelperExceptions.TagFormatNotSupportedException;


public class TagReader {
	
	private MessageDisplayer messageDisplayer;
	public static final int NEWCONFPRIORITY = 40;
	
	public TagReader( MessageDisplayer messageDisplayer ) {
		this.messageDisplayer = messageDisplayer;
	}
	
	/**
	 * Parses the wifi configuration details from the NFC tag.
	 * 
	 * @param intent Intent that contains the NFC tag info.
	 * @return An info object containing the wifi configuration details.
	 * @throws NoNdefMessagesExtraInTagException
	 * @throws TagFormatNotSupportedException
	 */
	public WifiInfo parseWifiInfoFromTag(Intent intent) 
			throws NoNdefMessagesExtraInTagException,
				   TagFormatNotSupportedException {
		
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if( rawMsgs == null ) {
			throw new NoNdefMessagesExtraInTagException("Intent did not include the parcelable for the NDEF messages.");
		}
		
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        NdefRecord wifiInfoRecord = msg.getRecords()[0];
        String wifiInfoStr = new String(wifiInfoRecord.getPayload());
//        mParentActivity.displayMessage("Wifi info read from tag:\n" + wifiInfoStr);
        
        return doParse(wifiInfoStr);
	}

    public WifiInfo doParse(String wifiInfoStr) throws TagFormatNotSupportedException {
        // validate the format of the data
        int indexOfSeparator = wifiInfoStr.indexOf( MainActivity.SEPARATOR );
        if ( indexOfSeparator != -1 ) {
        	String ssid = new String( wifiInfoStr.substring(0, indexOfSeparator) );
        	String preSharedKey = new String( wifiInfoStr.substring(indexOfSeparator + 1) );
        	        	
        	messageDisplayer.displayMessage("Wifi info read from tag: " + 
        					"\n\tSSID: \"" + ssid + "\"" +
        					"\n\tPre-shared key: \"" + preSharedKey + "\"" );
        	        	
        	return new WifiInfo(ssid, preSharedKey);
        	
        } else {
        	throw new TagFormatNotSupportedException("The expected separator char not found in the tag data");
        }
    }	
	
}