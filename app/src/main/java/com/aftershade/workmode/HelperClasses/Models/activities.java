package com.aftershade.workmode.HelperClasses.Models;

public class activities {

    private String title, startDate, endDate, repeat, invitee, length;

    public activities(String title, String startDate, String endDate, String repeat, String invitee, String length) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.repeat = repeat;
        this.invitee = invitee;
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRepeat() {
        return repeat;
    }

    public String getInvitee() {
        return invitee;
    }

    public String getLength() {
        return length;
    }
}
