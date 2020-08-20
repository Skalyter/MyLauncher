package com.tiberiugaspar.mylauncher.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.model.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppListViewHolder> {
    public List<AppInfo> appList = new ArrayList<>();
    private Context context;
    private boolean showAllApps;
    private Integer position;
    private int appsPerPage;

    public AppListAdapter(Context context, boolean showAllApps, Integer position) {
        this.context = context;
        this.showAllApps = showAllApps;
        if (position != null) this.position = position-1;
        new AppListThread().execute();
        appsPerPage = 30; //TODO: save this value locally according to user preferences (SharedPref / local DB)
    }

    public void addApp(AppInfo appInfo) {
        this.appList.add(appInfo);
    }

    @NonNull
    @Override
    public AppListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_app, parent, false);
        return new AppListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppListViewHolder holder, int position) {
        String appName = appList.get(position).getLabel().toString();
        Drawable appIcon = appList.get(position).getIcon();

        if (showAllApps){
            holder.appLabel.setTextColor(context.getResources().getColor(R.color.app_label_grey, null));
        } else {
            holder.appLabel.setTextColor(context.getResources().getColor(R.color.app_label_white, null));
        }
        holder.appLabel.setText(appName);
        Glide.with(context).load(appIcon)
                .apply(RequestOptions.fitCenterTransform()).into(holder.appIcon);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    class AppListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView appLabel;
        public ImageView appIcon;

        public AppListViewHolder(@NonNull View itemView) {
            super(itemView);

            appLabel = itemView.findViewById(R.id.app_name);
            appIcon = itemView.findViewById(R.id.app_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Context context = v.getContext();
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(
                            appList.get(position).getPackageName().toString());
            context.startActivity(intent);
        }
    }

    private void updateAdapter() {
        this.notifyItemInserted(this.getItemCount() - 1);
    }

    public class AppListThread extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            final PackageManager pm = context.getPackageManager();
            appList = new ArrayList<>();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> allApps = pm.queryIntentActivities(intent, 0);
            Collections.sort(allApps, new Comparator<ResolveInfo>() {
                @Override
                public int compare(ResolveInfo o1, ResolveInfo o2) {
                    return o1.loadLabel(pm).toString().compareToIgnoreCase(o2.loadLabel(pm).toString());
                }
            });
            if (showAllApps) {
                for (ResolveInfo info : allApps) {
                    AppInfo app = new AppInfo(
                            info.loadLabel(pm),
                            info.activityInfo.packageName,
                            info.activityInfo.loadIcon(pm));
                    addApp(app);
                }
            } else {
                int start = position * appsPerPage;
                int end = position * appsPerPage + appsPerPage;
                for (int i = start; i < end && i < allApps.size(); i++) {
                    ResolveInfo info = allApps.get(i);
                    AppInfo app = new AppInfo(
                            info.loadLabel(pm),
                            info.activityInfo.packageName,
                            info.activityInfo.loadIcon(pm));
                    addApp(app);
                }
            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateAdapter();
        }
    }
}
