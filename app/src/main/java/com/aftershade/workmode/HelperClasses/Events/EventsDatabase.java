package com.aftershade.workmode.HelperClasses.Events;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aftershade.workmode.HelperClasses.Models.ProgramOfflineModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class EventsDatabase {

    //DB Name
    public static final String DB_EVENTS = "EVENTS_DB";

    //Table Name
    public static final String TABLE_EVENTS = "EVENTS_TB";
    public static final String TABLE_PROGRAMS = "PROGRAMS_TB";

    //FIELDS
    public static final String EVENT_ID = "event_id";
    public static final String FIELD_CALENDAR_ID = "calendar_id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DT_START = "dtstart";
    public static final String FIELD_DT_END = "dtend";
    public static final String FIELD_TIME_START = "timeStart";
    public static final String FIELD_TIME_END = "timeEnd";
    public static final String FIELD_FREQ = "frequency";
    public static final String FIELD_COUNT = "count";
    public static final String FIELD_ALARM = "hasAlarm";
    public static final String FIELD_TIMEZONE = "eventTimezone";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_COMPLETE = "isComplete";
    public static final String FIELD_LOCATION = "location";
    public static final String FIELD_DAY_COUNT = "dayCount";

    public static final String FIELD_SKILL_LEVEL = "skillLevel";
    public static final String FIELD_TIME_SPAN = "timeSpan";
    public static final String FIELD_DURATION_SPAN = "durationSpan";
    public static final String FIELD_PROGRAM_GOAL = "programGoal";
    public static final String FIELD_PROGRAM_TYPE = "programType";

    public static final String FIELD_DAYO1 = "day01";
    public static final String FIELD_DAYO2 = "day02";
    public static final String FIELD_DAYO3 = "day03";
    public static final String FIELD_DAYO4 = "day04";
    public static final String FIELD_DAYO5 = "day05";
    public static final String FIELD_DAYO6 = "day06";
    public static final String FIELD_DAYO7 = "day07";
    public static final String FIELD_PROGRAM_END = "programEnd";
    public static final String FIELD_EXERCISE_DAYS = "exerciseDays";
    public static final String PROGRAM_MAX_DAYS = "programMaxDays";

    private SQLiteDatabase db;
    private final EventsHelper helper;
    private final Context context_;

    public EventsDatabase(Context context) {

        helper = new EventsHelper(context, DB_EVENTS, null, 1);
        db = helper.getWritableDatabase();
        context_ = context;

    }

    public long insertEvent(long eventId, int calendarId, String title, String dateStart, String dateEnd,
                            String timeStart, String timeEnd, String freq, String count, String desc) {
        ContentValues values = new ContentValues();
        values.put(EVENT_ID, eventId);
        values.put(FIELD_CALENDAR_ID, calendarId);
        values.put(FIELD_TITLE, title);
        values.put(FIELD_DT_START, dateStart);
        values.put(FIELD_DT_END, dateEnd);
        values.put(FIELD_TIME_START, timeStart);
        values.put(FIELD_TIME_END, timeEnd);
        values.put(FIELD_FREQ, freq);
        values.put(FIELD_COUNT, count);
        values.put(FIELD_ALARM, 1);
        values.put(FIELD_TIMEZONE, TimeZone.getDefault().getID());
        values.put(FIELD_DESCRIPTION, desc);

        return db.insert(TABLE_EVENTS, null, values);
    }

    public ArrayList<EventsModel> getAllEvents() {
        ArrayList<EventsModel> eventsHelpers = new ArrayList<>();

        db = helper.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM EVENTS_TB", null);
        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            eventsHelpers.add(new EventsModel(cur.getLong(cur.getColumnIndex(EVENT_ID)),
                    cur.getString(cur.getColumnIndex(FIELD_DT_START)),
                    cur.getString(cur.getColumnIndex(FIELD_DT_END)),
                    cur.getString(cur.getColumnIndex(FIELD_TIME_START)),
                    cur.getString(cur.getColumnIndex(FIELD_TIME_END)),
                    cur.getInt(cur.getColumnIndex(FIELD_CALENDAR_ID)),
                    cur.getInt(cur.getColumnIndex(FIELD_ALARM)),
                    cur.getString(cur.getColumnIndex(FIELD_TITLE)),
                    cur.getString(cur.getColumnIndex(FIELD_FREQ)),
                    cur.getString(cur.getColumnIndex(FIELD_COUNT)),
                    cur.getString(cur.getColumnIndex(FIELD_TIMEZONE)),
                    cur.getString(cur.getColumnIndex(FIELD_DESCRIPTION))));

            cur.moveToNext();
        }

        cur.close();

        return eventsHelpers;

    }

    public ArrayList<EventsModel> getSpecificEvent(long eventId) {
        ArrayList<EventsModel> eventsHelpers = new ArrayList<>();
        db = helper.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM EVENTS_TB WHERE " + EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});

        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                eventsHelpers.add(new EventsModel(cur.getLong(cur.getColumnIndex(EVENT_ID)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_START)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_END)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_START)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_END)),
                        cur.getInt(cur.getColumnIndex(FIELD_CALENDAR_ID)),
                        cur.getInt(cur.getColumnIndex(FIELD_ALARM)),
                        cur.getString(cur.getColumnIndex(FIELD_TITLE)),
                        cur.getString(cur.getColumnIndex(FIELD_FREQ)),
                        cur.getString(cur.getColumnIndex(FIELD_COUNT)),
                        cur.getString(cur.getColumnIndex(FIELD_TIMEZONE)),
                        cur.getString(cur.getColumnIndex(FIELD_DESCRIPTION))));

                cur.moveToNext();
            }
        }

        cur.close();

        return eventsHelpers;

    }

    public ArrayList<EventsModel> getTodayEvents() {

        ArrayList<EventsModel> eventsHelpers = new ArrayList<>();
        db = helper.getReadableDatabase();

        String date = todayDate();

        Cursor cur = db.rawQuery("SELECT * FROM EVENTS_TB WHERE " + FIELD_DT_START + " = ? ", new String[]{date});

        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                eventsHelpers.add(new EventsModel(cur.getLong(cur.getColumnIndex(EVENT_ID)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_START)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_END)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_START)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_END)),
                        cur.getInt(cur.getColumnIndex(FIELD_CALENDAR_ID)),
                        cur.getInt(cur.getColumnIndex(FIELD_ALARM)),
                        cur.getString(cur.getColumnIndex(FIELD_TITLE)),
                        cur.getString(cur.getColumnIndex(FIELD_FREQ)),
                        cur.getString(cur.getColumnIndex(FIELD_COUNT)),
                        cur.getString(cur.getColumnIndex(FIELD_TIMEZONE)),
                        cur.getString(cur.getColumnIndex(FIELD_DESCRIPTION))));

                cur.moveToNext();
            }
        }

        cur.close();


        return eventsHelpers;

    }

    public ArrayList<EventsModel> getWeekEvents() {

        ArrayList<EventsModel> eventsHelpers = new ArrayList<>();
        db = helper.getReadableDatabase();

        String date = todayDate();

        Cursor cur = db.rawQuery("SELECT * FROM EVENTS_TB WHERE " + FIELD_DT_START + " = " + date +
                " OR " + FIELD_FREQ + " IN (\"FREQ=DAILY\", \"FREQ=WEEKLY\")" +
                " OR " + FIELD_DT_END + "=" + date, null);

        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                eventsHelpers.add(new EventsModel(cur.getLong(cur.getColumnIndex(EVENT_ID)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_START)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_END)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_START)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_END)),
                        cur.getInt(cur.getColumnIndex(FIELD_CALENDAR_ID)),
                        cur.getInt(cur.getColumnIndex(FIELD_ALARM)),
                        cur.getString(cur.getColumnIndex(FIELD_TITLE)),
                        cur.getString(cur.getColumnIndex(FIELD_FREQ)),
                        cur.getString(cur.getColumnIndex(FIELD_COUNT)),
                        cur.getString(cur.getColumnIndex(FIELD_TIMEZONE)),
                        cur.getString(cur.getColumnIndex(FIELD_DESCRIPTION))));

                cur.moveToNext();
            }
        }

        cur.close();

        return eventsHelpers;

    }

    public ArrayList<EventsModel> getMonthEvents() {

        ArrayList<EventsModel> eventsHelpers = new ArrayList<>();
        db = helper.getReadableDatabase();

        String date = todayDate();

        Cursor cur = db.rawQuery("SELECT * FROM EVENTS_TB WHERE " + FIELD_DT_START + " = " + date +
                " OR " + FIELD_FREQ + " IN (DAILY, WEEKLY, MONTHLY)" +
                " OR " + FIELD_DT_END + "=" + date, null);

        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                eventsHelpers.add(new EventsModel(cur.getLong(cur.getColumnIndex(EVENT_ID)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_START)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_END)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_START)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_END)),
                        cur.getInt(cur.getColumnIndex(FIELD_CALENDAR_ID)),
                        cur.getInt(cur.getColumnIndex(FIELD_ALARM)),
                        cur.getString(cur.getColumnIndex(FIELD_TITLE)),
                        cur.getString(cur.getColumnIndex(FIELD_FREQ)),
                        cur.getString(cur.getColumnIndex(FIELD_COUNT)),
                        cur.getString(cur.getColumnIndex(FIELD_TIMEZONE)),
                        cur.getString(cur.getColumnIndex(FIELD_DESCRIPTION))));

                cur.moveToNext();
            }
        }

        cur.close();

        return eventsHelpers;

    }

    private String todayDate() {

        String startDate = "";

        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TimeZone.getDefault().getID()));

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DATE));

            if (Integer.parseInt(month) < 10) {
                month = "0" + month;
            }

            if (Integer.parseInt(day) < 10) {
                day = "0" + day;
            }

            startDate = year + "-" + month + "-" + day;

        } catch (Exception e) {
            Toast.makeText(context_, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return startDate;
    }

    public boolean updateEventStatus(long eventId, boolean isComplete) {
        db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_COMPLETE, isComplete);

        int rows = db.update(TABLE_EVENTS, values, EVENT_ID + " = " + eventId, null);

        return rows > 0;

    }

    public boolean updateEvent(long eventId, String title, String dateStart, String dateEnd,
                               String timeStart, String timeEnd, String freq, String count, String desc) {
        db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_TITLE, title);
        values.put(FIELD_DT_START, dateStart);
        values.put(FIELD_DT_END, dateEnd);
        values.put(FIELD_TIME_START, timeStart);
        values.put(FIELD_TIME_END, timeEnd);
        values.put(FIELD_DT_END, dateEnd);
        values.put(FIELD_FREQ, freq);
        values.put(FIELD_COUNT, count);
        values.put(FIELD_DESCRIPTION, desc);

        int rows = db.update(TABLE_EVENTS, values, EVENT_ID + " = " + eventId, null);

        return rows > 0;

    }

    /**
     * @param eventId   id of the event
     * @param tableName name of table
     * @return boolean
     * receives table name and event id and deletes that event from the specified table
     */
    public boolean deleteEvent_Program(long eventId, String tableName) {

        if (tableName.equals(TABLE_PROGRAMS)) {
            int rows = db.delete(TABLE_PROGRAMS, EVENT_ID + "=" + eventId, null);
            return rows > 0;
        } else {
            int rows = db.delete(TABLE_EVENTS, EVENT_ID + "=" + eventId, null);
            return rows > 0;
        }

    }

    /*Inserts new program data
     *the program inserted is a program created by an instructor not by a normal user
     */
    public long insertProgramEvent(long eventId, int calendarId, String title, String dateStart,
                                   String dateEnd, String timeStart, String timeEnd, String freq,
                                   String count, String desc, String durationSpan, String timeSpan,
                                   String skillLevel, String goal, String programType, int dayCount,
                                   String programEnd, String day01, String day02, String day03,
                                   String day04, String day05, String day06, String day07,
                                   String exerciseDays, int maxDays) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(EVENT_ID, eventId);
        values.put(FIELD_CALENDAR_ID, calendarId);
        values.put(FIELD_TITLE, title);
        values.put(FIELD_DT_START, dateStart);
        values.put(FIELD_DT_END, dateEnd);
        values.put(FIELD_TIME_START, timeStart);
        values.put(FIELD_TIME_END, timeEnd);
        values.put(FIELD_FREQ, freq);
        values.put(FIELD_DURATION_SPAN, durationSpan);
        values.put(FIELD_TIME_SPAN, timeSpan);
        values.put(FIELD_SKILL_LEVEL, skillLevel);
        values.put(FIELD_PROGRAM_GOAL, goal);
        values.put(FIELD_PROGRAM_TYPE, programType);
        values.put(FIELD_COUNT, count);
        values.put(FIELD_ALARM, 1);
        values.put(FIELD_TIMEZONE, TimeZone.getDefault().getID());
        values.put(FIELD_DESCRIPTION, desc);
        values.put(FIELD_DAY_COUNT, dayCount);
        values.put(FIELD_DAYO1, day01);
        values.put(FIELD_DAYO2, day02);
        values.put(FIELD_DAYO3, day03);
        values.put(FIELD_DAYO4, day04);
        values.put(FIELD_DAYO5, day05);
        values.put(FIELD_DAYO6, day06);
        values.put(FIELD_DAYO7, day07);
        values.put(FIELD_PROGRAM_END, programEnd);
        values.put(FIELD_EXERCISE_DAYS, exerciseDays);
        values.put(PROGRAM_MAX_DAYS, maxDays);

        return db.insertOrThrow(TABLE_PROGRAMS, null, values);
    }

    //Updates the programs day count
    public boolean updateProgramDayCount(long event_id, int dayCount) {
        db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_DAY_COUNT, dayCount);

        int rows = db.update(TABLE_PROGRAMS, values, EVENT_ID + " = " + event_id, null);

        return rows > 0;
    }

    public void createTable() {
        db = helper.getWritableDatabase();

        String qry1 = "CREATE TABLE PROGRAMS_TB(event_id long primary key, " +
                "calendar_id integer, " +
                "title TEXT, " +
                "dtstart TEXT, " +
                "dtend TEXT, " +
                "timeStart TEXT, " +
                "timeEnd TEXT, " +
                "hasAlarm integer, " +
                "frequency TEXT, " +
                "count TEXT, " +
                "eventTimezone TEXT, " +
                "description TEXT, " +
                "dayCount integer, " +
                FIELD_PROGRAM_END + " TEXT, " +
                PROGRAM_MAX_DAYS + " integer, " +
                FIELD_EXERCISE_DAYS + " TEXT, " +
                FIELD_TIME_SPAN + " TEXT, " +
                FIELD_SKILL_LEVEL + " TEXT, " +
                FIELD_DURATION_SPAN + " TEXT, " +
                FIELD_PROGRAM_GOAL + " TEXT, " +
                FIELD_PROGRAM_TYPE + " TEXT, " +
                FIELD_DAYO1 + " TEXT, " +
                FIELD_DAYO2 + " TEXT, " +
                FIELD_DAYO3 + " TEXT, " +
                FIELD_DAYO4 + " TEXT, " +
                FIELD_DAYO5 + " TEXT, " +
                FIELD_DAYO6 + " TEXT, " +
                FIELD_DAYO7 + " TEXT);";

        db.execSQL(qry1);
    }

    public void modifyTable() {
        db = helper.getWritableDatabase();

        String qry = "ALTER TABLE " + TABLE_PROGRAMS + " ADD " + PROGRAM_MAX_DAYS + " integer;";
        db.execSQL(qry);

    }

    public void truncateTable() {
        db = helper.getWritableDatabase();
        db.delete(TABLE_PROGRAMS, "1", null);

    }

    public ArrayList<ProgramOfflineModel> getProgramTodayEvents() {

        ArrayList<ProgramOfflineModel> eventsHelpers = new ArrayList<>();
        db = helper.getReadableDatabase();

        String date = todayDate();

        Cursor cur = db.rawQuery("SELECT * FROM PROGRAMS_TB WHERE " + FIELD_DT_START + " = ? OR "
                + FIELD_PROGRAM_END + " >= ?", new String[]{date, date});

        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                eventsHelpers.add(new ProgramOfflineModel(
                        cur.getLong(cur.getColumnIndex(EVENT_ID)),
                        cur.getInt(cur.getColumnIndex(FIELD_CALENDAR_ID)),
                        cur.getInt(cur.getColumnIndex(FIELD_DAY_COUNT)),
                        cur.getString(cur.getColumnIndex(FIELD_TITLE)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_START)),
                        cur.getString(cur.getColumnIndex(FIELD_DT_END)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_START)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_END)),
                        cur.getString(cur.getColumnIndex(FIELD_FREQ)),
                        cur.getString(cur.getColumnIndex(FIELD_COUNT)),
                        cur.getString(cur.getColumnIndex(FIELD_DESCRIPTION)),
                        cur.getString(cur.getColumnIndex(FIELD_SKILL_LEVEL)),
                        cur.getString(cur.getColumnIndex(FIELD_TIME_SPAN)),
                        cur.getString(cur.getColumnIndex(FIELD_DURATION_SPAN)),
                        cur.getString(cur.getColumnIndex(FIELD_PROGRAM_GOAL)),
                        cur.getString(cur.getColumnIndex(FIELD_PROGRAM_TYPE)),
                        cur.getString(cur.getColumnIndex(FIELD_PROGRAM_END)),
                        cur.getString(cur.getColumnIndex(FIELD_DAYO1)),
                        cur.getString(cur.getColumnIndex(FIELD_DAYO2)),
                        cur.getString(cur.getColumnIndex(FIELD_DAYO3)),
                        cur.getString(cur.getColumnIndex(FIELD_DAYO4)),
                        cur.getString(cur.getColumnIndex(FIELD_DAYO5)),
                        cur.getString(cur.getColumnIndex(FIELD_DAYO6)),
                        cur.getString(cur.getColumnIndex(FIELD_DAYO7)),
                        cur.getString(cur.getColumnIndex(FIELD_EXERCISE_DAYS)),
                        cur.getInt(cur.getColumnIndex(PROGRAM_MAX_DAYS))
                ));

                /*FIELD_SKILL_LEVEL= "skillLevel";
            public static final String FIELD_TIME_SPAN = "timeSpan";
            public static final String FIELD_DURATION_SPAN = "durationSpan";
            public static final String FIELD_PROGRAM_GOAL = "programGoal";
            public static final String FIELD_PROGRAM_TYPE = "programType";*/

                cur.moveToNext();
            }
        }

        cur.close();


        return eventsHelpers;

    }

    public ProgramOfflineModel getEvent(Long eventId) {

        ArrayList<ProgramOfflineModel> eventsHelpers = new ArrayList<>();
        db = helper.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM PROGRAMS_TB WHERE " + EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});

        ProgramOfflineModel model = null;

        if (cur.moveToFirst()) {
            model =  new ProgramOfflineModel(cur.getLong(cur.getColumnIndex(EVENT_ID)),
                    cur.getInt(cur.getColumnIndex(FIELD_CALENDAR_ID)),
                    cur.getInt(cur.getColumnIndex(FIELD_DAY_COUNT)),
                    cur.getString(cur.getColumnIndex(FIELD_TITLE)),
                    cur.getString(cur.getColumnIndex(FIELD_DT_START)),
                    cur.getString(cur.getColumnIndex(FIELD_DT_END)),
                    cur.getString(cur.getColumnIndex(FIELD_TIME_START)),
                    cur.getString(cur.getColumnIndex(FIELD_TIME_END)),
                    cur.getString(cur.getColumnIndex(FIELD_FREQ)),
                    cur.getString(cur.getColumnIndex(FIELD_COUNT)),
                    cur.getString(cur.getColumnIndex(FIELD_DESCRIPTION)),
                    cur.getString(cur.getColumnIndex(FIELD_SKILL_LEVEL)),
                    cur.getString(cur.getColumnIndex(FIELD_TIME_SPAN)),
                    cur.getString(cur.getColumnIndex(FIELD_DURATION_SPAN)),
                    cur.getString(cur.getColumnIndex(FIELD_PROGRAM_GOAL)),
                    cur.getString(cur.getColumnIndex(FIELD_PROGRAM_TYPE)),
                    cur.getString(cur.getColumnIndex(FIELD_PROGRAM_END)),
                    cur.getString(cur.getColumnIndex(FIELD_DAYO1)),
                    cur.getString(cur.getColumnIndex(FIELD_DAYO2)),
                    cur.getString(cur.getColumnIndex(FIELD_DAYO3)),
                    cur.getString(cur.getColumnIndex(FIELD_DAYO4)),
                    cur.getString(cur.getColumnIndex(FIELD_DAYO5)),
                    cur.getString(cur.getColumnIndex(FIELD_DAYO6)),
                    cur.getString(cur.getColumnIndex(FIELD_DAYO7)),
                    cur.getString(cur.getColumnIndex(FIELD_EXERCISE_DAYS)),
                    cur.getInt(cur.getColumnIndex(PROGRAM_MAX_DAYS)));
        }

        cur.close();

        return model;

    }

    private class EventsHelper extends SQLiteOpenHelper {

        public EventsHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory,
                            int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            /*String qry = "CREATE TABLE EVENTS_TB(event_id long primary key, " +
                    "calendar_id integer, " +
                    "title TEXT, " +
                    "dtstart TEXT, " +
                    "dtend TEXT, " +
                    "timeStart TEXT, " +
                    "timeEnd TEXT, " +
                    "hasAlarm integer, " +
                    "frequency TEXT, " +
                    "count TEXT, " +
                    "eventTimezone TEXT, " +
                    "description TEXT, " +
                    "isComplete boolean);";*/

            String qry1 = "CREATE TABLE PROGRAMS_TB(event_id long primary key, " +
                    "calendar_id integer, " +
                    "title TEXT, " +
                    "dtstart TEXT, " +
                    "dtend TEXT, " +
                    "timeStart TEXT, " +
                    "timeEnd TEXT, " +
                    "hasAlarm integer, " +
                    "frequency TEXT, " +
                    "count TEXT, " +
                    "eventTimezone TEXT, " +
                    "description TEXT, " +
                    "dayCount integer, " +
                    FIELD_TIME_SPAN + " TEXT, " +
                    FIELD_SKILL_LEVEL + " TEXT, " +
                    FIELD_DURATION_SPAN + " TEXT, " +
                    FIELD_TIME_SPAN + " TEXT, " +
                    FIELD_PROGRAM_GOAL + " TEXT, " +
                    FIELD_PROGRAM_TYPE + " TEXT, " +
                    FIELD_DAYO1 + " TEXT, " +
                    FIELD_DAYO2 + " TEXT, " +
                    FIELD_DAYO3 + " TEXT, " +
                    FIELD_DAYO4 + " TEXT, " +
                    FIELD_DAYO5 + " TEXT, " +
                    FIELD_DAYO6 + " TEXT, " +
                    FIELD_DAYO7 + " TEXT);";


            //db.execSQL(qry);
            db.execSQL(qry1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
