package com.tiberiugaspar.mylauncher.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.adapter.AppListAdapter;
import com.tiberiugaspar.mylauncher.database.AppDao;
import com.tiberiugaspar.mylauncher.model.AppInfo;
import com.tiberiugaspar.mylauncher.news.NewsFragment;
import com.tiberiugaspar.mylauncher.settings.SettingsFragment;
import com.tiberiugaspar.mylauncher.util.AppInfoUtil;
import com.tiberiugaspar.mylauncher.util.SettingsUtil;
import com.tiberiugaspar.mylauncher.util.WrapContentGridLayoutManager;

import static com.tiberiugaspar.mylauncher.util.SettingsUtil.SHARED_PREFERENCES_APP_GRID_LAYOUT_COLUMNS;
import static com.tiberiugaspar.mylauncher.util.SettingsUtil.SHARED_PREFERENCES_APP_GRID_LAYOUT_ROWS;
import static com.tiberiugaspar.mylauncher.util.SettingsUtil.getAppGridSize;

public class MainPagerActivity extends FragmentActivity {

    private LinearLayout homeScreenLayout;
    private ViewPager2 viewPager;
    private ImageView appPhone, appMessages, appDrawer, appBrowser, appCamera;
    private String phonePackage, smsPackage, browserPackage, cameraPackage;
    private LinearLayout dockLayout;

    private NestedScrollView appDrawerLayout;
    private RecyclerView appDrawerRecycler;
    private AppListAdapter appListAdapter;

    private Window window;

    /*
     * pageChangeCallback is used to interact with the user's swiping gestures, to show or hide
     * different views, according to the current page (the news fragment, home screen pages or
     * settings fragment)
     */
    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

            //if position (number of current page) is 0, that means the user is on the News Fragment,
            // therefore we need to hide the bottom appDock layout and to set the StatusBar and NavigationBar
            // to be translucent, using the method addTranslucentWindowFlags()
            //
            //if position is getNumberOfPages(getApplicationContext()) + magic_number - 1,
            //that means the user is on the Settings fragment and we'll proceed exactly the same as
            // in the News Fragment case
            if (position == 0
                    || (position == (AppInfoUtil.getLastHomeScreenAppPage(getApplicationContext())) +
                    getResources().getInteger(R.integer.view_pager_magic_number) - 1)) {

                Animation animation = AnimationUtils.loadAnimation(MainPagerActivity.this, android.R.anim.fade_out);
                dockLayout.startAnimation(animation);
                dockLayout.setVisibility(View.GONE);

                addTranslucentWindowFlags();

            } else {

                //if current position is not the first or the last page,
                // we check whether the navigationBarColor is transparent or not.
                //If so, it means the user swiped from position 0 or last, which means we need to clear
                //the translucent flags we added for the NavigationBar and StatusBar and to show the bottom
                //AppDock layout
                if (window.getNavigationBarColor()
                        == getResources().getColor(android.R.color.transparent, null)) {

                    Animation animation = AnimationUtils.loadAnimation(MainPagerActivity.this, android.R.anim.fade_in);
                    dockLayout.startAnimation(animation);
                    dockLayout.setVisibility(View.VISIBLE);

                    clearTranslucentWindowFlags();
                }
            }
        }
    };
    private AppDao appDao;
    private BroadcastReceiver packageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Triggered when a package is added, changed or removed
            if (intent.getAction() != null) {

                String packageName = intent.getData().toString();
                packageName = packageName.replace("package:", "");

                Log.d("MainPagerActivity", "onReceive: " + packageName);

                AppInfo appInfo = null;

                try {

                    appInfo = AppInfoUtil.getAppInfoFromPackageName(MainPagerActivity.this, packageName);

                } catch (PackageManager.NameNotFoundException e) {

                    e.printStackTrace();

                }

                if (appInfo != null) {
                    switch (intent.getAction()) {
                        case Intent.ACTION_PACKAGE_ADDED:

//                            appDao.insertAppInfo(appInfo);
                            Toast.makeText(context, appInfo.getLabel() + " installed", Toast.LENGTH_SHORT).show();
                            appListAdapter.addAppFromActivity(appInfo);

                            break;

                        case Intent.ACTION_PACKAGE_CHANGED:

                            appDao.updateAppInfo(appInfo);
                            appListAdapter.updateApp(appInfo);
                            break;

                        default:
                            break;
                    }

                } else {

                    appInfo = new AppInfo();
                    appInfo.setPackageName(packageName);

                    appDao.deleteAppInfo(appInfo);
                    appListAdapter.removeApp(appInfo);

                    Toast.makeText(context, packageName + " uninstalled", Toast.LENGTH_LONG).show();

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_pager);

        window = getWindow();

        appDao = new AppDao(this);

        findViewsById();
        configureDockDrawables();
        configureDockItemsListeners();

        int columns = SettingsUtil.getAppGridSize(this, SHARED_PREFERENCES_APP_GRID_LAYOUT_COLUMNS);

        appListAdapter = new AppListAdapter(this, true, null);
        appDrawerRecycler.setLayoutManager(new WrapContentGridLayoutManager(this, columns));
        appDrawerRecycler.setAdapter(appListAdapter);
        appDrawerLayout.setClickable(false);

        registerForContextMenu(appDrawerRecycler);

        FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1, false);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                });
        mediator.attach();


        //Register packageReceiver to listen to all the packages added or removed
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");

        registerReceiver(packageReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register the viewPager's onPageChangeCallback so it can listen to page changes
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister viewPager's onPageChangeCallback
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //As opposite to registerReceiver on the onCreate method, we need to unregister the receiver
        //in the onDestroy method
        unregisterReceiver(packageReceiver);
    }


    @Override
    public void onBackPressed() {

        //if the back button is pressed while the user is in AppDrawer layout, we'll hide the drawer
        if (appDrawerLayout.getVisibility() == View.VISIBLE) {

            hideAppDrawer();

        } else if (viewPager.getCurrentItem() == 0
                || viewPager.getCurrentItem() == (AppInfoUtil.getLastHomeScreenAppPage(getApplicationContext()) +
                getResources().getInteger(R.integer.view_pager_magic_number) - 1)) {

            //if the back button is pressed while the user is in the NewsFragment or SettingsFragment,
            // we'll send him back to the first app page
            viewPager.setCurrentItem(1, true);

        } else {

            //if none of the above, back button works as expected
            super.onBackPressed();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_app_drawer_item, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        //get the current app info from adapter
        AppInfo appInfo = appListAdapter.getSelectedApp();

        switch (item.getItemId()) {
            case R.id.menu_app_info:

                //start settings activity with application details

                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + appInfo.getPackageName()));

                startActivity(i);

                return true;

            case R.id.menu_add_to_homescreen:

                //insert app in DB and add it to the app list on the home screen
                appInfo.setOnHomeScreen(true);

                int lastPosition = AppInfoUtil.getLastAppPosition(this);

                int maxPosition = SettingsUtil.getAppGridSize(this, SHARED_PREFERENCES_APP_GRID_LAYOUT_ROWS)
                        * getAppGridSize(this, SHARED_PREFERENCES_APP_GRID_LAYOUT_COLUMNS);
                if (lastPosition < (maxPosition - 1)) {

                    appInfo.setPosition(lastPosition + 1);
                    appInfo.setPageNumber(AppInfoUtil.getLastHomeScreenAppPage(this));
                } else {

                    appInfo.setPosition(0);
                    appInfo.setPageNumber(AppInfoUtil.getLastHomeScreenAppPage(this) + 1);
                    AppInfoUtil.setLastHomeScreenAppPage(this, appInfo.getPageNumber());
                }

                AppInfoUtil.setLastAppPosition(this, appInfo.getPosition());

                appDao.insertAppInfo(appInfo);

                Toast.makeText(this, "Application added to home screen", Toast.LENGTH_SHORT).show();

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * This method is used to start an activity for a given intent,
     * using the slide up animation for the starting activity
     * and fade out animation for this activity
     *
     * @param intent is the intent of the package that contains the activity that should be initiated;
     */
    private void animateStartActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up, android.R.anim.fade_out);
    }

    /**
     * This method is used to start an activity for a given package name.
     * It initialises an intent using the packageManager's getLaunchIntentForPackage() method
     * then calls the {@link #animateStartActivity(Intent)} method, declared above
     *
     * @param packageName is the name of the package that should be initiated;
     * @see #animateStartActivity(Intent)
     */
    private void animateStartActivity(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        animateStartActivity(intent);
    }

    private void findViewsById() {
        homeScreenLayout = findViewById(R.id.layout_home);
        viewPager = findViewById(R.id.view_pager);
        appPhone = findViewById(R.id.app_phone);
        appMessages = findViewById(R.id.app_messages);
        appDrawer = findViewById(R.id.app_drawer);
        appBrowser = findViewById(R.id.app_browser);
        appCamera = findViewById(R.id.app_camera);
        dockLayout = findViewById(R.id.layout_home_apps);

        appDrawerLayout = findViewById(R.id.layout_drawer);
        appDrawerRecycler = findViewById(R.id.app_list);
    }

    private void configureDockItemsListeners() {

        appDrawer.setOnClickListener(v -> showAppDrawer());

        appPhone.setOnClickListener(v -> animateStartActivity(phonePackage));

        appMessages.setOnClickListener(v -> animateStartActivity(smsPackage));

        appBrowser.setOnClickListener(v -> animateStartActivity(browserPackage));

        appCamera.setOnClickListener(v -> animateStartActivity(cameraPackage));
    }

    /**
     * Shows the AppDrawer layout and hides the HomeScreen layout, using proper animations
     * then adds the translucent window flags by calling {@link #addTranslucentWindowFlags()}
     * Opposite of {@link #hideAppDrawer()}
     *
     * @see #addTranslucentWindowFlags();
     * @see #hideAppDrawer()
     */
    private void showAppDrawer() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        appDrawerLayout.startAnimation(slideUp);
        appDrawerLayout.postOnAnimation(() -> {
            appDrawerLayout.setVisibility(View.VISIBLE);
            homeScreenLayout.setVisibility(View.INVISIBLE);
            addTranslucentWindowFlags();
        });
    }

    /**
     * The opposite of {@link #showAppDrawer()}.
     * This method hides the AppDrawer layout and shows the HomeScreen layout, using a slide down
     * animation and clears the translucent window flags, using the {@link #clearTranslucentWindowFlags()}
     * method.
     *
     * @see #showAppDrawer();
     * @see #clearTranslucentWindowFlags();
     */
    private void hideAppDrawer() {

        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        appDrawerLayout.startAnimation(slideDown);
        appDrawerLayout.postOnAnimation(() -> {

            appDrawerLayout.setVisibility(View.INVISIBLE);
            homeScreenLayout.setVisibility(View.VISIBLE);
            clearTranslucentWindowFlags();
        });
    }

    /**
     * Set the app icon image resource, using a {@link Drawable} object and the {@link PackageManager}
     * to get the app icon.
     *
     * @param view        - the imageView object that needs to display the app icon;
     * @param packageName - the package name of the app, used to get the app icon;
     */
    private void setDrawableIcon(ImageView view, String packageName) {

        PackageManager pm = MainPagerActivity.this.getPackageManager();

        try {
            Drawable icon = pm.getApplicationIcon(packageName);
            view.setImageDrawable(icon);

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
    }

    /**
     * This method is used to get the package names and icons of the apps inside the bottom dock view -
     * Phone, Messages, Browser and Camera and uses the {@link #setDrawableIcon(ImageView, String)}
     * method to show the app icons properly
     *
     * @see #setDrawableIcon(ImageView, String)
     */
    private void configureDockDrawables() {

        //Get the default phone app package and set its icon
        phonePackage = ((TelecomManager) getSystemService(TELECOM_SERVICE)).getDefaultDialerPackage();
        setDrawableIcon(appPhone, phonePackage);

        //Get the default SMS app package and set its icon
        smsPackage = Telephony.Sms.getDefaultSmsPackage(this);
        setDrawableIcon(appMessages, smsPackage);

        //Get the default Browser app (Chrome) package and set its icon
        browserPackage = "com.android.chrome";
        setDrawableIcon(appBrowser, browserPackage);

        //Get the default Camera app package and set its icon
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        cameraPackage = resolveInfo.activityInfo.packageName;
        setDrawableIcon(appCamera, cameraPackage);
    }

    /**
     * This method is used to add translucent window flags in order to achieve a fullscreen-like activity,
     * by setting the StatusBar color transparent and by making the NavigationBar translucent
     * <p>
     * The opposite of {@link #clearTranslucentWindowFlags()}
     *
     * @see #clearTranslucentWindowFlags()
     */
    private void addTranslucentWindowFlags() {

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(getResources().getColor(R.color.colorTransparent50, null));
        window.setNavigationBarColor(getResources().getColor(android.R.color.transparent, null));
    }

    /**
     * The opposite method of {@link #addTranslucentWindowFlags()}
     * This method clears all the flags added in the above mentioned method
     *
     * @see #addTranslucentWindowFlags()
     */
    private void clearTranslucentWindowFlags() {

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent, null));
        window.setNavigationBarColor(
                getResources().getColor(R.color.colorTransparent50, null));
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            if (position == 0) {

                //Fragment News
                return new NewsFragment();
            } else if (position == getItemCount() - 1) {

                //fragment Settings
                return new SettingsFragment();
            } else {
                //Fragment HomeScreen
                return new HomeScreenFragment(position);
            }
        }

        @Override
        public int getItemCount() {

            return AppInfoUtil.getLastHomeScreenAppPage(getApplicationContext()) +
                    getResources().getInteger(R.integer.view_pager_magic_number);
        }
    }
}