package com.MBenDelphi.NativeUssdDemo;

import android.telephony.TelephonyManager;
import android.os.Handler;
import android.os.Looper;

public class NativeUssdHandler {

    private TelephonyManager delphiTelephonyMgr; // A reference to the TelephonyManager passed from Delphi
    private NativeUssdLogger delphiLogger; // A reference to the logger passed from Delphi (optional)
    private NativeUssdResponseCallback javaUssdCallback;
    private Handler mainHandler; // Handler tied to the main looper

    // Constructor with TelephonyManager and NativeUssdLogger parameters (logger can be optional)
    public NativeUssdHandler(TelephonyManager aTelephonyMgr, NativeUssdLogger aLogger) {
        if (aTelephonyMgr == null) {
            throw new IllegalArgumentException("TelephonyManager cannot be null");
        }
        this.delphiTelephonyMgr = aTelephonyMgr;
        this.delphiLogger = aLogger; // Logger can be null
        this.mainHandler = new Handler(Looper.getMainLooper()); // Initialize main looper handler
        this.javaUssdCallback = new NativeUssdResponseCallback(aLogger, this.mainHandler); // Pass the handler
    }

    // Method to send a USSD request, adding better error handling
    public void javaSendUssdRequest(String aUssdCode) {
        if (aUssdCode == null || aUssdCode.isEmpty()) {
            if (delphiLogger != null) {
                delphiLogger.logMessage("Invalid USSD code: cannot be null or empty");
            }
            return; // Abort if the USSD code is invalid
        }

        if (delphiLogger != null) {
            delphiLogger.logMessage("Java is Sending USSD Request: " + aUssdCode);
        }

        try {
            // Use the shared handler for USSD requests and callbacks
            delphiTelephonyMgr.sendUssdRequest(aUssdCode, this.javaUssdCallback, this.mainHandler);
        } catch (Exception e) {
            if (delphiLogger != null) {
                delphiLogger.logMessage("Error while sending USSD request: " + e.getMessage());
            }
        }
    }

    // Method to log a Hello World message and call a native Delphi method
    public void javaHelloWorld() {
        if (delphiLogger != null) {
            delphiLogger.logMessage("Hello, World from Java");
        }
        nativeDelphiGetJavaHelloWorld("From Native: Hello, World from Java");
    }

    // Method to reverse a string and log it
    public String javaGetRevertString(String aString) {
        if (aString == null) {
            if (delphiLogger != null) {
                delphiLogger.logMessage("Cannot reverse a null string.");
            }
            return null;
        }

        StringBuilder strBuilderReversed = new StringBuilder(aString).reverse();

        if (delphiLogger != null) {
            delphiLogger.logMessage("Original string: " + aString);
            delphiLogger.logMessage("Reversed string: " + strBuilderReversed.toString());
        }

        return strBuilderReversed.toString();
    }

    // Native method to be implemented in Delphi for receiving the Hello World message
    public native void nativeDelphiGetJavaHelloWorld(String aString);

    // Additional native method for general use in Delphi
    public native void nativeDelphiMethod();
}
