package de.richter.alarmmeldung.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.richter.alarmmeldung.Member;
import de.richter.alarmmeldung.Message;
import de.richter.alarmmeldung.SendingOrder;

public class SMSOutboxHelper {

    public static final String TAG = "SMSOutboxHelper";
    public static final String SMSOUT_TBL_NAME = "smsoutbox";
    public static final String SMSOUT_COL_MESSAGE_ID = "message_id";
    public static final String SMSOUT_COL_MEMBER_ID = "member_id";
    public static final String SMSOUT_TBL_CREATE = "CREATE TABLE IF NOT EXISTS " +
            SMSOUT_TBL_NAME + " ( " +
            SMSOUT_COL_MESSAGE_ID + " INTEGER NOT NULL, " +
            SMSOUT_COL_MEMBER_ID + " INTEGER NOT NULL, " +
            "PRIMARY KEY (" + SMSOUT_COL_MESSAGE_ID + ", " + SMSOUT_COL_MEMBER_ID + ")";

    private Context context;

    public SMSOutboxHelper(Context context) {
        this.context = context;
    }

    public void createTable(SQLiteDatabase db) {
        Log.i(TAG, "creating Table " + SMSOUT_TBL_NAME);
        DatabaseHelper.LogExec(TAG, SMSOUT_TBL_CREATE);
        db.execSQL(SMSOUT_TBL_CREATE);
    }

    public void dropTable(SQLiteDatabase db) {
        Log.i(TAG, "Dropping table " + SMSOUT_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SMSOUT_TBL_NAME);
    }

    public boolean addSMS(Message message, Member member, SQLiteDatabase db) {
        boolean ret = true;
        String sql;
        sql = "INSERT INTO " + SMSOUT_TBL_NAME +
                " ( " + SMSOUT_COL_MESSAGE_ID + ", " + SMSOUT_COL_MEMBER_ID + " ) VALUES ( " +
                message.getId() + " , " + member.getId() +
                " ) ";

        DatabaseHelper.LogExec(TAG, sql);
        try {
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Error adding SMS '" + message.getMessage() + "' for " + member + "");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public ArrayList<SendingOrder> getAllOrders(SQLiteDatabase db) {
        ArrayList<SendingOrder> orders = null;
        Cursor curs;
        String sql = "SELECT * FROM " + SMSOUT_TBL_NAME;
        curs = db.rawQuery(sql, null);
        if (!curs.moveToFirst()) {
            return null;
        }

        do {
            orders.add(new SendingOrder(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(SMSOUT_COL_MEMBER_ID))),
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(SMSOUT_COL_MESSAGE_ID)))
            ));
        } while (curs.moveToNext());

        return orders;
    }

    public boolean removeSMS(Message message, Member member, SQLiteDatabase db) {
        boolean ret = true;

        try {
            db.delete(SMSOUT_TBL_NAME, SMSOUT_COL_MEMBER_ID + " = " + member.getId() + " AND " + SMSOUT_COL_MESSAGE_ID + " = " + message.getId(), null);
        } catch (SQLException ex) {
            Log.e(TAG, "Error deleting SMS '" + message.getMessage() + "' for " + member);
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }
}
