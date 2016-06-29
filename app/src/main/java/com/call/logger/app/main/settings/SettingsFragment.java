package com.call.logger.app.main.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.call.logger.app.main.R;

public class SettingsFragment extends PreferenceActivity {
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new SetLocale().set(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setTitle(getString(R.string.settings));
        ListPreference preference = (ListPreference) findPreference("langCode");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                finish();
                Intent intent = getIntent();
                startActivity(intent);
                return true;
            }
        });
    }
}
