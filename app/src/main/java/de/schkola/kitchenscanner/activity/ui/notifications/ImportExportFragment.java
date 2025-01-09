package de.schkola.kitchenscanner.activity.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.activity.ImportExportActivity;

public class ImportExportFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import_export, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button importExportStartButton = view.findViewById(R.id.import_export_start);
        importExportStartButton.setOnClickListener(v -> {
            RadioGroup importExportType = view.findViewById(R.id.import_export_type);
            Intent intent = new Intent(getContext(), ImportExportActivity.class);
            if (importExportType.getCheckedRadioButtonId() == R.id.type_lunch_import) {
                intent.putExtra(ImportExportActivity.EXTRA_TYPE, ImportExportActivity.TYPE_LUNCH_IMPORT);
            } else if (importExportType.getCheckedRadioButtonId() == R.id.type_allergy_import) {
                intent.putExtra(ImportExportActivity.EXTRA_TYPE, ImportExportActivity.TYPE_ALLERGY_IMPORT);
            } else if (importExportType.getCheckedRadioButtonId() == R.id.type_lunch_export) {
                intent.putExtra(ImportExportActivity.EXTRA_TYPE, ImportExportActivity.TYPE_LUNCH_EXPORT);
            }
            startActivity(intent);
        });
    }
}