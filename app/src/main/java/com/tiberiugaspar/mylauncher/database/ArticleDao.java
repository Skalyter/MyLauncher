package com.tiberiugaspar.mylauncher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tiberiugaspar.mylauncher.model.Article;
import com.tiberiugaspar.mylauncher.model.Source;

import java.util.ArrayList;
import java.util.List;

import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_AUTHOR;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_DESCRIPTION;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_PUBLISHED_AT;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_SOURCE_NAME;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_TITLE;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_URL;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.COLUMN_URL_TO_IMAGE;
import static com.tiberiugaspar.mylauncher.database.DatabaseScheme.TABLE_ARTICLES;

public class ArticleDao implements IArticleDao {

    private static final String TAG = "ArticleDao";
    private Context context;

    public ArticleDao(Context context) {
        this.context = context;
    }

    @Override
    public void insertAllArticles(List<Article> articleList) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            for (Article article : articleList) {
                db.insert(TABLE_ARTICLES, null, entityToContentValues(article));
                Log.i(TAG, "insertAllArticles: " + article.getTitle() + " inserted");
            }
        } finally {
            db.close();
        }
    }

    @Override
    public void deleteAllArticles() {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.delete(TABLE_ARTICLES, null, null);
        } finally {
            db.close();
        }
    }

    @Override
    public List<Article> getAllArticles() {
        List<Article> articleList = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ARTICLES,
                null, null, null,
                null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    articleList.add(cursorToEntity(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return articleList;
    }

    private Article cursorToEntity(Cursor cursor) {
        Article article = new Article();

        article.setSource(new Source(cursor.getString(cursor.getColumnIndex(COLUMN_SOURCE_NAME))));
        article.setAuthor(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
        article.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        article.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        article.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
        article.setUrlToImage(cursor.getString(cursor.getColumnIndex(COLUMN_URL_TO_IMAGE)));
        article.setPublishedAt(cursor.getString(cursor.getColumnIndex(COLUMN_PUBLISHED_AT)));

        return article;
    }

    private ContentValues entityToContentValues(Article article) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_SOURCE_NAME, article.getSource().getName());
        contentValues.put(COLUMN_AUTHOR, article.getAuthor());
        contentValues.put(COLUMN_TITLE, article.getTitle());
        contentValues.put(COLUMN_DESCRIPTION, article.getDescription());
        contentValues.put(COLUMN_URL, article.getUrl());
        contentValues.put(COLUMN_URL_TO_IMAGE, article.getUrlToImage());
        contentValues.put(COLUMN_PUBLISHED_AT, article.getPublishedAt());

        return contentValues;
    }
}
