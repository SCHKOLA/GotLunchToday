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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.task.CustomerUpdateTask;
import de.schkola.kitchenscanner.task.DatabaseCustomerTask;
import de.schkola.kitchenscanner.util.AllergyUtil;
import de.schkola.kitchenscanner.util.LunchResult;
import de.schkola.kitchenscanner.util.LunchUtil;
import de.schkola.kitchenscanner.util.TorchManager;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DisplayActivity extends AppCompatActivity {

    private ScheduledExecutorService s;
    private TorchManager tm;
    private LunchDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        s = Executors.newScheduledThreadPool(1);
        tm = new TorchManager(this);
        if (Objects.equals(intent.getAction(), Intent.ACTION_RUN)) {
            startScan();
        }
        database = new DatabaseAccess(getApplicationContext()).getDatabase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (scanResult != null && scanResult.getContents() != null) {
                        new DatabaseCustomerTask(database, (c) -> {
                            fillInformation(c);
                            s.schedule(this::startScan, getSleepTimeMillis(), TimeUnit.MILLISECONDS);
                        }).execute(Integer.parseInt(scanResult.getContents()));
                    }
                } catch (Exception ex) {
                    Log.e("DisplayActivity", "Exception onActivityResult", ex);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
        //Shutdown ExecutorService
        s.shutdownNow();
        tm.shutdown();
        super.finish();
    }

    private void fillInformation(LunchResult result) {
        Customer c = result.getCustomer();
        List<Allergy> a = result.getAllergies();
        TextView name = findViewById(R.id.name);
        TextView clazz = findViewById(R.id.clazz);
        TextView lunch = findViewById(R.id.lunch);
        TextView gotToday = findViewById(R.id.gotToday);
        TextView allergies = findViewById(R.id.allergies);
        if (c != null) {
            c.gotLunch += 1;
            name.setText(c.name);
            if (!c.grade.equals("Mitarbeiter")) {
                clazz.setText(String.format("%s. Klasse", c.grade));
            } else {
                clazz.setText(c.grade);
            }
            lunch.setText(LunchUtil.getLunch(c.lunch));
            if (c.gotLunch > 1) {
                gotToday.setText(String.format(getString(R.string.gotLunch), c.gotLunch));
            } else {
                gotToday.setText("");
            }
            allergies.setText(AllergyUtil.getAllergies(a));
            new CustomerUpdateTask(database).execute(c);
        } else {
            name.setText("");
            clazz.setText("");
            lunch.setText("X");
            gotToday.setText("");
            allergies.setText("");
        }
    }

    private int getSleepTimeMillis() {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("rescan", "2")) * 1000;
    }

    /**
     * Starts barcode scan
     */
    private void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 10L);
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.initiateScan(Collections.singletonList("QR_CODE"));
    }
}
