package com.tiberiugaspar.mylauncher.model;

import android.graphics.drawable.Drawable;

public class AppInfo implements Comparable<AppInfo> {
    private CharSequence label;
    private CharSequence packageName;
    private Drawable icon;
    private int accessedCounter = 0;
    private boolean isOnHomeScreen;
    private int pageNumber;
    private int position;

    public AppInfo() {
    }

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

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public void setPackageName(CharSequence packageName) {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getAccessedCounter() {
        return accessedCounter;
    }

    public void setAccessedCounter(int accessedCounter) {
        this.accessedCounter = accessedCounter;
    }

    public boolean isOnHomeScreen() {
        return isOnHomeScreen;
    }

    public void setOnHomeScreen(boolean onHomeScreen) {
        isOnHomeScreen = onHomeScreen;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int compareTo(AppInfo appInfo) {
        return this.packageName.toString().compareToIgnoreCase(appInfo.packageName.toString());
    }
}
