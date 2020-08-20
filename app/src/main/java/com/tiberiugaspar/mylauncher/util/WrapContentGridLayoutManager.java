package com.tiberiugaspar.mylauncher.util;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WrapContentGridLayoutManager extends GridLayoutManager {
    public WrapContentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try{
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e){
            Log.e("GridLayoutManager", "onLayoutChildren: something's wrong I can feel it: " + e.getMessage() );
        }
    }
}
