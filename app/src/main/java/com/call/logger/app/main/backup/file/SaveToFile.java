package com.call.logger.app.main.backup.file;

import android.app.Activity;
import android.os.Environment;
import com.call.logger.app.main.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SaveToFile {
    private Activity activity;

    public SaveToFile(Activity activity) {
        this.activity = activity;
    }

    public void save(StringBuilder info) {
        DateFormat dateFormat = new SimpleDateFormat(activity.getString(R.string.date_format));
        Calendar cal = Calendar.getInstance();
        String sDate = dateFormat.format(cal.getTime());
        try {
            String fileName = "log_" + sDate + ".txt";
            File dir = new File(Environment.getExternalStorageDirectory() + "/CallAndSmsLogger");
            dir.mkdirs();
            File file = new File (dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(info.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
