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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.DatabaseAccess;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.task.TaskRunner;
import de.schkola.kitchenscanner.util.LunchResult;
import de.schkola.kitchenscanner.util.StringUtil;
import de.schkola.kitchenscanner.util.TorchManager;
import io.github.g00fy2.quickie.QRResult;
import io.github.g00fy2.quickie.ScanCustomCode;
import io.github.g00fy2.quickie.config.BarcodeFormat;
import io.github.g00fy2.quickie.config.ScannerConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class DisplayActivity extends AppCompatActivity {

    private final ActivityResultLauncher<ScannerConfig> qrScanner = registerForActivityResult(new ScanCustomCode(), this::handleScanResult);
    private ScheduledExecutorService s;
    private TorchManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        if (isRescanEnabled()) {
            s = Executors.newSingleThreadScheduledExecutor();
        } else {
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(view -> startScan());
        }
        tm = new TorchManager(this);
        if (Objects.equals(intent.getAction(), Intent.ACTION_RUN)) {
            startScan();
        }
    }

    @Override
    public void finish() {
        //Shutdown ExecutorService
        if (s != null) {
            s.shutdownNow();
        }
        tm.shutdown();
        super.finish();
    }

    private void clearInformation() {
        TextView name = findViewById(R.id.name);
        TextView clazz = findViewById(R.id.clazz);
        TextView lunch = findViewById(R.id.lunch);
        TextView gotToday = findViewById(R.id.gotToday);
        TextView allergies = findViewById(R.id.allergies);
        name.setText("");
        clazz.setText("");
        lunch.setText("");
        gotToday.setText("");
        allergies.setText("");
    }

    private void fillInformation(@NonNull LunchResult result) {
        clearInformation();
        Customer c = result.getCustomer();
        List<Allergy> a = result.getAllergies();
        TextView name = findViewById(R.id.name);
        TextView clazz = findViewById(R.id.clazz);
        TextView lunch = findViewById(R.id.lunch);
        TextView gotToday = findViewById(R.id.gotToday);
        TextView allergies = findViewById(R.id.allergies);
        if (c != null) {
            name.setText(c.name);
            if (c.grade.matches("\\d+")) {
                clazz.setText(getString(R.string.x_class, c.grade));
            } else {
                clazz.setText(c.grade);
            }
            lunch.setText(StringUtil.getLunch(c.lunch));
            if (c.gotLunch > 1) {
                gotToday.setText(getString(R.string.gotLunch, c.gotLunch));
            } else {
                gotToday.setText("");
            }
            allergies.setText(StringUtil.getAllergies(a));
        } else {
            lunch.setText("X");
        }
    }

    private boolean isRescanEnabled() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref != null) {
            return pref.getBoolean("rescan_enabled", true);
        }
        return true;
    }

    private int getRescanTime() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref != null) {
            return Integer.parseInt(pref.getString("rescan_time", "2"));
        }
        return 2;
    }

    /**
     * Starts barcode scan
     */
    private void startScan() {
        ScannerConfig scannerConfig = new ScannerConfig.Builder()
                .setBarcodeFormats(Collections.singletonList(BarcodeFormat.FORMAT_QR_CODE))
                .setHapticSuccessFeedback(false)
                .setShowTorchToggle(false)
                .setShowCloseButton(true)
                .setUseFrontCamera(false)
                .setKeepScreenOn(true)
                .build();
        qrScanner.launch(scannerConfig);
    }

    private void handleScanResult(@NonNull QRResult result) {
        if (result instanceof QRResult.QRError) {
            startScan();
        } else if (result instanceof QRResult.QRUserCanceled) {
            finish();
        } else if (result instanceof QRResult.QRSuccess) {
            String rawValue = ((QRResult.QRSuccess) result).component1().getRawValue();
            if (rawValue == null) {
                startScan();
            } else {
                processResult(rawValue);
            }
        }
    }

    private void processResult(@NotNull String rawValue) {
        Context applicationContext = getApplicationContext();
        TaskRunner.INSTANCE.executeAsync(() -> {
            LunchDatabase database = new DatabaseAccess(applicationContext).getDatabase();
            List<Allergy> a = new ArrayList<>();
            String cleanedString = rawValue.trim().replaceAll("\\D", "");
            try {
                int xba = Integer.parseInt(cleanedString);
                Customer c = database.customerDao().getCustomer(xba);
                if (c != null) {
                    c.gotLunch += 1;
                    a.addAll(database.allergyDao().getAllergies(c.xba));
                }
                database.customerDao().updateCustomer(c);
                database.close();
                return new LunchResult(c, a);
            } catch (NumberFormatException e) {
                return new LunchResult(null, a);
            }
        }, lunchResult -> {
            fillInformation(lunchResult);
            if (isRescanEnabled()) {
                s.schedule(this::startScan, getRescanTime(), TimeUnit.SECONDS);
            }
        });
    }
}
