package com.chat.chatserver.controller.model;

public class Message {
    private String senderName;
    private String receiverName;
    private String messageContent;
    private String date;

    private Status status;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Message(String senderName, String receiverName, String messageContent, String date, Status status) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.messageContent = messageContent;
        this.date = date;
        this.status = status;
    }

    public Message(){

    }
}
