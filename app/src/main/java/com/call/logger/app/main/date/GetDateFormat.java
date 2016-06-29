package com.call.logger.app.main.date;

import android.app.Activity;
import android.content.Context;
import com.call.logger.app.main.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDateFormat {
    private Context context;

    public GetDateFormat(Activity activity) {
        context = activity;
    }

    public String getDateTime(long milliseconds) {
        String format = context.getString(R.string.datetime_format);
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(milliseconds);
        return dateFormat.format(date);
    }
}
