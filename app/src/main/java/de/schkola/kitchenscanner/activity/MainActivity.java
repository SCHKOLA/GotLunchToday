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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.util.CSVFile_Allergie;
import de.schkola.kitchenscanner.util.CSVFile_Teilnahme;
import de.schkola.kitchenscanner.util.Person;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private static File lunch;

    /**
     * Lädt die Daten in den App Cache
     */
    public static void loadDataIntoApp(Activity instance) {
        try {
            CSVFile_Teilnahme file = new CSVFile_Teilnahme(new FileInputStream(new File(instance.getDir("CSV", MainActivity.MODE_PRIVATE), "teilnahme.csv")));
            for (String[] line : file.read()) {
                try {
                    new Person(Integer.parseInt(line[3].replace("\"", "")), line[2].replace("\"", ""), line[4].replace("\"", ""), Integer.parseInt(line[5].replace("\"", "")));
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException e) {
            new AlertDialog.Builder(instance)
                    .setTitle(R.string.fail_title)
                    .setMessage(R.string.csv_encoding_fail)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        } catch (FileNotFoundException e) {
            new AlertDialog.Builder(instance)
                    .setTitle(R.string.fail_title)
                    .setMessage(R.string.csv_read_fail)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        }
        try {
            CSVFile_Allergie allergie = new CSVFile_Allergie(new FileInputStream(new File(instance.getDir("CSV", MainActivity.MODE_PRIVATE), "allergie.csv")));
            for (String[] line : allergie.read()) {
                Person person = Person.getByXBA(Integer.parseInt(line[0]));
                if (person != null) {
                    person.addAllergie(line[1]);
                }
            }
        } catch (UnsupportedEncodingException | FileNotFoundException | ArrayIndexOutOfBoundsException ignored) {
        }
        if (!lunch.exists()) {
            lunch.mkdir();
        }
    }

    /**
     * Startet den Barcodescanner
     */
    public static void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(instance);
        ArrayList<String> qr_code = new ArrayList<>();
        qr_code.add("QR_CODE");
        integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 10L);
        integrator.setBeepEnabled(true);
        integrator.initiateScan(qr_code);
    }

    public static File getLunchDir() {
        return lunch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        lunch = this.getDir("Lunch", MainActivity.MODE_PRIVATE);
        //Setzte Activity Vollbild
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Setzte Content
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadDataIntoApp(this);
        //Setzte Action des Buttons
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null && scanResult.getContents() != null) {
                Log.d("BarCodeScan XBA-Nummer", scanResult.getContents());
                Person person = Person.getByXBA(Integer.parseInt(scanResult.getContents()));
                Intent intent = new Intent(this, DisplayActivity.class);
                if (person != null) {
                    intent.putExtra("name", person.getPersonName());
                    intent.putExtra("class", person.getClazz());
                    intent.putExtra("lunch", person.getLunch());
                    intent.putExtra("gotĹunch", person.getGotLunch() + 1);
                    intent.putExtra("allergie", person.getAllergie());
                    person.gotLunch();
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
                //Startet die Einstellungen
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
