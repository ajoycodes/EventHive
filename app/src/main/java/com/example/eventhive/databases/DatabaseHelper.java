package com.example.eventhive.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.eventhive.models.User;
import com.example.eventhive.models.Event;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventHive.db";
    private static final int DATABASE_VERSION = 3; // Incremented for new Schema

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_FNAME = "first_name";
    private static final String COL_USER_LNAME = "last_name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASS = "password";
    private static final String COL_USER_ROLE = "role";
    private static final String COL_USER_PHONE = "phone";

    // Events Table
    private static final String TABLE_EVENTS = "events";
    private static final String COL_EVENT_ID = "id";
    private static final String COL_EVENT_TITLE = "title";
    private static final String COL_EVENT_DATE = "date";
    private static final String COL_EVENT_LOCATION = "location";
    private static final String COL_EVENT_DESC = "description";
    private static final String COL_EVENT_IMAGE = "image_res_id"; // Storing resource ID for simplicity in demo
    private static final String COL_EVENT_STATUS = "status"; // Active, Hold, Cancelled

    // Tickets Table
    private static final String TABLE_TICKETS = "tickets";
    private static final String COL_TICKET_ID = "id";
    private static final String COL_TICKET_USER_ID = "user_id";
    private static final String COL_TICKET_EVENT_ID = "event_id";
    private static final String COL_TICKET_CODE = "unique_code"; // New Column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_FNAME + " TEXT, " +
                COL_USER_LNAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASS + " TEXT, " +
                COL_USER_ROLE + " TEXT, " +
                COL_USER_PHONE + " TEXT)";
        db.execSQL(createUsers);

        String createEvents = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EVENT_TITLE + " TEXT, " +
                COL_EVENT_DATE + " TEXT, " +
                COL_EVENT_LOCATION + " TEXT, " +
                COL_EVENT_DESC + " TEXT, " +
                COL_EVENT_IMAGE + " INTEGER, " +
                COL_EVENT_STATUS + " TEXT DEFAULT 'Active')";
        db.execSQL(createEvents);

        String createTickets = "CREATE TABLE " + TABLE_TICKETS + " (" +
                COL_TICKET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TICKET_USER_ID + " INTEGER, " +
                COL_TICKET_EVENT_ID + " INTEGER, " +
                COL_TICKET_CODE + " TEXT)";
        db.execSQL(createTickets);

        // Pre-populate some events
        insertDummyEvents(db);
        // Pre-populate default admin user
        insertDefaultUser(db);
    }

    private void insertDefaultUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_USER_FNAME, "Admin");
        values.put(COL_USER_LNAME, "User");
        values.put(COL_USER_EMAIL, "admin@eventhive.com");
        values.put(COL_USER_PASS, "admin123");
        values.put(COL_USER_ROLE, "Admin");
        values.put(COL_USER_PHONE, "0000000000");
        db.insert(TABLE_USERS, null, values);
    }

    private void insertDummyEvents(SQLiteDatabase db) {
        // Need to ensure these operations happen during creation
        // We'll insert raw SQL or use helper methods if called outside onCreate
        // For onCreate, we use standard SQL
        db.execSQL("INSERT INTO " + TABLE_EVENTS + " (title, date, location, description, image_res_id) VALUES " +
                "('WaveFest - Feel The Winter', '12 Dec - 10 PM', 'Bashundhara R/A, Dhaka', 'A music event bringing people together with electric energy.', 0)"); // 0
                                                                                                                                                                  // as
                                                                                                                                                                  // placeholder
        db.execSQL("INSERT INTO " + TABLE_EVENTS + " (title, date, location, description, image_res_id) VALUES " +
                "('Tech Summit 2024', '15 Jan - 9 AM', 'ICCB, Dhaka', 'The biggest tech conference in the city.', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        onCreate(db);
    }

    // --- User Operations ---
    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_FNAME, user.getFirstName());
        values.put(COL_USER_LNAME, user.getLastName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PASS, user.getPassword());
        values.put(COL_USER_ROLE, user.getRole());
        values.put(COL_USER_PHONE, user.getPhone());

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASS + "=?",
                new String[] { email, password }, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_FNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_LNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)));
            cursor.close();
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_FNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_LNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[] { String.valueOf(userId) });
    }

    // --- Event Operations ---
    public boolean createEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_TITLE, event.getTitle());
        values.put(COL_EVENT_DATE, event.getDate());
        values.put(COL_EVENT_LOCATION, event.getLocation());
        values.put(COL_EVENT_DESC, event.getDescription());
        values.put(COL_EVENT_IMAGE, 0); // Placeholder
        values.put(COL_EVENT_STATUS, event.getStatus());
        long res = db.insert(TABLE_EVENTS, null, values);
        return res != -1;
    }

    public boolean updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EVENT_TITLE, event.getTitle());
        values.put(COL_EVENT_DATE, event.getDate());
        values.put(COL_EVENT_LOCATION, event.getLocation());
        values.put(COL_EVENT_DESC, event.getDescription());
        values.put(COL_EVENT_STATUS, event.getStatus());
        int rows = db.update(TABLE_EVENTS, values, COL_EVENT_ID + "=?", new String[] { String.valueOf(event.getId()) });
        return rows > 0;
    }

    public void deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COL_EVENT_ID + "=?", new String[] { String.valueOf(eventId) });
    }

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);

        if (cursor.moveToFirst()) {
            do {
                String status = "Active"; // Default
                int statusIndex = cursor.getColumnIndex(COL_EVENT_STATUS);
                if (statusIndex != -1) {
                    status = cursor.getString(statusIndex);
                }

                Event event = new Event(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DESC)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_IMAGE)),
                        status);
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    // --- Ticket Operations ---
    public boolean registerTicket(int userId, int eventId, String uniqueCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TICKET_USER_ID, userId);
        values.put(COL_TICKET_EVENT_ID, eventId);
        values.put(COL_TICKET_CODE, uniqueCode);
        long res = db.insert(TABLE_TICKETS, null, values);
        return res != -1;
    }

    public List<com.example.eventhive.models.Ticket> getTicketsForUser(int userId) {
        List<com.example.eventhive.models.Ticket> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t." + COL_TICKET_ID + ", t." + COL_TICKET_USER_ID + ", t." + COL_TICKET_EVENT_ID + ", t."
                + COL_TICKET_CODE +
                ", e." + COL_EVENT_TITLE + ", e." + COL_EVENT_DATE + ", e." + COL_EVENT_LOCATION +
                " FROM " + TABLE_TICKETS + " t " +
                "JOIN " + TABLE_EVENTS + " e ON t." + COL_TICKET_EVENT_ID + " = e." + COL_EVENT_ID +
                " WHERE t." + COL_TICKET_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId) });
        if (cursor.moveToFirst()) {
            do {
                list.add(new com.example.eventhive.models.Ticket(
                        cursor.getInt(0), // ticket id
                        cursor.getInt(1), // user id
                        cursor.getInt(2), // event id
                        cursor.getString(3), // code
                        cursor.getString(4), // title
                        cursor.getString(5), // date
                        cursor.getString(6) // location
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
