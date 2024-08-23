package com.aftershade.workmode.HelperClasses.Models;

public class ScheduleHelper {

    String day, desc;

    public ScheduleHelper(String day, String desc) {
        this.day = day;
        this.desc = desc;
    }

    public String getDay() {
        return day;
    }

    public String getDesc() {
        return desc;
    }
}
