package de.schkola.kitchenscanner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.task.StatTask;
import de.schkola.kitchenscanner.task.TaskRunner;

public class StatsChartFragment extends Fragment {

    private final OptionsMenuProvider optionsMenuProvider = new OptionsMenuProvider();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.addMenuProvider(optionsMenuProvider);
        }
        return inflater.inflate(R.layout.content_stats_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LunchDatabase database = new DatabaseAccess(getContext()).getDatabase();
        TaskRunner.INSTANCE.executeAsyncTask(new StatTask(database, result -> {
            database.close();
            ((TextView) view.findViewById(R.id.orderedA)).setText(String.valueOf(result.getLunchA()));
            ((TextView) view.findViewById(R.id.gotA)).setText(String.valueOf(result.getDispensedA()));
            ((TextView) view.findViewById(R.id.getA)).setText(String.valueOf(result.getToDispenseA().size()));
            ((TextView) view.findViewById(R.id.orderedB)).setText(String.valueOf(result.getLunchB()));
            ((TextView) view.findViewById(R.id.gotB)).setText(String.valueOf(result.getDispensedB()));
            ((TextView) view.findViewById(R.id.getB)).setText(String.valueOf(result.getToDispenseB().size()));
            ((TextView) view.findViewById(R.id.orderedS)).setText(String.valueOf(result.getLunchS()));
            ((TextView) view.findViewById(R.id.gotS)).setText(String.valueOf(result.getDispensedS()));
            ((TextView) view.findViewById(R.id.getS)).setText(String.valueOf(result.getToDispenseS().size()));
            view.findViewById(R.id.statsOverviewLoadingScreen).setVisibility(View.GONE);
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.removeMenuProvider(optionsMenuProvider);
        }
    }

    public class OptionsMenuProvider implements MenuProvider {

        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_stats_chart, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_list) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, new StatsListFragment())
                        .commit();
                return true;
            }
            return false;
        }
    }
}
