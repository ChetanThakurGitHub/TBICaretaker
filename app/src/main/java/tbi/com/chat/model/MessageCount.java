package tbi.com.chat.model;

public class MessageCount {
    public String count = "";

    public MessageCount setValue(int value) {
        this.count = value + "";
        return this;
    }
}
