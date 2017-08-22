/*
 * MIT License
 *
 * Copyright 2017 Niklas Merkelt
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

package de.schkola.kitchenscanner.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class JsonScanTask extends AsyncTask<InputStream, Void, Boolean> {

    private final ProgressDialog dialog;
    private final File jsonFolder;
    private final boolean allergy;

    public JsonScanTask(ProgressDialog dialog, File jsonFolder, boolean allergy) {
        this.dialog = dialog;
        this.jsonFolder = jsonFolder;
        this.allergy = allergy;
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(InputStream... inputStreams) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreams[0], "ISO-8859-1"));
            File json = new File(jsonFolder, allergy ? "allergy.json" : "day.json");
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(json), "ISO-8859-1"));
            if (allergy) {
                scanAllergy(reader, writer);
            } else {
                scanDay(reader, writer);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStreams[0].close();
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        dialog.cancel();
    }

    private void scanDay(BufferedReader reader, JsonWriter writer) {
        int clazz = 2;
        int xba = 3;
        int name = 4;
        int lunch = 5;
        try {
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
        }
    }

    private void scanAllergy(BufferedReader reader, JsonWriter writer) {
        int xba = 0;
        int allergy = 1;
        try {
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
        }
    }
}
