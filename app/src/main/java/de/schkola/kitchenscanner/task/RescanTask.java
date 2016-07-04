package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;
import android.preference.PreferenceManager;

import de.schkola.kitchenscanner.activity.DisplayActivity;
import de.schkola.kitchenscanner.activity.MainActivity;

public class RescanTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... Void) {
        try {
            Thread.sleep(Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance()).getString("rescan", "2")) * 1000);
        } catch (InterruptedException ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        DisplayActivity.getInstance().finish();
        MainActivity.startScan();
    }
}
