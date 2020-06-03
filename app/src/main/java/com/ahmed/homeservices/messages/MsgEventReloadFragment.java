package com.ahmed.homeservices.messages;

public class MsgEventReloadFragment {

    private boolean reload = true;

    public MsgEventReloadFragment(boolean reload) {
        this.reload = reload;
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }
}
