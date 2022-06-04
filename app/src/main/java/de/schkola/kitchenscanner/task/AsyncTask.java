package de.schkola.kitchenscanner.task;

public interface AsyncTask<R> {

    R doInBackground();

    default void onPreExecute() {
        // Empty on purpose
    }

    default void onPostExecute(R result) {
        // Empty on purpose
    }
}
