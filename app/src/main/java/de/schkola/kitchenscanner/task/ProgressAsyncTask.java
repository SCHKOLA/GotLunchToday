package de.schkola.kitchenscanner.task;

import android.os.AsyncTask;

public abstract class ProgressAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private ProgressListener progressListener;

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected void onPreExecute() {
        if (progressListener != null)
            progressListener.onStart();
    }

    @Override
    protected void onPostExecute(Result result) {
        if (progressListener != null) {
            progressListener.onFinished();
        }
    }

    public interface ProgressListener {

        void onStart();

        void onFinished();
    }
}
