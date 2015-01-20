package de.richter.alarmmeldung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

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
        Log.d(TAG, "selectedGroupIndex: " + selectedGroupIndex);
    }

    private void onMessageFocusChanged(int position) {
        this.selectedMessageIndex = position;
        Log.d(TAG, "selectedMessageIndex: " + selectedMessageIndex);
    }

    private void sendBtnOnClick(){
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
        for (int i = 0; i < members.size(); i++)
            Toast.makeText(this, "would send SMS to: " + members.get(i).toString(), Toast.LENGTH_SHORT).show();
        // FIXME: send SMS here
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
