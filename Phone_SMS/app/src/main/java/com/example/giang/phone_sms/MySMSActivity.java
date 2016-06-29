package com.example.giang.phone_sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Giang on 6/22/2016.
 */
public class MySMSActivity extends AppCompatActivity {
    Button btnSendSMS;
    EditText editContent;
    TextView txtSendTo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sms);
        setContentView(R.layout.activity_my_sms);
        btnSendSMS =(Button) findViewById(R.id.btnSendSms);
        editContent =(EditText) findViewById(R.id.editSMS);
        txtSendTo=(TextView) findViewById(R.id.txtSendTo);
        // Lấy thông tin từ intent
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("DATA");
        final MyContact mc = (MyContact)bundle.getSerializable("CONTACT");
        btnSendSMS.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms(mc);
            }
        });
        txtSendTo.setText("Send to: " + mc.getPhone());
    }
    public void sendSms(MyContact mc){
        //Lấy mặt định SmsManager
        final SmsManager sms = SmsManager.getDefault();
        Intent msgSent = new Intent("ACTION_MSG_SENT");
        //Khai báo pendingintent để kiểm tra kết quả
        final PendingIntent pendingMsgSent = PendingIntent.getBroadcast(this, 0, msgSent, 0);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = getResultCode();
                String msg =  "Sent Ok";
                if(result != Activity.RESULT_OK){
                    msg = "Sent failed";
                }
                Toast.makeText(MySMSActivity.this, msg, Toast.LENGTH_LONG).show();

            }
        },new IntentFilter("ACTION_MSG_SENT"));
        // Gọi hàm sử lý tin nhắn
        sms.sendTextMessage(mc.getPhone(), null, editContent.getText()+"",pendingMsgSent, null);
        finish();
    }
}
