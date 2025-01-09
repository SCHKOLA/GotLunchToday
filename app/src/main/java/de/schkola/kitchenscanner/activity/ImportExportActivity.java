package de.schkola.kitchenscanner.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.task.TaskRunner;
import de.schkola.kitchenscanner.task.import_export.AllergyImportTask;
import de.schkola.kitchenscanner.task.import_export.LunchExportTask;
import de.schkola.kitchenscanner.task.import_export.LunchImportTask;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class ImportExportActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "TYPE";
    public static final String TYPE_LUNCH_IMPORT = "LUNCH_IMPORT";
    public static final String TYPE_ALLERGY_IMPORT = "ALLERGY_IMPORT";
    public static final String TYPE_LUNCH_EXPORT = "LUNCH_EXPORT";

    private static final String[] mimeTypes = {"text/csv", "text/comma-separated-values", "application/octet-stream"};

    private final ActivityResultLauncher<String> getFileLunchExport = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), this::handleSetUri);
    private final ActivityResultLauncher<String[]> getFileLunchImport = registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::handleSetUri);
    private final ActivityResultLauncher<String[]> getFileAllergyImport = registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::handleSetUri);

    private DatabaseAccess dbAccess;
    private String importExportType;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        dbAccess = new DatabaseAccess(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null) {
            TextView display = findViewById(R.id.importExportTypeDisplay);
            importExportType = intent.getStringExtra(EXTRA_TYPE);

            switch (importExportType != null ? importExportType : "") {
                case TYPE_LUNCH_IMPORT:
                    display.setText(R.string.import_lunch_data);
                    break;
                case TYPE_ALLERGY_IMPORT:
                    display.setText(R.string.import_allergy_data);
                    break;
                case TYPE_LUNCH_EXPORT:
                    display.setText(R.string.export_lunch_data);
                    break;
                default:
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.error_parameter)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                            .create().show();
                    break;
            }
        }

        Button fileChooseButton = findViewById(R.id.fileChooseButton);
        fileChooseButton.setOnClickListener(this::fileChooseButtonClick);
        EditText fileEditText = findViewById(R.id.fileEditText);
        fileEditText.setOnClickListener(this::fileChooseButtonClick);

        Button startActionButton = findViewById(R.id.startActionButton);
        startActionButton.setOnClickListener(this::startActionButtonClick);
    }

    private void fileChooseButtonClick(View view) {
        EditText fileEditText = findViewById(R.id.fileEditText);
        fileEditText.setText("");
        this.uri = null;

        switch (importExportType != null ? importExportType : "") {
            case TYPE_LUNCH_IMPORT:
                Toast.makeText(this, R.string.choose_day, Toast.LENGTH_LONG).show();
                getFileLunchImport.launch(mimeTypes);
                break;
            case TYPE_ALLERGY_IMPORT:
                Toast.makeText(this, R.string.choose_allergy, Toast.LENGTH_LONG).show();
                getFileAllergyImport.launch(mimeTypes);
                break;
            case TYPE_LUNCH_EXPORT:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = new Date(System.currentTimeMillis());
                getFileLunchExport.launch(String.format("export-lunch-%s.csv", formatter.format(date)));
                break;
            default:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.error_parameter)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                        .create().show();
                break;
        }
    }

    private void startActionButtonClick(View view) {
        if (this.uri == null) {
            return;
        }
        TextView importExportLoadingText = findViewById(R.id.importExportLoadingText);
        switch (importExportType != null ? importExportType : "") {
            case TYPE_LUNCH_IMPORT:
                importExportLoadingText.setText(R.string.import_ongoing);
                findViewById(R.id.importExportLoadingScreen).setVisibility(View.VISIBLE);
                startLunchImport(this.uri);
                break;
            case TYPE_ALLERGY_IMPORT:
                importExportLoadingText.setText(R.string.import_ongoing);
                findViewById(R.id.importExportLoadingScreen).setVisibility(View.VISIBLE);
                startAllergyImport(this.uri);
                break;
            case TYPE_LUNCH_EXPORT:
                importExportLoadingText.setText(R.string.export_ongoing);
                findViewById(R.id.importExportLoadingScreen).setVisibility(View.VISIBLE);
                startLunchExport(this.uri);
                break;
            default:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.error_parameter)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                        .create().show();
                break;
        }
    }

    private void handleSetUri(Uri uri) {
        EditText fileEditText = findViewById(R.id.fileEditText);
        this.uri = uri;
        if (uri == null) {
            fileEditText.setText("");
        } else {
            fileEditText.setText(uri.getLastPathSegment());
        }
    }

    private void startLunchImport(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            LunchImportTask lunchImportTask = new LunchImportTask(dbAccess.getDatabase(), inputStream, duplicates -> {
                findViewById(R.id.importExportLoadingScreen).setVisibility(View.GONE);
                if (duplicates.isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.success)
                            .setMessage(R.string.import_successful)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                            .create().show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.duplicate_entries)
                            .setItems(duplicates.stream().map(duplicate -> String.format(Locale.GERMANY, "[%d] %s {%s}", duplicate.getXba(), duplicate.getName(), duplicate.getLunches().stream().map(Object::toString).collect(Collectors.joining(",")))).toArray(String[]::new), null)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                            .create().show();
                }
            });
            TaskRunner.INSTANCE.executeAsyncTask(lunchImportTask);
        } catch (FileNotFoundException e) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_file_not_found)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
            Log.e(ImportExportActivity.class.getName(), "File not found (lunch import)", e);
        }
    }

    private void startAllergyImport(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            AllergyImportTask allergyImportTask = new AllergyImportTask(dbAccess.getDatabase(), inputStream, () -> {
                findViewById(R.id.importExportLoadingScreen).setVisibility(View.GONE);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.success)
                        .setMessage(R.string.import_successful)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                        .create().show();
            });
            TaskRunner.INSTANCE.executeAsyncTask(allergyImportTask);
        } catch (FileNotFoundException e) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_file_not_found)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
            Log.e(ImportExportActivity.class.getName(), "File not found (allergy import)", e);
        }
    }

    private void startLunchExport(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            LunchExportTask lunchExportTask = new LunchExportTask(dbAccess.getDatabase(), outputStream, () -> {
                findViewById(R.id.importExportLoadingScreen).setVisibility(View.GONE);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.success)
                        .setMessage(R.string.export_successful)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> finish())
                        .create().show();
            });
            TaskRunner.INSTANCE.executeAsyncTask(lunchExportTask);
        } catch (FileNotFoundException e) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_file_not_found)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
            Log.e(ImportExportActivity.class.getName(), "File not found (lunch export)", e);
        }
    }
}
