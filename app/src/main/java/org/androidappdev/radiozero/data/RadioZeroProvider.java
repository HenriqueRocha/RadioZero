package org.androidappdev.radiozero.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/*
 * Define an implementation of ContentProvider that stubs out
 * all methods
 */
public class RadioZeroProvider extends ContentProvider {
    private static final int BLOG = 100;
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RadioZeroDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RadioZeroContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, RadioZeroContract.PATH_BLOG, BLOG);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RadioZeroDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BLOG:
                return RadioZeroContract.BlogEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case BLOG:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RadioZeroContract.BlogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BLOG:
                db.insert(RadioZeroContract.BlogEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    /*
     * delete() always returns "no rows affected" (0)
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /*
     * update() always returns "no rows affected" (0)
     */

    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        return 0;
    }
}
