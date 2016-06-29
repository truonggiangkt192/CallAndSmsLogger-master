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
import com.call.logger.app.main.model.Call;

import java.util.List;

public class CallAdapter extends ArrayAdapter<Call> {
    private List<Call> calls;
    private Activity activity;
    private GetDateFormat dateFormat;

    public CallAdapter(Activity activity, List<Call> calls) {
        super(activity, R.layout.call_item);
        this.calls = calls;
        this.activity = activity;
        dateFormat = new GetDateFormat(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Call call = calls.get(position);

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.call_item, null);
        }

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBoxCall);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                call.setSelected(isChecked);
            }
        });

        checkBox.setChecked(call.isSelected());
        TextView textView = (TextView) view.findViewById(R.id.textViewPhone);
        textView.setText(call.getNumber());
        TextView textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewName.setText(call.getName());
        textView = (TextView) view.findViewById(R.id.textViewDuration);
        textView.setText(call.getDuration());
        textView = (TextView) view.findViewById(R.id.textViewDate);
        textView.setText(dateFormat.getDateTime(call.getDate()));
        textView = (TextView) view.findViewById(R.id.textViewType);

        if (call.getType().equalsIgnoreCase("Incoming")) {
            textView.setText(activity.getString(R.string.incoming));
            textView.setTextColor(Color.parseColor(activity.getString(R.color.blue)));
        } else if (call.getType().equalsIgnoreCase("Outgoing")) {
            textView.setText(activity.getString(R.string.outgoing));
            textView.setTextColor(Color.GREEN);
        } else if (call.getType().equalsIgnoreCase("Missed")) {
            textView.setText(activity.getString(R.string.missed));
            textView.setTextColor(Color.RED);
        }
        if (call.getName().equalsIgnoreCase("Unknown")) {
            textViewName.setText(activity.getString(R.string.unknown));
        }
        return view;
    }

    @Override
    public int getCount() {
        return calls.size();
    }
}
