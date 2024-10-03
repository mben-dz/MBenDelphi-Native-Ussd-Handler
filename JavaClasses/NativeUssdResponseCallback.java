package com.MBenDelphi.NativeUssdDemo;

import android.telephony.TelephonyManager;
import android.os.Handler;

public class NativeUssdResponseCallback extends TelephonyManager.UssdResponseCallback {

    private Handler mainHandler; // Handler passed from NativeUssdHandler
    private NativeUssdLogger delphiLogger; // A reference to the logger passed from Delphi (optional)

    // Constructor with NativeUssdLogger and Handler parameters
    public NativeUssdResponseCallback(NativeUssdLogger aLogger, Handler aHandler) {
        if (aHandler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        this.delphiLogger = aLogger;
        this.mainHandler = aHandler; // Use the handler passed from NativeUssdHandler
    }

    // Method to handle successful USSD responses
    @Override
    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
        mainHandler.post(() -> {
            if (delphiLogger != null) {
                delphiLogger.logMessage("USSD Response From Java: " + request);
                delphiLogger.logMessage("Response: " + response);
            }
            nativeOnReceiveUssdResponse(request, response.toString());
        });
    }

    // Method to handle failed USSD responses
    @Override
    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
        mainHandler.post(() -> {
            if (delphiLogger != null) {
                delphiLogger.logMessage("USSD request failed (Message From Java. Failure Code: " + failureCode + ")");
            }
            nativeOnReceiveUssdResponseFailed(request, failureCode);
        });
    }

    // Native methods to be implemented in Delphi
    public native void nativeOnReceiveUssdResponse(String aRequest, String aResponse);
    public native void nativeOnReceiveUssdResponseFailed(String aRequest, int aFailureCode);
}
