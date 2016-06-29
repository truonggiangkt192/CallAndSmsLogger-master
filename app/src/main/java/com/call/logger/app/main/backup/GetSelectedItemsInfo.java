package com.call.logger.app.main.backup;

import android.app.Activity;
import com.call.logger.app.main.R;
import com.call.logger.app.main.date.GetDateFormat;
import com.call.logger.app.main.model.Call;
import com.call.logger.app.main.model.Sms;

import java.util.List;

public class GetSelectedItemsInfo {
    private Activity activity;
    private GetDateFormat dateFormat;

    public GetSelectedItemsInfo(Activity activity) {
        this.activity = activity;
        dateFormat = new GetDateFormat(activity);
    }

    public StringBuilder getCallsInfo(List<Call> calls) {
        String duration = activity.getString(R.string.info_duration);
        String when = activity.getString(R.string.info_when);
        StringBuilder infoText = new StringBuilder();
        for (Call c : calls) {
            if (c.isSelected()) {
                String callInfo = String.format("%s: %s %s\n" + duration + " %s\n" + when + " %s\n",
                        getCallType(c.getType()), getName(c.getName()), c.getNumber(), c.getDuration(),
                        dateFormat.getDateTime(c.getDate()));
                infoText.append(callInfo).append("\n");
            }
        }
        return deleteSpaces(infoText);
    }

    public StringBuilder getSmsInfo(List<Sms> smsList) {
        String when = activity.getString(R.string.info_when);
        StringBuilder infoText = new StringBuilder();
        for (Sms c : smsList) {
            if (c.isSelected()) {
                String smsInfo = String.format("%s:\n%s\n%s %s\n" + when + " %s\n", getSmsType(c.getType()),
                        c.getMessage(), getName(c.getName()), c.getNumber(), dateFormat.getDateTime(c.getDate()));
                infoText.append(smsInfo).append("\n");
            }
        }
        return deleteSpaces(infoText);
    }

    private String getCallType(String type) {
        if (type.equalsIgnoreCase("Incoming")) {
            return activity.getString(R.string.incoming);
        } else if (type.equalsIgnoreCase("Outgoing")) {
            return activity.getString(R.string.outgoing);
        } else if (type.equalsIgnoreCase("Missed")) {
            return activity.getString(R.string.missed);
        }
        return null;
    }

    private String getSmsType(String type) {
        if (type.equalsIgnoreCase("Inbox")) {
            return activity.getString(R.string.sms_inbox);
        } else if (type.equalsIgnoreCase("Sent")) {
            return activity.getString(R.string.sms_sent);
        }
        return null;
    }

    private String getName(String name) {
        if (name.equalsIgnoreCase("Unknown")) {
            return activity.getString(R.string.unknown);
        }
        return name;
    }

    private StringBuilder deleteSpaces(StringBuilder emailText) {
        int length = emailText.length();
        emailText.delete(length - 2, length);
        return emailText;
    }


    public boolean isSelectedCalls(List<Call> calls) {
        for (Call call : calls) {
            if (call.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSelectedSms(List<Sms> smsList) {
        for (Sms sms : smsList) {
            if (sms.isSelected()) {
                return true;
            }
        }
        return false;
    }
}