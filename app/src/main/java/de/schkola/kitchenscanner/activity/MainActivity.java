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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.util.JsonAllergyParser;
import de.schkola.kitchenscanner.util.JsonDayParser;
import de.schkola.kitchenscanner.util.Person;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private static boolean loaded = false;

    /**
     * Loads data into the app cache
     */
    private boolean loadDataIntoApp() {
        if (!loaded) {
            try {
                new JsonDayParser(new File(getDir("JSON", MODE_PRIVATE), "day.json"), this).parse();
            } catch (IOException e) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.no_file_found)
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
                return false;
            }
            try {
                new JsonAllergyParser(new File(getDir("JSON", MODE_PRIVATE), "allergy.json")).parse();
            } catch (IOException ignored) {
            }
            loaded = true;
        }
        return true;
    }

    /**
     * Starts barcode scan
     */
    public static void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(instance);
        ArrayList<String> qr_code = new ArrayList<>();
        qr_code.add("QR_CODE");
        integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 10L);
        integrator.setBeepEnabled(true);
        integrator.initiateScan(qr_code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        File lunch = this.getDir("Lunch", MODE_PRIVATE);
        if (!lunch.exists()) {
            lunch.mkdir();
        }
        //Set Content
        setContentView(R.layout.activity_main);

        //Set Action Buttons
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (loadDataIntoApp()) {
                startScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null && scanResult.getContents() != null) {
                Person person = Person.getByXBA(Integer.parseInt(scanResult.getContents()));
                Intent intent = new Intent(this, DisplayActivity.class);
                if (person != null) {
                    person.gotLunch();
                    intent.putExtra("name", person.getPersonName());
                    intent.putExtra("class", person.getClazz());
                    intent.putExtra("lunch", person.getLunch());
                    intent.putExtra("gotLunch", person.getGotLunch());
                    intent.putExtra("allergies", person.getAllergies());
                } else {
                    intent.putExtra("lunch", "X");
                }
                startActivity(intent);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Start settings
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_stats:
                if (loadDataIntoApp()) {
                    startActivity(new Intent(this, StatsActivity.class));
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
