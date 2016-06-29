package com.call.logger.app.main.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.call.logger.app.main.R;
import com.call.logger.app.main.database.CallAndSmsSQLiteOpenHelper;
import com.call.logger.app.main.model.Sms;
import com.call.logger.app.main.providers.ContactNameProvider;

public class IncomingSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Sms sms = null;
        CallAndSmsSQLiteOpenHelper openHelper = new CallAndSmsSQLiteOpenHelper(context);
        final Bundle bundle = intent.getExtras();
        StringBuilder builder = new StringBuilder();
        ContactNameProvider nameProvider = new ContactNameProvider(context);

        if (bundle != null) {
            final Object[] pdusObj = (Object[]) bundle.get("pdus");
            for (Object aPdusObj : pdusObj) {
                sms = new Sms();
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                builder.append(currentMessage.getDisplayMessageBody());
                sms.setNumber(currentMessage.getDisplayOriginatingAddress());
                sms.setDate(currentMessage.getTimestampMillis());
                sms.setType(context.getString(R.string.sms_inbox));
                String address = nameProvider.checkName(currentMessage.getDisplayOriginatingAddress());

                if (null != address) {
                    sms.setName(address);
                } else {
                    sms.setName(context.getString(R.string.unknown));
                }
            }
            assert sms != null;
            sms.setMessage(builder.toString());
            openHelper.addSms(sms);
        }
    }
}
