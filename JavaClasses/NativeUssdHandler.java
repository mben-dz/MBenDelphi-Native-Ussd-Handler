package com.MBenDelphi.NativeUssdDemo;

import android.telephony.TelephonyManager;
import android.os.Handler;
import android.os.Looper;

public class NativeUssdHandler {

    private TelephonyManager delphiTelephonyMgr; // A reference to the TelephonyManager passed from Delphi
    private NativeUssdLogger delphiLogger; // A reference to the logger passed from Delphi (optional)
    private Handler mainHandler; // Handler tied to the main looper

    // Constructor with TelephonyManager and NativeUssdLogger parameters (logger can be optional)
    public NativeUssdHandler(TelephonyManager aTelephonyMgr, NativeUssdLogger aLogger) {
        if (aTelephonyMgr == null) {
            throw new IllegalArgumentException("TelephonyManager cannot be null");
        }
        this.delphiTelephonyMgr = aTelephonyMgr;
        this.delphiLogger = aLogger; // Logger can be null
        this.mainHandler = new Handler(Looper.getMainLooper()); // Initialize main looper handler
    }

    // Method to send a USSD request with an externally created NativeUssdResponseCallback
    public void javaSendUssdRequest(String aUssdCode, NativeUssdResponseCallback aCallback) {
        if (aUssdCode == null || aUssdCode.isEmpty()) {
            if (delphiLogger != null) {
                delphiLogger.logMessage("Invalid USSD code: cannot be null or empty");
            }
            return; // Abort if the USSD code is invalid
        }

        if (aCallback == null) {
            throw new IllegalArgumentException("NativeUssdResponseCallback cannot be null");
        }

        if (delphiLogger != null) {
            delphiLogger.logMessage("Java is Sending USSD Request: " + aUssdCode);
        }

        try {
            // Use the callback passed from Delphi and the shared handler
            delphiTelephonyMgr.sendUssdRequest(aUssdCode, aCallback, this.mainHandler);
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

    // Method to return the main handler to be accessed in Delphi
    public Handler getMainHandler() {
        return mainHandler;
    }

    // Native method to be implemented in Delphi for receiving the Hello World message
    public native void nativeDelphiGetJavaHelloWorld(String aString);

    // Additional native method for general use in Delphi
    public native void nativeDelphiMethod();
}
