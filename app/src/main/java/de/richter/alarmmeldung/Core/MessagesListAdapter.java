package de.richter.alarmmeldung.Core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.richter.alarmmeldung.R;

public class MessagesListAdapter extends BaseAdapter {


    Context context;
    List<Map<String, Message>> data;

    public MessagesListAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.data = convert_arrayList_to_MapList(messages);
    }

    public MessagesListAdapter(Context context, List<Map<String, Message>> messages) {
        this.context = context;
        this.data = messages;
    }

    public List<Map<String, Message>> convert_arrayList_to_MapList(ArrayList<Message> messages) {
        List<Map<String, Message>> ret = new ArrayList<Map<String, Message>>();
        for (int i = 0; i < messages.size(); i++) {
            Map<String, Message> curMap = new HashMap<String, Message>();
            curMap.put(Message.MESSAGE_KEY, messages.get(i));
            ret.add(curMap);
        }
        return ret;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    public Message getMessage(int i) {
        return ((HashMap<String, Message>) getItem(i)).get(Message.MESSAGE_KEY);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Message message = getMessage(i);

        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.message_view, null);
        }

        TextView tv = (TextView) view.findViewById(R.id.lst_message_txt);
        tv.setText(message.getMessage());
        tv.setTag("" + i);

        view.findViewById(R.id.lst_message_delete).setTag("" + i);
        view.findViewById(R.id.lst_message_edit).setTag("" + i);

        return view;
    }
}
