package org.androidappdev.radiozero;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Fragment that display "About" information like the manifesto and some social network links.
 *
 * @author <a href="mailto:hmrocha@gmail.com">Henrique Rocha</a>
 */
public class AboutFragment extends Fragment implements View.OnClickListener {


    public AboutFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        // TODO Work on this later if I publish the app.

//        rootView.findViewById(R.id.facebook_link).setOnClickListener(this);
//        rootView.findViewById(R.id.google_plus_link).setOnClickListener(this);
//        rootView.findViewById(R.id.flickr_link).setOnClickListener(this);
//        rootView.findViewById(R.id.radialx_link).setOnClickListener(this);

        return rootView;
    }

    // TODO Work on this later if I publish the app.

    @Override
    public void onClick(View view) {
//        int id = view.getId();
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        switch (id) {
//            case R.id.facebook_link:
//                intent = getOpenInFacebookIntent(
//                        getActivity(), "http://www.facebook.com/radiozero");
//                break;
//            case R.id.google_plus_link:
//                intent.setData(Uri.parse("https://plus.google.com/113155695079240313645"));
//                break;
//            case R.id.flickr_link:
//                intent.setData(Uri.parse("http://flickr.com/radiozero"));
//                break;
//            case R.id.radialx_link:
//                intent.setData(Uri.parse("http://radialx.radiozero.pt"));
//                break;
//        }
//        startActivity(intent);
    }

}
