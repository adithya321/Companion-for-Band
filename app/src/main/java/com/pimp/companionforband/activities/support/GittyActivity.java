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

package com.pimp.companionforband.activities.support;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.gitty_reporter.GittyReporter;
import com.pimp.companionforband.R;

public class GittyActivity extends GittyReporter {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setTargetRepository("adithya321", "Companion-for-Band");
        setGuestOAuth2Token(getString(R.string.oauth_token));
        canEditDebugInfo(true);
    }
}
