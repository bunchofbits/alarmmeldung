package de.richter.alarmmeldung;

public class Message {

    public static final String MESSAGE_KEY = "MESSAGE_KEY";

    private int id;
    private String message;

    public Message(){
        id = -1;
        message = null;
    }

    public Message(String message){
        this.message = message;
    }

    public Message(int id, String message){this.id = id; this.message = message;}

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

    @Override
    public String toString(){
        return "(" + this.id + "): " + this.message;
    }
}
