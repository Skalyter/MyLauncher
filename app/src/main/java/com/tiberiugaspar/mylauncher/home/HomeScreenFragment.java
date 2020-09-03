package com.tiberiugaspar.mylauncher.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.adapter.AppListAdapter;
import com.tiberiugaspar.mylauncher.model.AppInfo;
import com.tiberiugaspar.mylauncher.util.WrapContentGridLayoutManager;

public class HomeScreenFragment extends Fragment {

    private int position;
    private AppListAdapter appListAdapter;
    private RecyclerView recyclerView;

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
        recyclerView = view.findViewById(R.id.app_list);
        if (appListAdapter == null) {
            appListAdapter = new AppListAdapter(getActivity(), false, position);
        } else {
            appListAdapter.setPosition(position);
        }
        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 5));
        recyclerView.setAdapter(appListAdapter);
        recyclerView.setScrollContainer(false);

        registerForContextMenu(recyclerView);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {

        getActivity().getMenuInflater().inflate(R.menu.menu_home_screen_item, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AppInfo appInfo = appListAdapter.getCurrentApp();
        switch (item.getItemId()) {
            case R.id.menu_app_info:
                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + appInfo.getPackageName()));
                startActivity(i);

                return true;

            case R.id.menu_change_position:
                Toast.makeText(getContext(), "Change position", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_remove:
                appListAdapter.removeApp(appInfo);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}