package de.richter.alarmmeldung.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.richter.alarmmeldung.Group;

public class GroupsHelper {

    public static final String TAG = "MessagesHelper";

    public static final String GROUP_TBL_NAME = "groups";
    public static final String GROUP_COL_ID = "ID";
    public static final String GROUP_COL_NAME = "name";
    public static final String GROUP_TBL_CREATE = "CREATE TABLE IF NOT EXISTS "
            + GROUP_TBL_NAME + " ( "
            + GROUP_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GROUP_COL_NAME + " VARCHAR NOT NULL"
            + " )";

    private Context context;

    public GroupsHelper(Context context) {
        this.context = context;
    }

    public void createTable(SQLiteDatabase db) {
        Log.i(TAG, "Creating Table " + GROUP_TBL_NAME);
        DatabaseHelper.LogExec(TAG, GROUP_TBL_CREATE);
        db.execSQL(GROUP_TBL_CREATE);
    }

    public void dropTable(SQLiteDatabase db) {
        Log.i(TAG, "Dropping table " + GROUP_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TBL_NAME);
    }

    public boolean addGroup(Group group, SQLiteDatabase db) {
        boolean ret = true;
        ContentValues vals = new ContentValues();
        vals.put(GROUP_COL_NAME, group.getName());
        try {
            db.insert(GROUP_TBL_NAME, null, vals);
        } catch (SQLException ex) {
            Log.e(TAG, "Error inserting new message!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public ArrayList<Group> getAllGroups(SQLiteDatabase db) {
        ArrayList<Group> groups;
        Cursor curs;

        curs = db.query(GROUP_TBL_NAME, new String[]{GROUP_COL_ID, GROUP_COL_NAME}, null, null, null, null, GROUP_COL_ID);
        if (!curs.moveToFirst()) {
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
        return groups;
    }

    public Group getGroupByID(int id, SQLiteDatabase db) {
        Group group;
        Cursor curs;
        String sql = "SELECT * FROM " + GROUP_TBL_NAME + " WHERE " + GROUP_COL_ID + " = '" + id + "'";

        curs = db.rawQuery(sql, null);

        if (!curs.moveToFirst()) {
            return null;
        }

        group = new Group(
                Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_ID))),
                curs.getString(curs.getColumnIndexOrThrow(GROUP_COL_NAME))
        );
        return group;
    }

    public boolean updateGroup(Group group, SQLiteDatabase db) {
        boolean ret = true;
        ContentValues vals = new ContentValues();
        vals.put(GROUP_COL_NAME, group.getName());
        String sql =
                "UPDATE " + GROUP_TBL_NAME +
                        " SET " + GROUP_COL_NAME + " = '" + group.getName() + "'" +
                        " WHERE " + GROUP_COL_ID + " = " + group.getId();

        DatabaseHelper.LogExec(TAG, sql);
        try {
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Error updating Group!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public boolean deleteGroup(Group group, SQLiteDatabase db) {
        boolean ret = true;
        String sql;
        sql = "DELETE FROM " + GROUP_TBL_NAME + " WHERE " + GROUP_COL_ID + " = " + group.getId();
        DatabaseHelper.LogExec(TAG, sql);
        try {
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Error deleting Group!");
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }
}
