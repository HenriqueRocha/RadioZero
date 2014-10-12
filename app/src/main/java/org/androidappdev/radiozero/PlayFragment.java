package org.androidappdev.radiozero;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link org.androidappdev.radiozero.PlayFragment.OnPlayPressedListener} interface
 * to handle interaction events.
 */
public class PlayFragment extends Fragment implements View.OnClickListener {

    public static final int PAUSED = 0;
    public static final int PLAYING = 1;
    private static final String LOG_TAG = PlayFragment.class.getSimpleName();
    private static final String STATE_KEY = "state";
    private OnPlayPressedListener mListener;
    private ImageButton mPlayButton;
    private int mState;

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mState = savedInstanceState.getInt(STATE_KEY);
        }

        View view = inflater.inflate(R.layout.fragment_play, container, false);

        mPlayButton = (ImageButton) view.findViewById(R.id.play_button);

        Resources resources = getResources();
        mPlayButton.setImageDrawable(mState == PAUSED
                ? resources.getDrawable(R.drawable.ic_action_play)
                : resources.getDrawable(R.drawable.ic_action_pause));

        mPlayButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPlayPressedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnPlayPressedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_KEY, mState);
    }

    @Override
    public void onClick(View view) {
        if (view == mPlayButton) {
            Log.i(LOG_TAG, "STATE: " + mState);
            switch (mState) {
                case PLAYING:
                    mState = PAUSED;
                    mListener.onPlayPressed(PAUSED);
                    break;
                case PAUSED:
                    mState = PLAYING;
                    mListener.onPlayPressed(PLAYING);
                    break;
            }
            switchPlayButtonState();
        }
    }

    private void switchPlayButtonState() {
        Resources resources = getResources();
        mPlayButton.setImageDrawable(mState == PAUSED
                ? resources.getDrawable(R.drawable.ic_action_play)
                : resources.getDrawable(R.drawable.ic_action_pause));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnPlayPressedListener {
        public void onPlayPressed(int action);
    }

}
