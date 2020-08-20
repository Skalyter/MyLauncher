package com.tiberiugaspar.mylauncher.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public abstract class Util {
    public static int getNumberOfPages(Context context){
        PackageManager pm = context.getPackageManager();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);
        if (allApps.size()%30==0){
            return allApps.size()/30;
        }
        return allApps.size()/30+1;
    }
}
