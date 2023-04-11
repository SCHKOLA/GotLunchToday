package de.schkola.kitchenscanner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
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
import de.schkola.kitchenscanner.util.LunchListAdapter;

public class StatsListFragment extends Fragment {

    private final OptionsMenuProvider optionsMenuProvider = new OptionsMenuProvider();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.addMenuProvider(optionsMenuProvider);
        }
        return inflater.inflate(R.layout.content_stats_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LunchDatabase database = new DatabaseAccess(getContext()).getDatabase();
        TaskRunner.INSTANCE.executeAsyncTask(new StatTask(database, result -> {
            database.close();
            ExpandableListView listView = view.findViewById(R.id.listview);
            listView.setAdapter(new LunchListAdapter(getContext(), result.getToDispenseA(), result.getToDispenseB(), result.getToDispenseS()));
            view.findViewById(R.id.statsListLoadingScreen).setVisibility(View.GONE);
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
            menuInflater.inflate(R.menu.menu_stats_list, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_chart) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.container, new StatsChartFragment())
                        .commit();
                return true;
            }
            return false;
        }
    }
}
