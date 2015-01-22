package de.richter.alarmmeldung.SMS;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

public class SendBroadcastReciever extends BroadcastReceiver {

    public static final String TAG = "SendBroadcastReciever";


    private void setStatus() {
        // TODO: Update Status in the MainActivity
        return;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int resCode;
        String number;
        Bundle b;
        SMSHelper smsHelper;

        b = intent.getExtras();
        number = b.getString(SMSHelper.KEY_RECV_NUMBER);
        smsHelper = (SMSHelper) b.getSerializable(SMSHelper.KEY_CALLER_OBJECT);

        resCode = getResultCode();
        switch (resCode) {
            case Activity.RESULT_OK:
                Log.d(TAG, "SMS Sent to " + number);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.i(TAG, "SMS sending failed!");
            default:
                Log.e(TAG, "unknown Resultcode! " + resCode);
        }
    }
}
