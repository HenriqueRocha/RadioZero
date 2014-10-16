package org.androidappdev.radiozero;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.androidappdev.radiozero.sync.SyncAdapter;

/**
 * Entry point of the app.
 *
 * @author <a href="mailto:hmrocha@gmail.com">Henrique Rocha</a>
 */
public class MainActivity extends ActionBarActivity
        implements
        PlayFragment.OnPlayPressedListener,
        BlogListFragment.Callback,
        ActionBar.TabListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ViewPager mPager;
    private RadioZeroPagerAdapter mPagerAdapter;
    private boolean mTabletUi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabletUi = mPager == null;

        if (mTabletUi) { // We don't use a ViewPager in 10" tablet UI.

        } else {
            setupPhoneUi();
        }

        SyncAdapter.initializeSyncAdapter(this);
    }

    private void setupPhoneUi() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mPagerAdapter = new RadioZeroPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(long id) {
        if (mTabletUi) {
            BlogEntryFragment fragment = BlogEntryFragment.newInstance(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.blog_entry_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, BlogEntryActivity.class);
            intent.putExtra(BlogEntryActivity.ID_KEY, id);
            startActivity(intent);
        }
    }

    @Override
    public void onPlayPressed(int action) {
        switch (action) {
            case PlayFragment.PLAYING:
                startService(RadioZeroService.makeIntent(this, RadioZeroService.ACTION_PLAY));
                break;
            case PlayFragment.PAUSED:
                stopService(new Intent(this, RadioZeroService.class));
                break;
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    private class RadioZeroPagerAdapter extends FragmentStatePagerAdapter {
        public RadioZeroPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PlayFragment();
                case 1:
                    return new BlogListFragment();
                default:
                    return null; // Can't happen.
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Resources resources = getResources();
            switch (position) {
                case 0:
                    return resources.getString(R.string.play);
                case 1:
                    return resources.getString(R.string.blog);
                default:
                    return ""; // Can't happen.
            }
        }
    }
}