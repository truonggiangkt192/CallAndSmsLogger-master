package com.call.logger.app.main.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.call.logger.app.main.services.SmsService;

public class OutgoingSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SmsService.class);
        context.startService(service);
    }
}
