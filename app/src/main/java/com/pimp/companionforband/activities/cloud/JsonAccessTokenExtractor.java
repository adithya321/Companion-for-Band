package com.pimp.companionforband.activities.cloud;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonAccessTokenExtractor {
    private Pattern accessTokenPattern = Pattern.compile("\"access_token\":\\s*\"(\\S*?)\"");

    public String extract(String response) {
        if (response != null && !response.trim().equals("")) {
            Matcher matcher = accessTokenPattern.matcher(response);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return response;
    }
}