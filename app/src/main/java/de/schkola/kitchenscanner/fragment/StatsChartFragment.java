package de.schkola.kitchenscanner.fragment;

import android.app.ProgressDialog;
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
import androidx.fragment.app.Fragment;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.task.ProgressAsyncTask;
import de.schkola.kitchenscanner.task.StatTask;

public class StatsChartFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_stats_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.collecting_data));
        dialog.setMessage(getString(R.string.collecting_data_lunch));
        StatTask statTask = new StatTask((new DatabaseAccess(getContext())).getDatabase(), result -> {
            ((TextView) view.findViewById(R.id.orderedA)).setText(String.valueOf(result.getLunchA()));
            ((TextView) view.findViewById(R.id.gotA)).setText(String.valueOf(result.getDispensedA()));
            ((TextView) view.findViewById(R.id.getA)).setText(String.valueOf(result.getToDispenseA().size()));
            ((TextView) view.findViewById(R.id.orderedB)).setText(String.valueOf(result.getLunchB()));
            ((TextView) view.findViewById(R.id.gotB)).setText(String.valueOf(result.getDispensedB()));
            ((TextView) view.findViewById(R.id.getB)).setText(String.valueOf(result.getToDispenseB().size()));
            ((TextView) view.findViewById(R.id.orderedS)).setText(String.valueOf(result.getLunchS()));
            ((TextView) view.findViewById(R.id.gotS)).setText(String.valueOf(result.getDispensedS()));
            ((TextView) view.findViewById(R.id.getS)).setText(String.valueOf(result.getToDispenseS().size()));
        });
        statTask.setProgressListener(new ProgressAsyncTask.ProgressListener() {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinished() {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        statTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stats_chart, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_list) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, new StatsListFragment())
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
