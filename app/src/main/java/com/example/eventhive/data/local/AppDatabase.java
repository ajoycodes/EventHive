package com.example.eventhive.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.eventhive.data.local.dao.EventDao;
import com.example.eventhive.data.local.dao.TicketDao;
import com.example.eventhive.data.local.dao.UserDao;
import com.example.eventhive.data.local.entities.EventEntity;
import com.example.eventhive.data.local.entities.TicketEntity;
import com.example.eventhive.data.local.entities.UserEntity;

/**
 * Room Database for EventHive application.
 * Singleton pattern ensures only one instance exists.
 */
@Database(entities = { UserEntity.class, EventEntity.class, TicketEntity.class }, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "EventHive.db";
    private static volatile AppDatabase INSTANCE;

    // DAO access methods
    public abstract UserDao userDao();

    public abstract EventDao eventDao();

    public abstract TicketDao ticketDao();

    /**
     * Gets the singleton instance of the database.
     * Creates it if it doesn't exist.
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                            .fallbackToDestructiveMigration() // Handle schema mismatches by recreating DB
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Migration from version 3 (old SQLite) to version 4 (Room).
     * Fixes: id column must be NOT NULL, and email must have unique index.
     */
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // SQLite doesn't support ALTER COLUMN, so we need to recreate the table

            // Step 1: Create new table with correct schema
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `users_new` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`first_name` TEXT, " +
                            "`last_name` TEXT, " +
                            "`email` TEXT, " +
                            "`password` TEXT, " +
                            "`role` TEXT, " +
                            "`phone` TEXT)");

            // Step 2: Copy data from old table (id will be preserved)
            database.execSQL(
                    "INSERT INTO `users_new` (id, first_name, last_name, email, password, role, phone) " +
                            "SELECT id, first_name, last_name, email, password, role, phone FROM `users`");

            // Step 3: Drop old table
            database.execSQL("DROP TABLE `users`");

            // Step 4: Rename new table
            database.execSQL("ALTER TABLE `users_new` RENAME TO `users`");

            // Step 5: Create unique index on email
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`)");
        }
    };

    /**
     * Migration to add timestamp column to events.
     */
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add timestamp column with default value 0
            database.execSQL("ALTER TABLE events ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 0");

            // Note: We cannot easily parse dates in SQL here because formats vary (12 Dec -
            // 10 PM)
            // The Repository layer will be responsible for updating timestamps
            // the first time the app runs with this new version
        }
    };

    /**
     * Callback to populate initial data when database is created.
     */
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Insert default admin user
            db.execSQL("INSERT INTO users (first_name, last_name, email, password, role, phone) " +
                    "VALUES ('Admin', 'User', 'admin@eventhive.com', 'admin123', 'Admin', '0000000000')");

            // Insert dummy events with parsed timestamps (approximate for demo)
            // 12 Dec -> 1733932800000 (roughly Dec 2024 if current is Dec 2024, or next
            // year)
            // For safety in this demo, passing 0, repository will fix it or we rely on
            // logic
            db.execSQL("INSERT INTO events (title, date, location, description, image_res_id, status, timestamp) " +
                    "VALUES ('WaveFest - Feel The Winter', '12 Dec - 10 PM', 'Bashundhara R/A, Dhaka', " +
                    "'A music event bringing people together with electric energy.', 0, 'Active', 1734000000000)");

            db.execSQL("INSERT INTO events (title, date, location, description, image_res_id, status, timestamp) " +
                    "VALUES ('Tech Summit 2024', '15 Jan - 9 AM', 'ICCB, Dhaka', " +
                    "'The biggest tech conference in the city.', 0, 'Active', 1736932800000)");
        }
    };
}
