package de.richter.alarmmeldung;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsExpListAdapter extends BaseExpandableListAdapter{

    private List<Map<String, Group>> groups;
    private List<List<Map<String, Member>>> children;
    private Context context;

    public GroupsExpListAdapter(Context context, ArrayList<Group> groups, ArrayList<List<Member>> member){
        this.context = context;
        this.groups = convert_groupArrayList_to_GroupMapList(groups);
        this.children = convert_memberArrayList_to_MemberMapList(member);
    }
    public GroupsExpListAdapter(Context context, List<Map<String, Group>> groups, List<List<Map<String, Member>>> children) {
        this.context = context;
        this.children = children;
        this.groups = groups;
    }

    private List<Map<String, Group>> convert_groupArrayList_to_GroupMapList(ArrayList<Group> groups){
        if(groups == null)
            return null;
        ArrayList<Map<String, Group>> mapList = new ArrayList<Map<String, Group>>();
        for (int i = 0; i < groups.size(); i++) {
            Map<String, Group> map = new HashMap<String, Group>();
            map.put(Group.GROUP_KEY, groups.get(i));
            mapList.add(map);
        }
        return mapList;
    }

    private List<List<Map<String, Member>>> convert_memberArrayList_to_MemberMapList(ArrayList<List<Member>> memberArray){
        if(memberArray == null)
            return null;
        List<List<Map<String, Member>>> memberMapList = new ArrayList<List<Map<String, Member>>>();
        for (int i = 0; i < memberArray.size(); i++) {
            List<Member> memberList = memberArray.get(i);
            if(memberList == null){
                Log.w(MainActivity.TAG, "Could not find member " + i + " in memberarray!");
                Toast.makeText(this.context, "Member " + i + " is null!", Toast.LENGTH_SHORT).show();
                continue;
            }
            List<Map<String, Member>> singleMemberMapList = new ArrayList<Map<String, Member>>();
            for (int j = 0; j < memberList.size(); j++) {
                Map<String, Member> map = new HashMap<String, Member>();
                map.put(Member.MEMBER_KEY, memberList.get(j));
                singleMemberMapList.add(map);
            }
            memberMapList.add(singleMemberMapList);
        }
        return memberMapList;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
       return children.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return groups.get(i).get(Group.GROUP_KEY);
    }

    @Override
    public Object getChild(int i, int i2) {
        return children.get(i).get(i2).get(Member.MEMBER_KEY);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.group_view, null);
        }
        Group grp = (Group) getGroup(i);
        TextView tv = (TextView) view.findViewById(R.id.lst_group_txt);
        tv.setText(grp.getName());
        ImageView iv = (ImageView) view.findViewById(R.id.lst_group_edit);
        if(iv != null) {
            iv.setTag("" + i);
        } else {
            Log.w(MainActivity.TAG, "Could not find View!");
        }

        iv = (ImageView) view.findViewById(R.id.lst_group_delete);
        if(iv != null) {
            iv.setTag("" + i);
        } else {
            Log.w(MainActivity.TAG, "Could not find View!");
        }

        iv = (ImageView) view.findViewById(R.id.lst_group_add_member);
        if(iv != null) {
            iv.setTag("" + i);
        } else {
            Log.w(MainActivity.TAG, "Could not find View!");
        }

        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.member_view, null);
        }
        Member member = ((Map<String, Member>) getChild(i, i2)).get(Member.MEMBER_KEY);
        TextView tv = (TextView) view.findViewById(R.id.lst_member_txt);
        tv.setText(member.getName() + "\n" + member.getNumber());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        // TODO: implement?
        return false;
    }
}
