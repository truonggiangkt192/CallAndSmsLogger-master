package com.call.logger.app.main.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.call.logger.app.main.R;
import com.call.logger.app.main.date.GetDateFormat;
import com.call.logger.app.main.model.Sms;

import java.util.List;

public class SmsAdapter extends ArrayAdapter<Sms> {
    private List<Sms> smsList;
    private Activity activity;
    private GetDateFormat dateFormat;

    public SmsAdapter(Activity activity, List<Sms> smsList) {
        super(activity, R.layout.call_item);
        this.smsList = smsList;
        this.activity = activity;
        dateFormat = new GetDateFormat(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Sms sms = smsList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.sms_item, null);
        }

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxSMS);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sms.setSelected(isChecked);
            }
        });

        checkBox.setChecked(sms.isSelected());
        TextView textView = (TextView) view.findViewById(R.id.textViewSms);
        textView.setText(sms.getMessage());
        TextView textViewName = (TextView) view.findViewById(R.id.textViewSmsName);
        textViewName.setText(sms.getName());
        textView = (TextView) view.findViewById(R.id.textViewSmsNumber);
        textView.setText(sms.getNumber());
        textView = (TextView) view.findViewById(R.id.textViewSmsDate);
        textView.setText(dateFormat.getDateTime(sms.getDate()));
        textView = (TextView) view.findViewById(R.id.textViewSmsType);
        textView.setText(sms.getType());

        if (sms.getType().equalsIgnoreCase("Inbox")) {
            textView.setText(activity.getString(R.string.inbox));
            textView.setTextColor(Color.parseColor(activity.getString(R.color.blue)));
        } else if (sms.getType().equalsIgnoreCase("Sent")) {
            textView.setText(activity.getString(R.string.sent));
            textView.setTextColor(Color.GREEN);
        }
        if (sms.getName().equalsIgnoreCase("Unknown")) {
            textViewName.setText(activity.getString(R.string.unknown));
        }
        return view;
    }

    @Override
    public int getCount() {
        return smsList.size();
    }
}
