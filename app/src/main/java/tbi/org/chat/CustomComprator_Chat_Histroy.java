package tbi.org.chat;


import java.util.Comparator;

import tbi.org.chat.model.Chatting;

public class CustomComprator_Chat_Histroy implements Comparator<Chatting> {
    @Override
    public int compare(Chatting t2, Chatting t1) {
        //compareToIgnoreCase
        return (t2.timeStamp + "").compareTo(t1.timeStamp + "");
    }
}