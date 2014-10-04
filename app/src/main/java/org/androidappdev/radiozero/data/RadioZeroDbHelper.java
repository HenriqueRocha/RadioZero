package org.androidappdev.radiozero.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;

/**
 * @author Henrique Rocha
 */
public class RadioZeroDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "radiozero.db";

    private static final int DATABASE_VERSION = 1;

    public RadioZeroDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BLOG_TABLE = "CREATE TABLE " + BlogEntry.TABLE_NAME + " (" +
                BlogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BlogEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                BlogEntry.COLUMN_LINK + " TEXT NOT NULL, " +
                BlogEntry.COLUMN_PUBDATE + " TEXT NOT NULL, " +
                BlogEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL);";

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
