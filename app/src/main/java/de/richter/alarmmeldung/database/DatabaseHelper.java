package de.richter.alarmmeldung.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.richter.alarmmeldung.Group;
import de.richter.alarmmeldung.Member;
import de.richter.alarmmeldung.Message;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    // Database
    public static final String DB_NAME = "alarmmeldung.db";
    public static final int DB_VERSION = 1;

    /* **** TABLES **** */
    // Member Table
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

    // Group Table
    public static final String GROUP_TBL_NAME = "groups";
    public static final String GROUP_COL_ID = "ID";
    public static final String GROUP_COL_NAME = "name";
    public static final String GROUP_TBL_CREATE = "CREATE TABLE IF NOT EXISTS "
            + GROUP_TBL_NAME + " ( "
            + GROUP_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GROUP_COL_NAME + " VARCHAR NOT NULL"
            + " )";

    // Member - Group Correlation Table
    public static final String MEM_GRP_TBL_NAME = "member_group_corr";
    public static final String MEM_GRP_COL_ID = "ID";
    public static final String MEM_GRP_COL_MEM = "member_id";
    public static final String MEM_GRP_COL_GRP = "group_id";
    public static final String MEM_GRP_TBL_CREATE = "CREATE TABLE IF NOT EXISTS "
            + MEM_GRP_TBL_NAME + " ( "
            + MEM_GRP_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MEM_GRP_COL_MEM + " INTEGER NOT NULL, "
            + MEM_GRP_COL_GRP + " INTEGER NOT NULL"
            + " )";

    Context context;
    MessagesHelper messagesHelper;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.messagesHelper = new MessagesHelper(context);
    }

    public static void LogExec(String TAG, String sql) {
        Log.d(DatabaseHelper.TAG + " - " + TAG, "Executing: " + sql);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "creating Table " + MEMBER_TBL_NAME);
        Log.d(TAG, "executing '" + MEMBER_TBL_CREATE + "' ");
        db.execSQL(MEMBER_TBL_CREATE);

        Log.i(TAG, "creating Table " + GROUP_TBL_NAME);
        Log.d(TAG, "executing '" + GROUP_TBL_CREATE + "' ");
        db.execSQL(GROUP_TBL_CREATE);

        Log.i(TAG, "creating Table " + MEM_GRP_TBL_NAME);
        Log.d(TAG, "executing '" + MEM_GRP_TBL_CREATE + "' ");
        db.execSQL(MEM_GRP_TBL_CREATE);

        messagesHelper.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Dropping all tables");

        Log.i(TAG, "Dropping table " + MEMBER_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEMBER_TBL_NAME);

        Log.i(TAG, "Dropping table " + GROUP_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TBL_NAME);

        Log.i(TAG, "Dropping table " + MEM_GRP_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEM_GRP_TBL_NAME);

        onCreate(db);
    }

    public void addMessage(Message msg) {
        SQLiteDatabase db = getWritableDatabase();
        messagesHelper.addMessage(msg, db);
        db.close();
    }

    public void addGroup(Group group) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GROUP_COL_NAME, group.getName());

        db.insert(GROUP_TBL_NAME, null, values);

        db.close();
    }

    public void addMember(Member member) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MEMBER_COL_NAME, member.getName());
        values.put(MEMBER_COL_NUMBER, member.getNumber());
        db.insert(MEMBER_TBL_NAME, null, values);
    }

    public void addMemberToGroup(Member member, Group group) {
        SQLiteDatabase db = getWritableDatabase();

        if (member.getId() < 0 || group.getId() < 0) {
            Log.e(TAG, "Insert into " + MEM_GRP_TBL_NAME + " caused by ID<0 for member or group!");
        }

        String sql = "INSERT INTO " + MEM_GRP_TBL_NAME +
                " ( " + MEM_GRP_COL_MEM + ", " + MEM_GRP_COL_GRP + " ) VALUES ( " +
                member.getId() + ", " + group.getId() +
                " )";

        Log.d(TAG, "Executing: " + sql);
        db.execSQL(sql);

        db.close();
    }

    public ArrayList<Message> getAllMessages() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Message> messages;
        messages = messagesHelper.getAllMessages(db);
        db.close();
        return messages;
    }

    public ArrayList<Group> getAllGroups() {
        ArrayList<Group> groups;
        SQLiteDatabase db = getReadableDatabase();
        Cursor curs;

        curs = db.query(GROUP_TBL_NAME, new String[]{GROUP_COL_ID, GROUP_COL_NAME}, null, null, null, null, GROUP_COL_ID);
        if (!curs.moveToFirst()) {
            db.close();
            return null;
        }
        groups = new ArrayList<Group>();
        do {
            Group grp = new Group(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_ID))),
                    curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_NAME))
            );
            groups.add(grp);

        } while (curs.moveToNext());
        db.close();
        return groups;
    }

    public ArrayList<Member> getAllMember() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor curs;
        ArrayList<Member> ret;

        curs = db.query(MEMBER_TBL_NAME, new String[]{MEMBER_COL_ID, MEMBER_COL_NAME, MEMBER_COL_NUMBER}, null, null, null, null, MEMBER_COL_ID);

        if (!curs.moveToFirst()) {
            db.close();
            return null;
        }

        ret = new ArrayList<Member>();
        do {
            Member mem = new Member(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_ID))),
                    curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NAME)),
                    curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NUMBER)));
            ret.add(mem);

        } while (curs.moveToNext());

        db.close();
        return ret;
    }

    public Group getGroupByID(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Group group;
        Cursor curs;
        String sql = "SELECT * FROM " + GROUP_TBL_NAME + " WHERE " + GROUP_COL_ID + " = '" + id + "'";

        curs = db.rawQuery(sql, null);

        if (!curs.moveToFirst()) {
            db.close();
            return null;
        }

        group = new Group(
                Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_ID))),
                curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_NAME))
        );
        return group;
    }

    public ArrayList<Member> getMemberArrayFromGroup(int groupID) {
        return getMemberArrayFromGroup(getGroupByID(groupID));
    }

    public ArrayList<Member> getMemberArrayFromGroup(Group group) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor curs;
        ArrayList<Member> member;
        String sql = "SELECT " +
                GROUP_TBL_NAME + "." + GROUP_COL_ID + ", " +
                GROUP_TBL_NAME + "." + GROUP_COL_NAME + ", " +
                MEMBER_TBL_NAME + "." + MEMBER_COL_ID + ", " +
                MEMBER_TBL_NAME + "." + MEMBER_COL_NAME + ", " +
                MEMBER_TBL_NAME + "." + MEMBER_COL_NUMBER +
                " FROM " + MEM_GRP_TBL_NAME +
                " JOIN " + MEMBER_TBL_NAME + " ON " + MEM_GRP_COL_MEM + " = " + MEMBER_TBL_NAME + "." + MEMBER_COL_ID +
                " JOIN " + GROUP_TBL_NAME + " ON " + MEM_GRP_COL_GRP + " = " + GROUP_TBL_NAME + "." + GROUP_COL_ID +
                " AND " + MEM_GRP_COL_GRP + " = " + group.getId();

        if (group == null) {
            Log.e(TAG, "Parameter (group to search from) is null!");
            return null;
        }

        curs = db.rawQuery(sql, null);
        Log.d(TAG, "Executing: " + sql);
        if (!curs.moveToFirst()) {
            db.close();
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

        db.close();
        return member;
    }

    public Member getMemberByNumber(Member toSearch) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor curs;
        Member ret;

        curs = db.query(MEMBER_TBL_NAME,
                new String[]{MEMBER_COL_ID,
                        MEMBER_COL_NAME, MEMBER_COL_NUMBER}, MEMBER_COL_NUMBER + " IS '" + toSearch.getNumber() + "'",
                null, null, null, null);

        if (!curs.moveToFirst()) {
            db.close();
            return null;
        }
        ret = new Member(
                Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_ID))),
                curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NAME)),
                curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NUMBER)));

        db.close();
        return ret;
    }

    public void updateMessage(Message new_msg) {
        SQLiteDatabase db = getWritableDatabase();
        if (!messagesHelper.updateMessage(new_msg, db)) {
            Toast.makeText(this.context, "Error updating Message!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void updateGroup(Group new_grp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(GROUP_COL_NAME, new_grp.getName());
        String cmd =
                "UPDATE " + GROUP_TBL_NAME +
                        " SET " + GROUP_COL_NAME + " = '" + new_grp.getName() + "'" +
                        " WHERE " + GROUP_COL_ID + " = " + new_grp.getId();

        Log.d(TAG, "Executing: " + cmd);
        db.execSQL(cmd);
        db.close();
    }

    public void updateMember(Member new_member) {
        // TODO: test function!
        Toast.makeText(context, "function updateMember() is actually not tested!", Toast.LENGTH_SHORT);
        Log.w(TAG, "function updateMember() is actually not tested!");
        SQLiteDatabase db = getWritableDatabase();
        String cmd =
                "UPDATE " + MEMBER_TBL_NAME +
                        " SET " +
                        MEMBER_COL_NAME + " = '" + new_member.getName() + "', " +
                        MEMBER_COL_NUMBER + " = '" + new_member.getNumber() + "'" +
                        " WHERE " +
                        GROUP_COL_ID + " = " + new_member.getId();

        Log.d(TAG, "Executing: " + cmd);
        db.execSQL(cmd);
        db.close();
    }

    public void deleteMessage(Message message) {
        SQLiteDatabase db = getWritableDatabase();
        if (!messagesHelper.deleteMessage(message, db)) {
            Toast.makeText(this.context, "Error deleting Message!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void deleteGroup(int ID) {
        SQLiteDatabase db;
        String sql;
        db = getWritableDatabase();
        sql = "DELETE FROM " + GROUP_TBL_NAME + " WHERE " + GROUP_COL_ID + " = " + ID;
        Log.d(TAG, "Executing: " + sql);
        db.execSQL(sql);
        db.close();
    }

    public void deleteMember(int ID) {
        // FIXME: only delete member, if not in any group assigned!
        // TODO: test function!
        Toast.makeText(context, "function deleteMember() is actually not tested!", Toast.LENGTH_SHORT);
        Log.w(TAG, "function deleteMember() is actually not tested!");
        SQLiteDatabase db = getWritableDatabase();
        String del_Str = "DELETE FROM " + MEMBER_TBL_NAME + " WHERE " + MEMBER_COL_ID + " = " + ID;
        Log.d(TAG, "Executing: " + del_Str);
        db.execSQL(del_Str);
        db.close();
    }
}
