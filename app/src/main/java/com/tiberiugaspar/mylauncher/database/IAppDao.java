package com.tiberiugaspar.mylauncher.database;

import com.tiberiugaspar.mylauncher.model.AppInfo;

import java.util.List;

public interface IAppDao {

    void insertAppInfo(AppInfo appInfo);

    void deleteAppInfo(AppInfo appInfo);

    void updateAppInfo(AppInfo appInfo);

    List<AppInfo> getAllAppsAlphabetically();

    List<AppInfo> getAllAppsByFrequency();

    List<AppInfo> getAppsForHomeScreen(int pageNumber);
}
