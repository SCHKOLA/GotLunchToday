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

public class LunchExportTask extends ProgressAsyncTask<OutputStream, Void, Boolean> {

    private final LunchDatabase database;

    public LunchExportTask(LunchDatabase database) {
        this.database = database;
    }

    @Override
    protected Boolean doInBackground(OutputStream... outputStream) {
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(outputStream[0], StandardCharsets.ISO_8859_1), CSVFormat.DEFAULT)) {
            printer.printRecord("XBA", "Name", "Gericht");
            List<Customer> c = database.customerDao().getCustomerGotLunch();
            for (Customer customer : c) {
                printer.printRecord(customer.xba, customer.name, customer.lunch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream[0].flush();
                outputStream[0].close();
            } catch (IOException ignored) {
            }
        }
        return null;
    }
}
