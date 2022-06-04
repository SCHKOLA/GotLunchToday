package de.schkola.kitchenscanner.task;

import android.app.ProgressDialog;

public abstract class ProgressAsyncTask<R> implements AsyncTask<R> {

    private ProgressDialog dialog;

    public void setProgressDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onPreExecute() {
        if (dialog != null)
            dialog.show();
    }

    @Override
    public void onPostExecute(R result) {
        if (dialog != null) {
            dialog.dismiss();
            dialog.cancel();
        }
    }
}
