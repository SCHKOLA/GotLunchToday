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

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.task.DoLaterTask;

public class DisplayActivity extends AppCompatActivity {

    private static DoLaterTask rct;
    private static Camera camera;

    public static void setFlashLight(boolean b) {
        if (camera == null) {
            try {
                camera = Camera.open();
                camera.startPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        if (camera != null) {
            Camera.Parameters p = camera.getParameters();
            if (b) {
                // Turn on
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
            } else {
                // Turn off
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.release();
                camera = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Stop last RescanTask (Bug-Fix)
        if (rct != null) {
            rct.cancel(true);
        }
        super.onCreate(savedInstanceState);
        //Start DoLaterTask
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            new DoLaterTask(250, () -> DisplayActivity.setFlashLight(true), () -> DisplayActivity.setFlashLight(false)).execute();
        } else {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                manager.setTorchMode("0", true);
                new DoLaterTask(250, () -> {
                    try {
                        manager.setTorchMode("0", false);
                    } catch (CameraAccessException ignored) {
                    }
                });
            } catch (CameraAccessException ignored) {
            }
        }
        //Set die Activity Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set Content
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        //Edit Content
        ((TextView) findViewById(R.id.name)).setText(intent.getStringExtra("name"));
        ((TextView) findViewById(R.id.clazz)).setText(intent.getStringExtra("class"));
        ((TextView) findViewById(R.id.lunch)).setText(intent.getStringExtra("lunch"));
        TextView tv_gotLunch = (TextView) findViewById(R.id.gotToday);
        if (intent.getIntExtra("gotLunch", 0) > 1) {
            tv_gotLunch.setText(String.format("%s%s%s", getString(R.string.gotLunch_2), String.valueOf(intent.getIntExtra("gotLunch", 0)), getString(R.string.gotLunch_1)));
        }
        ((TextView) findViewById(R.id.allergie)).setText(intent.getStringExtra("allergies"));
        //Start rescan
        rct = new DoLaterTask(getSleepTimeMillis(), () -> {
            finish();
            MainActivity.startScan();
        });
        rct.execute();
    }

    private int getSleepTimeMillis() {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("rescan", "2")) * 1000;
    }
}
