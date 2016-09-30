package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;

import de.schkola.kitchenscanner.activity.DisplayActivity;

/**
 * Dieser Task wird ausgeführt, wenn das Blitzlicht erscheint
 */
public class FlashLightTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        DisplayActivity.setFlashLight(true);
    }

    @Override
    protected Void doInBackground(Void... Void) {
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        DisplayActivity.setFlashLight(false);
    }
}