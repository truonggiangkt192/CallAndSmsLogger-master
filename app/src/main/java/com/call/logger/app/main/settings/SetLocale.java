package com.call.logger.app.main.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

public class SetLocale {

    public void set(Activity activity) {
        String language = getLanguageCode(activity);
        if ("".equals(language)) {
            String code = Locale.getDefault().getLanguage();
            if (code.equals("ru")) {
                changeLocale(activity, code);
            } else {
                changeLocale(activity, "en");
            }
        } else {
            changeLocale(activity, language);
        }
    }

    private String getLanguageCode(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString("langCode", "");
    }

    private void changeLocale(Activity activity, String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());
    }
}
