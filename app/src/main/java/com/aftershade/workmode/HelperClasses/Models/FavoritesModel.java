package com.aftershade.workmode.HelperClasses.Models;

public class FavoritesModel {

    String trainerId, userId, courseName;

    public FavoritesModel() {
    }

    public FavoritesModel(String trainerId, String userId, String courseName) {
        this.trainerId = trainerId;
        this.userId = userId;
        this.courseName = courseName;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCourseName() {
        return courseName;
    }
}
