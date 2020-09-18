package com.tiberiugaspar.mylauncher.database;

import android.provider.BaseColumns;

public interface DatabaseScheme extends BaseColumns {

    String TABLE_APPS = "apps";

    String COLUMN_PACKAGE = "package";
    String COLUMN_LABEL = "app_label";
    String COLUMN_ACCESSED_COUNTER = "accessed_counter";
    String COLUMN_IS_ON_HOME_SCREEN = "is_on_home_screen";
    String COLUMN_PAGE_NUMBER = "page_number";
    String COLUMN_POSITION = "position";

    String CREATE_TABLE_APPS = "CREATE TABLE "
            + TABLE_APPS + " ("
            + COLUMN_PACKAGE + " TEXT, "
            + COLUMN_LABEL + " TEXT, "
            + COLUMN_ACCESSED_COUNTER + " INTEGER, "
            + COLUMN_IS_ON_HOME_SCREEN + " INTEGER, " //value 0 -> false; value 1 -> true
            + COLUMN_PAGE_NUMBER + " INTEGER, "
            + COLUMN_POSITION + " INTEGER)";

    String TABLE_ARTICLES = "articles";

    String COLUMN_SOURCE_NAME = "source_name";
    String COLUMN_AUTHOR = "author";
    String COLUMN_TITLE = "title";
    String COLUMN_DESCRIPTION = "description";
    String COLUMN_URL = "url";
    String COLUMN_URL_TO_IMAGE = "url_to_image";
    String COLUMN_PUBLISHED_AT = "published_at";

    String CREATE_TABLE_ARTICLES = "CREATE TABLE "
            + TABLE_ARTICLES + " ("
            + COLUMN_SOURCE_NAME + " TEXT, "
            + COLUMN_AUTHOR + " TEXT, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_URL + " TEXT, "
            + COLUMN_URL_TO_IMAGE + " TEXT, "
            + COLUMN_PUBLISHED_AT + " TEXT)";

    String DROP_TABLE_APPS = "DROP TABLE IF EXISTS " + TABLE_APPS;

    String DROP_TABLE_ARTICLES = "DROP TABLE IF EXISTS " + TABLE_ARTICLES;
}
