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

package de.schkola.kitchenscanner.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.schkola.kitchenscanner.R;

/**
 * Diese Klassen sorgt für das Asyncrone Kopieren der CSV Dateien
 */
public class CSVCopy extends AsyncTask<Void, Void, Boolean> {

    private final ProgressDialog dialog;
    private final Activity instance;
    private final boolean allergy;

    public CSVCopy(Activity instance, boolean allergy) {
        this.instance = instance;
        this.allergy = allergy;
        this.dialog = new ProgressDialog(instance);
        this.dialog.setCancelable(false);
        this.dialog.setTitle(instance.getString(R.string.copy_title));
        this.dialog.setMessage(instance.getString(R.string.copy_alert));
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean re = false;
        try {
            //MountPoint -> If you want to use an other Device CHANGE THIS!
            File usb = new File("/storage/usbdisk");
            if (usb.exists()) {
                for (File f : usb.listFiles()) {
                    if (!allergy && f.getName().endsWith(".csv") && !f.getName().equals("allergie.csv")) {
                        File csv = instance.getDir("CSV", Activity.MODE_PRIVATE);
                        if (!csv.exists()) {
                            csv.mkdir();
                        }
                        File csv_file = new File(csv, "day.csv");
                        ByteStreams.copy(new FileInputStream(f.getAbsolutePath()), new FileOutputStream(csv_file.getAbsolutePath()));
                        re = true;
                    } else if (allergy && f.getName().equals("allergie.csv")) {
                        File csv = instance.getDir("CSV", Activity.MODE_PRIVATE);
                        if (!csv.exists()) {
                            csv.mkdir();
                        }
                        File csv_file = new File(csv, "allergy.csv");
                        ByteStreams.copy(new FileInputStream(f.getAbsolutePath()), new FileOutputStream(csv_file.getAbsolutePath()));
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return re;
    }

    @Override
    protected void onPostExecute(Boolean b) {
        dialog.dismiss();
        dialog.cancel();
        if (!b) {
            new AlertDialog.Builder(instance)
                    .setTitle(R.string.fail_title)
                    .setMessage(R.string.copy_fail)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        } else {
            new AlertDialog.Builder(instance)
                    .setTitle(R.string.success_title)
                    .setMessage(R.string.copy_success)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        }
    }
}
