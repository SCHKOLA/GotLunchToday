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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.task.CSVSearch;
import de.schkola.kitchenscanner.task.JsonAllergyTask;
import de.schkola.kitchenscanner.task.JsonDayTask;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DAY = 42;
    private static final int REQUEST_CODE_ALLERGY = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set die Activity Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set Content
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
                //Start copy CSV-Files
                new AlertDialog.Builder(this)
                        .setTitle(R.string.copy_title)
                        .setMessage(R.string.copy_request)
                        .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                new CSVSearch(this, false).execute();
                            } else {
                                Toast.makeText(this, "Bitte wähle die Tagesdatei!", Toast.LENGTH_LONG);
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("text/*");
                                startActivityForResult(intent, REQUEST_CODE_DAY);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create().show();
                return true;
            case R.id.action_CSV_copy_allergy:
                //Start copy CSV-Files
                new AlertDialog.Builder(this)
                        .setTitle(R.string.copy_title)
                        .setMessage(R.string.copy_request)
                        .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                new CSVSearch(this, true).execute();
                            } else {
                                Toast.makeText(this, "Bitte wähle die Allergiedatei!", Toast.LENGTH_LONG);
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("text/*");
                                startActivityForResult(intent, REQUEST_CODE_ALLERGY);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create().show();
                return true;
            case R.id.action_delete_data:
                //Delete Cache
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_title)
                        .setMessage(R.string.delete_request)
                        .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                            for (File f : getDir("Lunch", MainActivity.MODE_PRIVATE).listFiles()) {
                                f.delete();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        try {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                if (requestCode == REQUEST_CODE_DAY) {
                    new JsonDayTask(getContentResolver().openInputStream(resultData.getData()), this).execute();
                } else if (requestCode == REQUEST_CODE_ALLERGY) {
                    new JsonAllergyTask(getContentResolver().openInputStream(resultData.getData()), this).execute();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
