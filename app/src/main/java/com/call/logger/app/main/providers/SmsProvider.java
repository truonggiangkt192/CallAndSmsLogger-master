package com.call.logger.app.main.providers;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.call.logger.app.main.R;
import com.call.logger.app.main.model.Sms;

import java.util.ArrayList;
import java.util.List;

public class SmsProvider {
    private Activity context;

    public SmsProvider(Activity context) {
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    public List<Sms> getAllSms() {
        List<Sms> smsList = new ArrayList<Sms>();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        context.startManagingCursor(c);
        int totalSMS = c.getCount();

        ContactNameProvider nameProvider = new ContactNameProvider(context);

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                Sms sms = new Sms();
                String address = c.getString(c.getColumnIndexOrThrow("address"));
                sms.setNumber(address);
                sms.setMessage(c.getString(c.getColumnIndexOrThrow("body")));
                sms.setDate(c.getLong(c.getColumnIndexOrThrow("date")));
                String type = getSmsType(c.getString(c.getColumnIndexOrThrow("type")));
                sms.setType(type);
                address = nameProvider.checkName(address);

                if (null != address) {
                    sms.setName(address);
                } else {
                    sms.setName(context.getString(R.string.unknown));
                }
                smsList.add(sms);
                c.moveToNext();
            }
        }
        return smsList;
    }

    private String getSmsType(String smsType) {
        String type = null;
        if (smsType.equals("1")) {
            type = "Inbox";
        } else if (smsType.equals("2")) {
            type = "Sent";
        }
        return type;
    }
}
