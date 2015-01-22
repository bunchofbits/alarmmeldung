package de.richter.alarmmeldung.SMS;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;


public class SMSHelper implements Serializable {

    public static final String TAG = "SMSHelper";
    public static final String ACTION_SMS_SENT = "SMS_SENT";
    public static final String ACTION_SMS_DELIVERED = "SMS_DELIVERED";
    public static final String KEY_RECV_NUMBER = "KEY_RECV_NUMBER";
    public static final String KEY_CALLER_OBJECT = "KEY_CALLER_OBJECT";

    private Context context;
    private SmsManager smsManager;
    private LinearLayout status_lout;

    public SMSHelper(Context context, LinearLayout status_lout) {
        this.context = context;
        this.smsManager = SmsManager.getDefault();
        this.status_lout = status_lout;
    }

    public void sendSms(String text, String number) {
        ArrayList<String> smsList;
        View stat_vw;

        Intent sentBcIntent;
        Intent delivBcIntent;

        PendingIntent piSent;
        PendingIntent piDelivered;

        sentBcIntent = new Intent(ACTION_SMS_SENT);
        delivBcIntent = new Intent(ACTION_SMS_DELIVERED);

        Bundle b = new Bundle();
        b.putCharSequence(KEY_RECV_NUMBER, number);
        b.putSerializable(KEY_CALLER_OBJECT, this);
        sentBcIntent.putExtras(b);

        piSent = PendingIntent.getBroadcast(this.context, 0, sentBcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        piDelivered = PendingIntent.getBroadcast(this.context, 0, delivBcIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        smsList = smsManager.divideMessage(text);
        for (int i = 0; i < smsList.size(); i++) {
            Log.d(TAG, "Sending SMS: '" + smsList.get(i) + "'");
            smsManager.sendTextMessage(number, null, smsList.get(i), piSent, piDelivered);
        }
    }
}
