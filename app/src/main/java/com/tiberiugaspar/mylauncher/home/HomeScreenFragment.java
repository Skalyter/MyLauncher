package com.tiberiugaspar.mylauncher.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.adapter.AppListAdapter;
import com.tiberiugaspar.mylauncher.database.AppDao;
import com.tiberiugaspar.mylauncher.model.AppInfo;
import com.tiberiugaspar.mylauncher.util.WrapContentGridLayoutManager;

import java.util.Collections;

public class HomeScreenFragment extends Fragment {

    private int pageNumber;
    private AppListAdapter appListAdapter;
    private RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;

    private AppDao appDao;

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    public HomeScreenFragment(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    private ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            //swap apps order in the appListAdapter and notify items moved
            Collections.swap(appListAdapter.appList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            appListAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

            AppInfo appInfo1 = appListAdapter.appList.get(viewHolder.getAdapterPosition());
            AppInfo appInfo2 = appListAdapter.appList.get(target.getAdapterPosition());

            //swap app's current positions
            appInfo1.setPosition(viewHolder.getAdapterPosition());
            appInfo2.setPosition(target.getAdapterPosition());

            //swap apps position in DB
            appDao.updateAppInfo(appInfo1);
            appDao.updateAppInfo(appInfo2);

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.app_list);

        appDao = new AppDao(getContext());
        if (appListAdapter == null) {

            appListAdapter = new AppListAdapter(getActivity(), false, pageNumber);
        } else {

            appListAdapter.setPageNumber(pageNumber);
        }

        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(), 5));
        recyclerView.setAdapter(appListAdapter);
        itemTouchHelper = new ItemTouchHelper(_ithCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //register the recyclerView object for the contextual menu on long clicking an app
        registerForContextMenu(recyclerView);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {

        //we removed the super method because it called the onCreateContextMenu() method from the parent activity
        // - inflating both the AppDrawer's contextual menu and the Home Screen's contextual menu
        getActivity().getMenuInflater().inflate(R.menu.menu_home_screen_item, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterForContextMenu(recyclerView);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AppInfo appInfo = appListAdapter.getSelectedApp();
        switch (item.getItemId()) {
            case R.id.menu_home_screen_app_info:

                //open settings' app details activity for given app package
                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + appInfo.getPackageName()));
                startActivity(i);
                return true;

            case R.id.menu_remove:

                appListAdapter.removeApp(appInfo);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}