package org.androidappdev.radiozero;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class BlogListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

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
        getActivity().startService(new Intent(RadioZeroService.ACTION_PLAY));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity(), RadioZeroService.class));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mListener.onItemSelected(id);
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
