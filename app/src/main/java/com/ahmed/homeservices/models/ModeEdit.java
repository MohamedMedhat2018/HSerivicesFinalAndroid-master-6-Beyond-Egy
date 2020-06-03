package com.ahmed.homeservices.models;

import androidx.annotation.NonNull;

import com.ahmed.homeservices.constants.Constants;

public class ModeEdit {

    private String path;
    private String mode;//in case of null paths
//    private String mode;


    public ModeEdit(String path, String mode) {
        this.path = path;
        this.mode = mode;
    }

    public ModeEdit() {
        this.mode = Constants.Modes.NO_FROM_USER_OR_ABOVE;
        this.path = null;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
