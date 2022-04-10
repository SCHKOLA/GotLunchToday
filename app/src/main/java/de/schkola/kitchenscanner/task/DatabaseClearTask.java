package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;
import de.schkola.kitchenscanner.database.LunchDatabase;

public class DatabaseClearTask extends AsyncTask<Void, Void, Void> {

    private final LunchDatabase database;

    public DatabaseClearTask(LunchDatabase database) {
        this.database = database;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        database.customerDao().deleteAll();
        return null;
    }
}
