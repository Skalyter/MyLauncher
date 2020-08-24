package com.tiberiugaspar.mylauncher.drawer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.adapter.AppListAdapter;
import com.tiberiugaspar.mylauncher.util.WrapContentGridLayoutManager;

public class AppDrawerActivity extends AppCompatActivity {

    private AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_drawer);
        adapter = new AppListAdapter(this, true, null);
        RecyclerView appList = findViewById(R.id.app_list);
        appList.setLayoutManager(new WrapContentGridLayoutManager(this, 5));
        appList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down);
    }
}