package com.pimp.companionforband.activities.cloud;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonAccessTokenExtractor {
    private Pattern accessTokenPattern = Pattern.compile("\"access_token\":\\s*\"(\\S*?)\"");
    private Pattern refreshTokenPattern = Pattern.compile("\"refresh_token\":\\s*\"(\\S*?)\"");

    public String extractAccessToken(String response) {
        if (response != null && !response.trim().equals("")) {
            Matcher matcher = accessTokenPattern.matcher(response);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    public String extractRefreshToken(String response) {
        if (response != null && !response.trim().equals("")) {
            Matcher matcher = refreshTokenPattern.matcher(response);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}