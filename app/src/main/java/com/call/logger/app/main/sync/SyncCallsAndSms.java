package com.call.logger.app.main.sync;

import android.app.Activity;
import android.content.Context;
import com.call.logger.app.main.database.CallAndSmsSQLiteOpenHelper;
import com.call.logger.app.main.model.Call;
import com.call.logger.app.main.model.Sms;
import com.call.logger.app.main.providers.CallLogProvider;
import com.call.logger.app.main.providers.SmsProvider;

import java.util.Collections;
import java.util.List;

public class SyncCallsAndSms {
    private static CallAndSmsSQLiteOpenHelper openHelper;

    public static void syncCalls(Context context) {
        openHelper = new CallAndSmsSQLiteOpenHelper(context);
        int count = openHelper.getCount("CALLS");
        if (0 == count) {
            CallLogProvider callLogsProvider = new CallLogProvider(context);
            List<Call> calls = callLogsProvider.readCallLogs();
            Collections.reverse(calls);
            for (Call call : calls) {
                openHelper.addCall(call);
            }
        }
    }

    public static void syncSms(Activity context) {
        openHelper = new CallAndSmsSQLiteOpenHelper(context);
        int count = openHelper.getCount("MESSAGES");
        if (0 == count) {
            SmsProvider smsProvider = new SmsProvider(context);
            List<Sms> smsList = smsProvider.getAllSms();
            Collections.reverse(smsList);
            for (Sms sms : smsList) {
                openHelper.addSms(sms);
            }
        }
    }
}
