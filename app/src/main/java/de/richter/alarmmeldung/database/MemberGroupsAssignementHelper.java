package de.richter.alarmmeldung.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.richter.alarmmeldung.Group;
import de.richter.alarmmeldung.Member;

public class MemberGroupsAssignementHelper {

    public static final String TAG = "MemberGroupsAssignementHelper";

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

    private Context context;

    public MemberGroupsAssignementHelper(Context context) {
        this.context = context;
    }

    public void createTable(SQLiteDatabase db) {
        Log.i(TAG, "creating Table " + MEM_GRP_TBL_NAME);
        DatabaseHelper.LogExec(TAG, MEM_GRP_TBL_CREATE);
        db.execSQL(MEM_GRP_TBL_CREATE);
    }

    public void dropTable(SQLiteDatabase db) {
        Log.i(TAG, "Dropping table " + MEM_GRP_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEM_GRP_TBL_NAME);
    }

    public boolean addMemberToGroup(Member member, Group group, SQLiteDatabase db) {
        boolean ret = true;
        if (member.getId() < 0 || group.getId() < 0) {
            Log.e(TAG, "Insert into " + MEM_GRP_TBL_NAME + " caused by ID<0 for member or group!");
        }

        String sql = "INSERT INTO " + MEM_GRP_TBL_NAME +
                " ( " + MEM_GRP_COL_MEM + ", " + MEM_GRP_COL_GRP + " ) VALUES ( " +
                member.getId() + ", " + group.getId() +
                " )";

        DatabaseHelper.LogExec(TAG, sql);
        try {
            db.execSQL(sql);
        } catch (SQLException ex) {
            Log.e(TAG, "Error adding Member " + member + " to Group " + group);
            Log.e(TAG, ex.getMessage());
            ret = false;
        }
        return ret;
    }

    public ArrayList<Member> getMemberWithAssignedGroup(Group group, SQLiteDatabase db) {
        Cursor curs;
        ArrayList<Member> member;
        String sql = "SELECT " +
                GroupsHelper.GROUP_TBL_NAME + "." + GroupsHelper.GROUP_COL_ID + ", " +
                GroupsHelper.GROUP_TBL_NAME + "." + GroupsHelper.GROUP_COL_NAME + ", " +
                MemberHelper.MEMBER_TBL_NAME + "." + MemberHelper.MEMBER_COL_ID + ", " +
                MemberHelper.MEMBER_TBL_NAME + "." + MemberHelper.MEMBER_COL_NAME + ", " +
                MemberHelper.MEMBER_TBL_NAME + "." + MemberHelper.MEMBER_COL_NUMBER +
                " FROM " + MEM_GRP_TBL_NAME +
                " JOIN " + MemberHelper.MEMBER_TBL_NAME + " ON " + MEM_GRP_COL_MEM + " = " + MemberHelper.MEMBER_TBL_NAME + "." + MemberHelper.MEMBER_COL_ID +
                " JOIN " + GroupsHelper.GROUP_TBL_NAME + " ON " + MEM_GRP_COL_GRP + " = " + GroupsHelper.GROUP_TBL_NAME + "." + GroupsHelper.GROUP_COL_ID +
                " AND " + MEM_GRP_COL_GRP + " = " + group.getId();

        if (group == null) {
            Log.e(TAG, "Parameter (group to search from) is null!");
            return null;
        }

        DatabaseHelper.LogExec(TAG, sql);
        try {
            curs = db.rawQuery(sql, null);
        } catch (SQLException ex) {
            Log.e(TAG, "Could not get Member for Group " + group);
            Log.e(TAG, ex.getMessage());
            return null;
        }

        if (!curs.moveToFirst()) {
            return null;
        }
        member = new ArrayList<Member>();
        do {
            Member mem = new Member(
                    Integer.parseInt(curs.getString(curs.getColumnIndexOrThrow(MemberHelper.MEMBER_COL_ID))),
                    curs.getString(curs.getColumnIndexOrThrow(MemberHelper.MEMBER_COL_NAME)),
                    curs.getString(curs.getColumnIndexOrThrow(MemberHelper.MEMBER_COL_NUMBER)));
            member.add(mem);

        } while (curs.moveToNext());
        return member;
    }
}
