package com.example.groupvideochatdemo;

public class OpenTokConfig {

    /**
     * Create your own project from OpenTok dashboard
     *
     * url:     https://dashboard.tokbox.com/projects
     *
     * and fill below variables
     */

    // Add OpenTok API key here copied from your own project
    public static final String API_KEY = "46613432";

    // Create a session id and add generated Session ID
    public static final String SESSION_ID = "1_MX40NjYxMzQzMn5-MTU5NzU4Mjg2NzcyNn5ObERUbk15M3hvNFJsUktzam00endtV3d-fg";

    // Generate token using session id and add token here (from the dashboard or using an OpenTok server SDK)
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NjYxMzQzMiZzaWc9NWEwNGE5YjBiZjQwZDFkNGZjZTM5MzFmMWE2ZmRmODE1YzNiZDc1NTpzZXNzaW9uX2lkPTFfTVg0ME5qWXhNelF6TW41LU1UVTVOelU0TWpnMk56Y3lObjVPYkVSVWJrMTVNM2h2TkZKc1VrdHphbTAwZW5kdFYzZC1mZyZjcmVhdGVfdGltZT0xNTk3NTgyOTA5Jm5vbmNlPTAuOTI0NjUwOTY1OTA5NDYyNCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTk3NjA0NTA4JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";


    // *** The code below is to validate this configuration file.***
    public static String errorMessage;
    public static boolean isConfigsValid() {
        if (OpenTokConfig.API_KEY != null && !OpenTokConfig.API_KEY.isEmpty()
                && OpenTokConfig.SESSION_ID != null && !OpenTokConfig.SESSION_ID.isEmpty()
                && OpenTokConfig.TOKEN != null && !OpenTokConfig.TOKEN.isEmpty()) {
            return true;
        }
        else {
            errorMessage = "API KEY, SESSION ID and TOKEN in OpenTokConfig.java cannot be null or empty.";
            return false;
        }
    }
}
