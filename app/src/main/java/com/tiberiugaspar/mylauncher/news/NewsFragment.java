package com.tiberiugaspar.mylauncher.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.adapter.ArticleAdapter;
import com.tiberiugaspar.mylauncher.model.Article;
import com.tiberiugaspar.mylauncher.model.News;
import com.tiberiugaspar.mylauncher.news_api.ApiClient;
import com.tiberiugaspar.mylauncher.news_api.ApiInterface;
import com.tiberiugaspar.mylauncher.util.NewsApiUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";

    private ArticleAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Article> articleList = new ArrayList<>();

    private ContentLoadingProgressBar progressBar;

    public NewsFragment() {
        // Required empty public constructor
    }

    //used to retrieve the API key from NDK
    static {
        System.loadLibrary("keys");
    }

    public native String getNativeNewsApiKey();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = view.findViewById(R.id.news_feed_layout);
        progressBar = view.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.show();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ArticleAdapter(articleList, getContext());
        recyclerView.setAdapter(adapter);

        downloadNews();

        refreshLayout.setOnRefreshListener(() -> downloadNews());
    }

    /**
     * This method is used to download the articles for the news feed, using {@link Call<News>}
     */
    private void downloadNews() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<News> call;

        String country = NewsApiUtil.getCountry();
        String key = getNativeNewsApiKey();

        call = apiInterface.getNews(country, key);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {

                if (refreshLayout.isRefreshing()) {

                    refreshLayout.setRefreshing(false);
                }

                if (response.isSuccessful() && response.body().getArticles() != null) {

                    //Clear the article list if there are any new articles to be retrieved
                    if (!articleList.isEmpty()) {

                        articleList.clear();
                    }

                    articleList = response.body().getArticles();
                    adapter.setArticleList(articleList);
                    adapter.notifyDataSetChanged();

                    if (progressBar.isShown()) {
                        progressBar.hide();
                        progressBar.setVisibility(View.GONE);
                    }

                } else {

                    Toast.makeText(getContext(), "No result!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {

                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}