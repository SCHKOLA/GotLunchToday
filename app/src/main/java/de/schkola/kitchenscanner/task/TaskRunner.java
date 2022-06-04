package de.schkola.kitchenscanner.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {

    public static final TaskRunner INSTANCE = new TaskRunner();

    private final Executor executor = Executors.newFixedThreadPool(4);
    private final Handler handler = new Handler(Looper.getMainLooper());

    public <R> void executeAsyncTask(AsyncTask<R> task) {
        executor.execute(() -> {
            handler.post(task::onPreExecute);
            final R result = task.doInBackground();
            handler.post(() -> task.onPostExecute(result));
        });
    }

    public void executeAsync(Runnable callable) {
        executor.execute(() -> {
            try {
                callable.run();
            } catch (Exception e) {
                Log.e("TaskRunner", "Task has thrown a exception", e);
            }
        });
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        executor.execute(() -> {
            try {
                final R result = callable.call();
                handler.post(() -> callback.onComplete(result));
            } catch (Exception e) {
                Log.e("TaskRunner", "Task has thrown a exception", e);
            }
        });
    }

    public interface Callback<R> {
        void onComplete(R result);
    }
}
