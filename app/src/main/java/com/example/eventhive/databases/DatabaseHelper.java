package com.example.eventhive.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.eventhive.models.User;
import com.example.eventhive.models.Event;
import com.example.eventhive.models.Ticket;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventHive.db";
    private static final int DATABASE_VERSION = 6; // Incremented for user_uid support

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
    private static final String COL_EVENT_IMAGE = "image_res_id"; // Legacy field
    private static final String COL_EVENT_STATUS = "status"; // Active, Hold, Cancelled
    // New event columns
    private static final String COL_EVENT_TICKET_PRICE = "ticket_price";
    private static final String COL_EVENT_TICKET_QUANTITY = "ticket_quantity";
    private static final String COL_EVENT_COVER_IMAGE_PATH = "cover_image_path";
    private static final String COL_EVENT_GALLERY_PATHS = "gallery_image_paths";
    private static final String COL_EVENT_TYPE = "event_type";

    // Tickets Table
    private static final String TABLE_TICKETS = "tickets";
    private static final String COL_TICKET_ID = "id";
    private static final String COL_TICKET_USER_ID = "user_id"; // Legacy integer ID
    private static final String COL_TICKET_USER_UID = "user_uid"; // Firebase UID
    private static final String COL_TICKET_EVENT_ID = "event_id";
    private static final String COL_TICKET_CODE = "unique_code";
    private static final String COL_TICKET_TIMESTAMP = "purchase_timestamp";

    // Notifications Table
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COL_NOTIF_ID = "id";
    private static final String COL_NOTIF_TITLE = "title";
    private static final String COL_NOTIF_MESSAGE = "message";
    private static final String COL_NOTIF_TIMESTAMP = "timestamp";
    private static final String COL_NOTIF_IS_READ = "is_read";
    private static final String COL_NOTIF_USER_ID = "user_id"; // Legacy
    private static final String COL_NOTIF_USER_UID = "user_uid"; // Firebase UID
    private static final String COL_NOTIF_RELATED_EVENT_ID = "related_event_id";

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
                COL_EVENT_STATUS + " TEXT DEFAULT 'Active', " +
                COL_EVENT_TICKET_PRICE + " REAL DEFAULT 0.0, " +
                COL_EVENT_TICKET_QUANTITY + " INTEGER DEFAULT 0, " +
                COL_EVENT_COVER_IMAGE_PATH + " TEXT, " +
                COL_EVENT_GALLERY_PATHS + " TEXT, " +
                COL_EVENT_TYPE + " TEXT DEFAULT 'Other')";
        db.execSQL(createEvents);

        String createTickets = "CREATE TABLE " + TABLE_TICKETS + " (" +
                COL_TICKET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TICKET_USER_ID + " INTEGER, " +
                COL_TICKET_USER_UID + " TEXT, " +
                COL_TICKET_EVENT_ID + " INTEGER, " +
                COL_TICKET_CODE + " TEXT, " +
                COL_TICKET_TIMESTAMP + " INTEGER DEFAULT 0)";
        db.execSQL(createTickets);

        String createNotifications = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COL_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTIF_TITLE + " TEXT, " +
                COL_NOTIF_MESSAGE + " TEXT, " +
                COL_NOTIF_TIMESTAMP + " INTEGER, " +
                COL_NOTIF_IS_READ + " INTEGER DEFAULT 0, " +
                COL_NOTIF_USER_ID + " INTEGER, " +
                COL_NOTIF_USER_UID + " TEXT, " +
                COL_NOTIF_RELATED_EVENT_ID + " INTEGER DEFAULT 0)";
        db.execSQL(createNotifications);

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
        db.execSQL("INSERT INTO " + TABLE_EVENTS + " (title, date, location, description, image_res_id) VALUES " +
                "('WaveFest - Feel The Winter', '12 Dec - 10 PM', 'Bashundhara R/A, Dhaka', 'A music event bringing people together with electric energy.', 0)");
        db.execSQL("INSERT INTO " + TABLE_EVENTS + " (title, date, location, description, image_res_id) VALUES " +
                "('Tech Summit 2024', '15 Jan - 9 AM', 'ICCB, Dhaka', 'The biggest tech conference in the city.', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            try {
                db.execSQL(
                        "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_TICKET_PRICE + " REAL DEFAULT 0.0");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_TICKET_QUANTITY
                        + " INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_COVER_IMAGE_PATH + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_GALLERY_PATHS + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_TYPE + " TEXT DEFAULT 'Other'");
                db.execSQL(
                        "ALTER TABLE " + TABLE_TICKETS + " ADD COLUMN " + COL_TICKET_TIMESTAMP + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error during v3->v4 migration: " + e.getMessage());
            }
        }

        if (oldVersion < 5) {
            try {
                String createNotifications = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                        COL_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NOTIF_TITLE + " TEXT, " +
                        COL_NOTIF_MESSAGE + " TEXT, " +
                        COL_NOTIF_TIMESTAMP + " INTEGER, " +
                        COL_NOTIF_IS_READ + " INTEGER DEFAULT 0, " +
                        COL_NOTIF_USER_ID + " INTEGER, " +
                        COL_NOTIF_RELATED_EVENT_ID + " INTEGER DEFAULT 0)";
                db.execSQL(createNotifications);
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error during v4->v5 migration: " + e.getMessage());
            }
        }

        if (oldVersion < 6) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_TICKETS + " ADD COLUMN " + COL_TICKET_USER_UID + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_NOTIFICATIONS + " ADD COLUMN " + COL_NOTIF_USER_UID + " TEXT");
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error during v5->v6 migration: " + e.getMessage());
            }
        }
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
        values.put(COL_EVENT_IMAGE, 0);
        values.put(COL_EVENT_STATUS, event.getStatus());
        values.put(COL_EVENT_TICKET_PRICE, event.getTicketPrice());
        values.put(COL_EVENT_TICKET_QUANTITY, event.getTicketQuantity());
        values.put(COL_EVENT_COVER_IMAGE_PATH, event.getCoverImagePath());
        values.put(COL_EVENT_GALLERY_PATHS, event.getGalleryImagePaths());
        values.put(COL_EVENT_TYPE, event.getEventType());
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
        values.put(COL_EVENT_TICKET_PRICE, event.getTicketPrice());
        values.put(COL_EVENT_TICKET_QUANTITY, event.getTicketQuantity());
        values.put(COL_EVENT_COVER_IMAGE_PATH, event.getCoverImagePath());
        values.put(COL_EVENT_GALLERY_PATHS, event.getGalleryImagePaths());
        values.put(COL_EVENT_TYPE, event.getEventType());
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
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_LOCATION));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DESC));

                String status = "Active";
                int statusIndex = cursor.getColumnIndex(COL_EVENT_STATUS);
                if (statusIndex != -1 && cursor.getString(statusIndex) != null)
                    status = cursor.getString(statusIndex);

                double ticketPrice = 0.0;
                int priceIndex = cursor.getColumnIndex(COL_EVENT_TICKET_PRICE);
                if (priceIndex != -1)
                    ticketPrice = cursor.getDouble(priceIndex);

                int ticketQuantity = 0;
                int quantityIndex = cursor.getColumnIndex(COL_EVENT_TICKET_QUANTITY);
                if (quantityIndex != -1)
                    ticketQuantity = cursor.getInt(quantityIndex);

                String coverImagePath = "";
                int coverPathIndex = cursor.getColumnIndex(COL_EVENT_COVER_IMAGE_PATH);
                if (coverPathIndex != -1) {
                    coverImagePath = cursor.getString(coverPathIndex);
                    if (coverImagePath == null)
                        coverImagePath = "";
                }

                String galleryPaths = "";
                int galleryIndex = cursor.getColumnIndex(COL_EVENT_GALLERY_PATHS);
                if (galleryIndex != -1) {
                    galleryPaths = cursor.getString(galleryIndex);
                    if (galleryPaths == null)
                        galleryPaths = "";
                }

                String eventType = "Other";
                int typeIndex = cursor.getColumnIndex(COL_EVENT_TYPE);
                if (typeIndex != -1 && cursor.getString(typeIndex) != null)
                    eventType = cursor.getString(typeIndex);

                Event event = new Event(id, title, date, location, description,
                        status, ticketPrice, ticketQuantity, coverImagePath, galleryPaths, eventType);
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    // --- Ticket Operations ---
    public boolean registerTicket(int userId, int eventId, String uniqueCode) {
        return registerTicket(userId, eventId, uniqueCode, System.currentTimeMillis());
    }

    public boolean registerTicket(int userId, int eventId, String uniqueCode, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TICKET_USER_ID, userId);
        values.put(COL_TICKET_EVENT_ID, eventId);
        values.put(COL_TICKET_CODE, uniqueCode);
        values.put(COL_TICKET_TIMESTAMP, timestamp);
        long res = db.insert(TABLE_TICKETS, null, values);
        return res != -1;
    }

    public boolean registerTicket(String userUid, int eventId, String uniqueCode) {
        return registerTicket(userUid, eventId, uniqueCode, System.currentTimeMillis());
    }

    public boolean registerTicket(String userUid, int eventId, String uniqueCode, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TICKET_USER_UID, userUid);
        values.put(COL_TICKET_EVENT_ID, eventId);
        values.put(COL_TICKET_CODE, uniqueCode);
        values.put(COL_TICKET_TIMESTAMP, timestamp);
        long res = db.insert(TABLE_TICKETS, null, values);
        return res != -1;
    }

    public List<Ticket> getTicketsForUser(int userId) {
        List<Ticket> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t." + COL_TICKET_ID + ", t." + COL_TICKET_USER_ID + ", t." + COL_TICKET_EVENT_ID + ", t."
                + COL_TICKET_CODE + ", t." + COL_TICKET_TIMESTAMP +
                ", e." + COL_EVENT_TITLE + ", e." + COL_EVENT_DATE + ", e." + COL_EVENT_LOCATION +
                " FROM " + TABLE_TICKETS + " t " +
                "JOIN " + TABLE_EVENTS + " e ON t." + COL_TICKET_EVENT_ID + " = e." + COL_EVENT_ID +
                " WHERE t." + COL_TICKET_USER_ID + " = ?" +
                " ORDER BY t." + COL_TICKET_TIMESTAMP + " DESC";

        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId) });
        if (cursor.moveToFirst()) {
            do {
                long timestamp = 0;
                int timestampIndex = cursor.getColumnIndex(COL_TICKET_TIMESTAMP);
                if (timestampIndex != -1)
                    timestamp = cursor.getLong(timestampIndex);

                list.add(new Ticket(
                        String.valueOf(cursor.getInt(1)), // userId
                        String.valueOf(cursor.getInt(2)), // eventId
                        cursor.getString(3), // uniqueCode
                        timestamp,
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Ticket> getTicketsForUser(String userUid) {
        List<Ticket> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t." + COL_TICKET_ID + ", t." + COL_TICKET_USER_ID + ", t." + COL_TICKET_EVENT_ID + ", t."
                + COL_TICKET_CODE + ", t." + COL_TICKET_TIMESTAMP +
                ", e." + COL_EVENT_TITLE + ", e." + COL_EVENT_DATE + ", e." + COL_EVENT_LOCATION +
                " FROM " + TABLE_TICKETS + " t " +
                "JOIN " + TABLE_EVENTS + " e ON t." + COL_TICKET_EVENT_ID + " = e." + COL_EVENT_ID +
                " WHERE t." + COL_TICKET_USER_UID + " = ?" +
                " ORDER BY t." + COL_TICKET_TIMESTAMP + " DESC";

        Cursor cursor = db.rawQuery(query, new String[] { userUid });
        if (cursor.moveToFirst()) {
            do {
                long timestamp = 0;
                int timestampIndex = cursor.getColumnIndex(COL_TICKET_TIMESTAMP);
                if (timestampIndex != -1)
                    timestamp = cursor.getLong(timestampIndex);

                list.add(new Ticket(
                        String.valueOf(cursor.getInt(1)), // userId (int -> String)
                        String.valueOf(cursor.getInt(2)), // eventId (int -> String)
                        cursor.getString(3), // uniqueCode
                        timestamp,
                        cursor.getString(5), // title
                        cursor.getString(6), // date
                        cursor.getString(7))); // location
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

}
