package de.richter.alarmmeldung;

public class Member {

    public static final String MEMBER_KEY = "MEMBER_KEY";

    private int id;
    private String name;
    private String number;

    public Member(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public Member(String name, String number) {
        this.id = -1;
        this.name = name;
        this.number = number;
    }

    public Member() {
        this.id = -1;
        this.name = null;
        this.number = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return "" + this.name + " <" + this.number + "> ";
    }
}
