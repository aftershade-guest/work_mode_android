package com.aftershade.workmode.HelperClasses.Events;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TimeZone;

public class EventsController {

    Context context;

    public EventsController(Context context) {
        this.context = context;
    }

    public long addEvent(int calIds, String title, long startDate, long endDate, String location,
                         String rrule, String description, String email) {
        long id = 0;
        try {
            final ContentResolver cr = context.getContentResolver();

            ContentValues cv = new ContentValues();
            cv.put("calendar_id", calIds);
            cv.put("title", title);
            cv.put("dtstart", startDate);
            cv.put("hasAlarm", 1);
            cv.put(CalendarContract.Events.EVENT_LOCATION, location);

            if (!email.isEmpty()) {
                cv.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);
            }
            cv.put(CalendarContract.Events.RRULE, rrule);
            cv.put("dtend", endDate);
            cv.put("eventTimezone", TimeZone.getDefault().getID());
            cv.put(CalendarContract.Events.DESCRIPTION, description);

            Uri newEvent;
            newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);

            if (newEvent != null) {
                id = Long.parseLong(newEvent.getLastPathSegment());

                if (!email.isEmpty()) {
                    ContentValues values1 = new ContentValues();
                    values1.put(CalendarContract.Attendees.EVENT_ID, id);
                    values1.put(CalendarContract.Attendees.ATTENDEE_EMAIL, email);
                    values1.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_REQUIRED);
                            /*values1.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
                            values1.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED);*/

                    cr.insert(CalendarContract.Attendees.CONTENT_URI, values1);
                }

                ContentValues values = new ContentValues();
                values.put("event_id", id);
                values.put("method", 1);
                values.put("minutes", 15);

                cr.insert(Uri.parse("content://com.android.calendar/reminders"), values);
            }

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return id;
    }

    public void deleteEvent(long eventId) {

        int deleted = context.getContentResolver().delete(CalendarContract.Events.CONTENT_URI,
                CalendarContract.Events._ID + "=?", new String[]{Long.toString(eventId)});

    }

    public int updateEvent(long eventId, int calIds, String title, long startDate, long endDate, String location,
                            String rrule, String description) {

        ContentValues cv = new ContentValues();
        cv.put("calendar_id", calIds);
        cv.put("title", title);
        cv.put("dtstart", startDate);
        cv.put(CalendarContract.Events.EVENT_LOCATION, location);
        cv.put(CalendarContract.Events.RRULE, rrule);
        cv.put("dtend", endDate);
        cv.put(CalendarContract.Events.DESCRIPTION, description);

        return context.getContentResolver().update(CalendarContract.Events.CONTENT_URI, cv,
                "_id = ?", new String[]{String.valueOf(eventId)});

    }

}
