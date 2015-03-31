package de.richter.alarmmeldung.Core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.richter.alarmmeldung.R;
import de.richter.alarmmeldung.database.DatabaseHelper;

public class EditGroupsActivity extends ExpandableListActivity {

    public static final String TAG = "EditGroupsActivity";
    public static final int GET_CONTACT = 1;

    private Group grpToWork;
    private Member memToWork;

    private EditText grpDlgTxt;
    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_groups);

        update_exp_list_view();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_message:
                addGroupOnClick();
                return true;
            default:
                return false;
        }
    }

    private void addGroupDlgOnClick() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        String str = grpDlgTxt.getText().toString();
        dbh.addGroup(new Group(str));
        update_exp_list_view();
    }

    /* call defined in xml */
    private void addGroupOnClick() {
        alert = new AlertDialog.Builder(this);
        grpDlgTxt = new EditText(this);

        alert.setTitle(R.string.add_group);
        grpDlgTxt.setMaxLines(1);
        grpDlgTxt.setText("");

        alert.setView(grpDlgTxt);
        alert.setCancelable(false);

        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addGroupDlgOnClick();
            }
        });
        alert.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alert.create();
        alert.show();
    }

    private void editGroupDlgOnClick() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        Group grp_update = grpToWork;
        grp_update.setName(grpDlgTxt.getText().toString());
        dbh.updateGroup(grp_update);
        update_exp_list_view();
    }

    /* call defined in xml */
    public void editGroupOnCLick(View view) {
        int pos;

        alert = new AlertDialog.Builder(this);
        grpDlgTxt = new EditText(this);

        pos = Integer.parseInt(view.getTag().toString());
        grpToWork = (Group) getExpandableListAdapter().getGroup(pos);

        alert.setTitle(R.string.edit_group);
        grpDlgTxt.setMaxLines(1);
        grpDlgTxt.setText(grpToWork.getName());

        alert.setView(grpDlgTxt);
        alert.setCancelable(false);

        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editGroupDlgOnClick();
            }
        });
        alert.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alert.create();
        alert.show();
    }

    private void deleteGroupDlgOnClick() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.deleteGroup(grpToWork);
        update_exp_list_view();
    }

    /* call defined in xml */
    public void deleteGroupOnClick(View view) {
        int pos;

        pos = Integer.parseInt(view.getTag().toString());
        grpToWork = (Group) getExpandableListAdapter().getGroup(pos);

        alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.delete_qst);
        alert.setMessage(getString(R.string.delete_group_qst) + "\n" + "'" + grpToWork.getName() + "'");
        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteGroupDlgOnClick();
            }
        });
        alert.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alert.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Member mem;
        DatabaseHelper dbh;
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EditGroupsActivity.GET_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        number = number.replaceAll("[a-zA-Z/\\[\\]\\-\\\\ ]+", "");

                        dbh = new DatabaseHelper(this);
                        mem = new Member(name, number);
                        if (dbh.getMemberByNumber(Integer.parseInt(mem.getNumber())) == null) {
                            dbh.addMember(mem);
                        }
                        mem = dbh.getMemberByNumber(Integer.parseInt(mem.getNumber()));
                        dbh.addMemberToGroup(mem, grpToWork);
                        update_exp_list_view();
                    }
                }
                break;
            default:
                // Do nothing
        }
    }

    public void addMemberToGroupOnClick(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        grpToWork = (Group) getExpandableListAdapter().getGroup(pos);
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, EditGroupsActivity.GET_CONTACT);
    }

    public void editMemberOnCLick(View view) {
        Toast.makeText(this, "NOT YET IMPLEMENTED!", Toast.LENGTH_LONG).show();
        return;
    }

    public void deleteMemberDlgOnCLick() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.deleteMemberFromGroup(memToWork, grpToWork);
        update_exp_list_view();
        return;
    }

    public void deleteMemberOnCLick(View view) {
        int pos_grp;
        int pos_mem;
        String pos[];

        pos = view.getTag().toString().split(",");
        pos_grp = Integer.parseInt(pos[0]);
        pos_mem = Integer.parseInt(pos[1]);

        grpToWork = (Group) getExpandableListAdapter().getGroup(pos_grp);
        memToWork = grpToWork.getMember().get(pos_mem);

        alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.delete_qst);
        alert.setMessage(getString(R.string.delete_member_from_group_qst) + "\n\n" + memToWork.getName() + "\n" + memToWork.getNumber());
        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMemberDlgOnCLick();
            }
        });
        alert.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alert.create();
        alert.show();
        return;
    }

    private void update_exp_list_view() {
        ArrayList<Group> groups;
        ArrayList<Member> singleMemberList;
        ArrayList<List<Member>> memberList;
        DatabaseHelper dbh = new DatabaseHelper(this);
        groups = dbh.getAllGroups();

        if (groups == null) {
            groups = new ArrayList<Group>();
        }
        memberList = new ArrayList<List<Member>>();
        for (int i = 0; i < groups.size(); i++) {
            singleMemberList = dbh.getMemberArrayFromGroup(groups.get(i).getId());

            if (singleMemberList == null) {
                singleMemberList = new ArrayList<Member>();
            }

            memberList.add(singleMemberList);
        }
        setListAdapter(new GroupsExpListAdapter(this, groups, memberList));
    }
}
