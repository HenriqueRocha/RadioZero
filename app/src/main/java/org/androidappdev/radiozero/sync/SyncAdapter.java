package org.androidappdev.radiozero.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.DateUtils;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidappdev.radiozero.R;
import org.androidappdev.radiozero.RadioZeroXmlParser;
import org.androidappdev.radiozero.data.RadioZeroContract.BlogEntry;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * Created by hmrocha on 10/3/14.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final long SYNC_INTERVAL = DateUtils.DAY_IN_MILLIS;
    public static final long SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, long syncInterval, long flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // we can enable inexact timers in our periodic sync
//            SyncRequest request = new SyncRequest.Builder().
//                    syncPeriodic(syncInterval, flexTime).
//                    setSyncAdapter(account, authority).build();
//            ContentResolver.requestSync(request);
//        } else {
        ContentResolver.addPeriodicSync(account,
                authority, new Bundle(), syncInterval);
//        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle bundle,
            String s,
            ContentProviderClient contentProviderClient,
            SyncResult syncResult) {

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://www.radiozero.pt/feed/")
                    .build();
            Response response = client.newCall(request).execute();
            RadioZeroXmlParser parser = new RadioZeroXmlParser();
            List<RadioZeroXmlParser.Item> items = parser.parse(response.body().byteStream());
            response.body().byteStream().close();
            Log.d(LOG_TAG, "items: " + items.size());
            contentProviderClient.delete(BlogEntry.CONTENT_URI, null, null);

            ContentValues[] values = new ContentValues[items.size()];
            int i = 0;
            for (RadioZeroXmlParser.Item item : items) {
                values[i] = new ContentValues();
                values[i].put(BlogEntry.COLUMN_TITLE, item.title);
                values[i].put(BlogEntry.COLUMN_LINK, item.link);
                values[i].put(BlogEntry.COLUMN_PUBDATE, item.pubDate);
                values[i].put(BlogEntry.COLUMN_DESCRIPTION, item.description);
                contentProviderClient.insert(BlogEntry.CONTENT_URI, values[i]);
                i++;
            }

            // We don't need old cached values.
//            contentProviderClient.delete(BlogEntry.CONTENT_URI, null, null);

//            contentProviderClient.bulkInsert(BlogEntry.CONTENT_URI, values);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
