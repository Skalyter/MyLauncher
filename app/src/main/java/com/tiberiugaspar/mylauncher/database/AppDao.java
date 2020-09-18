package com.tiberiugaspar.mylauncher.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tiberiugaspar.mylauncher.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_ACCESSED_COUNTER;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_IS_ON_HOME_SCREEN;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_LABEL;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_PACKAGE;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_PAGE_NUMBER;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_POSITION;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.TABLE_APPS;

public class AppDao implements IAppDao {

    private static final String TAG = "AppDao";
    private Context context;

    public AppDao(Context context) {
        this.context = context;
    }

    @Override
    public void insertAppInfo(AppInfo appInfo) {

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {

            db.insert(TABLE_APPS, null, entityToContentValues(appInfo));
            Log.i(TAG, "insertAppInfo: " + appInfo.getLabel() + " inserted successfully");

        } finally {
            db.close();
        }
    }

    @Override
    public void deleteAppInfo(AppInfo appInfo) {

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.delete(TABLE_APPS,
                    COLUMN_PACKAGE + "=?",
                    new String[]{String.valueOf(appInfo.getPackageName())});

            Log.i(TAG, "deleteAppInfo: " + appInfo.getLabel() + " deleted successfully");
        } finally {

            db.close();
        }
    }

    @Override
    public void updateAppInfo(AppInfo appInfo) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.update(TABLE_APPS, entityToContentValues(appInfo), COLUMN_PACKAGE + "=?",
                    new String[]{String.valueOf(appInfo.getPackageName())});

            Log.d(TAG, "updateAppInfo: success" + appInfo.getLabel() + " " + appInfo.getPosition());

        } finally {
            db.close();
        }
    }

    @Override
    public List<AppInfo> getAllAppsAlphabetically() {

        List<AppInfo> appInfoList = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_APPS,
                null,
                null,
                null,
                null,
                null, COLUMN_LABEL);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    appInfoList.add(cursorToEntity(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return appInfoList;
    }

    @Override
    public List<AppInfo> getAllAppsByFrequency() {

        List<AppInfo> appInfoList = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_APPS,
                null,
                null,
                null,
                null,
                null, COLUMN_ACCESSED_COUNTER + " DESC");
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    appInfoList.add(cursorToEntity(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return appInfoList;
    }

    @Override
    public List<AppInfo> getAppsForHomeScreen(int pageNumber) {

        List<AppInfo> appInfoList = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_APPS,
                null,
                COLUMN_PAGE_NUMBER + "=? ",
                new String[]{String.valueOf(pageNumber)},
                null,
                null, COLUMN_POSITION);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    appInfoList.add(cursorToEntity(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return appInfoList;
    }

    private AppInfo cursorToEntity(Cursor cursor) {

        AppInfo appInfo = new AppInfo();

        try {

            appInfo.setLabel(cursor.getString(cursor.getColumnIndex(COLUMN_LABEL)));
            appInfo.setPackageName(cursor.getString(cursor.getColumnIndex(COLUMN_PACKAGE)));
            appInfo.setIcon(context.getPackageManager().getApplicationIcon(appInfo.getPackageName().toString()));
            appInfo.setPageNumber(cursor.getInt(cursor.getColumnIndex(COLUMN_PAGE_NUMBER)));
            appInfo.setPosition(cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION)));
            if (cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ON_HOME_SCREEN)) == 1) {

                appInfo.setOnHomeScreen(true);
            } else {

                appInfo.setOnHomeScreen(false);
            }

        } catch (PackageManager.NameNotFoundException e) {

            Log.e(TAG, "cursorToEntity: something went wrong" + e.getMessage());
        }
        return appInfo;
    }

    private ContentValues entityToContentValues(AppInfo appInfo) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_LABEL, appInfo.getLabel().toString());
        contentValues.put(COLUMN_PACKAGE, appInfo.getPackageName().toString());
        contentValues.put(COLUMN_ACCESSED_COUNTER, 0);
        if (appInfo.isOnHomeScreen()) {

            contentValues.put(COLUMN_IS_ON_HOME_SCREEN, 1);
        } else {
            contentValues.put(COLUMN_IS_ON_HOME_SCREEN, 0);
        }
        contentValues.put(COLUMN_PAGE_NUMBER, appInfo.getPageNumber());
        contentValues.put(COLUMN_POSITION, appInfo.getPosition());
        return contentValues;
    }
}
