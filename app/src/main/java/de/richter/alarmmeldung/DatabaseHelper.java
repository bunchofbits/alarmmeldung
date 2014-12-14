package de.richter.alarmmeldung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

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
            + MEMBER_COL_ID +     " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MEMBER_COL_NAME +   " VARCHAR NOT NULL, "
            + MEMBER_COL_NUMBER + " VARCHAR NOT NULL"
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

    public static final String MESSAGE_TBL_NAME = "messages";
    public static final String MESSAGE_COL_ID = "ID";
    public static final String MESSAGE_COL_MESSAGE = "message";
    public static final String MESSAGE_TBL_CREATE = "CREATE TABLE IF NOT EXISTS "
            + MESSAGE_TBL_NAME + " ( "
            + MESSAGE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MESSAGE_COL_MESSAGE + " VARCHAR NOT NULL"
            + " )";


    Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
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

        Log.i(TAG, "creating Table " + MESSAGE_TBL_NAME);
        Log.d(TAG, "executing '" + MESSAGE_TBL_CREATE + "' ");
        db.execSQL(MESSAGE_TBL_CREATE);
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
        ContentValues vals = new ContentValues();
        vals.put(MESSAGE_COL_MESSAGE, msg.getMessage());

        Log.d(TAG, "Inserting Message '" + msg.getMessage() + "'");
        db.insert(MESSAGE_TBL_NAME, null, vals);
        db.close();
    }

    public ArrayList<Message> getAllMessages() {
        ArrayList<Message> msgs;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;

        cursor = db.query(MESSAGE_TBL_NAME, new String[] {MESSAGE_COL_ID, MESSAGE_COL_MESSAGE}, null, null, null, null, MESSAGE_COL_ID);
        if (!cursor.moveToFirst()) {
            db.close();
            return null;
        }


        msgs = new ArrayList<Message>();
        do{
            msgs.add(new Message(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COL_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COL_MESSAGE))));
        }while(cursor.moveToNext());

        db.close();
        return msgs;
    }

    public void updateMessage(Message new_msg) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(MESSAGE_COL_MESSAGE, new_msg.getMessage());
        String cmd =
                "UPDATE " + MESSAGE_TBL_NAME +
                " SET " + MESSAGE_COL_MESSAGE + " = '" + new_msg.getMessage() + "'" +
                " WHERE " + MESSAGE_COL_ID + " = " + new_msg.getId();
        Log.d(MainActivity.TAG, "Executing: " + cmd);
        db.execSQL(cmd);
        db.close();
    }

    public void deleteMessageByID(int ID) {
        SQLiteDatabase db = getWritableDatabase();
        String del_Str = "DELETE FROM " + MESSAGE_TBL_NAME + " WHERE " + MESSAGE_COL_ID + " = " + ID;
        Log.d(MainActivity.TAG, "Executing: " + del_Str);
        db.execSQL(del_Str);
        db.close();
    }

    public void addGroup(Group group) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GROUP_COL_NAME, group.getName());

        db.insert(GROUP_TBL_NAME, null, values);

        db.close();
    }

    public ArrayList<Group> getAllGroups() {
        ArrayList<Group> groups;
        SQLiteDatabase db = getReadableDatabase();
        Cursor curs;

        curs = db.query(GROUP_TBL_NAME, new String[] {GROUP_COL_ID, GROUP_COL_NAME}, null, null, null, null, GROUP_COL_ID);
        if(!curs.moveToFirst()) {
            db.close();
            return null;
        }
        groups = new ArrayList<Group>();
        do{
            Group grp = new Group(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_ID))),
                    curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_NAME))
            );

            Log.d(TAG, grp.toString());
            groups.add(grp);

        }while(curs.moveToNext());
        db.close();
        return groups;
    }

    public void updateGroup(Group new_grp) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(GROUP_COL_NAME, new_grp.getName());
        String cmd =
                "UPDATE " + GROUP_TBL_NAME +
                " SET " + GROUP_COL_NAME + " = '" + new_grp.getName() + "'" +
                " WHERE " + GROUP_COL_ID + " = " + new_grp.getId();

        Log.d(MainActivity.TAG, "Executing: " + cmd);
        db.execSQL(cmd);
        db.close();
    }

    public void deleteGroup(int ID) {
        SQLiteDatabase db = getWritableDatabase();
        String del_Str = "DELETE FROM " + GROUP_TBL_NAME + " WHERE " + GROUP_COL_ID + " = " + ID;
        Log.d(MainActivity.TAG, "Executing: " + del_Str);
        db.execSQL(del_Str);
        db.close();
    }

    public void addMember(Member member) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MEMBER_COL_NAME, member.getName());
        values.put(MEMBER_COL_NUMBER, member.getNumber());

        db.insert(MEMBER_TBL_NAME, null, values);
        db.close();
    }

    public ArrayList<Member> getAllMember() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor curs;
        ArrayList<Member> ret;

        curs = db.query(MEMBER_TBL_NAME, new String[] {MEMBER_COL_ID, MEMBER_COL_NAME, MEMBER_COL_NUMBER}, null, null, null,null, MEMBER_COL_ID );

        if(!curs.moveToFirst()) {
            db.close();
            return null;
        }

        ret = new ArrayList<Member>();
        do{
            Member mem = new Member(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_ID))),
                    curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NAME)),
                    curs.getString(curs.getColumnIndexOrThrow(MEMBER_COL_NUMBER)));

            Log.d(TAG, mem.toString());
            ret.add(mem);

        }while(curs.moveToNext());

        db.close();
        return ret;
    }
    
    // TODO: updateMember()
    // TODO: deleteMember()

    public void addMemberToGroup(Member member, Group group) {
        SQLiteDatabase db = getWritableDatabase();
        // TODO: implement
        Toast.makeText(context, "IMPLEMENT 'addMemberToGroup()' !", Toast.LENGTH_SHORT).show();
        db.close();
    }
    // TODO: GetMemberFromGroup ()

    // TODO: Add Member Array To Group () ?

    public ArrayList<Member> getMemberArrayFromGroup(int groupID) {
        Toast.makeText(this.context, "IMPLEMENT 'getMemberArrayFromGroup'!", Toast.LENGTH_SHORT).show();
        return getAllMember();
    }

}
