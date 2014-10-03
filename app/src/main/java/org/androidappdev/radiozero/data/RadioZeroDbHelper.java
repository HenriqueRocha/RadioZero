package org.androidappdev.radiozero.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;

/**
 * Created by hmrocha on 10/3/14.
 */
public class RadioZeroDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "radiozero.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public RadioZeroDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_BLOG_TABLE = "CREATE TABLE " + BlogEntry.TABLE_NAME + " (" +
                BlogEntry.COLUMN_XML + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_BLOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BlogEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
