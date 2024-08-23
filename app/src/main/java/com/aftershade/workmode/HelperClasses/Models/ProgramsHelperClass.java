package com.aftershade.workmode.HelperClasses.Models;

public class ProgramsHelperClass {

    private String courseName, duration, durationSpan, startDay, endDay, time,
            timeSpan, goal, type, skillLevel, description, dateCreated, timeCreated,
            dateTimeCreated, trainerId;

    private double rating;

    public ProgramsHelperClass() {
    }

    public ProgramsHelperClass(String courseName, String duration, String durationSpan,
                               String startDay, String endDay, String time, String timeSpan,
                               String goal, String type, String skillLevel, String description,
                               String dateCreated, String timeCreated, String dateTimeCreated,
                               String trainerId, double rating) {
        this.courseName = courseName;
        this.duration = duration;
        this.durationSpan = durationSpan;
        this.startDay = startDay;
        this.endDay = endDay;
        this.time = time;
        this.timeSpan = timeSpan;
        this.goal = goal;
        this.type = type;
        this.skillLevel = skillLevel;
        this.description = description;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.dateTimeCreated = dateTimeCreated;
        this.trainerId = trainerId;
        this.rating = rating;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDuration() {
        return duration;
    }

    public String getDurationSpan() {
        return durationSpan;
    }

    public String getStartDay() {
        return startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public String getTime() {
        return time;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public String getGoal() {
        return goal;
    }

    public String getType() {
        return type;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public String getDescription() {
        return description;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getDateTimeCreated() {
        return dateTimeCreated;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public double getRating() {
        return rating;
    }
}