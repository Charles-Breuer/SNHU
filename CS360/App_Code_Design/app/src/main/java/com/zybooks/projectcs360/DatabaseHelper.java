package com.zybooks.projectcs360;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
    //------------- Class Fields ----------------//
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "eventManager";

    private static final String TABLE_EVENTS = "events";
    private static final String KEY_EVENT_ID = "id";
    private static final String KEY_EVENT_DATE = "date";
    private static final String KEY_EVENT_TIME = "time";
    private static final String KEY_EVENT_TITLE = "title";
    private static final String KEY_EVENT_DESCRIPTION = "description";

    private static final String TABLE_USERS = "users";
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password"; // Note: In a real-world scenario, store a hashed password, not plain text!

    //------------- Constructor(s) ----------------//
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //------------- Override Functions ----------------//

    // This onCreate function initializes connection with in-device datatables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_EVENT_ID + " INTEGER PRIMARY KEY,"
                + KEY_EVENT_DATE + " TEXT,"
                + KEY_EVENT_TIME + " TEXT,"
                + KEY_EVENT_TITLE + " TEXT,"
                + KEY_EVENT_DESCRIPTION + " TEXT,"
                + "user_id INTEGER,"
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id)" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    // This function establishes database upgrade functionality.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion < 2)
        {
            db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN user_id INTEGER REFERENCES " + TABLE_USERS + "(id)");
        }
    }


    //------------- External Functionality ----------------//

    // Create new event for current database. Event is associated with a user.
    public void addEvent(EventModel event, long userId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_DATE, event.getDate());
        values.put(KEY_EVENT_TIME, event.getTime());
        values.put(KEY_EVENT_TITLE, event.getTitle());
        values.put(KEY_EVENT_DESCRIPTION, event.getDescription());
        values.put("user_id", userId);

        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    // Retrieve an event from database.
    public EventModel getEvent(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS,
                new String[]{KEY_EVENT_ID, KEY_EVENT_DATE, KEY_EVENT_TIME, KEY_EVENT_TITLE, KEY_EVENT_DESCRIPTION},
                KEY_EVENT_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        EventModel event = new EventModel(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4));
        cursor.close();

        return event;
    }

    // Update an event in the database.
    public boolean updateEvent(EventModel event)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_DATE, event.getDate());
        values.put(KEY_EVENT_TIME, event.getTime());
        values.put(KEY_EVENT_TITLE, event.getTitle());
        values.put(KEY_EVENT_DESCRIPTION, event.getDescription());

        // Updating row
        int rowsAffected = db.update(TABLE_EVENTS, values, KEY_EVENT_ID + " = ?", new String[] { String.valueOf(event.getId()) });

        return rowsAffected > 0;  // return true if one or more rows were updated, otherwise false
    }

    // Delete an Event in the database
    public boolean deleteEvent(EventModel event)
    {

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            int rowsAffected = db.delete(TABLE_EVENTS, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(event.getId())});
            return rowsAffected > 0;
        }
    }

    // Retrieve all events associated with a particular user from the database.
    public List<EventModel> getAllEventsForUser(long userId)
    {
        List<EventModel> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS,
                null,
                "user_id=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        // Check if the cursor has any rows
        if (cursor.moveToFirst())
        {
            do {
                EventModel event = new EventModel();
                event.setId(cursor.getLong(0));
                event.setDate(cursor.getString(1));
                event.setTime(cursor.getString(2));
                event.setTitle(cursor.getString(3));
                event.setDescription(cursor.getString(4));

                eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return eventList;  // This will return an empty list if the cursor had no rows
    }

    // Add a new user to the database.
    public void addUser(String username, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password); // Again, hash the password before saving!

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    // Authenticate user credentials. Return status as boolean.
    public boolean authenticateUser(String username, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_USER_ID},
                KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.close();
            return true;
        }
        return false;
    }

    // Check database for username presence. Used for new registration to prevent username
    // collisions.
    public boolean doesUsernameExist(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_USER_ID},
                KEY_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.close();
            return true;  // Username exists
        }

        if(cursor != null) {
            cursor.close();
        }
        return false;  // Username does not exist
    }

    // Retrieve the userID associated with a username.
    public long getUserId(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{KEY_USER_ID},
                KEY_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            long userId = cursor.getLong(0);
            cursor.close();
            return userId;
        }

        if(cursor != null)
        {
            cursor.close();
        }
        return -1; // Error or user not found
    }
}