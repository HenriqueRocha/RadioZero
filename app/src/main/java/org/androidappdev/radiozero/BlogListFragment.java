package org.androidappdev.radiozero;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment that displays a list of blog entries.
 *
 * @author <a href="mailto:hmrocha@gmail.com">Henrique Rocha</a>
 */
public class BlogListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, SimpleCursorAdapter.ViewBinder {

    private static final String LOG_TAG = BlogListFragment.class.getSimpleName();

    private static final String LISTVIEW_STATE_KEY = "listview_state";

    private final String[] PROJECTION = {
            BlogEntry._ID,
            BlogEntry.COLUMN_TITLE,
            BlogEntry.COLUMN_PUBDATE,
            BlogEntry.COLUMN_IMAGE
    };
    private final String[] FROM = {
            BlogEntry.COLUMN_TITLE,
            BlogEntry.COLUMN_PUBDATE,
            BlogEntry.COLUMN_IMAGE
    };
    private final int[] TO = {
            R.id.list_item_title_textview,
            R.id.list_item_pubdate_textview,
            R.id.list_item_imageview,
    };
    private SimpleCursorAdapter mAdapter;
    private Callback mListener;
    private Parcelable mListViewState;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(LISTVIEW_STATE_KEY)) {
            mListViewState = savedInstanceState.getParcelable(LISTVIEW_STATE_KEY);
        }

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_blog_entry,
                null,
                FROM,
                TO,
                0);
        mAdapter.setViewBinder(this);
//        getActivity().startService(new Intent(RadioZeroService.ACTION_PLAY));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ListView listView = getListView();
        if (listView != null) {
            outState.putParcelable(LISTVIEW_STATE_KEY, listView.onSaveInstanceState());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().stopService(new Intent(getActivity(), RadioZeroService.class));
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

        if (mListViewState != null) {
            getListView().onRestoreInstanceState(mListViewState);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (view.getId()) {
            case R.id.list_item_imageview:
                Picasso
                        .with(getActivity())
                        .load(cursor.getString(columnIndex))
                        .fit()
                        .centerCrop()
                        .into((ImageView) view);
                return true;
            case R.id.list_item_pubdate_textview:
                String date = cursor.getString(columnIndex);
                try {
                    SimpleDateFormat d =
                            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ", Locale.getDefault());
                    ((TextView) view).setText(
                            DateUtils.getRelativeTimeSpanString(
                                    getActivity(), d.parse(date).getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return false;
        }
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
