package com.call.logger.app.main.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.call.logger.app.main.database.CallAndSmsSQLiteOpenHelper;
import com.call.logger.app.main.model.Call;
import com.call.logger.app.main.providers.CallLogProvider;
import com.call.logger.app.main.sync.SyncCallsAndSms;

import java.util.List;

public class CallListenerReceiver extends BroadcastReceiver {
    private static boolean status;

    @Override
    public void onReceive(Context context, Intent intent) {
        SyncCallsAndSms.syncCalls(context);
        CallAndSmsSQLiteOpenHelper helper = new CallAndSmsSQLiteOpenHelper(context);
        CallLogProvider provider = new CallLogProvider(context);

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                status = true;
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                status = true;
            }

            if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //ждем добавления звонка в бд телефона
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (status) {
                    List<Call> call = provider.readCallLogs();
                    helper.addCall(call.get(0));
                }
                status = false;
            }
        }
    }
}
