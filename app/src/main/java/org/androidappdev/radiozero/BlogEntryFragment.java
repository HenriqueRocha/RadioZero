package org.androidappdev.radiozero;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Fragment that displays a blog entry.
 * Use the {@link BlogEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author <a href="mailto:hmrocha@gmail.com">Henrique Rocha</a>
 */
public class BlogEntryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = BlogEntryFragment.class.getSimpleName();

    private static final String ARG_ID = "id";

    private static final String[] PROJECTION = new String[]{
            BlogEntry.COLUMN_TITLE,
            BlogEntry.COLUMN_URL,
            BlogEntry.COLUMN_PUBDATE,
            BlogEntry.COLUMN_ARTICLE,
            BlogEntry.COLUMN_IMAGE
    };

    private long mId;
    private TextView mTitle;
    private TextView mPubDate;
    private TextView mArticle;
    private ImageView mImage;
    private String mUrl;
    private ShareActionProvider mShareActionProvider;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_entry, container, false);
        mTitle = (TextView) view.findViewById(R.id.detail_title_textview);
        mPubDate = (TextView) view.findViewById(R.id.detail_pubdate_textview);
        mArticle = (TextView) view.findViewById(R.id.detail_article_textview);
        mImage = (ImageView) view.findViewById(R.id.detail_imageview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().supportInvalidateOptionsMenu();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.blog_entry_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mUrl != null) {
            mShareActionProvider.setShareIntent(createShareArticleIntent());
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(BlogEntry.COLUMN_TITLE);
            mTitle.setText(cursor.getString(index));
            index = cursor.getColumnIndex(BlogEntry.COLUMN_URL);
            mUrl = cursor.getString(index);
            index = cursor.getColumnIndex(BlogEntry.COLUMN_PUBDATE);
            String date = cursor.getString(index);
            SimpleDateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ", Locale.getDefault());
            try {
                mPubDate.setText(DateUtils.getRelativeTimeSpanString(getActivity(), d.parse(date).getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            index = cursor.getColumnIndex(BlogEntry.COLUMN_ARTICLE);
            mArticle.setText(Html.fromHtml(cursor.getString(index)));
            mArticle.setMovementMethod(LinkMovementMethod.getInstance());
            index = cursor.getColumnIndex(BlogEntry.COLUMN_IMAGE);
            Picasso.with(getActivity()).load(cursor.getString(index)).into(mImage);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareArticleIntent());
            }
        }
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
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Nothing to do.
    }

    private Intent createShareArticleIntent() {
        Intent shareIntent = new Intent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
        shareIntent.setType("text/plain");
        return shareIntent;
    }
}
