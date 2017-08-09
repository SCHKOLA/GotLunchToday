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

public class JsonDayTask extends AsyncTask<Void, Void, Boolean> {

    private final InputStream is;
    private final Activity activity;
    private final ProgressDialog dialog;

    public JsonDayTask(InputStream is, Activity activity) {
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
        int clazz = 2;
        int xba = 3;
        int name = 4;
        int lunch = 5;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
            File json = new File(activity.getDir("JSON", Activity.MODE_PRIVATE), "day.json");
            FileOutputStream out = new FileOutputStream(json);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "ISO-8859-1"));
            writer.beginArray();
            String csvLine;
            boolean first = true;
            boolean headline = false;
            while ((csvLine = reader.readLine()) != null) {
                String[] line = csvLine.split(csvLine.split(",").length > 2 ? "," : ";");
                if (first) {
                    for (int i = 0; i < line.length; i++) {
                           if (line[i].replace("\"", "").equalsIgnoreCase("Klasse")) {
                               clazz = i;
                               headline = true;
                           } else if (line[i].replace("\"", "").equalsIgnoreCase("XBA")) {
                               xba = i;
                               headline = true;
                           } else if (line[i].replace("\"", "").equalsIgnoreCase("Name")) {
                               name = i;
                               headline = true;
                           } else if (line[i].replace("\"", "").equalsIgnoreCase("Gericht")) {
                               lunch = i;
                               headline = true;
                           }
                    }
                    if (!headline) {
                        writer.beginObject();
                        writer.name("class").value(line[clazz].replace("\"", ""));
                        writer.name("xba").value(Integer.parseInt(line[xba].replace("\"", "")));
                        writer.name("name").value(line[name].replace("\"", ""));
                        writer.name("lunch").value(line[lunch].replace("\"", ""));
                        writer.endObject();
                    }
                    first = false;
                } else {
                    writer.beginObject();
                    writer.name("class").value(line[clazz].replace("\"", ""));
                    writer.name("xba").value(Integer.parseInt(line[xba].replace("\"", "")));
                    writer.name("name").value(line[name].replace("\"", ""));
                    writer.name("lunch").value(line[lunch].replace("\"", ""));
                    writer.endObject();
                }
            }
            writer.endArray();
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
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
