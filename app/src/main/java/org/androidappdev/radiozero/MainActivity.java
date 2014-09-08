package org.androidappdev.radiozero;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private View mFacebookLink;

        public PlaceholderFragment() {
        }

        /**
         * Open given url in Facebook app or browser if app is not installed.
         *
         * @param url url to be opened
         * @return an intent to open give url
         */
        private static Intent getOpenInFacebookIntent(Context context, String url) {
            Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            try {
                context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                resultIntent.setData(Uri.parse("fb://facewebmodal/f?href=" + url));
            } catch (PackageManager.NameNotFoundException e) {
                resultIntent.setData(Uri.parse(url));
            }
            return resultIntent;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mFacebookLink = rootView.findViewById(R.id.facebook_link);
            mFacebookLink.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.facebook_link) {
                startActivity(getOpenInFacebookIntent(
                        getActivity(), "http://www.facebook.com/radiozero"));
            }
        }

    }
}
