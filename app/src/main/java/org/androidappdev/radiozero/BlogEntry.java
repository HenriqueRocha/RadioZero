package org.androidappdev.radiozero;

import android.content.ContentValues;

import org.androidappdev.radiozero.data.RadioZeroContract;

/**
 * @author <a href="mailto:hmrocha@gmail.com">Henrique Rocha</a>
 */
public class BlogEntry {
    private String title;
    private String link;
    private String pubDate;
    private String content;
    private String image;

    public BlogEntry(String title, String link, String pubDate, String content) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.content = content;
        extractImageFromContent();
    }

    private void extractImageFromContent() {
        if (content.contains("<img ")) {
            String img = content.substring(content.indexOf("<img "));
            String cleanUp = img.substring(0, img.indexOf(">") + 1);
            img = img.substring(img.indexOf("src=") + 5);
            int indexOf = img.indexOf("'");
            if (indexOf == -1) {
                indexOf = img.indexOf("\"");
            }
            img = img.substring(0, indexOf);

            this.image = img;

            this.content = this.content.replace(cleanUp, "");
        }
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RadioZeroContract.BlogEntry.COLUMN_TITLE, this.title);
        contentValues.put(RadioZeroContract.BlogEntry.COLUMN_URL, this.link);
        contentValues.put(RadioZeroContract.BlogEntry.COLUMN_PUBDATE, this.pubDate);
        contentValues.put(RadioZeroContract.BlogEntry.COLUMN_ARTICLE, this.content);
        contentValues.put(RadioZeroContract.BlogEntry.COLUMN_IMAGE, this.image);
        return contentValues;
    }
}
