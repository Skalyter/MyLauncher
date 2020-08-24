package com.tiberiugaspar.mylauncher.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.drawer.AppDrawerActivity;
import com.tiberiugaspar.mylauncher.news.NewsFragment;
import com.tiberiugaspar.mylauncher.util.HomeScreenUtil;

public class MainPagerActivity extends FragmentActivity {

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private ImageView appPhone, appMessages, appDrawer, appBrowser, appCamera;
    private LinearLayout dockLayout;

    private String phonePackage, smsPackage, browserPackage, cameraPackage;

    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            Window window = getWindow();
            if (position == 0
                    || (position == (HomeScreenUtil.getNumberOfPages(getApplicationContext()) +
                    getResources().getInteger(R.integer.view_pager_magic_number) - 1))) {

                Animation animation = AnimationUtils.loadAnimation(MainPagerActivity.this, android.R.anim.fade_out);
                dockLayout.setVisibility(View.GONE);
                dockLayout.setAnimation(animation);

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.setStatusBarColor(getResources().getColor(R.color.colorTransparent50, null));
                window.setNavigationBarColor(getResources().getColor(android.R.color.transparent, null));
            } else {
                if (window.getNavigationBarColor()
                        == getResources().getColor(android.R.color.transparent, null)) {

                    Animation animation = AnimationUtils.loadAnimation(MainPagerActivity.this, android.R.anim.fade_in);
                    dockLayout.startAnimation(animation);
                    dockLayout.setVisibility(View.VISIBLE);

                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                    window.setStatusBarColor(getResources().getColor(android.R.color.transparent, null));
                    window.setNavigationBarColor(
                            getResources().getColor(R.color.colorTransparent50, null));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);

        findViewsById();
        configureDockDrawables();
        configureListeners();

        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1, true);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                });
        mediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
    }

    private void findViewsById() {
        viewPager = findViewById(R.id.view_pager);
        appPhone = findViewById(R.id.app_phone);
        appMessages = findViewById(R.id.app_messages);
        appDrawer = findViewById(R.id.app_drawer);
        appBrowser = findViewById(R.id.app_browser);
        appCamera = findViewById(R.id.app_camera);
        dockLayout = findViewById(R.id.layout_home_apps);
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
        ResolveInfo resolveInfo2 = getPackageManager().resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        cameraPackage = resolveInfo2.activityInfo.packageName;
        setDrawableIcon(appCamera, cameraPackage);
    }

    private void configureListeners() {
        appDrawer.setOnClickListener(v -> animateStartActivity(new Intent(MainPagerActivity.this, AppDrawerActivity.class)));
        appPhone.setOnClickListener(v -> animateStartActivity(phonePackage));

        appMessages.setOnClickListener(v -> animateStartActivity(smsPackage));

        appBrowser.setOnClickListener(v -> animateStartActivity(browserPackage));

        appCamera.setOnClickListener(v -> animateStartActivity(cameraPackage));
    }

    private void animateStartActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up, android.R.anim.fade_out);
    }

    private void animateStartActivity(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        animateStartActivity(intent);
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