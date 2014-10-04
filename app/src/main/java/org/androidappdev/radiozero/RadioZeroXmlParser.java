package org.androidappdev.radiozero;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Parser for the RSS feed from http://www.radiozero.pt/feed/
 * Code based on https://developer.android.com/training/basics/network-ops/xml.html
 *
 * @author Henrique Rocha hmrocha@gmail.com
 */
public class RadioZeroXmlParser {
    private static final String LOG_TAG = RadioZeroXmlParser.class.getSimpleName();

    private static final String sNs = null;

    private static final String TAG_CHANNEL = "channel";
    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_PUBDATE = "pubDate";
    private static final String TAG_DESCRIPTION = "description";

    /**
     * Parse the XML in the given input stream.
     *
     * @param in input stream to read the XML from
     * @return a list with all the entries of the feed.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<Item> parse(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readFeed(parser);
    }

    private List<Item> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Item> result = null;
        parser.require(XmlPullParser.START_TAG, sNs, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("channel")) {
                result = readChannel(parser);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private List<Item> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Item> entries = new LinkedList<Item>();

        parser.require(XmlPullParser.START_TAG, sNs, TAG_CHANNEL);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (TAG_ITEM.equals(name)) {
                entries.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, sNs, TAG_ITEM);
        String title = null;
        String link = null;
        String pubDate = null;
        String description = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (TAG_TITLE.equals(name)) {
                title = readTitle(parser);
            } else if (TAG_LINK.equals(name)) {
                link = readLink(parser);
            } else if (TAG_PUBDATE.equals(name)) {
                pubDate = readPubDate(parser);
            } else if (TAG_DESCRIPTION.equals(name)) {
                description = readDescription(parser);
            } else {
                skip(parser);
            }
        }
        return new Item(title, link, pubDate, description);
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, sNs, TAG_TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, sNs, TAG_TITLE);
        return title;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, sNs, TAG_LINK);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, sNs, TAG_LINK);
        return title;
    }

    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, sNs, TAG_PUBDATE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, sNs, TAG_PUBDATE);
        return title;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, sNs, TAG_DESCRIPTION);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, sNs, TAG_DESCRIPTION);
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     * RSS feed item.
     */
    public static class Item {
        public String title;
        public String link;
        public String pubDate;
        public String description;

        public Item(String title, String link, String pubDate, String description) {
            this.title = title;
            this.link = link;
            this.pubDate = pubDate;
            this.description = description;
        }
    }
}
