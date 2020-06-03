package com.ahmed.homeservices.messages;

public class MessageUrlPathEvent {

    String url;

    public MessageUrlPathEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
