package de.richter.alarmmeldung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import de.richter.alarmmeldung.database.DatabaseHelper;

public class MainActivity extends Activity {

    public static final String TAG = "Alarmmeldung";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    private void sendBtnOnClick(){
        Toast.makeText(this, "NOT YET IMPLEMENTED!", Toast.LENGTH_LONG).show();
        DatabaseHelper dbh = new DatabaseHelper(this);
        ArrayList<Member> members = dbh.getAllMember();
        if(members == null) {
            Log.d(TAG, "No Members added yet");
            return;
        }
        // TODO: send messages here
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
