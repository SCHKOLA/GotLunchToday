package de.schkola.kitchenscanner.activity.ui.lunch_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.task.StatTask;
import de.schkola.kitchenscanner.task.TaskRunner;
import de.schkola.kitchenscanner.util.LunchListAdapter;

public class LunchListFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lunch_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LunchDatabase database = new DatabaseAccess(getContext()).getDatabase();
        TaskRunner.INSTANCE.executeAsyncTask(new StatTask(database, result -> {
            database.close();
            ExpandableListView listView = view.findViewById(R.id.lunch_list_view);
            listView.setAdapter(new LunchListAdapter(getContext(), result.getToDispenseA(), result.getToDispenseB(), result.getToDispenseS()));
            view.findViewById(R.id.lunch_list_loading_screen).setVisibility(View.GONE);
        }));
    }
}