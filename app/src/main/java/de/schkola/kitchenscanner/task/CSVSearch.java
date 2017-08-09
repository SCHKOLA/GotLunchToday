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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.schkola.kitchenscanner.R;

/**
 * Searches needed CSV-Files on the USB disk
 */
public class CSVSearch extends AsyncTask<Void, Void, File> {

    private final ProgressDialog dialog;
    private final Activity instance;
    private final boolean allergy;

    public CSVSearch(Activity instance, boolean allergy) {
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
    protected File doInBackground(Void... voids) {
        //MountPoint -> If you want to use an other Device CHANGE THIS!
        File usb = new File("/storage/usbdisk");
        if (usb.exists()) {
            for (File f : usb.listFiles()) {
                if (!allergy && f.getName().endsWith(".csv") && !f.getName().equals("allergie.csv")) {
                    return f;
                } else if (allergy && f.getName().equals("allergie.csv")) {
                    return f;
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(File f) {
        dialog.dismiss();
        dialog.cancel();
        if (f != null) {
            try {
                if (allergy) {
                    new JsonAllergyTask(new FileInputStream(f.getAbsolutePath()), instance).execute();
                } else {
                    new JsonDayTask(new FileInputStream(f.getAbsolutePath()), instance).execute();
                }
            } catch (FileNotFoundException e) {
                new AlertDialog.Builder(instance)
                        .setTitle(R.string.fail_title)
                        .setMessage(R.string.csv_read_fail)
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
            }
        } else {
            new AlertDialog.Builder(instance)
                    .setTitle(R.string.fail_title)
                    .setMessage(R.string.csv_read_fail)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        }
    }
}
