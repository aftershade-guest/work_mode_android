package com.aftershade.workmode.HelperClasses.Models;

public class PostsHelperClass {

    String postId, dateCreated, timeCreated, datetimeCreated, postedBy, caption;

    public PostsHelperClass() {
    }

    public PostsHelperClass(String postId, String dateCreated, String timeCreated, String datetimeCreated, String postedBy, String caption) {
        this.postId = postId;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.datetimeCreated = datetimeCreated;
        this.postedBy = postedBy;
        this.caption = caption;
    }

    public String getPostId() {
        return postId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getDatetimeCreated() {
        return datetimeCreated;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getCaption() {
        return caption;
    }
}
