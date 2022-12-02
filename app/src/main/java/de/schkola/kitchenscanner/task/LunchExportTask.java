package de.schkola.kitchenscanner.task;

import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class LunchExportTask extends ProgressAsyncTask<Void> {

    private final LunchDatabase database;
    private final OutputStream outputStream;

    public LunchExportTask(LunchDatabase database, OutputStream outputStream) {
        this.database = database;
        this.outputStream = outputStream;
    }

    @Override
    public Void doInBackground() {
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), CSVFormat.DEFAULT)) {
            printer.printRecord("XBA", "Name", "Gericht");
            List<Customer> c = database.customerDao().getCustomerGotLunch();
            for (Customer customer : c) {
                printer.printRecord(customer.xba, customer.name, customer.lunch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException ignored) {
                // Empty on purpose
            }
        }
        return null;
    }
}
