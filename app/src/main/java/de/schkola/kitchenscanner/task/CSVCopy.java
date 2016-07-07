package de.schkola.kitchenscanner.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.activity.MainActivity;

/**
 * Diese Klassen sorgt für das Asyncrone Kopieren der CSV Dateien
 */
public class CSVCopy extends AsyncTask<Void, Void, Boolean> {

    private ProgressDialog dialog;

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(MainActivity.getInstance());
        dialog.setCancelable(false);
        dialog.setTitle(MainActivity.getInstance().getString(R.string.copy_title));
        dialog.setMessage(MainActivity.getInstance().getString(R.string.copy_alert));
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean re = false;
        try {
            //Mountpoint -> Bei der verwendung eines anderen Gerätes ändern
            File usb = new File("/mnt/usb");
            if (usb.exists()) {
                File[] files = usb.listFiles();
                for (File f : files) {
                    if (f.getName().endsWith(".csv") && !f.getName().equals("allergie.csv")) {
                        File csv = MainActivity.getInstance().getDir("CSV", MainActivity.MODE_PRIVATE);
                        if (!csv.exists()) {
                            csv.mkdir();
                        }
                        File csv_file = new File(csv, "teilnahme.csv");
                        FileInputStream in = new FileInputStream(f.getAbsolutePath());
                        FileOutputStream out = new FileOutputStream(csv_file.getAbsolutePath());

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.flush();
                        out.close();
                        re = true;
                    } else if (f.getName().equals("allergie.csv")) {
                        File csv = MainActivity.getInstance().getDir("CSV", MainActivity.MODE_PRIVATE);
                        if (!csv.exists()) {
                            csv.mkdir();
                        }
                        File csv_file = new File(csv, "allergie.csv");
                        FileInputStream in = new FileInputStream(f.getAbsolutePath());
                        FileOutputStream out = new FileOutputStream(csv_file.getAbsolutePath());

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.flush();
                        out.close();
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
            new AlertDialog.Builder(MainActivity.getInstance())
                    .setTitle(R.string.fail_title)
                    .setMessage(R.string.copy_fail)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        } else {
            MainActivity.loadDataIntoApp();
            new AlertDialog.Builder(MainActivity.getInstance())
                    .setTitle(R.string.success_title)
                    .setMessage(R.string.copy_success)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        }
    }
}
