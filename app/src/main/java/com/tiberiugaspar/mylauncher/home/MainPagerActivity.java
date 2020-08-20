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
    private LinearLayout bottomApps;

    private String phonePackage, smsPackage, browserPackage, cameraPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);

        findViewsById();
        setDrawables();
        setListeners();
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1, true);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {});
        mediator.attach();
    }

    private void findViewsById() {
        viewPager = findViewById(R.id.view_pager);
        appPhone = findViewById(R.id.app_phone);
        appMessages = findViewById(R.id.app_messages);
        appDrawer = findViewById(R.id.app_drawer);
        appBrowser = findViewById(R.id.app_browser);
        appCamera = findViewById(R.id.app_camera);
        bottomApps = findViewById(R.id.layout_home_apps);
    }

    private void setDrawables() {
        //set phone app icon
        phonePackage = ((TelecomManager) getSystemService(TELECOM_SERVICE)).getDefaultDialerPackage();
        setDrawableIcon(appPhone, phonePackage);

        //set sms app icon
        smsPackage = Telephony.Sms.getDefaultSmsPackage(this);
        setDrawableIcon(appMessages, smsPackage);

        //set browser app icon
//        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://example.com"));
//        ResolveInfo resolveInfo = getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
//        browserPackage = resolveInfo.activityInfo.packageName;
        browserPackage = "com.android.chrome";
        setDrawableIcon(appBrowser, browserPackage);

        //set camera app icon
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo resolveInfo2 = getPackageManager().resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        cameraPackage = resolveInfo2.activityInfo.packageName;
        setDrawableIcon(appCamera, cameraPackage);
    }

    private void setListeners() {
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
            if (position == 0){
                //Fragment news
                setTheme(R.style.NoActionBarTranslucent);
                bottomApps.setVisibility(View.GONE);
                return new NewsFragment();
            } else if (position == getItemCount()-1){
                //fragment settings
                bottomApps.setVisibility(View.VISIBLE);
                return new Fragment();
            } else{
                setTheme(R.style.NoActionBar);
                bottomApps.setVisibility(View.VISIBLE);
                return new HomeScreenFragment(position);
            }
        }

        @Override
        public int getItemCount() {
            return HomeScreenUtil.getNumberOfPages(getApplicationContext())+2;
        }
    }
}