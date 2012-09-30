package com.example.wlanhelper;

public class WifiSetupException extends Exception {
    private static final long serialVersionUID = 42L; // for serialization

    public WifiSetupException() {
    }

    public WifiSetupException(String details) {
        super(details);
    }

    public WifiSetupException(String details, Throwable throwable) {
        super(details, throwable);
    }
}
