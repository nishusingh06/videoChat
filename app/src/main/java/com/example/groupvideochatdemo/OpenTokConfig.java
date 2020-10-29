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
    public static final String API_KEY = "";

    // Create a session id and add generated Session ID
    public static final String SESSION_ID = "";

    // Generate token using session id and add token here (from the dashboard or using an OpenTok server SDK)
    public static final String TOKEN = "";


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
