package de.richter.alarmmeldung;

/**
 * Created by Simon on 22.01.2015.
 */
public class SendingOrder {

    private int messageID;
    private int memberID;

    public SendingOrder(int memberID, int messageID) {
        this.memberID = memberID;
        this.messageID = messageID;
    }

    public int getMessageID() {
        return messageID;
    }

    public int getMemberID() {
        return memberID;
    }
}
