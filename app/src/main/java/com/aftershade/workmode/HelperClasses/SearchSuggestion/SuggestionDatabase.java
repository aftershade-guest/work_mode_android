package com.aftershade.workmode.HelperClasses.SearchSuggestion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;

import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.Helper;


public class SuggestionDatabase{

    public static final String DB_SUGGESTION = "SUGGESTION_DB";
    public static final String TABLE_SUGGESTION = "SUGGESTION_TB";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_SUGGESTION = "suggestion";

    private SQLiteDatabase db;
    private Helper helper;

    public SuggestionDatabase(Context context) {

        helper = new Helper(context, DB_SUGGESTION, null, 1);
        db = helper.getWritableDatabase();
    }

    public long insertSuggestion(String text) {
        ContentValues values = new ContentValues();
        values.put(FIELD_SUGGESTION, text);
        return db.insert(TABLE_SUGGESTION, null, values);
    }

    public Cursor getSuggestions(String text) {
        return db.query(TABLE_SUGGESTION, new String[]{FIELD_ID, FIELD_SUGGESTION},
                FIELD_SUGGESTION + " LIKE '" + text + "%'", null, null,
                null, FIELD_ID + " DESC", "7");
    }

    private class Helper extends SQLiteOpenHelper {

        public Helper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory,
                       int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_SUGGESTION + "(" + FIELD_ID + " integer primary key autoincrement, " +
                    FIELD_SUGGESTION + " TEXT UNIQUE ON CONFLICT REPLACE);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
