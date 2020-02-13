package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;
import androidx.core.util.Consumer;
import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import de.schkola.kitchenscanner.database.LunchDatabase;
import de.schkola.kitchenscanner.util.LunchResult;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCustomerTask extends AsyncTask<Integer, Void, LunchResult> {

    private LunchDatabase database;
    private Consumer<LunchResult> consumer;

    public DatabaseCustomerTask(LunchDatabase database, Consumer<LunchResult> consumer) {
        this.database = database;
        this.consumer = consumer;
    }

    @Override
    protected LunchResult doInBackground(Integer... integers) {
        Customer c = database.customerDao().getCustomer(integers[0]);
        List<Allergy> a = new ArrayList<>();
        if (c != null) {
            a = database.allergyDao().getAllergies(c.xba);
        }
        return new LunchResult(c, a);
    }

    @Override
    protected void onPostExecute(LunchResult result) {
        consumer.accept(result);
    }
}
