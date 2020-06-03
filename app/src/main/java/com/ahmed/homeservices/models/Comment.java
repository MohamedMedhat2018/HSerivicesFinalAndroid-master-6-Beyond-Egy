package com.ahmed.homeservices.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Comment implements Serializable {
    private String comment;
    private String type = "";

    private String freelancerId;
    @Exclude
    private String commentId;
//    @Exclude
    private boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    @Exclude
    public String getCommentId() {
        return commentId;
    }

        @Exclude
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Comment() {
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFreelancerId() {
        return freelancerId;
    }

    public void setFreelancerId(String freelancerId) {
        this.freelancerId = freelancerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
