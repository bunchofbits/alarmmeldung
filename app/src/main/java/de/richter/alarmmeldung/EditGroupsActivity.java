package de.richter.alarmmeldung;

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


public class EditGroupsActivity extends ExpandableListActivity {

    private Group grpToWork;
    private EditText grpDlgTxt;
    public static final int GET_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
        AlertDialog.Builder alert;

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
        AlertDialog.Builder alert;
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
        dbh.deleteGroup(grpToWork.getId());
        update_exp_list_view();
    }

    /* call defined in xml */
    public void deleteGroupOnClick(View view) {
        AlertDialog.Builder alert;
        int pos;

        pos = Integer.parseInt(view.getTag().toString());
        grpToWork = (Group) getExpandableListAdapter().getGroup(pos);

        alert = new AlertDialog.Builder(this);
        alert.setView(grpDlgTxt);
        alert.setTitle(R.string.delete_qst);
        alert.setMessage("" + getString(R.string.delete_group_qst) + "\n" + "'" + grpToWork.getName() + "'");
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
        switch(requestCode) {
            case EditGroupsActivity.GET_CONTACT:
                if(resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                    if(cursor.moveToFirst()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        number = number.replaceAll("[a-zA-Z/\\[\\]\\-\\\\ ]+", "");

                        Toast.makeText(this, "Selected Contact: ", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Name: '" + name + "'", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Number: '" + number + "'", Toast.LENGTH_SHORT).show();
                        dbh = new DatabaseHelper(this);
                        mem = new Member(name, number);
                        // TODO: if( dbh.getMemberByNumber() == NULL ) { dbh.addMember(mem); }
                        dbh.addMember(mem);
                        dbh.addMemberToGroup(mem, grpToWork);
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

    private void update_exp_list_view() {
        ArrayList<Group> groups;
        ArrayList<Member> singleMemberList;
        ArrayList<List<Member>> memberList;
        DatabaseHelper dbh = new DatabaseHelper(this);
        groups = dbh.getAllGroups();

        if(groups == null) {
            groups = new ArrayList<Group>();
        }
        memberList = new ArrayList<List<Member>>();
        for (int i = 0; i < groups.size(); i++) {
            singleMemberList = dbh.getMemberArrayFromGroup(groups.get(i).getId());

            if(singleMemberList == null) // keine Member gefunden: leeres Array adden
                singleMemberList = new ArrayList<Member>();

            memberList.add(singleMemberList);
        }
        setListAdapter(new GroupsExpListAdapter(this, groups, memberList));
    }
}
