package com.pimp.companionforband;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.gitty_reporter.GittyReporter;

public class GittyActivity extends GittyReporter {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setTargetRepository("adithya321", "Companion-for-Band");
        setGuestOAuth2Token(getString(R.string.oauth_token));
        canEditDebugInfo(true);
    }
}
