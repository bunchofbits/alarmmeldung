package de.richter.alarmmeldung.database;

import android.content.Context;
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

    Context context;
    MessagesHelper messagesHelper;
    GroupsHelper groupsHelper;
    MemberHelper memberHelper;
    MemberGroupsAssignementHelper memGrpAssHelper;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.messagesHelper = new MessagesHelper(context);
        this.groupsHelper = new GroupsHelper(context);
        this.memberHelper = new MemberHelper(context);
        this.memGrpAssHelper = new MemberGroupsAssignementHelper(context);
    }

    public static void LogExec(String TAG, String sql) {
        Log.d(TAG, "Executing: " + sql);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        memberHelper.createTable(db);
        groupsHelper.createTable(db);
        memGrpAssHelper.createTable(db);
        messagesHelper.createTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Dropping all tables");

        memberHelper.dropTable(db);
        groupsHelper.dropTable(db);
        memGrpAssHelper.dropTable(db);
        messagesHelper.dropTable(db);
        onCreate(db);
    }

    public void addMessage(Message msg) {
        SQLiteDatabase db = getWritableDatabase();
        if (!messagesHelper.addMessage(msg, db)) {
            Toast.makeText(context, "Error adding Message!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void addGroup(Group group) {
        SQLiteDatabase db = getWritableDatabase();
        if (!groupsHelper.addGroup(group, db)) {
            Toast.makeText(context, "Error adding Group!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void addMember(Member member) {
        SQLiteDatabase db = getWritableDatabase();
        if (!memberHelper.addMember(member, db)) {
            Toast.makeText(context, "Error adding Member!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void addMemberToGroup(Member member, Group group) {
        SQLiteDatabase db = getWritableDatabase();
        if (!memGrpAssHelper.addMemberToGroup(member, group, db)) {
            Toast.makeText(context, "Error adding Member to Group!", Toast.LENGTH_LONG).show();
        }
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
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Member> member;
        ArrayList<Group> groups = groupsHelper.getAllGroups(db);
        for (int i = 0; groups != null && i < groups.size(); i++) {
            addAssignedMemberToGroup(groups.get(i));
        }
        db.close();
        return groups;
    }

    public ArrayList<Member> getAllMember() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Member> member = memberHelper.getAllMember(db);
        db.close();
        return member;
    }

    public Group getGroupByID(int id) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Member> member;
        Group group = groupsHelper.getGroupByID(id, db);
        if (group == null) {
            Toast.makeText(context, "Could not get Group!", Toast.LENGTH_LONG).show();
        }
        addAssignedMemberToGroup(group);

        db.close();
        return group;
    }

    public ArrayList<Member> getMemberArrayFromGroup(int groupID) {
        return getMemberArrayFromGroup(getGroupByID(groupID));
    }

    private ArrayList<Member> getMemberArrayFromGroup(Group group) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Member> member;
        member = memGrpAssHelper.getMemberWithAssignedGroup(group, db);
        db.close();
        return member;
    }

    private void addAssignedMemberToGroup(Group group) {
        if (group == null)
            return;
        ArrayList<Member> member = getMemberArrayFromGroup(group);
        if (member == null)
            return;
        group.setMember(member);
    }

    public Member getMemberByNumber(Member toSearch) {
        SQLiteDatabase db = getReadableDatabase();
        Member member = memberHelper.getMemberByNumber(toSearch.getNumber(), db);
        db.close();
        return member;
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
        if (!groupsHelper.updateGroup(new_grp, db)) {
            Toast.makeText(context, "Error updating Group!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void updateMember(Member new_member) {
        SQLiteDatabase db = getWritableDatabase();
        if (!memberHelper.updateMember(new_member, db)) {
            Toast.makeText(this.context, "Error updating Member!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void deleteMessage(Message message) {
        SQLiteDatabase db = getWritableDatabase();
        if (!messagesHelper.deleteMessage(message, db)) {
            Toast.makeText(this.context, "Error deleting Message!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void deleteGroup(Group group) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Member> member = group.getMember();
        for (int i = 0; member != null && i < member.size(); i++) {
            deleteMemberFromGroup(member.get(i), group);
        }
        if (!groupsHelper.deleteGroup(group, db)) {
            Toast.makeText(context, "Error deleting Group!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void deleteMember(Member member) {
        SQLiteDatabase db = getWritableDatabase();
        if (!memberHelper.deleteMember(member, db)) {
            Toast.makeText(this.context, "Error deleting Member!", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    public void deleteMemberFromGroup(Member member, Group group) {
        SQLiteDatabase db = getWritableDatabase();
        if (!memGrpAssHelper.deleteMemberGroupAssignment(member, group, db)) {
            Toast.makeText(context, "Could not remove " + member + " from Group " + group, Toast.LENGTH_LONG).show();
        }
        // FIXME: remove member if not assigned anymore
    }
}
