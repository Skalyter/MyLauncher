package com.tiberiugaspar.mylauncher.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public abstract class HomeScreenUtil {
    /**
     * This method is used to get the number of pages that are required in order to show all apps
     * over multiple pages in the HomeScreen fragment, considering that a page can hold 30 apps.
     * <p>
     * It is just a test method and it will be removed soon
     *
     * @param context - the context that calls the method
     * @return (integer) - the number of pages needed to show all apps
     */
    public static int getNumberOfPages(Context context) {
        PackageManager pm = context.getPackageManager();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);
        if (allApps.size() % 30 == 0) {
            return allApps.size() / 30;
        }
        return allApps.size()/30+1;
    }
}
