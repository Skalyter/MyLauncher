package com.tiberiugaspar.mylauncher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.tiberiugaspar.mylauncher.model.AppInfo;

public class AppInfoUtil {

    public static final String SHARED_PREFERENCES = "com.skalyter.mylauncher";
    public static final String SHARED_PREFERENCES_LAST_HOME_PAGE_NUMBER = "last_home_page_number";
    public static final String SHARED_PREFERENCES_LAST_HOME_SCREEN_APP_POSITION = "last_home_screen_app_position";

    /**
     * This method is used to retrieve an {@link AppInfo} object, according to its package name
     *
     * @param context     used to instantiate the {@link PackageManager}
     * @param packageName used to get the application info
     * @return {@link AppInfo} a custom object, containing the app label, package and icon
     * @throws PackageManager.NameNotFoundException if the package name is not found
     */
    public static AppInfo getAppInfoFromPackageName(Context context, String packageName)
            throws PackageManager.NameNotFoundException {

        PackageManager packageManager = context.getPackageManager();

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);

            Drawable icon = packageManager.getApplicationIcon(applicationInfo);
            CharSequence label = packageManager.getApplicationLabel(applicationInfo);
            return new AppInfo(label, packageName, icon);

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get the current last home screen page which contains at least one app.
     *
     * @param context used to instantiate the {@link SharedPreferences}
     * @return last home screen page's position
     */
    public static int getLastHomeScreenAppPage(Context context) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        return sharedPreferences.getInt(SHARED_PREFERENCES_LAST_HOME_PAGE_NUMBER, 1);
    }

    /**
     * Set the current last home screen page which contains at least one app (used specialy
     * when an app is added on the Home Screen or when a package is installed)
     *
     * @param context    used to instantiate the {@link SharedPreferences}
     * @param pageNumber the updated last page
     */
    public static void setLastHomeScreenAppPage(Context context, int pageNumber) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SHARED_PREFERENCES_LAST_HOME_PAGE_NUMBER, pageNumber);
        editor.apply();
    }

    /**
     * Get the position of the last app on the Home Screen's last App Page
     *
     * @param context used to instantiate {@link SharedPreferences}
     * @return the last app's position
     */
    public static int getLastAppPosition(Context context) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        return sharedPreferences.getInt(SHARED_PREFERENCES_LAST_HOME_SCREEN_APP_POSITION, 0);
    }

    /**
     * Set the position of the last app on the Home Screen's last App Page (used especially when an app
     * is installed or an app is added to the Home Screen)
     *
     * @param context  used to instantiate {@link SharedPreferences}
     * @param position the new last position (most likely, the old last position + 1)
     */
    public static void setLastAppPosition(Context context, int position) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SHARED_PREFERENCES_LAST_HOME_SCREEN_APP_POSITION, position);
        editor.apply();
    }
}
