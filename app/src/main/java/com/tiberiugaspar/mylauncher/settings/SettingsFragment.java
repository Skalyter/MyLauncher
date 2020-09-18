package com.tiberiugaspar.mylauncher.settings;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.tiberiugaspar.mylauncher.R;
import com.tiberiugaspar.mylauncher.util.SettingsUtil;

public class SettingsFragment extends Fragment {

    boolean isMenuAppGridLayout = false, isMenuLauncherStyle = false, isMenuAppsOrder = false;
    View.OnLongClickListener _appGridLayoutClickListener = v -> {

        isMenuAppGridLayout = true;

        return false;
    };
    View.OnLongClickListener _launcherStyleClickListener = v -> {

        isMenuLauncherStyle = true;

        return false;
    };
    View.OnLongClickListener _appsOrderClickListener = (View.OnLongClickListener) v -> {

        isMenuAppsOrder = true;

        return false;
    };
    private TextView labelAppGridLayout, labelLauncherStyle, labelAppsOrder;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cardAppGridLayout = view.findViewById(R.id.card_app_grid_layout);
        CardView cardLauncherStyle = view.findViewById(R.id.card_launcher_style);
        CardView cardAppsOrder = view.findViewById(R.id.card_apps_order);
        labelAppGridLayout = view.findViewById(R.id.label_app_grid_layout);
        labelLauncherStyle = view.findViewById(R.id.label_launcher_style);
        labelAppsOrder = view.findViewById(R.id.label_apps_order);

        labelAppGridLayout.setText(SettingsUtil.getSharedPreferencesAppGridLayout(getContext()));
        labelLauncherStyle.setText(SettingsUtil.getSharedPreferencesLauncherStyle(getContext()));
        labelAppsOrder.setText(SettingsUtil.getSharedPreferencesAppsOrder(getContext()));

        cardAppGridLayout.setOnLongClickListener(_appGridLayoutClickListener);
        cardLauncherStyle.setOnLongClickListener(_launcherStyleClickListener);
        cardAppsOrder.setOnLongClickListener(_appsOrderClickListener);

        registerForContextMenu(cardAppGridLayout);
        registerForContextMenu(cardLauncherStyle);
        registerForContextMenu(cardAppsOrder);

    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {

        if (isMenuAppGridLayout) {

            getActivity().getMenuInflater().inflate(R.menu.menu_app_grid_layout, menu);

            isMenuAppGridLayout = false;

        } else if (isMenuLauncherStyle) {

            getActivity().getMenuInflater().inflate(R.menu.menu_launcher_style, menu);

            isMenuLauncherStyle = false;

        } else {

            getActivity().getMenuInflater().inflate(R.menu.menu_apps_order, menu);

            isMenuAppsOrder = false;

        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        Toast.makeText(getContext(), "Changes saved. Please restart the launcher.", Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.menu_item_grid_4x4:
            case R.id.menu_item_grid_5x4:
            case R.id.menu_item_grid_5x5:
            case R.id.menu_item_grid_6x5:

                SettingsUtil.setAppGridLayout(getContext(), item.getTitle().toString());
                labelAppGridLayout.setText(item.getTitle());

                return true;

            case R.id.menu_item_one_layer:
            case R.id.menu_item_two_layers:

                SettingsUtil.setLauncherStyle(getContext(), item.getTitle().toString());
                labelLauncherStyle.setText(item.getTitle());

                return true;

            case R.id.menu_item_alphabetically:
            case R.id.menu_item_by_frequency:

                SettingsUtil.setAppsOrder(getContext(), item.getTitle().toString());
                labelAppsOrder.setText(item.getTitle());

                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }
}