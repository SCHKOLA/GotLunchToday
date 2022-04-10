package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;
import androidx.core.util.Consumer;
import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.util.LunchResult;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCustomerTask extends AsyncTask<String, Void, LunchResult> {

    private final LunchDatabase database;
    private final Consumer<LunchResult> consumer;

    public DatabaseCustomerTask(LunchDatabase database, Consumer<LunchResult> consumer) {
        this.database = database;
        this.consumer = consumer;
    }

    @Override
    protected LunchResult doInBackground(String... strings) {
        List<Allergy> a = new ArrayList<>();
        String cleanedString = strings[0].trim().replaceAll("\\D", "");
        try {
            int xba = Integer.parseInt(cleanedString);
            Customer c = database.customerDao().getCustomer(xba);
            if (c != null) {
                a.addAll(database.allergyDao().getAllergies(c.xba));
            }
            return new LunchResult(c, a);
        } catch (NumberFormatException e) {
            return new LunchResult(null, a);
        }
    }

    @Override
    protected void onPostExecute(LunchResult result) {
        consumer.accept(result);
    }
}
