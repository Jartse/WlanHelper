package com.example.wlanhelper;

public class WriteException extends Exception {
    private static final long serialVersionUID = 42L; // for serialization

    public WriteException() {
    }

    public WriteException(String details) {
        super(details);
    }

    public WriteException(String details, Throwable throwable) {
        super(details, throwable);
    }
}
