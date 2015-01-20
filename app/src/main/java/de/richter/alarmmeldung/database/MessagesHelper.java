package de.richter.alarmmeldung.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.richter.alarmmeldung.Message;

public class MessagesHelper {

    public static final String TAG = "MessagesHelper";
    public static final String MESSAGE_TBL_NAME = "messages";
    public static final String MESSAGE_COL_ID = "ID";
    public static final String MESSAGE_COL_MESSAGE = "message";
    public static final String MESSAGE_TBL_CREATE = "CREATE TABLE IF NOT EXISTS "
            + MESSAGE_TBL_NAME + " ( "
            + MESSAGE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MESSAGE_COL_MESSAGE + " VARCHAR NOT NULL"
            + " )";

    Context context;
    SQLiteDatabase database;

    public MessagesHelper(Context context) {
        this.context = context;
        database = null;
    }

    public void createTable(SQLiteDatabase db) {
        Log.i(TAG, "creating Table " + MESSAGE_TBL_NAME);
        DatabaseHelper.LogExec(TAG, MESSAGE_TBL_CREATE);
        db.execSQL(MESSAGE_TBL_CREATE);
    }

    public void dropTable(SQLiteDatabase db) {
        Log.i(TAG, "Dropping table " + MESSAGE_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TBL_NAME);
    }

    public boolean addMessage(Message msg, SQLiteDatabase db) {
        boolean ret = true;
        ContentValues vals = new ContentValues();
        vals.put(MESSAGE_COL_MESSAGE, msg.getMessage());
        try {
            Log.d(TAG, "Inserting Message '" + msg.getMessage() + "'");
            db.insert(MESSAGE_TBL_NAME, null, vals);
        } catch (SQLException ex) {
            Log.e(TAG, "Error inserting new message!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public ArrayList<Message> getAllMessages(SQLiteDatabase db) {
        ArrayList<Message> msgs;
        Cursor cursor;
        String sql = "SELECT * FROM " + MESSAGE_TBL_NAME;

        DatabaseHelper.LogExec(TAG, sql);
        cursor = db.rawQuery(sql, null);
        if (!cursor.moveToFirst()) {
            return null;
        }

        msgs = new ArrayList<Message>();
        do {
            msgs.add(new Message(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COL_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COL_MESSAGE))));
        } while (cursor.moveToNext());
        return msgs;
    }

    public boolean updateMessage(Message message, SQLiteDatabase db) {
        boolean ret = true;
        ContentValues vals = new ContentValues();
        vals.put(MESSAGE_COL_MESSAGE, message.getMessage());
        String sql =
                "UPDATE " + MESSAGE_TBL_NAME +
                        " SET " + MESSAGE_COL_MESSAGE + " = '" + message.getMessage() + "'" +
                        " WHERE " + MESSAGE_COL_ID + " = " + message.getId();
        try {
            DatabaseHelper.LogExec(TAG, sql);
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Could not update Message!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public boolean deleteMessage(Message message, SQLiteDatabase db) {
        boolean ret = true;
        String sql = "DELETE FROM " + MESSAGE_TBL_NAME +
                " WHERE " + MESSAGE_COL_ID + " = " + message.getId();
        try {
            DatabaseHelper.LogExec(TAG, sql);
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Could not delete Message!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    protected Context getContext() {
        return this.context;
    }
}
