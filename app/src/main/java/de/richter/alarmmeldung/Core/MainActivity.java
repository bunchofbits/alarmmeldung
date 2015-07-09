package de.richter.alarmmeldung.Core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.richter.alarmmeldung.R;
import de.richter.alarmmeldung.database.DatabaseHelper;

public class MainActivity extends Activity {

    public static final String TAG = "Alarmmeldung";

    private int selectedGroupIndex;
    private int selectedMessageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.grp_spn);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onGroupFocusChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                onGroupFocusChanged(-1);
            }
        });
        spinner = (Spinner) findViewById(R.id.msg_spn);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onMessageFocusChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                onMessageFocusChanged(-1);
            }
        });
        Button send_btn = (Button) findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBtnOnClick();
            }
        });

        Button dbgBtn = (Button) findViewById(R.id.debug_btn);
        dbgBtn.setVisibility(Button.VISIBLE);
        dbgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDebugOnClick();
            }
        });
    }

    private void addDebugOnClick() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.recreateDatabase();

        dbh.addMember(new Member("Simon Richter", "+4917661548379"));
        dbh.addMessage(new Message("Debugmessage - you should not receive that message! If you do, please let me know: +49176 615 483 79"));
        dbh.addMessage(new Message("Debugmessage Nr. 2 This message is too long for a single SMS so this message will be sent in 2 parts - you should not receive that messages! If you do, please let me know: +49 176 615 483 79"));
        dbh.addGroup(new Group("dbgGrp"));
        dbh.addMemberToGroup(dbh.getAllMember().get(0), dbh.getAllGroups().get(0));
        update_messages_and_groups();
    }

    @Override
    protected void onResume(){
        super.onResume();
        update_messages_and_groups();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_edit_messages){
            Intent intent;
            intent = new Intent(this, EditMessagesActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_edit_groups) {
                Intent intent;
                intent = new Intent(this, EditGroupsActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onGroupFocusChanged(int position) {
        this.selectedGroupIndex = position;
    }

    private void onMessageFocusChanged(int position) {
        this.selectedMessageIndex = position;
    }

    private void sendBtnDlgOnClick() {
        LinearLayout status_lout;
        Toast.makeText(this, "NOT YET IMPLEMENTED!", Toast.LENGTH_SHORT).show();
        DatabaseHelper dbh = new DatabaseHelper(this);
        ArrayList<Group> groups = dbh.getAllGroups();
        if (groups == null) {
            Log.w(TAG, "Could not send SMS: no groups added!");
            Toast.makeText(this, R.string.no_groups_added, Toast.LENGTH_SHORT).show();
            return;
        }
        if (groups.get(selectedGroupIndex) == null) {
            Log.e(TAG, "Could not send SMS: wrong GroupIndex!");
            return;
        }
        ArrayList<Member> members = groups.get(selectedGroupIndex).getMember();
        if(members == null) {
            Log.d(TAG, "Could not send SMS: No Members added yet!");
            return;
        }
        ArrayList<Message> messages = dbh.getAllMessages();
        if (messages == null) {
            Log.d(TAG, "Could not send SMS: No Messages added yet!");
            return;
        }
        if (messages.get(selectedMessageIndex) == null) {
            Log.e(TAG, "Could not send SMS: wrong MessageIndex!");
            return;
        }
        status_lout = (LinearLayout) findViewById(R.id.status_lout);
        if (status_lout.getChildCount() > 0) {
            status_lout.removeAllViews();
        }
        SMSHelper smsHelper = new SMSHelper(this, status_lout);

        for (int i = 0; i < members.size(); i++) {
            View vw = getLayoutInflater().inflate(R.layout.sms_stats_row_view, null);
            ((TextView) vw.findViewById(R.id.sms_status_name_txt)).setText(members.get(i).getName());
            ((TextView) vw.findViewById(R.id.sms_status_number_txt)).setText(members.get(i).getNumber());
            ((TextView) vw.findViewById(R.id.sms_status_sent_txt)).setText("not sent");
            ((TextView) vw.findViewById(R.id.sms_status_delivered_txt)).setText("not delivered");
            status_lout.addView(vw);
            Toast.makeText(this, "send SMS '" + messages.get(selectedMessageIndex) +
                    "'\n\nto: " + members.get(i).toString(), Toast.LENGTH_SHORT).show();
            smsHelper.sendSms(messages.get(0).getMessage(), members.get(i));
        }
    }

    private void sendBtnOnClick() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.send_qst);
        alert.setCancelable(false);
        alert.setMessage(R.string.send_sms_qst);

        alert.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendBtnDlgOnClick();
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

    /* get all Messages and Groups and fill the Spinners*/
    private void update_messages_and_groups(){
        update_messages();
        update_groups();
    }

    private void update_messages(){
        ArrayAdapter<String> adapter;
        Spinner msgSpn = (Spinner) findViewById(R.id.msg_spn);
        DatabaseHelper dbh = new DatabaseHelper(this);
        ArrayList<Message> messages = dbh.getAllMessages();
        if(messages == null){
            Toast.makeText(this, R.string.no_messages_added, Toast.LENGTH_LONG).show();
            msgSpn.setEnabled(false);
        }else {
            msgSpn.setEnabled(true);
            ArrayList<String> messagesForAdapter = new ArrayList<String>();
            for (int i = 0; i < messages.size(); i++)
                messagesForAdapter.add(messages.get(i).getMessage());

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, messagesForAdapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            msgSpn.setAdapter(adapter);
        }
    }

    private void update_groups(){
        ArrayAdapter<String> adapter;
        Spinner grpSpn = (Spinner) findViewById(R.id.grp_spn);
        DatabaseHelper dbh = new DatabaseHelper(this);
        ArrayList<Group> groups = dbh.getAllGroups();
        if(groups == null){
            Toast.makeText(this, R.string.no_groups_added, Toast.LENGTH_LONG).show();
            grpSpn.setEnabled(false);
        }else{
            grpSpn.setEnabled(true);
            ArrayList<String> groupsForAdapter = new ArrayList<String>();
            for(int i = 0; i < groups.size(); i++)
                groupsForAdapter.add(groups.get(i).getName());

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupsForAdapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            grpSpn.setAdapter(adapter);
        }
    }

}
