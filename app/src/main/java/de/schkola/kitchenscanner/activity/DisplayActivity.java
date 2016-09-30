package de.schkola.kitchenscanner.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import de.schkola.kitchenscanner.R;
import de.schkola.kitchenscanner.task.FlashLightTask;
import de.schkola.kitchenscanner.task.RescanTask;

public class DisplayActivity extends AppCompatActivity {

    private static RescanTask rct;
    private static DisplayActivity instance;
    private static Camera camera;

    public static DisplayActivity getInstance() {
        return instance;
    }

    public static void setFlashLight(boolean b) {
        if (camera == null) {
            try {
                camera = Camera.open();
            } catch (RuntimeException e) {
                camera = Camera.open();
            }
        }
        Camera.Parameters p = camera.getParameters();
        if (b) {
            // Turn on
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
        } else {
            // Turn off
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Beende den letzten Rescantask (Bug-Fix)
        if (rct != null) {
            rct.cancel(true);
        }
        instance = this;
        super.onCreate(savedInstanceState);
        //Setzte die Activity Vollbild
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Setzte Content
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        //Bearbeite Content
        TextView tv_name = (TextView) findViewById(R.id.name);
        tv_name.setText(intent.getStringExtra("name"));
        TextView tv_clazz = (TextView) findViewById(R.id.clazz);
        tv_clazz.setText(intent.getStringExtra("class"));
        TextView tv_lunch = (TextView) findViewById(R.id.lunch);
        tv_lunch.setText(intent.getStringExtra("lunch"));
        TextView tv_gotĹunch = (TextView) findViewById(R.id.gotToday);
        if (intent.getIntExtra("gotĹunch", 0) > 1) {
            tv_gotĹunch.setText(String.format("%s%s%s", getString(R.string.gotLunch_2), String.valueOf(intent.getIntExtra("gotĹunch", 0)), getString(R.string.gotLunch_1)));
        }
        TextView tv_allergie = (TextView) findViewById(R.id.allergie);
        tv_allergie.setText(intent.getStringExtra("allergie"));
        //Starte FlashLightTask
        new FlashLightTask().execute();
        //Starte neuen Rescan task
        rct = new RescanTask();
        rct.execute();
    }
}
