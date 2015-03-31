package de.richter.alarmmeldung.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.richter.alarmmeldung.Core.Member;
import de.richter.alarmmeldung.Core.SMS;

public class SMSOutboxHelper {

    public static final String TAG = "SMSOutboxHelper";
    public static final String SMSOUT_TBL_NAME = "smsoutbox";
    public static final String SMSOUT_COL_ID = "ID";
    public static final String SMSOUT_COL_MESSAGE = "message";
    public static final String SMSOUT_COL_MEMBER_ID = "member_id";
    public static final String SMSOUT_TBL_CREATE = "CREATE TABLE IF NOT EXISTS " +
            SMSOUT_TBL_NAME + " ( " +
            SMSOUT_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SMSOUT_COL_MESSAGE + " VARCHAR NOT NULL, " +
            SMSOUT_COL_MEMBER_ID + " INTEGER NOT NULL ) ) ";

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

    public boolean addSMS(SMS sms, SQLiteDatabase db) {
        boolean ret = true;
        String sql;
        sql = "INSERT INTO " + SMSOUT_TBL_NAME +
                " ( " + SMSOUT_COL_MESSAGE + ", " + SMSOUT_COL_MEMBER_ID + " ) VALUES ( " +
                "'" + sms.getMessage() + "' , " + sms.getMember().getId() +
                " ) ";

        DatabaseHelper.LogExec(TAG, sql);
        try {
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Error adding SMS '" + sms.getMessage() + "' for " + sms.getMember() + "");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public ArrayList<SMS> getAllSMS(SQLiteDatabase db) {
        ArrayList<SMS> smses = null;
        Member mem;
        DatabaseHelper dbh = null;
        Cursor curs;
        String sql = "SELECT * FROM " + SMSOUT_TBL_NAME;
        curs = db.rawQuery(sql, null);
        if (!curs.moveToFirst()) {
            return null;
        }

        dbh = new DatabaseHelper(this.context);
        do {
            mem = dbh.getMemberByNumber(Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(SMSOUT_COL_MEMBER_ID))));
            smses.add(new SMS(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow((SMSOUT_COL_ID)))),
                    curs.getString(curs.getColumnIndexOrThrow(SMSOUT_COL_MESSAGE)),
                    mem
            ));
        } while (curs.moveToNext());
        return smses;
    }

    public boolean removeSMS(SMS sms, SQLiteDatabase db) {
        boolean ret = true;

        try {
            db.delete(SMSOUT_TBL_NAME, SMSOUT_COL_ID + " = " + sms.getID(), null);
        } catch (SQLException ex) {
            Log.e(TAG, "Error deleting SMS '" + sms.getMessage() + "' for " + sms.getMember());
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }
}
