package com.tiberiugaspar.mylauncher.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    public static final String API_KEY="450215834b3e476dad2a443d88ffeb2d";

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<Article> articleList = new ArrayList<>();

    public NewsFragment() {
        // Required empty public constructor
    }

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
        recyclerView = view.findViewById(R.id.recycler_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadJson();
        refreshLayout.setOnRefreshListener(() -> loadJson());
    }

    public void loadJson(){
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<News> call;
        String country = NewsApiUtil.getCountry();
        call = apiInterface.getNews(country, API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){
                    if (!articleList.isEmpty()){
                        articleList.clear();
                    }
                    articleList = response.body().getArticles();
                    adapter = new ArticleAdapter(articleList, getContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                } else {
                    Toast.makeText(getContext(), "No result!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });
        
    }

}