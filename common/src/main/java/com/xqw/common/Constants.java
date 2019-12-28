package com.xqw.common;

/**
 *
 */
public class Constants {
    private static String clientId;

    public static volatile Object lastClipboardContent = "";

    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String clientId) {
        Constants.clientId = clientId;
    }
}
