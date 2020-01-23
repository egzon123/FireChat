package com.example.firechat;

import java.util.Date;

public class ChatMessage {
    private String email;
    private String msg;
    private String messageUserId;


    public ChatMessage(String msg, String messageUser, String messageUserId) {
        this.email = email;
        this.msg = messageUser;

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

    public String getMessageUser() {
        return email;
    }

    public void setMessageUser(String messageUser) {
        this.email = messageUser;
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
