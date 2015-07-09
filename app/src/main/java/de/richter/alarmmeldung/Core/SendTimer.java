package de.richter.alarmmeldung.Core;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.LinearLayout;

public class SendTimer extends IntentService {

    public static final String KEY_RECV_NUMBER = "KEY_RECV_NUMBER";
    public static final String KEY_CALLER_OBJECT = "KEY_CALLER_OBJECT";
    private static final String TAG = "SendTimer";
    private static final String ACTION_SMS_SENT = "SMS_SENT";
    private static final String ACTION_SMS_DELIVERED = "SMS_DELIVERED";

    private CountDownTimer timer;
    private SmsManager smsManager;
    private LinearLayout status_lout;
    private Context context;

    public SendTimer() {
        super("AlammeldungSmsSender");
        Log.d(TAG, "default Constructor called!!!");
    }

    public SendTimer(Context context, LinearLayout status_lout) {
        super("AlammeldungSmsSender");
        this.smsManager = SmsManager.getDefault();
        this.status_lout = status_lout;
        this.context = context;
        this.timer = null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent called");
    }

    private void sendSMS() {
        Intent sentBcIntent;
        Intent delivBcIntent;

        PendingIntent piSent;
        PendingIntent piDelivered;

        sentBcIntent = new Intent(ACTION_SMS_SENT);
        delivBcIntent = new Intent(ACTION_SMS_DELIVERED);

        //Bundle b = new Bundle();
        //b.putCharSequence(KEY_RECV_NUMBER, number);
        //b.putSerializable(KEY_CALLER_OBJECT, this);
        //sentBcIntent.putExtras(b);
        Log.w(TAG, "Implement sendSMS in " + TAG);
//        piSent = PendingIntent.getBroadcast(this.context, 0, sentBcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        piDelivered = PendingIntent.getBroadcast(this.context, 0, delivBcIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Log.d(TAG, "Sending SMS: '" + smsList.get(i) + "'");
        //smsManager.sendTextMessage(number, null, smsList.get(i), piSent, piDelivered);
    }

    public void start() {
        startService(new Intent(this.context, SendTimer.class));
        Log.d(TAG, "start called)");
    }
}
