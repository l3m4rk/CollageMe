package com.example.android.collageme;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by l3m4rk on 15.12.14.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }
}
