/*
 * Companion for Band
 * Copyright (C) 2016  Adithya J
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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