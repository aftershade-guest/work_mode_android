package com.aftershade.workmode.HelperClasses.Models;

public class MessagesModel {

    private String message, type, from, to, messageId, time, date, fileName, state, mimeType;

    public MessagesModel() {
    }

    public MessagesModel(String message, String type, String from, String to, String messageId,
                         String time, String date, String fileName, String state, String mimeType) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.to = to;
        this.messageId = messageId;
        this.time = time;
        this.date = date;
        this.fileName = fileName;
        this.state = state;
        this.mimeType = mimeType;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getFileName() {
        return fileName;
    }

    public String getState() {
        return state;
    }

    public String getMimeType() {
        return mimeType;
    }
}
