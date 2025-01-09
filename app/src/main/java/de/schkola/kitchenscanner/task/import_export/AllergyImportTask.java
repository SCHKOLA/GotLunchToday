package de.schkola.kitchenscanner.task.import_export;

import android.util.Log;
import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.task.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class AllergyImportTask implements AsyncTask<Void> {

    private final LunchDatabase database;
    private final InputStream inputStream;
    private final Runnable resultListener;

    public AllergyImportTask(LunchDatabase db, InputStream inputStream, Runnable resultListener) {
        this.database = db;
        this.inputStream = inputStream;
        this.resultListener = resultListener;
    }

    @Override
    public Void doInBackground() {
        try {
            CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setAllowMissingColumnNames(true)
                    .build();
            CSVParser csvParser = CSVParser.parse(inputStream, StandardCharsets.UTF_8, format);
            database.allergyDao().deleteAll();
            scanAllergy(csvParser, database);
        } catch (IOException e) {
            Log.e("AllergyImportTask", "Error in allergy import", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
                // Empty on purpose
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        resultListener.run();
    }

    private void scanAllergy(CSVParser csvParser, LunchDatabase database) {
        try {
            for (CSVRecord r : csvParser) {
                Allergy a = new Allergy();
                a.xba = Integer.parseInt(r.get(0));
                a.allergy = r.get(1);
                database.allergyDao().insertAllergy(a);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
    }
}
