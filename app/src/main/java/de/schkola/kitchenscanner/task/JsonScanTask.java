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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

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
            CSVFormat format = CSVFormat.DEFAULT;
            if (!allergy) {
                format = format.withDelimiter(';')
                        .withSkipHeaderRecord()
                        .withHeader("WT", "KW", "Klasse", "XBA", "Name", "Gericht");
            }

            CSVParser csvParser = CSVParser.parse(inputStreams[0], Charset.forName("ISO-8859-1"), format);
            File json = new File(jsonFolder, allergy ? "allergy.json" : "day.json");
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(json), "ISO-8859-1"));
            if (allergy) {
                scanAllergy(csvParser, writer);
            } else {
                scanDay(csvParser, writer);
            }
        } catch (IOException e) {
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

    private void scanDay(CSVParser csvParser, JsonWriter writer) {
        try {
            writer.beginArray();
            for (CSVRecord record : csvParser) {
                writer.beginObject();
                writer.name("class").value(record.get("Klasse"));
                writer.name("xba").value(Integer.parseInt(record.get("XBA")));
                writer.name("name").value(record.get("Name"));
                writer.name("lunch").value(record.get("Gericht"));
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
    }

    private void scanAllergy(CSVParser csvParser, JsonWriter writer) {
        int xba = 0;
        int allergy = 1;
        try {
            writer.beginArray();
            for (CSVRecord record : csvParser) {
                writer.beginObject();
                writer.name("xba").value(Integer.parseInt(record.get(xba)));
                writer.name("allergy").value(record.get(allergy));
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
    }
}
