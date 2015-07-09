package de.richter.alarmmeldung.Core;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.richter.alarmmeldung.database.DatabaseHelper;


public class SMSHelper {

    public static final String TAG = "SMSHelper";

    private Context context;
    private SmsManager smsManager;
    private SendTimer sendTimer;

    public SMSHelper(Context context, LinearLayout status_lout) {
        this.context = context;
        this.smsManager = SmsManager.getDefault();
        this.sendTimer = new SendTimer(context, status_lout);
    }

    // TODO: add SMS to the database
    // TODO: then add a timer to send messages from database
    public void sendSms(String text, Member member) {
        ArrayList<String> smsList;
        DatabaseHelper dbh;

        dbh = new DatabaseHelper(this.context);
        smsList = smsManager.divideMessage(text);
        for (int i = 0; i < smsList.size(); i++) {
            dbh.addSms(new SMS(smsList.get(i), member));
        }
        sendTimer.start();
    }
}
