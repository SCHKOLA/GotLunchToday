/*
 * MIT License
 *
 * Copyright 2016 Niklas Merkelt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.task.CsvImportTask;
import de.schkola.kitchenscanner.task.LunchExportTask;
import de.schkola.kitchenscanner.task.TaskRunner;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseAccess dbAccess;
    private final ActivityResultLauncher<String> createDocument = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), this::handleExport);
    private final ActivityResultLauncher<String[]> getContentDay = registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::handleUriDay);
    private final ActivityResultLauncher<String[]> getContentAllergy = registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::handleUriAllergy);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Content
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        dbAccess = new DatabaseAccess(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_CSV_copy) {
            //Start copy CSV-Files
            new AlertDialog.Builder(this)
                    .setTitle(R.string.csv_import)
                    .setMessage(R.string.copy_request_day)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        deleteFiles();
                        startImport(false);
                    })
                    .setNegativeButton(R.string.no, (dialog, witch) -> startImport(false))
                    .setNeutralButton(R.string.cancel, null)
                    .create().show();
            return true;
        } else if (item.getItemId() == R.id.action_CSV_copy_allergy) {
            //Start copy CSV-Files
            new AlertDialog.Builder(this)
                    .setTitle(R.string.csv_import)
                    .setMessage(R.string.copy_request_allergy)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> startImport(true))
                    .setNegativeButton(R.string.no, null)
                    .create().show();
            return true;
        } else if (item.getItemId() == R.id.action_delete_data) {
            //Delete Cache
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete_request)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> deleteFiles())
                    .setNegativeButton(R.string.no, null)
                    .create().show();
            return true;
        } else if (item.getItemId() == R.id.action_export_lunch_data) {
            //Export Lunch Data to CSV
            startExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startImport(boolean allergy) {
        int text = allergy ? R.string.choose_allergy : R.string.choose_day;
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        String[] mimeTypes = {"text/csv", "text/comma-separated-values", "application/octet-stream"};
        if (allergy) {
            getContentAllergy.launch(mimeTypes);
        } else {
            getContentDay.launch(mimeTypes);
        }
    }

    private void deleteFiles() {
        TaskRunner.INSTANCE.executeAsync(() -> dbAccess.getDatabase().customerDao().deleteAll());
    }

    private void startExport() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        createDocument.launch(String.format("export-lunch-%s.csv", formatter.format(date)));
    }

    @NonNull
    private LunchExportTask createExportTask(OutputStream outputStream) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.lunch_export));
        dialog.setMessage(getString(R.string.lunch_export_ongoing));
        LunchExportTask lunchExportTask = new LunchExportTask(dbAccess.getDatabase(), outputStream);
        lunchExportTask.setProgressDialog(dialog);
        return lunchExportTask;
    }

    @NonNull
    private CsvImportTask createImportTask(boolean allergy, InputStream inputStream) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.csv_import));
        dialog.setMessage(getString(R.string.csv_import_ongoing));
        CsvImportTask csvImportTask = new CsvImportTask(dbAccess.getDatabase(), allergy, inputStream);
        csvImportTask.setProgressDialog(dialog);
        csvImportTask.setCsvImportListener(duplicateXba -> new AlertDialog.Builder(this)
                .setTitle(R.string.duplicate_xba)
                .setItems(duplicateXba.toArray(new String[0]), null)
                .setPositiveButton(android.R.string.ok, null)
                .create().show());
        return csvImportTask;
    }

    private void handleUriDay(Uri uri) {
        handleUri(uri, false);
    }

    private void handleUriAllergy(Uri uri) {
        handleUri(uri, true);
    }

    private void handleUri(Uri data, boolean allergy) {
        if (data != null) {
            InputStream inputStream;
            try {
                inputStream = getContentResolver().openInputStream(data);
            } catch (FileNotFoundException ignored) {
                return;
            }
            TaskRunner.INSTANCE.executeAsyncTask(createImportTask(allergy, inputStream));
        }
    }

    private void handleExport(Uri data) {
        if (data != null) {
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(data);
                TaskRunner.INSTANCE.executeAsyncTask(createExportTask(outputStream));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
