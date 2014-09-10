package org.androidappdev.radiozero;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;


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
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnPreparedListener {

        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
        private MediaPlayer mMediaPlayer;

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

            rootView.findViewById(R.id.facebook_link).setOnClickListener(this);
            rootView.findViewById(R.id.google_plus_link).setOnClickListener(this);
            rootView.findViewById(R.id.flickr_link).setOnClickListener(this);
            rootView.findViewById(R.id.radialx_link).setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            try {
                String url = "http://stream.radiozero.pt:8000/zero64.mp3";
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
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
        public void onClick(View view) {
            int id = view.getId();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            switch (id) {
                case R.id.facebook_link:
                    intent = getOpenInFacebookIntent(
                            getActivity(), "http://www.facebook.com/radiozero");
                    break;
                case R.id.google_plus_link:
                    intent.setData(Uri.parse("https://plus.google.com/113155695079240313645"));
                    break;
                case R.id.flickr_link:
                    intent.setData(Uri.parse("http://flickr.com/radiozero"));
                    break;
                case R.id.radialx_link:
                    intent.setData(Uri.parse("http://radialx.radiozero.pt"));
                    break;
            }
            startActivity(intent);
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
        }
    }
}
