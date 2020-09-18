package com.tiberiugaspar.mylauncher.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.tiberiugaspar.mylauncher.R;

public class SettingsUtil {

    public static final String SHARED_PREFERENCES = "com.skalyter.mylauncher";
    public static final String SHARED_PREFERENCES_APP_GRID_LAYOUT_ROWS = "app_grid_layout_rows";
    public static final String SHARED_PREFERENCES_APP_GRID_LAYOUT_COLUMNS = "app_grid_layout_columns";
    public static final String SHARED_PREFERENCES_LAUNCHER_STYLE = "launcher_style";
    public static final String SHARED_PREFERENCES_APPS_ORDER = "apps_order";

    public static int getAppGridSize(Context context, String orientation) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        return sharedPreferences.getInt(orientation, 4);
    }

    @SuppressLint("DefaultLocale")
    public static String getSharedPreferencesAppGridLayout(Context context) {

        return String.format("%dx%d",
                getAppGridSize(context, SHARED_PREFERENCES_APP_GRID_LAYOUT_ROWS),
                getAppGridSize(context, SHARED_PREFERENCES_APP_GRID_LAYOUT_COLUMNS));
    }

    public static void setAppGridLayout(Context context, String gridLayout) {

        String[] sizes = gridLayout.split("x");

        int rows = Integer.parseInt(sizes[0]);
        int columns = Integer.parseInt(sizes[1]);

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(SHARED_PREFERENCES_APP_GRID_LAYOUT_ROWS, rows);
        editor.putInt(SHARED_PREFERENCES_APP_GRID_LAYOUT_COLUMNS, columns);
        editor.apply();
    }

    public static String getSharedPreferencesLauncherStyle(Context context) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.getInt(SHARED_PREFERENCES_LAUNCHER_STYLE, 2) == 1) {
            return context.getString(R.string.shared_pref_one_layer);

        } else {
            return context.getString(R.string.shared_pref_two_layers);
        }
    }

    public static void setLauncherStyle(Context context, String style) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (style.equals(context.getString(R.string.shared_pref_one_layer))) {
            editor.putInt(SHARED_PREFERENCES_LAUNCHER_STYLE, 1);
        } else {
            editor.putInt(SHARED_PREFERENCES_LAUNCHER_STYLE, 2);
        }
        editor.apply();
    }

    public static String getSharedPreferencesAppsOrder(Context context) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        return sharedPreferences.getString(SHARED_PREFERENCES_APPS_ORDER,
                context.getString(R.string.shared_pref_alphabetically));
    }

    public static void setAppsOrder(Context context, String order) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SHARED_PREFERENCES_APPS_ORDER, order);
        editor.apply();
    }

}
