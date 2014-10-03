package org.androidappdev.radiozero.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the radio zero database.
 *
 * @author Henrique Rocha
 */
public class RadioZeroContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "org.androidappdev.radiozero";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_BLOG = "blog_entries";

    public interface BlogEntry extends BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLOG).build();
        String TABLE_NAME = "blog_entries";
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_BLOG;
        String COLUMN_XML = "xml";

    }
}
