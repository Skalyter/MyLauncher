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
import com.tiberiugaspar.mylauncher.model.AppInfo;
import com.tiberiugaspar.mylauncher.news.NewsFragment;
import com.tiberiugaspar.mylauncher.util.HomeScreenUtil;
import com.tiberiugaspar.mylauncher.util.WrapContentGridLayoutManager;

public class MainPagerActivity extends FragmentActivity {

    private LinearLayout homeScreenLayout;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private ImageView appPhone, appMessages, appDrawer, appBrowser, appCamera;
    private String phonePackage, smsPackage, browserPackage, cameraPackage;
    private LinearLayout dockLayout;

    private NestedScrollView appDrawerLayout;
    private RecyclerView appDrawerRecycler;
    private AppListAdapter appListAdapter;

    private Window window;
    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

            if (position == 0
                    || (position == (HomeScreenUtil.getNumberOfPages(getApplicationContext()) +
                    getResources().getInteger(R.integer.view_pager_magic_number) - 1))) {

                Animation animation = AnimationUtils.loadAnimation(MainPagerActivity.this, android.R.anim.fade_out);
                dockLayout.startAnimation(animation);
                dockLayout.setVisibility(View.GONE);

                addTranslucentWindowFlags();

            } else {
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
    private BroadcastReceiver packageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                String packageName = intent.getData().toString();
                packageName = packageName.replace("package:", "");
                Log.d("MainPagerActivity", "onReceive: " + packageName);
                //TODO: replace with add/remove app from homescreen and DB
                switch (intent.getAction()) {
                    case Intent.ACTION_PACKAGE_ADDED:
                        Toast.makeText(context, packageName + " installed", Toast.LENGTH_SHORT).show();
                        break;
                    case Intent.ACTION_PACKAGE_CHANGED:
                        break;
                    case Intent.ACTION_PACKAGE_REMOVED:
                        Toast.makeText(context, packageName + " uninstalled", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);

        window = getWindow();


        findViewsById();
        configureDockDrawables();
        configureListeners();

        appListAdapter = new AppListAdapter(this, true, null);
        appDrawerRecycler.setLayoutManager(new WrapContentGridLayoutManager(this, 5));
        appDrawerRecycler.setAdapter(appListAdapter);
        appDrawerLayout.setClickable(false);

        registerForContextMenu(appDrawerRecycler);

        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1, false);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                });
        mediator.attach();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");

        //Register packageReceiver to listen to all the packages added or removed
        registerReceiver(packageReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(packageReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

        viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
    }

    @Override
    public void onBackPressed() {
        if (appDrawerLayout.getVisibility() == View.VISIBLE) {
            hideAppDrawer();

        } else if (viewPager.getCurrentItem() == 0) {
            viewPager.setCurrentItem(1, true);

        } else {
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
        AppInfo appInfo = appListAdapter.getCurrentApp();
        switch (item.getItemId()) {
            case R.id.menu_app_info:
                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + appInfo.getPackageName()));

                startActivity(i);

                return true;

            case R.id.menu_add_to_homescreen:
                //TODO: add app in db/update
                Toast.makeText(this, "Add to homescreen", Toast.LENGTH_SHORT).show();

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void animateStartActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up, android.R.anim.fade_out);
    }

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

    private void configureListeners() {
        appDrawer.setOnClickListener(v -> showAppDrawer());
        appPhone.setOnClickListener(v -> animateStartActivity(phonePackage));

        appMessages.setOnClickListener(v -> animateStartActivity(smsPackage));

        appBrowser.setOnClickListener(v -> animateStartActivity(browserPackage));

        appCamera.setOnClickListener(v -> animateStartActivity(cameraPackage));
    }

    private void showAppDrawer() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        appDrawerLayout.startAnimation(slideUp);
        appDrawerLayout.postOnAnimation(() -> {
            appDrawerLayout.setVisibility(View.VISIBLE);
            homeScreenLayout.setVisibility(View.INVISIBLE);
            addTranslucentWindowFlags();
        });
    }

    private void hideAppDrawer() {
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        appDrawerLayout.startAnimation(slideDown);
        appDrawerLayout.postOnAnimation(() -> {
            appDrawerLayout.setVisibility(View.INVISIBLE);
            homeScreenLayout.setVisibility(View.VISIBLE);
            clearTranslucentWindowFlags();
        });
    }

    private void setDrawableIcon(ImageView view, String packageName) {
        PackageManager pm = MainPagerActivity.this.getPackageManager();
        try {
            Drawable icon = pm.getApplicationIcon(packageName);
            view.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void configureDockDrawables() {
        //set phone app icon
        phonePackage = ((TelecomManager) getSystemService(TELECOM_SERVICE)).getDefaultDialerPackage();
        setDrawableIcon(appPhone, phonePackage);

        //set sms app icon
        smsPackage = Telephony.Sms.getDefaultSmsPackage(this);
        setDrawableIcon(appMessages, smsPackage);

        //set browser app icon
        browserPackage = "com.android.chrome";
        setDrawableIcon(appBrowser, browserPackage);

        //set camera app icon
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        cameraPackage = resolveInfo.activityInfo.packageName;
        setDrawableIcon(appCamera, cameraPackage);
    }

    private void addTranslucentWindowFlags() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setStatusBarColor(getResources().getColor(R.color.colorTransparent50, null));
        window.setNavigationBarColor(getResources().getColor(android.R.color.transparent, null));
    }

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
                //Fragment news
                return new NewsFragment();
            } else if (position == getItemCount() - 1) {
                //fragment settings
                return new Fragment();
            } else {
                return new HomeScreenFragment(position);
            }
        }

        @Override
        public int getItemCount() {
            return HomeScreenUtil.getNumberOfPages(getApplicationContext()) +
                    getResources().getInteger(R.integer.view_pager_magic_number);
        }
    }
}