package com.tiberiugaspar.mylauncher.database;

import com.tiberiugaspar.mylauncher.model.Article;

import java.util.List;

public interface IArticleDao {

    void insertAllArticles(List<Article> articleList);

    void deleteAllArticles();

    List<Article> getAllArticles();
}
