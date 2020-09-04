package com.tiberiugaspar.mylauncher.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Vibrator;
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
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppListViewHolder> {

    public List<AppInfo> appList = new ArrayList<>();
    private Context context;
    private boolean showAllApps;

    private Integer pageNumber;
    private AppInfo appInfo;

    private int appsPerPage;

    private Vibrator vibrator;


    public AppListAdapter(Context context, boolean showAllApps, Integer pageNumber) {

        this.context = context;
        this.showAllApps = showAllApps;

        if (pageNumber != null) {
            this.pageNumber = pageNumber - 1;
        }

        new GetAppListThread().execute();
        appsPerPage = 30; //TODO: save this value locally according to user preferences (SharedPref / local DB)
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void setPageNumber(int pageNumber) {

        //update the list of apps for each screen, according to the number of page
        this.pageNumber = pageNumber;
        if (!appList.isEmpty()) {
            appList.clear();
        }
        new GetAppListThread().execute();
    }

    public AppInfo getSelectedApp() {
        return appInfo;
    }

    public void removeApp(AppInfo appInfo) {

        int position = appList.indexOf(appInfo);

        //TODO: remove app from DB

        //removing app from the list and updating the adapter
        appList.remove(appInfo);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, appList.size());
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

        //if showAllApps == true that means we're in the Drawer view, with the white background,
        //so we'll need to set the app name text color to grey;
        // otherwise, we're in the Home Screen, so it will be white.
        if (showAllApps) {
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

    /*
     * This method is called when the AsyncTask thread is finished and the
     * onPostExecute method is called - to notify the adapter that the item list have new items
     * */
    private void updateAdapter() {
        this.notifyItemInserted(appList.size() - 1);
    }

    class AppListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public TextView appLabel;
        public ImageView appIcon;

        public AppListViewHolder(@NonNull View itemView) {
            super(itemView);

            appLabel = itemView.findViewById(R.id.app_name);
            appIcon = itemView.findViewById(R.id.app_icon);

            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            //When the user clicks on any app, it will be opened using the launch intent for selected
            //app's package
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(
                            appList.get(position).getPackageName().toString());
            context.startActivity(intent);

            //TODO: increment package accessed number in DB
        }

        @Override
        public boolean onLongClick(View v) {
            //When the user long clicks on any app, an haptic feedback is given
            // and the context menu is showed

            vibrator.vibrate(context.getResources().getInteger(R.integer.vibrate_duration));

            v.showContextMenu();

            //We set the appInfo object with the selected app, so it can be called from the activity
            appInfo = appList.get(getAdapterPosition());

            return true;
        }
    }

    /*
     * This class is used to retrieve the list of all applications which are installed on the user's
     * device, using an AsyncTask
     * */
    public class GetAppListThread extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            //TODO: move these lines inside the if(showAllApps) condition

            final PackageManager pm = context.getPackageManager();
            appList = new ArrayList<>();

            //creating an intent object with the ACTION_MAIN and CATEGORY_LAUNCHER flags
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            //getting all apps using the package manager's queryIntentActivities() method
            List<ResolveInfo> allApps = pm.queryIntentActivities(intent, 0);

            //sorting the app list so it can be shown alphabetically, using an anonymous comparator
            Collections.sort(allApps, (o1, o2) -> o1.loadLabel(pm).toString().compareToIgnoreCase(o2.loadLabel(pm).toString()));

            if (showAllApps) {
                for (ResolveInfo info : allApps) {
                    AppInfo app = new AppInfo(
                            info.loadLabel(pm),
                            info.activityInfo.packageName,
                            info.activityInfo.loadIcon(pm));
                    addApp(app);
                }
            } else {
                //TODO: get apps from db according to the page number and delete the lines below
                int start = pageNumber * appsPerPage;
                int end = pageNumber * appsPerPage + appsPerPage;
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

            //notify adapter that the list got new items
            updateAdapter();
        }
    }
}