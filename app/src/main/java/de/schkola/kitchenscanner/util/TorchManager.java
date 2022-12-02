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

@RequiresApi(Build.VERSION_CODES.M)
public class TorchManager {

    private static final long TORCH_WAIT_TIME = 150;

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
        cameraManager.registerAvailabilityCallback(availabilityCallback, null);
        torchCallback = new TorchCallback();
        cameraManager.registerTorchCallback(torchCallback, null);
    }

    public void shutdown() {
        //Reset torch mode
        executorService.shutdownNow();
        cameraManager.unregisterAvailabilityCallback(availabilityCallback);
        cameraManager.unregisterTorchCallback(torchCallback);
        try {
            cameraManager.setTorchMode("0", false);
        } catch (CameraAccessException ignored) {
            // Empty on purpose
        }
    }

    private class AvailabilityCallback extends CameraManager.AvailabilityCallback {

        @Override
        public void onCameraAvailable(@NonNull String cameraId) {
            if (cameraId.equals("0")) {
                try {
                    cameraManager.setTorchMode("0", true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TorchCallback extends CameraManager.TorchCallback {

        @Override
        public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
            if (executorService.isShutdown()) {
                return;
            }
            if (cameraId.equals("0") && enabled) {
                executorService.schedule(() -> {
                    try {
                        cameraManager.setTorchMode("0", false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }, TORCH_WAIT_TIME, TimeUnit.MILLISECONDS);
            }
        }
    }
}
