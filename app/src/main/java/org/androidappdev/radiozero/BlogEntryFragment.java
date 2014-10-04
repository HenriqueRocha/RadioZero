package org.androidappdev.radiozero;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlogEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlogEntryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_ID = "id";

    private static final String[] PROJECTION = new String[]{
            BlogEntry.COLUMN_TITLE,
            BlogEntry.COLUMN_LINK,
            BlogEntry.COLUMN_PUBDATE,
            BlogEntry.COLUMN_DESCRIPTION
    };

    private long mId;
    private TextView mTitle;
    private TextView mLink;
    private TextView mPubDate;
    private TextView mDescription;

    public BlogEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id entry id
     * @return A new instance of fragment BlogEntryFragment.
     */
    public static BlogEntryFragment newInstance(long id) {
        BlogEntryFragment fragment = new BlogEntryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_entry, container, false);
        mTitle = (TextView) view.findViewById(R.id.detail_title_textview);
        mLink = (TextView) view.findViewById(R.id.detail_link_textview);
        mPubDate = (TextView) view.findViewById(R.id.detail_pubdate_textview);
        mDescription = (TextView) view.findViewById(R.id.detail_description_textview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                BlogEntry.CONTENT_URI,
                PROJECTION,
                BlogEntry._ID + "=" + mId,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(BlogEntry.COLUMN_TITLE);
            mTitle.setText(cursor.getString(index));
            index = cursor.getColumnIndex(BlogEntry.COLUMN_LINK);
            mLink.setText(cursor.getString(index));
            index = cursor.getColumnIndex(BlogEntry.COLUMN_PUBDATE);
            mPubDate.setText(cursor.getString(index));
            index = cursor.getColumnIndex(BlogEntry.COLUMN_DESCRIPTION);
            mDescription.setText(cursor.getString(index));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Nothing to do.
    }
}
