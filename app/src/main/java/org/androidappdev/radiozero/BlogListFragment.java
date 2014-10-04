package org.androidappdev.radiozero;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class BlogListFragment extends ListFragment implements
        MediaPlayer.OnPreparedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = BlogListFragment.class.getSimpleName();
    private final String[] PROJECTION = {
            BlogEntry._ID,
            BlogEntry.COLUMN_TITLE,
            BlogEntry.COLUMN_LINK,
            BlogEntry.COLUMN_PUBDATE,
            BlogEntry.COLUMN_DESCRIPTION
    };
    private final String[] FROM = {
            BlogEntry.COLUMN_TITLE,
            BlogEntry.COLUMN_LINK,
            BlogEntry.COLUMN_PUBDATE,
            BlogEntry.COLUMN_DESCRIPTION
    };
    private final int[] TO = {
            R.id.list_item_title_textview,
            R.id.list_item_link_textview,
            R.id.list_item_pubdate_textview,
            R.id.list_item_description_textview,
    };
    private MediaPlayer mMediaPlayer;
    private SimpleCursorAdapter mAdapter;
    private Callback mListener;

    public BlogListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement Callback");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String quality = prefs.getString(SettingsActivity.KEY_PREF_QUALITY, "1");

        String url = SettingsActivity.PREF_HIGH_QUALITY.equals(quality)
                ? "http://stream.radiozero.pt:8000/zero128.mp3"
                : "http://stream.radiozero.pt:8000/zero64.mp3";

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setOnPreparedListener(this);
//                mMediaPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_blog_entry,
                null,
                FROM,
                TO,
                0);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onStop();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mListener.onItemSelected(id);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                BlogEntry.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mAdapter.swapCursor(cursor);
        }

        if (getListAdapter() == null) {
            setListAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(long id);
    }
}
