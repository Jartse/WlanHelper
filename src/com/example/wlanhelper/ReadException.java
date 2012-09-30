package com.example.wlanhelper;

public class ReadException extends Exception {
    private static final long serialVersionUID = 42L; // for serialization

    public ReadException() {
    }

    public ReadException(String details) {
        super(details);
    }

    public ReadException(String details, Throwable throwable) {
        super(details, throwable);
    }
}
