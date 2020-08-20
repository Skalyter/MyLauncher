package com.tiberiugaspar.mylauncher.model;

import android.graphics.drawable.Drawable;

public class AppInfo implements Comparable<AppInfo> {
    private CharSequence label;
    private CharSequence packageName;
    private Drawable icon;

    public AppInfo(CharSequence label, CharSequence packageName, Drawable icon) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
    }

    public CharSequence getLabel() {
        return label;
    }


    public CharSequence getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    @Override
    public int compareTo(AppInfo appInfo) {
        return this.label.toString().compareToIgnoreCase(appInfo.label.toString());
    }
}
