package com.call.logger.app.main.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import com.call.logger.app.main.database.CallAndSmsSQLiteOpenHelper;
import com.call.logger.app.main.model.Sms;
import com.call.logger.app.main.providers.ContactNameProvider;

public class SmsService extends Service {
    private static final String CONTENT_SMS = "content://sms/";
    private long id = 0;
    private ContactNameProvider nameProvider;
    private CallAndSmsSQLiteOpenHelper openHelper;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentResolver contentResolver = getContentResolver();
        contentResolver.registerContentObserver(Uri.parse(CONTENT_SMS), true, new OutgoingSmsObserver(new Handler()));
        nameProvider = new ContactNameProvider(this);
        openHelper = new CallAndSmsSQLiteOpenHelper(this);
        return super.onStartCommand(intent, flags, startId);
    }

    class OutgoingSmsObserver extends ContentObserver {
        public OutgoingSmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Uri uri = Uri.parse(CONTENT_SMS);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToNext();
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));

            if(protocol == null){
                long messageId = cursor.getLong(cursor.getColumnIndex("_id"));
                //проверяем не обрабатывали ли мы это сообщение только-что
                if (messageId != id){
                    id = messageId;
                    int threadId = cursor.getInt(cursor.getColumnIndex("thread_id"));
                    Cursor c = getContentResolver().query(Uri.parse("content://sms/outbox/" + threadId),
                            null, null, null, null);
                    c.moveToNext();

                    Sms sms = new Sms();
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    sms.setNumber(address);
                    sms.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("body")));
                    sms.setDate(cursor.getLong(cursor.getColumnIndexOrThrow("date")));
                    sms.setType("Sent");
                    address = nameProvider.checkName(address);

                    if (null != address) {
                        sms.setName(address);
                    } else {
                        sms.setName("Unknown");
                    }
                    openHelper.addSms(sms);
                }
            }
        }
    }
}
