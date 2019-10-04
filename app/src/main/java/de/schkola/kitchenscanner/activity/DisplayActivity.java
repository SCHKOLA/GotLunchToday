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
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.util.Person;

public class DisplayActivity extends AppCompatActivity {

    private ScheduledExecutorService s;
    private CameraManager manager;
    private AVCallback av;
    private TCallback t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        s = Executors.newScheduledThreadPool(2);
        while (manager == null) {
            manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }
        av = new AVCallback();
        t = new TCallback();
        manager.registerAvailabilityCallback(av, null);
        manager.registerTorchCallback(t, null);
        if (Objects.equals(intent.getAction(), Intent.ACTION_RUN)) {
            startScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null && scanResult.getContents() != null) {
                fillInformation(Person.getByXBA(Integer.parseInt(scanResult.getContents())));
                s.schedule(this::startScan, getSleepTimeMillis(), TimeUnit.MILLISECONDS);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ex) {
            Log.e("DisplayActivity", "Exception onActivityResult", ex);
        }
    }

    @Override
    public void finish() {
        //Shutdown ExecutorService
        s.shutdownNow();
        //Reset torch mode
        manager.unregisterAvailabilityCallback(av);
        manager.unregisterTorchCallback(t);
        try {
            manager.setTorchMode("0", false);
        } catch (CameraAccessException ignored) {
        }
        super.finish();
    }

    private void fillInformation(Person p) {
        TextView name = findViewById(R.id.name);
        TextView clazz = findViewById(R.id.clazz);
        TextView lunch = findViewById(R.id.lunch);
        TextView gotToday = findViewById(R.id.gotToday);
        TextView allergies = findViewById(R.id.allergies);
        if (p != null) {
            p.gotLunch();
            name.setText(p.getPersonName());
            clazz.setText(p.getClazz());
            lunch.setText(p.getLunch());
            byte gotLunch = p.getGotLunch();
            if (gotLunch > 1) {
                gotToday.setText(String.format(getString(R.string.gotLunch), gotLunch));
            }
            allergies.setText(p.getAllergies());
        } else {
            name.setText("");
            clazz.setText("");
            lunch.setText("X");
            gotToday.setText("");
            allergies.setText("");
        }
    }

    private int getSleepTimeMillis() {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("rescan", "2")) * 1000;
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

    private class AVCallback extends CameraManager.AvailabilityCallback {

        @Override
        public void onCameraAvailable(@NonNull String cameraId) {
            if (cameraId.equals("0")) {
                try {
                    manager.setTorchMode("0", true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TCallback extends CameraManager.TorchCallback {

        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            if (cameraId.equals("0") && enabled) {
                s.schedule(() -> {
                    try {
                        manager.setTorchMode("0", false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }, 150, TimeUnit.MILLISECONDS);
            }
        }
    }
}
