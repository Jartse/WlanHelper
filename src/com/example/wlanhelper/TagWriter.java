package com.example.wlanhelper;

import java.io.IOException;
import java.nio.charset.Charset;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

public class TagWriter {

    private MessageDisplayer messageDisplayer;

    public TagWriter(MessageDisplayer messageDisplayer) {
        this.messageDisplayer = messageDisplayer;
    }

    /**
     * Format a tag and write our NDEF message
     */
    public void writeTag(Intent intent, WifiInfo wifiInfo)
            throws IOException, FormatException, WriteException {
        Log.d("JARI WLAN", "Entered writeTag()");

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            throw new WriteException("Intent did not include the TAG parcelable extra.");
        }

        // record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord("com.example.wlanhelper");

        // record that contains our custom wifi setup data, using custom MIME_TYPE
        byte[] payload = wifiInfoAsString(wifiInfo).getBytes();
        byte[] mimeBytes = Constants.WLAN_HELPER_MIMETYPE.getBytes(Charset.forName("US-ASCII"));
        NdefRecord wifiInfoRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        NdefMessage message = new NdefMessage(new NdefRecord[] { wifiInfoRecord, appRecord });

        Ndef ndef = Ndef.get(tag);
        if (ndef != null) { // see if tag is already NDEF formatted
            writeFormattedTag(message, ndef);

        } else { // tag was not formatted -> attempt to format tag (with the message)
            writeUnformattedTag(tag, message);
        }
        messageDisplayer.displayMessage("Tag written successfully!\nClose this app and scan tag.");
    }

    private void writeUnformattedTag(Tag tag, NdefMessage message)
            throws FormatException, WriteException {
        NdefFormatable format = NdefFormatable.get(tag);
        if (format != null) {
            try {
                format.connect();
                format.format(message);
            } catch (IOException e) {
                throw new WriteException("Unable to format tag to NDEF");
            }
        } else {
            throw new WriteException("Tag doesn't support NDEF format");
        }
    }

    private void writeFormattedTag(NdefMessage message, Ndef ndef)
            throws IOException, FormatException, WriteException {
        ndef.connect();

        if (!ndef.isWritable()) {
            throw new WriteException("Read-only tag");
        }

        // work out how much space we need for the data
        int size = message.toByteArray().length;
        if (ndef.getMaxSize() < size) {
            throw new WriteException("Content to be written is too big for the used tag");
        }

        ndef.writeNdefMessage(message);
    }

    private String wifiInfoAsString(WifiInfo wifiInfo) {
        return new String( wifiInfo.getSsid() + Constants.WIFIINFO_SEPARATOR + wifiInfo.getPreSharedKey() );
    }
    
}
