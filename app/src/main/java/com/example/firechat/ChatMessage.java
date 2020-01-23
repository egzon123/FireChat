package com.example.firechat;

import java.util.Date;

public class ChatMessage {
    private String email;
    private String msg;
    private String messageUserId;
    private long messageTime;


    public ChatMessage(String msg, String email, String messageUserId) {
        this.email = email;
        this.msg = msg;
        messageTime = new Date().getTime();
        this.messageUserId = messageUserId;
    }

    public ChatMessage(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageText='" + msg + '\'' +
                ", messageUser='" + email + '\'' +
                ", messageUserId='" + messageUserId + '\'' +
               +
                '}';
    }
}
