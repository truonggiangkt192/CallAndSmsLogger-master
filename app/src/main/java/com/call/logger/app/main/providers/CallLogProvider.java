package com.call.logger.app.main.providers;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import com.call.logger.app.main.model.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallLogProvider {
    private Context context;

    public CallLogProvider(Context context) {
        this.context = context;
    }

    public List<Call> readCallLogs() {
        List<Call> callList = new ArrayList<Call>();
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String cacheNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                long dateTimeMillis = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                long durationSeconds = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
                int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

                String duration = getDuration(durationSeconds * 1000);

                if (cacheNumber == null)
                    cacheNumber = number;
                if (name == null)
                    name = "Unknown";

                String type = getCallType(callType);

                Call call = new Call();
                call.setName(name);
                call.setNumber(cacheNumber);
                call.setDuration(duration);
                call.setDate(dateTimeMillis);
                call.setType(type);
                callList.add(call);
            }
            cursor.close();
        }
        return callList;
    }

    private String getCallType(int callType) {
        String type = null;
        if (callType == CallLog.Calls.INCOMING_TYPE) {
            type = "Incoming";
        } else if (callType == CallLog.Calls.MISSED_TYPE) {
            type = "Missed";
        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
            type = "Outgoing";
        }
        return type;
    }

    private String getDuration(long milliseconds) {
        Date date = new Date();
        date.setTime(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        return dateFormat.format(date);
    }
}
