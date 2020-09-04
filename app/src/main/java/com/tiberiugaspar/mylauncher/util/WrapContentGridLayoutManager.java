package com.tiberiugaspar.mylauncher.util;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class was created in order to fix a bug that created an IndexOutOfBoundsException when an adapter
 * was reused in more than one scenario. It only logs the exception when it occurs, without causing any other
 * bugs to the app
 */
public class WrapContentGridLayoutManager extends GridLayoutManager {

    public WrapContentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("GridLayoutManager", "onLayoutChildren: something's wrong I can feel it: " + e.getMessage());
        }
    }
}
