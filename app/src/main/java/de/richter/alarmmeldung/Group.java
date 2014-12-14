package de.richter.alarmmeldung;

import java.util.ArrayList;
import java.util.List;

public class Group {

    public static final String GROUP_KEY = "GROUP_KEY";

    private int id;
    private String name;
    private List<Member> member;

    public Group(int id, String name, List<Member> member) {
        this.id = id;
        this.name = name;
        this.member = member;
    }

    public Group(int id, String name){
        this.id = id;
        this.name = name;
        this.member = null;
    }

    public Group(String name, List<Member> member) {
        this.id = -1;
        this.name = name;
        this.member = member;
    }

    public Group(String name) {
        this.id = 0;
        this.name = name;
        this.member = null;
    }

    public void addMember(Member member){
        if(this.member == null)
            this.member = new ArrayList<Member>();

        this.member.add(member);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMember() {
        return this.member;
    }

    public void setMember(List<Member> member) {
        this.member = member;
    }

    public String toString() {
        return "" + this.name;
    }
}
