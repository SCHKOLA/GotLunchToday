package de.schkola.kitchenscanner.task.import_export;

import android.util.Log;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.task.AsyncTask;
import de.schkola.kitchenscanner.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

public class LunchImportTask implements AsyncTask<Void> {

    private final LunchDatabase database;
    private final InputStream inputStream;
    private final Map<Integer, Duplicate> duplicateEntries = Collections.synchronizedMap(new HashMap<>());
    private final Consumer<Collection<Duplicate>> resultListener;

    public LunchImportTask(LunchDatabase db, InputStream inputStream, Consumer<Collection<Duplicate>> resultListener) {
        this.database = db;
        this.inputStream = inputStream;
        this.resultListener = resultListener;
    }

    @Override
    public Void doInBackground() {
        try {
            CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                    .setAllowMissingColumnNames(true)
                        .setQuoteMode(QuoteMode.MINIMAL)
                        .setSkipHeaderRecord(true)
                        .setHeader()
                        .build();

            CSVParser csvParser = CSVParser.parse(inputStream, StandardCharsets.UTF_8, format);
            database.customerDao().deleteAll();
            scanDay(csvParser, database);
        } catch (IOException e) {
            Log.e("LunchImportTask", "Error in lunch import", e);
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
        resultListener.accept(duplicateEntries.values());
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
                    Duplicate duplicate = duplicateEntries.get(customer.xba);
                    if (duplicate == null) {
                        duplicate = new Duplicate(customer.name, customer.xba);
                        duplicate.lunches.add(checkCustomer.lunch);
                    }
                    duplicate.addLunch(customer.lunch);
                    duplicateEntries.put(customer.xba, duplicate);
                    continue;
                }
                database.customerDao().insertCustomer(customer);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading CSV file: ", ex);
        }
    }

    public static class Duplicate {
        private final int xba;
        private final String name;
        private final List<Byte> lunches = new ArrayList<>();

        public Duplicate(String name, int xba) {
            this.name = name;
            this.xba = xba;
        }

        public List<Byte> getLunches() {
            return lunches;
        }

        public String getName() {
            return name;
        }

        public boolean isPlausible() {
            byte compare = -1;
            for (Byte lunch : lunches) {
                if (compare == -1) {
                    compare = lunch;
                } else if (compare != lunch) {
                    return false;
                }
            }
            return true;
        }

        public int getXba() {
            return xba;
        }

        public void addLunch(byte lunch) {
            lunches.add(lunch);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Duplicate)) return false;
            Duplicate duplicate = (Duplicate) o;
            return xba == duplicate.xba;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(xba);
        }
    }
}
