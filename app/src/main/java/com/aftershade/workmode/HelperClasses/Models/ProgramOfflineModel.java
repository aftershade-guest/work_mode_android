package com.aftershade.workmode.HelperClasses.Models;

public class ProgramOfflineModel {

    long eventId;
    int calendarId, dayCount, maxDays;
    String title, dateStart, dateEnd, timeStart, timeEnd, freq, count, desc, programEnd,
            day01, day02,  day03, day04, day05, day06, day07, skillLevel, programGoal,
            timeSpan, durationSpan, programType, exerciseDays;

    public ProgramOfflineModel() {
    }

    public ProgramOfflineModel(long eventId, int calendarId, int dayCount, String title,
                               String dateStart, String dateEnd, String timeStart, String timeEnd,
                               String freq, String count, String desc, String skillLevel,
                               String timeSpan, String durationSpan, String programGoal,
                               String programType, String programEnd, String day01, String day02,
                               String day03, String day04, String day05, String day06, String day07,
                               String exerciseDays, int maxDays) {
        this.eventId = eventId;
        this.calendarId = calendarId;
        this.dayCount = dayCount;
        this.title = title;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.freq = freq;
        this.count = count;
        this.skillLevel = skillLevel;
        this.timeSpan = timeSpan;
        this.durationSpan = durationSpan;
        this.programGoal = programGoal;
        this.programType = programType;
        this.desc = desc;
        this.programEnd = programEnd;
        this.exerciseDays = exerciseDays;
        this.maxDays = maxDays;
        this.day01 = day01;
        this.day02 = day02;
        this.day03 = day03;
        this.day04 = day04;
        this.day05 = day05;
        this.day06 = day06;
        this.day07 = day07;
    }

    public long getEventId() {
        return eventId;
    }

    public int getCalendarId() {
        return calendarId;
    }

    public int getDayCount() {
        return dayCount;
    }

    public String getTitle() {
        return title;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getFreq() {
        return freq;
    }

    public String getCount() {
        return count;
    }

    public String getDesc() {
        return desc;
    }

    public String getProgramEnd() {
        return programEnd;
    }

    public String getDay01() {
        return day01;
    }

    public String getDay02() {
        return day02;
    }

    public String getDay03() {
        return day03;
    }

    public String getDay04() {
        return day04;
    }

    public String getDay05() {
        return day05;
    }

    public String getDay06() {
        return day06;
    }

    public String getDay07() {
        return day07;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public String getProgramGoal() {
        return programGoal;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public String getDurationSpan() {
        return durationSpan;
    }

    public String getProgramType() {
        return programType;
    }

    public String getExerciseDays() {
        return exerciseDays;
    }

    public int getMaxDays() {
        return maxDays;
    }
}
