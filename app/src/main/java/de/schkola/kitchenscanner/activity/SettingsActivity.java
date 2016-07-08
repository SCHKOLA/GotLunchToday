package de.schkola.kitchenscanner.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.task.CSVCopy;

public class SettingsActivity extends AppCompatActivity {

    private static SettingsActivity instance;

    public static SettingsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        //Setzte die Activity Vollbild
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Setzte Content
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_CSV_copy:
                //Startet das kopieren der CSV-Dateien
                new AlertDialog.Builder(instance)
                        .setTitle(R.string.copy_title)
                        .setMessage(R.string.copy_request)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new CSVCopy().execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create().show();
                return true;
            case R.id.action_delete_data:
                //Löscht den Cache
                new AlertDialog.Builder(instance)
                        .setTitle(R.string.delete_title)
                        .setMessage(R.string.delete_request)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (File f : MainActivity.getLunchDir().listFiles()) {
                                    f.delete();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
