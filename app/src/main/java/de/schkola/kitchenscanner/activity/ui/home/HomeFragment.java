package de.schkola.kitchenscanner.activity.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.activity.DisplayActivity;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.task.StatTask;
import de.schkola.kitchenscanner.task.TaskRunner;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DisplayActivity.class);
            intent.setAction(Intent.ACTION_RUN);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        if (view != null) {
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
                view.findViewById(R.id.home_loading_screen).setVisibility(View.GONE);
            }));
        }
    }
}