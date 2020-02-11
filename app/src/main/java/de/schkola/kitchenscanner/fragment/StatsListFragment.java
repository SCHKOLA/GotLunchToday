package de.schkola.kitchenscanner.fragment;

import android.app.ProgressDialog;
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
import androidx.fragment.app.Fragment;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.task.StatTask;
import de.schkola.kitchenscanner.util.LunchListAdapter;

public class StatsListFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_stats_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.collecting_data));
        dialog.setMessage(getString(R.string.collecting_data_lunch));
        new StatTask(dialog, (new DatabaseAccess(getContext())).getDatabase(), (result) -> {
            ExpandableListView listView = view.findViewById(R.id.listview);
            listView.setAdapter(new LunchListAdapter(getContext(), result.getToDispenseA(), result.getToDispenseB(), result.getToDispenseS()));
        }).execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stats_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_chart) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, new StatsChartFragment())
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
