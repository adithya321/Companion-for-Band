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
