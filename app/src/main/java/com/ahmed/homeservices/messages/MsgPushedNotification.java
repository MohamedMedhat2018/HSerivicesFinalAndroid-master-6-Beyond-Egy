package com.ahmed.homeservices.messages;


public class MsgPushedNotification {

    boolean pushedOrNot = true;

    public MsgPushedNotification(boolean pushedOrNot) {
        this.pushedOrNot = pushedOrNot;
    }


    public boolean isPushedOrNot() {
        return pushedOrNot;
    }

    public void setPushedOrNot(boolean pushedOrNot) {
        this.pushedOrNot = pushedOrNot;
    }


}
