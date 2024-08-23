package com.aftershade.workmode.HelperClasses.Models;

public class ProgramReviewModel {

    private String courseName, date, rating, reviewDesc, userFullName, userId, trainerId;

    public ProgramReviewModel() {
    }

    public ProgramReviewModel(String courseName, String date, String rating, String reviewDesc,
                              String userFullName, String userId, String trainerId) {
        this.courseName = courseName;
        this.date = date;
        this.rating = rating;
        this.reviewDesc = reviewDesc;
        this.userFullName = userFullName;
        this.userId = userId;
        this.trainerId = trainerId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDate() {
        return date;
    }

    public String getRating() {
        return rating;
    }

    public String getReviewDesc() {
        return reviewDesc;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserId() {
        return userId;
    }

    public String getTrainerId() {
        return trainerId;
    }
}
