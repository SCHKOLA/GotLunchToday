package de.schkola.kitchenscanner.activity;

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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import de.schkola.kitchenscanner.util.CSVFile;
import de.schkola.kitchenscanner.util.Person;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.task.CSVCopy;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private static File lunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        lunch = instance.getDir("Lunch", MainActivity.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadDataIntoApp();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
    }

    public static void loadDataIntoApp() {
        try {
            CSVFile file = new CSVFile(new FileInputStream(new File(instance.getDir("CSV", MainActivity.MODE_PRIVATE), "teilnahme.csv")));
            for (String[] line : file.read(true)) {
                new Person(Integer.parseInt(line[3]), line[2], line[4], Integer.parseInt(line[6]));
            }
            CSVFile allergie = new CSVFile(new FileInputStream(new File(instance.getDir("CSV", MainActivity.MODE_PRIVATE), "allergie.csv")));
            for (String[] line : allergie.read(false)) {
                Person person = Person.getByXBA(Integer.parseInt(line[0]));
                if (person != null) {
                    person.addAllergie(line[1]);
                }
            }
        } catch (UnsupportedEncodingException e) {
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
        if (!lunch.exists()) {
            lunch.mkdir();
        } else {
            for (File f : lunch.listFiles()) {
                f.delete();
            }
        }
    }

    public static void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(instance);
        ArrayList<String> qr_code = new ArrayList<>();
        qr_code.add("QR_CODE");
        integrator.initiateScan(qr_code);
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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_CSV_copy:
                new CSVCopy().execute();
                return true;
            case R.id.action_delete_data:
                Person.clearData();
                for (File f : lunch.listFiles()) {
                    f.delete();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }
}
