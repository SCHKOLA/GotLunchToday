package de.schkola.kitchenscanner.util;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TorchManager {

    private static final String LOG_TAG = "TorchManager";
    private static final long TORCH_WAIT_TIME = 25;
    private static final long TORCH_ON_TIME = 300;
    private static final String CAMERA_ID = "0";

    private final ScheduledExecutorService executorService;
    private CameraManager cameraManager;
    private final AvailabilityCallback availabilityCallback;
    private final TorchCallback torchCallback;

    public TorchManager(Context context) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        while (cameraManager == null) {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
        availabilityCallback = new AvailabilityCallback();
        torchCallback = new TorchCallback();
    }

    public void enable() {
        cameraManager.registerAvailabilityCallback(availabilityCallback, null);
        cameraManager.registerTorchCallback(torchCallback, null);
    }

    public void shutdown() {
        cameraManager.unregisterAvailabilityCallback(availabilityCallback);
        cameraManager.unregisterTorchCallback(torchCallback);
        executorService.shutdownNow();
        try {
            cameraManager.setTorchMode(CAMERA_ID, false);
        } catch (CameraAccessException ignored) {
            // Empty on purpose
        }
    }

    private class AvailabilityCallback extends CameraManager.AvailabilityCallback {

        @Override
        public void onCameraAvailable(@NonNull String cameraId) {
            if (cameraId.equals(CAMERA_ID)) {
                executorService.schedule(() -> {
                    try {
                        cameraManager.setTorchMode(CAMERA_ID, true);
                    } catch (CameraAccessException e) {
                        Log.e(LOG_TAG, "torch can't be turned on", e);
                    }
                }, TORCH_WAIT_TIME, TimeUnit.MILLISECONDS);
            }
        }
    }

    private class TorchCallback extends CameraManager.TorchCallback {

        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            if (executorService.isShutdown()) {
                return;
            }
            if (cameraId.equals(CAMERA_ID) && enabled) {
                executorService.schedule(() -> {
                    try {
                        cameraManager.setTorchMode(CAMERA_ID, false);
                    } catch (CameraAccessException e) {
                        Log.e(LOG_TAG, "torch can't be turned off", e);
                    }
                }, TORCH_ON_TIME, TimeUnit.MILLISECONDS);
            }
        }
    }
}
