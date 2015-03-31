package de.richter.alarmmeldung.Core;

public class Message {

    public static final String MESSAGE_KEY = "MESSAGE_KEY";

    private int id;
    private String message;

    public Message() {
        id = -1;
        message = null;
    }

    public Message(String message) {
        this.message = message;
    }

    public Message(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "(" + this.id + "): " + this.message;
    }
}
