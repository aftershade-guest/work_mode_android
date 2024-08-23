package com.aftershade.workmode.HelperClasses.Events;

public class EventsModel {

    private long event_id;
    private String dtstart, dtend, timeStart, timeEnd, frequency, count;
    private int calendar_id, hasAlarm;
    private String title, eventTimezone, description;

    public EventsModel(long event_id, String dtstart, String dtend, String timeStart, String timeEnd, int calendar_id, int hasAlarm,
                       String title, String frequency, String count, String eventTimezone, String description) {
        this.event_id = event_id;
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.calendar_id = calendar_id;
        this.hasAlarm = hasAlarm;
        this.title = title;
        this.frequency = frequency;
        this.count = count;
        this.eventTimezone = eventTimezone;
        this.description = description;
    }

    public long getEvent_id() {
        return event_id;
    }

    public String getDtstart() {
        return dtstart;
    }

    public String getDtend() {
        return dtend;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getCount() {
        return count;
    }

    public int getCalendar_id() {
        return calendar_id;
    }

    public int getHasAlarm() {
        return hasAlarm;
    }

    public String getTitle() {
        return title;
    }

    public String getEventTimezone() {
        return eventTimezone;
    }

    public String getDescription() {
        return description;
    }
}
