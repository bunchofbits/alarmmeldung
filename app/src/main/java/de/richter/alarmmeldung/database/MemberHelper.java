package de.richter.alarmmeldung.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.richter.alarmmeldung.Core.Member;

public class MemberHelper {

    public static final String TAG = "MemberHelper";

    public static final String MEMBER_TBL_NAME = "member";
    public static final String MEMBER_COL_ID = "ID";
    public static final String MEMBER_COL_NAME = "name";
    public static final String MEMBER_COL_NUMBER = "number";
    public static final String MEMBER_TBL_CREATE = "CREATE TABLE IF NOT EXISTS "
            + MEMBER_TBL_NAME + " ( "
            + MEMBER_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MEMBER_COL_NAME + " VARCHAR NOT NULL, "
            + MEMBER_COL_NUMBER + " VARCHAR NOT NULL UNIQUE"
            + " )";

    private Context context;

    public MemberHelper(Context context) {
        this.context = context;
    }

    public void createTable(SQLiteDatabase db) {
        Log.i(TAG, "creating Table " + MEMBER_TBL_NAME);
        DatabaseHelper.LogExec(TAG, MEMBER_TBL_CREATE);
        db.execSQL(MEMBER_TBL_CREATE);
    }

    public void dropTable(SQLiteDatabase db) {
        Log.i(TAG, "Dropping table " + MEMBER_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEMBER_TBL_NAME);
    }

    public boolean addMember(Member member, SQLiteDatabase db) {
        boolean ret = true;
        ContentValues values = new ContentValues();
        values.put(MEMBER_COL_NAME, member.getName());
        values.put(MEMBER_COL_NUMBER, member.getNumber());
        try {
            db.insert(MEMBER_TBL_NAME, null, values);
        } catch (SQLException ex) {
            Log.e(TAG, "Error inserting new member!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }

        return ret;
    }

    public ArrayList<Member> getAllMember(SQLiteDatabase db) {
        Cursor curs;
        ArrayList<Member> member;

        curs = db.query(MEMBER_TBL_NAME, new String[]{MEMBER_COL_ID, MEMBER_COL_NAME, MEMBER_COL_NUMBER}, null, null, null, null, MEMBER_COL_ID);

        if (!curs.moveToFirst()) {
            return null;
        }

        member = new ArrayList<Member>();
        do {
            Member mem = new Member(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_ID))),
                    curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NAME)),
                    curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NUMBER)));
            member.add(mem);

        } while (curs.moveToNext());
        return member;
    }

    public Member getMemberByNumber(String number, SQLiteDatabase db) {
        Cursor curs;
        Member ret;

        curs = db.query(MEMBER_TBL_NAME,
                new String[]{MEMBER_COL_ID,
                        MEMBER_COL_NAME, MEMBER_COL_NUMBER}, MEMBER_COL_NUMBER + " IS '" + number + "'",
                null, null, null, null);

        if (!curs.moveToFirst()) {
            return null;
        }
        ret = new Member(
                Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_ID))),
                curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NAME)),
                curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NUMBER)));

        return ret;
    }

    public boolean updateMember(Member new_member, SQLiteDatabase db) {
        boolean ret = true;
        // TODO: test function!
        Toast.makeText(context, "function updateMember() is actually not tested!", Toast.LENGTH_SHORT);
        Log.w(TAG, "function updateMember() is actually not tested!");
        String sql =
                "UPDATE " + MEMBER_TBL_NAME +
                        " SET " +
                        MEMBER_COL_NAME + " = '" + new_member.getName() + "', " +
                        MEMBER_COL_NUMBER + " = '" + new_member.getNumber() + "'" +
                        " WHERE " +
                        GroupsHelper.GROUP_COL_ID + " = " + new_member.getId();

        try {
            DatabaseHelper.LogExec(TAG, sql);
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Error updating member!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public boolean deleteMember(Member member, SQLiteDatabase db) {
        boolean ret = true;
        // FIXME: only delete member, if not in any group assigned!
        // TODO: test function!
        Toast.makeText(context, "function deleteMember() is actually not tested!", Toast.LENGTH_SHORT);
        Log.w(TAG, "function deleteMember() is actually not tested!");
        String sql = "DELETE FROM " + MEMBER_TBL_NAME + " WHERE " + MEMBER_COL_ID + " = " + member.getId();
        try {
            DatabaseHelper.LogExec(TAG, sql);
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Error updating member!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

}
