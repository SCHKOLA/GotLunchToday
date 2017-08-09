package de.schkola.kitchenscanner.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.schkola.kitchenscanner.R;

public class JsonAllergyTask extends AsyncTask<Void, Void, Boolean> {

    private final InputStream is;
    private final Activity activity;
    private final ProgressDialog dialog;

    public JsonAllergyTask(InputStream is, Activity activity) {
        this.is = is;
        this.activity = activity;
        this.dialog = new ProgressDialog(activity);
        this.dialog.setCancelable(false);
        this.dialog.setTitle(activity.getString(R.string.copy_title));
        this.dialog.setMessage(activity.getString(R.string.copy_alert));
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        int xba = 0;
        int allergy = 1;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            File json = new File(activity.getDir("JSON", Activity.MODE_PRIVATE), "allergy.json");
            FileOutputStream out = new FileOutputStream(json);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "ISO-8859-1"));
            writer.beginArray();
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] line = csvLine.split(",");
                writer.beginObject();
                writer.name("xba").value(Integer.parseInt(line[xba]));
                writer.name("allergy").value(line[allergy]);
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        dialog.cancel();
    }
}
