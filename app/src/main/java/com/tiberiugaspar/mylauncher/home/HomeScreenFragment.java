package com.tiberiugaspar.mylauncher.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.mylauncher.adapter.AppListAdapter;
import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.util.WrapContentGridLayoutManager;

public class HomeScreenFragment extends Fragment {

    private int position;
    private AppListAdapter appListAdapter;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    public HomeScreenFragment(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.app_list);
        appListAdapter = new AppListAdapter(getActivity(), false, position);
        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 5));
        recyclerView.setAdapter(appListAdapter);
    }
}