package de.schkola.kitchenscanner.task;

import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CsvImportTask extends ProgressAsyncTask<Void> {

    private final LunchDatabase database;
    private final boolean allergy;
    private final InputStream inputStream;
    private final Set<String> duplicateXba = Collections.synchronizedSet(new LinkedHashSet<>());
    private CsvImportTask.CsvImportListener cil;

    public CsvImportTask(LunchDatabase db, boolean allergy, InputStream inputStream) {
        this.database = db;
        this.allergy = allergy;
        this.inputStream = inputStream;
    }

    public void setCsvImportListener(CsvImportTask.CsvImportListener cil) {
        this.cil = cil;
    }

    @Override
    public Void doInBackground() {
        try {
            CSVFormat format = CSVFormat.DEFAULT.withAllowMissingColumnNames();
            if (!allergy) {
                format = format.withDelimiter(';')
                        .withSkipHeaderRecord()
                        .withHeader();
            }

            CSVParser csvParser = CSVParser.parse(inputStream, StandardCharsets.ISO_8859_1, format);
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
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (cil != null && !duplicateXba.isEmpty()) {
            cil.onDuplicateXba(duplicateXba);
        }
    }

    private void scanDay(CSVParser csvParser, LunchDatabase database) {
        try {
            for (CSVRecord r : csvParser) {
                Customer customer = new Customer();
                customer.grade = r.get("Klasse");
                customer.xba = Integer.parseInt(r.get("XBA"));
                customer.name = r.get("Name");
                customer.lunch = StringUtil.getLunch(r.get("Gericht"));
                Customer checkCustomer = database.customerDao().getCustomer(customer.xba);
                if (checkCustomer != null) {
                    duplicateXba.add(String.format("[%s]: %s", r.get("XBA"), r.get("Name")));
                    continue;
                }
                database.customerDao().insertCustomer(customer);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading CSV file: ", ex);
        }
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

    public interface CsvImportListener {
        void onDuplicateXba(Set<String> duplicateXba);
    }
}
