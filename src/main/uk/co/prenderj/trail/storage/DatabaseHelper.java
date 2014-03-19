package uk.co.prenderj.trail.storage;

import uk.co.prenderj.trail.activity.MainActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Represents the internal SQLite database. Not to be confused with
 * the web server database.
 * @author Joshua Prendergast
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "trail.db";
    private static final int DATABASE_VERSION = 3;
    
    public DatabaseHelper() {
        super(MainActivity.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
        
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create comments table with 5 fields
        db.execSQL("CREATE TABLE comments " +
                "(_id INTEGER PRIMARY KEY, lat DOUBLE NOT NULL," +
                " lng DOUBLE NOT NULL, body VARCHAR(255), attachmentId INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Database upgrade (" + oldVersion + " to " + newVersion + "), dropping all data");
        db.execSQL("DROP TABLE IF EXISTS comments");
        onCreate(db);
    }

}