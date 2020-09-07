package com.minemodsgames.enbl;

import javafx.scene.control.Label;

import java.sql.Time;
import java.sql.Date;

public class Message extends Label {
    public Date msgDate;
    public Time msgTime;

    public String sender;

    public Message(String message) {
        super(message);
    }

    public Message(Label message) {
        this(message.getText());
    }

    public Message(String message, Date msgDate, Time msgTime, double maxWidth) {
        this(message);
        this.msgDate = msgDate;
        this.msgTime = msgTime;
        setMaxWidth(maxWidth);
    }

    public Message(Label message, Date msgDate, Time msgTime, double maxWidth) {
        this(message.getText(), msgDate, msgTime, maxWidth);
    }

    public void setMessage() {
        String msg = "";
        msg += sender;
        msg += "\n";
        msg += getText();
        msg += "\n";
        msg += msgDate.toString();
        msg += " ";
        msg += msgTime.toString();

        setText(msg);
    }
}
