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
    private static final int DATABASE_VERSION = 4; // Incremented for new fields

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
    private static final String COL_TICKET_USER_ID = "user_id";
    private static final String COL_TICKET_EVENT_ID = "event_id";
    private static final String COL_TICKET_CODE = "unique_code";
    private static final String COL_TICKET_TIMESTAMP = "purchase_timestamp"; // New column

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
                COL_TICKET_EVENT_ID + " INTEGER, " +
                COL_TICKET_CODE + " TEXT, " +
                COL_TICKET_TIMESTAMP + " INTEGER DEFAULT 0)";
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
        // Handle upgrade from version 3 to 4
        if (oldVersion < 4) {
            // Add new columns to events table
            try {
                db.execSQL(
                        "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_TICKET_PRICE + " REAL DEFAULT 0.0");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_TICKET_QUANTITY
                        + " INTEGER DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_COVER_IMAGE_PATH + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_GALLERY_PATHS + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COL_EVENT_TYPE + " TEXT DEFAULT 'Other'");

                // Add new column to tickets table
                db.execSQL(
                        "ALTER TABLE " + TABLE_TICKETS + " ADD COLUMN " + COL_TICKET_TIMESTAMP + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                android.util.Log.e("DatabaseHelper", "Error during migration: " + e.getMessage());
                // If migration fails, fall back to recreating tables (data loss)
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
                onCreate(db);
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
        values.put(COL_EVENT_IMAGE, 0); // Legacy placeholder
        values.put(COL_EVENT_STATUS, event.getStatus());
        // New fields
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
        // Update new fields if needed
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
                // Read basic fields
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EVENT_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_LOCATION));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DESC));

                // Read status
                String status = "Active"; // Default
                int statusIndex = cursor.getColumnIndex(COL_EVENT_STATUS);
                if (statusIndex != -1 && cursor.getString(statusIndex) != null) {
                    status = cursor.getString(statusIndex);
                }

                // Read new fields with safe column index checking
                double ticketPrice = 0.0;
                int priceIndex = cursor.getColumnIndex(COL_EVENT_TICKET_PRICE);
                if (priceIndex != -1) {
                    ticketPrice = cursor.getDouble(priceIndex);
                }

                int ticketQuantity = 0;
                int quantityIndex = cursor.getColumnIndex(COL_EVENT_TICKET_QUANTITY);
                if (quantityIndex != -1) {
                    ticketQuantity = cursor.getInt(quantityIndex);
                }

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
                if (typeIndex != -1 && cursor.getString(typeIndex) != null) {
                    eventType = cursor.getString(typeIndex);
                }

                // Create event with comprehensive constructor
                Event event = new Event(id, title, date, location, description,
                        status, ticketPrice, ticketQuantity,
                        coverImagePath, galleryPaths, eventType);
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    } // --- Ticket Operations ---

    public boolean registerTicket(int userId, int eventId, String uniqueCode) {
        return registerTicket(userId, eventId, uniqueCode, System.currentTimeMillis());
    }

    // Overloaded method with timestamp
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

    public List<com.example.eventhive.models.Ticket> getTicketsForUser(int userId) {
        List<com.example.eventhive.models.Ticket> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t." + COL_TICKET_ID + ", t." + COL_TICKET_USER_ID + ", t." + COL_TICKET_EVENT_ID + ", t."
                + COL_TICKET_CODE + ", t." + COL_TICKET_TIMESTAMP +
                ", e." + COL_EVENT_TITLE + ", e." + COL_EVENT_DATE + ", e." + COL_EVENT_LOCATION +
                " FROM " + TABLE_TICKETS + " t " +
                "JOIN " + TABLE_EVENTS + " e ON t." + COL_TICKET_EVENT_ID + " = e." + COL_EVENT_ID +
                " WHERE t." + COL_TICKET_USER_ID + " = ?" +
                " ORDER BY t." + COL_TICKET_TIMESTAMP + " DESC"; // Sort by newest first

        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId) });
        if (cursor.moveToFirst()) {
            do {
                // Read timestamp with safe column checking
                long timestamp = 0;
                int timestampIndex = cursor.getColumnIndex(COL_TICKET_TIMESTAMP);
                if (timestampIndex != -1) {
                    timestamp = cursor.getLong(timestampIndex);
                }

                list.add(new com.example.eventhive.models.Ticket(
                        cursor.getInt(0), // ticket id
                        cursor.getInt(1), // user id
                        cursor.getInt(2), // event id
                        cursor.getString(3), // code
                        timestamp, // timestamp
                        cursor.getString(5), // title (index 4 is timestamp, so event data starts at 5)
                        cursor.getString(6), // date
                        cursor.getString(7) // location
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
