package cz.vutbr.fit.mogger;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import static cz.vutbr.fit.mogger.Constants.CHECK;
import static cz.vutbr.fit.mogger.Constants.SAVE;


public class MoveTriggerActivity extends Activity implements OnClickListener {

    TextView textView;
    Button button1;
    Button button2;
    SensorManager sensorManager;
    Sensor accelerometer;
    Sounds sounds;
    FileStorage storage;

    Listener fastestListener;

    // prace s gesty
    Gesture gesture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        fastestListener = new Listener(this);
        sensorManager.registerListener(fastestListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        sounds = new Sounds();
        storage = new FileStorage(getApplicationContext());
        gesture = new Gesture(this, storage);

        // GUI kravinky
        textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Mogger v 1.3");
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        // stavy pro buttony
        button1.setTag(1);
        button2.setTag(1);

        if (storage.loadConfig()) {
            textView.setText("Loaded");
        } else {
            textView.setText("Config Not Found...");
        }
    }

    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(fastestListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(fastestListener);
    }

    // zachyceni kliku na buttony a zavolani jejich akci
    public void onClick(View v) {

        final int id = v.getId();
        int status;

        switch (id) {
            // kontrola s ulozenym gestem
            case R.id.button1:
                status = (Integer) button1.getTag();

                if (status == 1) {
                    button2.setEnabled(false);
                    fastestListener.startRecording();
                    button1.setTag(2);
                    this.button1.setText("Stop");
                    textView.setText("Testing...");
                } else {
                    fastestListener.stopRecording();
                    button1.setTag(1);
                    this.button1.setText("Start");
                    textView.setText("Mogger v 1.3");
                    button2.setEnabled(true);
                }
                break;

            // proces ulozeni novehogesta
            case R.id.button2:

                status = (Integer) button2.getTag();

                if (status == 1) {
                    button2.setTag(2);
                    button1.setEnabled(false);
                    textView.setText("Make gesture! First beep - start recording. Second beep - stop recording.");
                    this.button2.setText("Stop");

                    // promaze data stareho ulozeneho gesta
//                    gesture.clear();

                    // zpozdeni 2 sec pred samotnym nahravanim gesta
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            // ulozeni gesta
                            fastestListener.startRecording();
                        }
                    }, 1000);
                }

                break;
        }

    }

    public int mogger_action() {

        int status1 = (Integer) button1.getTag();

        // porovnani gest
        if (status1 == 2) {
            return CHECK;
        }

        // ulozeni noveho gesta
        return SAVE;

    }
}
