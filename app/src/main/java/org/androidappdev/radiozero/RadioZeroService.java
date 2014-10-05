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
 * Created by hmrocha on 10/5/14.
 */
public class RadioZeroService extends Service implements MediaPlayer.OnPreparedListener {
    public static final String ACTION_PLAY = "org.androidappdev.radiozero.action.PLAY";
    private static final String LOG_TAG = RadioZeroService.class.getSimpleName();
    private static final int STATE_IDLE = 0;
    private int mState = STATE_IDLE;
    private static final int STATE_PLAYING = 1;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_PLAY)) {
            processPlayRequest();
        }

        return START_NOT_STICKY;
    }

    private void processPlayRequest() {
        if (mState == STATE_PLAYING) return;

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
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mWifiLock.isHeld()) mWifiLock.release();
        stopForeground(true);
        super.onDestroy();
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
        String songName = "Yo!";
// assign the song name to songName
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification();
        notification.tickerText = "Radio Zero";
        notification.icon = R.drawable.ic_launcher;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "MusicPlayerSample",
                "Playing: " + songName, pi);
        startForeground(NOTIFICATION_ID, notification);
    }

}
