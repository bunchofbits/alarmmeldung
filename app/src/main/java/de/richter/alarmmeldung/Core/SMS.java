package de.richter.alarmmeldung.Core;

/**
 * Created by Simon on 22.01.2015.
 */
public class SMS {

    private String message;
    private Member member;
    private int ID;

    public SMS(int id, String message, Member member) {
        this.ID = id;
        this.member = member;
        this.message = message;
    }

    public SMS(String message, Member member) {
        this.member = member;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Member getMember() {
        return member;
    }

    public int getID() {
        return ID;
    }
}
