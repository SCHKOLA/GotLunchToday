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
import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.util.LunchUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CsvImportTask extends AsyncTask<InputStream, Void, Boolean> {

    private final ProgressDialog dialog;
    private final LunchDatabase database;
    private final boolean allergy;

    public CsvImportTask(ProgressDialog dialog, LunchDatabase database, boolean allergy) {
        this.dialog = dialog;
        this.database = database;
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
            if (allergy) {
                database.allergyDao().deleteAll();
                scanAllergy(csvParser, database);
            } else {
                scanDay(csvParser, database);
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

    private void scanDay(CSVParser csvParser, LunchDatabase database) {
        try {
            for (CSVRecord record : csvParser) {
                Customer customer = new Customer();
                customer.grade = record.get("Klasse");
                customer.xba = Integer.parseInt(record.get("XBA"));
                customer.name = record.get("Name");
                customer.lunch = LunchUtil.getLunch(record.get("Gericht"));
                database.customerDao().insertCustomer(customer);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
    }

    private void scanAllergy(CSVParser csvParser, LunchDatabase database) {
        int xba = 0;
        int allergy = 1;
        try {
            for (CSVRecord record : csvParser) {
                Allergy a = new Allergy();
                a.xba = Integer.parseInt(record.get(xba));
                a.allergy = record.get(allergy);
                database.allergyDao().insertAllergy(a);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
    }


}
