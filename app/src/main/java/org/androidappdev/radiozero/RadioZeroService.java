package org.androidappdev.radiozero;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

/**
 * Service that streams the radio.
 *
 * @author <a href="mailto:hmrocha@gmail.com">Henrique Rocha</a>
 */
public class RadioZeroService extends Service implements MediaPlayer.OnPreparedListener {
    public static final String ACTION_PLAY = "org.androidappdev.radiozero.action.PLAYING";
    public static final String ACTION_PAUSE = "org.androidappdev.radiozero.action.PAUSED";
    private static final String LOG_TAG = RadioZeroService.class.getSimpleName();
    private static final int STATE_IDLE = 0;
    private int mState = STATE_IDLE;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    /**
     * The ID we use for the notification (the onscreen alert that appears at the notification
     * area at the top of the screen as an icon -- and as text as well if the user expands the
     * notification area).
     */
    private static final int NOTIFICATION_ID = 1;
    private MediaPlayer mMediaPlayer;
    /**
     * Wifi lock that we hold when streaming files from the internet, in order to prevent the
     * device from shutting off the Wifi radio.
     */
    private WifiManager.WifiLock mWifiLock;

    public static Intent makeIntent(Context context, String action) {
        Intent intent = new Intent(context, RadioZeroService.class);
        intent.setAction(action);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_PLAY:
                processPlayRequest();
                break;
            case ACTION_PAUSE:
                processPauseRequest();
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        cleanUp();
        super.onDestroy();
    }

    private void processPauseRequest() {
        if (mState == STATE_IDLE) return;

        updateStateInNotification(STATE_PREPARING);

        cleanUp();
    }

    private void cleanUp() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mState = STATE_IDLE;
        }
        if (mWifiLock.isHeld()) mWifiLock.release();
        stopForeground(true);
        stopSelf();
    }

    private void processPlayRequest() {
        if (mState == STATE_PLAYING) return;

        if (mState == STATE_IDLE) {
            updateStateInNotification(STATE_PREPARING);
        }

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String quality = prefs.getString(SettingsActivity.KEY_PREF_QUALITY, "1");

        String url = SettingsActivity.PREF_HIGH_QUALITY.equals(quality)
                ? "http://stream.radiozero.pt:8000/zero128.mp3"
                : "http://stream.radiozero.pt:8000/zero64.mp3";

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

        mState = STATE_PLAYING;
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        mWifiLock.acquire();

        updateStateInNotification(mState);
    }

    private void updateStateInNotification(int state) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification();
        notification.tickerText = getString(R.string.app_name);
        // TODO: This should not be a color icon, it should follow the guidelines.
        // If I actually publish this app I have to learn how to create some icons.
        notification.icon = R.drawable.ic_launcher;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        // TODO: This was taken from the offical docs. Find non deprecated replacement if
        // I actually get to publish this.
        notification.setLatestEventInfo(
                getApplicationContext(),
                getString(R.string.app_name) /* contentTitle */,
                getStringForState(state) /* contentText */,
                pendingIntent);

        startForeground(NOTIFICATION_ID, notification);
    }

    private CharSequence getStringForState(int state) {
        switch (state) {
            case STATE_PREPARING:
                return getString(R.string.state_preparing);
            case STATE_PLAYING:
                return getString(R.string.state_playing);
            default:
                return null; // can't happen
        }
    }
}
