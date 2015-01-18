package de.richter.alarmmeldung;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.richter.alarmmeldung.database.DatabaseHelper;

public class EditMessagesActivity extends ListActivity {

    Message msgToWork;
    EditText msgDlgTxt;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_messages);

        update_list_view();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_messages, menu);
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_message:
                addMessageOnClick();
                return true;
            default:
                return false;
        }
    }

    private void dlgAddMsgOnClick(){
        String msg = msgDlgTxt.getText().toString();
        if(msg.isEmpty()){
            Toast.makeText(this, R.string.no_text_entered, Toast.LENGTH_LONG).show();
            return;
        }
        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.addMessage(new Message(msg));
        update_list_view();
    }

    public void addMessageOnClick(){
        AlertDialog.Builder alert;
        SmsTextWatcher watcher;

        msgDlgTxt = new EditText(this);
        msgDlgTxt.setMaxLines(5);
        msgDlgTxt.setText("");

        // Views for Dialog
        LinearLayout vertLout = new LinearLayout(this);
        TextView charCountTxt = new TextView(this);

        vertLout.setOrientation(LinearLayout.VERTICAL);

        vertLout.addView(msgDlgTxt);
        vertLout.addView(charCountTxt);

        alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.edit_message);

        watcher = new SmsTextWatcher(charCountTxt);
        charCountTxt.setText(
                watcher.recalculate_remaining_chars(msgDlgTxt.getText()));

        msgDlgTxt.addTextChangedListener(watcher);
        alert.setView(vertLout);

        alert.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });


        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dlgAddMsgOnClick();
            }
        });

        alert.create();
        alert.show();

        update_list_view();
    }

    private void dlgEditMsgOnClick(){
        Message new_msg = new Message();
        String msg = msgDlgTxt.getText().toString();
        if(msg.isEmpty()){
            Toast.makeText(this, R.string.no_text_entered, Toast.LENGTH_LONG).show();
            return;
        }
        new_msg.setMessage(msg);
        new_msg.setId(msgToWork.getId());

        DatabaseHelper dbh = new DatabaseHelper(this);
        dbh.updateMessage(new_msg);
        update_list_view();
    }

    /* call defined in xml */
    public void editMessageOnCLick(View v) {

        AlertDialog.Builder alert;
        SmsTextWatcher watcher;
        int pos;

        pos = Integer.parseInt("" + v.getTag());
        msgToWork = ((MessagesListAdapter)getListAdapter()).getMessage(pos);

        msgDlgTxt = new EditText(this);
        msgDlgTxt.setMaxLines(5);
        msgDlgTxt.setText(msgToWork.getMessage());

        // Views for Dialog
        LinearLayout vertLout = new LinearLayout(this);
        TextView charCountTxt = new TextView(this);

        vertLout.setOrientation(LinearLayout.VERTICAL);

        vertLout.addView(msgDlgTxt);
        vertLout.addView(charCountTxt);

        alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.edit_message);
        alert.setCancelable(false);

        watcher = new SmsTextWatcher(charCountTxt);
        charCountTxt.setText(
                watcher.recalculate_remaining_chars(msgDlgTxt.getText()));

        msgDlgTxt.addTextChangedListener(watcher);
        alert.setView(vertLout);

        alert.setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });


        alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dlgEditMsgOnClick();
            }
        });

        alert.create();
        alert.show();

        update_list_view();
    }

    private void dlgDelMsgOnClick(){
        DatabaseHelper dbh = new DatabaseHelper(this);
        Log.i(MainActivity.TAG, "Deleting Message" + msgToWork);
        dbh.deleteMessageByID(msgToWork.getId());
        update_list_view();
    }

    /* call defined in xml */
    public void deleteMessageOnCLick(View v){
        int pos = Integer.parseInt("" + v.getTag());
        msgToWork = ((MessagesListAdapter)getListAdapter()).getMessage(pos);
        String rly_del_str = getString(R.string.delete_message_qst);
        String msg_str = getString(R.string.message);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.delete_qst);
        alert.setMessage("" + rly_del_str + "\n" + msg_str + ":\n" + msgToWork.getMessage());

        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dlgDelMsgOnClick();
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

    private void update_list_view() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        ArrayList<Message> messages = dbh.getAllMessages();
        if(messages == null) {
            messages = new ArrayList<Message>(); // set empty List
        }
        setListAdapter(new MessagesListAdapter(this, messages));
    }
}
