package com.call.logger.app.main.backup.email;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.call.logger.app.main.R;
import com.call.logger.app.main.backup.GetSelectedItemsInfo;
import com.call.logger.app.main.model.Call;
import com.call.logger.app.main.model.Sms;
import com.call.logger.app.main.settings.SettingsFragment;

import java.util.List;

public class SendEmail {
    private Activity activity;
    private GetSelectedItemsInfo itemsInfo;

    public SendEmail(Activity activity) {
        this.activity = activity;
        itemsInfo = new GetSelectedItemsInfo(activity);
    }

    public void sendCalls(List<Call> calls) {
        StringBuilder mailText = itemsInfo.getCallsInfo(calls);
        startEmailIntent(mailText);
    }

    public void sendSms(List<Sms> smsList) {
        StringBuilder mailText = itemsInfo.getSmsInfo(smsList);
        startEmailIntent(mailText);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null;
    }

    private void startEmailIntent(StringBuilder emailText) {
        Intent email = new Intent(Intent.ACTION_SENDTO);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_SUBJECT, "Call n SMS logger");
        email.putExtra(Intent.EXTRA_TEXT, emailText.toString());

        String mail = getEmailAddress();
        if (mail.equals("")) {
            Intent settings = new Intent(activity, SettingsFragment.class);
            activity.startActivity(settings);
        } else {
            email.setData(Uri.parse("mailto:" + mail));
            email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(Intent.createChooser(email, activity.getString(R.string.chooser)));
        }
    }

    private String getEmailAddress() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getString("email", "");
    }
}
