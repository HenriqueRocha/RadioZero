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

    public static final String PATH_BLOG = "blog";

    public interface BlogEntry extends BaseColumns {
        Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLOG).build();
        String TABLE_NAME = "blog_entries";
        String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_BLOG;
        String COLUMN_TITLE = "title";
        String COLUMN_URL = "url";
        String COLUMN_PUBDATE = "pubDate";
        String COLUMN_ARTICLE = "article";
        String COLUMN_IMAGE = "image";

    }
}
