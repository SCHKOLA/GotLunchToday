package de.schkola.kitchenscanner.util;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TorchManager {

    private ScheduledExecutorService s;
    private CameraManager manager;
    private AVCallback av;
    private TCallback t;

    public TorchManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            s = Executors.newSingleThreadScheduledExecutor();
            while (manager == null) {
                manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            }
            av = new AVCallback();
            manager.registerAvailabilityCallback(av, null);
            t = new TCallback();
            manager.registerTorchCallback(t, null);
        }
    }

    public void shutdown() {
        //Reset torch mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            s.shutdownNow();
            manager.unregisterAvailabilityCallback(av);
            manager.unregisterTorchCallback(t);
            try {
                manager.setTorchMode("0", false);
            } catch (CameraAccessException ignored) {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private class AVCallback extends CameraManager.AvailabilityCallback {

        @Override
        public void onCameraAvailable(@NonNull String cameraId) {
            if (cameraId.equals("0")) {
                try {
                    manager.setTorchMode("0", true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private class TCallback extends CameraManager.TorchCallback {

        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            if (s.isShutdown()) {
                return;
            }
            if (cameraId.equals("0") && enabled) {
                s.schedule(() -> {
                    try {
                        manager.setTorchMode("0", false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }, 150, TimeUnit.MILLISECONDS);
            }
        }
    }
}
