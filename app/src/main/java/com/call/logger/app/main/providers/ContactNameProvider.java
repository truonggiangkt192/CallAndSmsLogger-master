package com.call.logger.app.main.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ContactNameProvider {
    private Context context;

    public ContactNameProvider(Context context) {
        this.context = context;
    }

    public String checkName(String number) {
        String contact = null;
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = context.getContentResolver().query(uri, new String[] {PhoneLookup.DISPLAY_NAME},
                PhoneLookup.NUMBER + "='" + number + "'", null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            contact=cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }
        return contact;
    }
}
