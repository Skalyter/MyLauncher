package com.tiberiugaspar.mylauncher.news;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.adapter.NewsAdapter;
import com.tiberiugaspar.mylauncher.model.NewsInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private List<NewsInfo> newsList;

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
        adapter = new NewsAdapter(view.getContext());
//        new FetchFeedTask().execute();
    }

//    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {
//
//        private String urlLink;
//
//        @Override
//        protected void onPreExecute() {
//            refreshLayout.setRefreshing(true);
//            urlLink = "https://news.google.com/rss";
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            if (TextUtils.isEmpty(urlLink))
//                return false;
//
//            try {
//                URL url = new URL(urlLink);
//                InputStream inputStream = url.openConnection().getInputStream();
//                newsList = adapter.parseFeed(inputStream);
//                return true;
//            } catch (IOException e) {
//                Log.e(TAG, "Error", e);
//            } catch (XmlPullParserException e) {
//                Log.e(TAG, "Error", e);
//            }
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            refreshLayout.setRefreshing(false);
//
//            if (success) {
//                // Fill RecyclerView
//                recyclerView.setAdapter(new NewsAdapter(getContext(), newsList));
//            } else {
//                Log.d(TAG, "onPostExecute: eroare");
//            }
//        }
//    }
}